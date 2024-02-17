package com.bungdz.Wizards_App;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bungdz.Wizards_App.constants.Constants;
import com.bungdz.Wizards_App.adapter.ViewPagerAdapter;
import com.bungdz.Wizards_App.models.NodeInfo;
import com.bungdz.Wizards_App.models.SharedViewModel;
import com.bungdz.Wizards_App.networking.MqttConnectionCallback;
import com.bungdz.Wizards_App.networking.ThingsBoardHandle;
import com.bungdz.Wizards_App.models.ThingsBoardInfo;
import com.bungdz.Wizards_App.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private ActivityMainBinding binding;
    private long backPressTime;
    private Toast mToast;
    private static final int FRAGMENT_HOME = 1;
    private static final int FRAGMENT_MESH_INFO = 2;
    private static final int FRAGMENT_DEVICES_CONTROL = 3;
    private static final int FRAGMENT_CREATE_ALARM = 4;
    private static final int NAV_HOME = R.id.nav_home;
    private static final int NAV_MESH_INFO = R.id.nav_mesh_info;
    private static final int NAV_DEVICE_CONTROL = R.id.nav_device_control;
    private static final int NAV_ALARM = R.id.nav_alarm;
    private int currentFragment = FRAGMENT_HOME;
    public  String  parent;
    public  SharedViewModel sharedViewModel;
    private Handler handler = new Handler();
    private Runnable taskRunnable = new Runnable() {
        @Override
        public void run() {
            ThingsBoardHandle.getjwtToken("bungvu50@gmail.com", "123456", new Callback(){
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.d("Main Activity", "Error - " + e.getMessage());
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if (response.isSuccessful()) {
                        JsonElement rootElement = JsonParser.parseString(response.body().string());
                        if (rootElement.isJsonObject()) {
                            JsonObject jsonObject = rootElement.getAsJsonObject();
                            if (jsonObject.has("token")) {
                                ThingsBoardInfo.JWT_TOKEN = jsonObject.get("token").getAsString();
                            } else {
                                Log.d("Main Activity","Token not found in JSON data.");
                            }
                        }
                    } else {
                        Log.d("Main Activity", "Not Success - code: " + response.code());
                    }
                }
            });
            handler.postDelayed(this, 3600000); // 1 giờ
        }
    };

    private Runnable taskGetActiveRunnable = new Runnable() {
        @Override
        public void run() {
            ThingsBoardHandle.getActiveGateway(ThingsBoardInfo.JWT_TOKEN, new Callback() {
                private String responseData;
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    responseData = response.body().string();
                    response.body().close();
                    Log.d("getUpdateAlarm",responseData);
                    try {
                        JsonElement jsonElement = JsonParser.parseString(responseData);
                        JsonArray jsonArray = jsonElement.getAsJsonArray();
                        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                        boolean value = jsonObject.get("value").getAsBoolean();
                        if (!value) {
                            sharedViewModel.setActive(false);
                            Log.d("isGatewayConnected","false");
                            //mToast = Toast.makeText(MainActivity.this, "Gateway Disconnected !!!", Toast.LENGTH_LONG);
                            //mToast.show();
                            // Thay thế bằng cách hiển thị thông báo trong ứng dụng Android của bạn
                            //connectionStatus.setText("Mất Kết nối đến Gateway !!!");
                            //statusBar.setVisibility(View.VISIBLE);
                        } else {
                            Log.d("isGatewayConnected","true");
                            sharedViewModel.setActive(true);
                            //statusBar.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            handler.postDelayed(this, 30000); // 1 giờ
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        // getting our root layout in our view.
        View view = binding.getRoot();
        setContentView(view);
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        setSupportActionBar(binding.toolbar);
        setUpViewPager();
        View headerView = binding.navView.getHeaderView(0);
        TextView usernameTextView = headerView.findViewById(R.id.tv_own);
        TextView gmailTextView = headerView.findViewById(R.id.tv_userSide);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USER GMAIL")) {
            String userEmail = intent.getStringExtra("USER GMAIL");
            gmailTextView.setText(userEmail);
        }
//        ThingsBoardHandle.mqttAndroidClientHiveMQ = ThingsBoardHandle.ConnectMqttHiveMQ(getApplicationContext(), Constants.CLIENT_HIVEMQ_ID, new MqttConnectionCallback() {
//            @Override
//            public void onSuccess() {
//                Log.d("Main Activity", "Ket noi MQTT Thanh cong  ");
//            }
//
//            @Override
//            public void onFailure(Throwable exception) {
//                Log.d("Main Activity", "Ket noi MQTT That bai  ");
//            }
//        });
        ArrayList<String> listRole = new ArrayList<>();
        ThingsBoardHandle.getMesh(ThingsBoardInfo.JWT_TOKEN, Constants.THINGS_BOARD_INFOS[0].getDeviceID(), new Callback() {
            private String responseData;
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                responseData = response.body().string();
                response.body().close();
                JsonArray jsonArray = JsonParser.parseString(responseData).getAsJsonArray();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                    JsonObject value = jsonObject.get("value").getAsJsonObject();
                    listRole.add(value.get("role").getAsString());
                }
                sharedViewModel.setListRole(listRole);
            }
        });

        handler.post(taskRunnable);
        //handler.post(taskGetActiveRunnable);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,binding.drawerLayout,binding.toolbar,R.string.open,R.string.close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        binding.navView.setNavigationItemSelectedListener(this);
        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case NAV_HOME:
                    openHomeFragment();
                    binding.navView.setCheckedItem(R.id.nav_sidebar_home);
                    break;

                case NAV_MESH_INFO:
                    openMeshInfoFragment();
                    binding.navView.setCheckedItem(R.id.nav_sidebar_mesh_info);
                    break;

                case NAV_DEVICE_CONTROL:
                    openControlDeviceFragment();
                    binding.navView.setCheckedItem(R.id.nav_sidebar_device_control);
                    break;

                case NAV_ALARM:
                    openHenGioFragment();
                    binding.navView.setCheckedItem(R.id.nav_sidebar_create_alarm);
                    break;
            }
            setTitleToolBar();
            return true;
        });
        //replaceFragment(new HomeFragment());
        binding.navView.setCheckedItem(R.id.nav_sidebar_home);
        binding.bottomNavigation.getMenu().findItem(R.id.nav_home).setChecked(true);
        setTitleToolBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy lịch thực hiện tác vụ khi Activity bị hủy
        handler.removeCallbacks(taskRunnable);
//        if (webSocketManager != null) {
//            webSocketManager.disconnect();
//        }
    }

    private void openHomeFragment() {
        if(currentFragment != FRAGMENT_HOME){
            binding.navHostFragment.setCurrentItem(0);
            //replaceFragment(new HomeFragment());
            currentFragment=FRAGMENT_HOME;
        }
    }

    private void openMeshInfoFragment() {
        if(currentFragment != FRAGMENT_MESH_INFO){
            binding.navHostFragment.setCurrentItem(1);
            //replaceFragment(new OutSlideFragment());
            currentFragment= FRAGMENT_MESH_INFO;
        }
    }

    private void openControlDeviceFragment() {
        if(currentFragment != FRAGMENT_DEVICES_CONTROL){
            binding.navHostFragment.setCurrentItem(2);
            // replaceFragment(new FirstFloorFragment());
            currentFragment= FRAGMENT_DEVICES_CONTROL;
        }
    }

    private void openHenGioFragment() {
        if(currentFragment != FRAGMENT_CREATE_ALARM){
            binding.navHostFragment.setCurrentItem(3);
            // replaceFragment(new AlarmFragment());
            currentFragment= FRAGMENT_CREATE_ALARM;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_chart) {
            Intent intent = new Intent(MainActivity.this, ChartActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id= item.getItemId();

        if(id==R.id.nav_sidebar_home){
            openHomeFragment();
            binding.navView.getMenu().findItem(R.id.nav_home).setChecked(true);
        } else if(id==R.id.nav_sidebar_mesh_info){

            openMeshInfoFragment();
            binding.navView.getMenu().findItem(R.id.nav_mesh_info).setChecked(true);
        }
        else if(id==R.id.nav_sidebar_device_control){

            openControlDeviceFragment();
            binding.navView.getMenu().findItem(R.id.nav_device_control).setChecked(true);
        }else if(id==R.id.nav_sidebar_create_alarm){
            openHenGioFragment();
            binding.navView.getMenu().findItem(R.id.nav_alarm).setChecked(true);
        }else if(id==R.id.nav_logout){
            Toast.makeText(MainActivity.this, "Đăng xuất thành công!!!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        setTitleToolBar();
        DrawerLayout drawerLayout=findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setTitleToolBar(){
        String titile;
        switch (currentFragment){
            case FRAGMENT_HOME:
                titile=getString(R.string.menu_home);
                break;
            case FRAGMENT_MESH_INFO:
                titile=getString(R.string.menu_mesh_info);
                break;
            case FRAGMENT_DEVICES_CONTROL:
                titile=getString(R.string.menu_device);
                break;
            case FRAGMENT_CREATE_ALARM:
                titile=getString(R.string.menu_create_alarm);
                break;
            default:
                titile = "Alarm List";
                break;
        }

        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle(titile);
        }
    }

    private void setUpViewPager() {
        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(this);

        binding.navHostFragment.setAdapter(mViewPagerAdapter);

        binding.navHostFragment.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {

                    case 0:
                        currentFragment=FRAGMENT_HOME;
                        binding.bottomNavigation.getMenu().findItem(R.id.nav_home).setChecked(true);
                        binding.navView.getMenu().findItem(R.id.nav_sidebar_home).setChecked(true);
                        break;

                    case 1:
                        currentFragment= FRAGMENT_MESH_INFO;
                        binding.navView.getMenu().findItem(R.id.nav_sidebar_mesh_info).setChecked(true);
                        binding.bottomNavigation.getMenu().findItem(R.id.nav_mesh_info).setChecked(true);
                        break;

                    case 2:
                        currentFragment= FRAGMENT_DEVICES_CONTROL;
                        binding.navView.getMenu().findItem(R.id.nav_sidebar_device_control).setChecked(true);
                        binding.bottomNavigation.getMenu().findItem(R.id.nav_device_control).setChecked(true);
                        break;

                    case 3:
                        currentFragment= FRAGMENT_CREATE_ALARM;
                        binding.navView.getMenu().findItem(R.id.nav_sidebar_create_alarm).setChecked(true);
                        binding.bottomNavigation.getMenu().findItem(R.id.nav_alarm).setChecked(true);
                        break;
                }
                setTitleToolBar();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.END)){
            binding.drawerLayout.closeDrawer(GravityCompat.END);
        }else  if (backPressTime + 2000 > System.currentTimeMillis()) {
            mToast.cancel();
            super.onBackPressed();
            return;
        } else {
            mToast = Toast.makeText(MainActivity.this, "Press back again to exit the application !", Toast.LENGTH_SHORT);
            mToast.show();
        }

        backPressTime = System.currentTimeMillis();
    }
}