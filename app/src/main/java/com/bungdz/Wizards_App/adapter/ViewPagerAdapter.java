package com.bungdz.Wizards_App.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bungdz.Wizards_App.ui.AlarmUI.CreateAlarmFragment;
import com.bungdz.Wizards_App.ui.home.HomeFragment;
import com.bungdz.Wizards_App.ui.MeshNetwork.MeshNetworkFragment;
import com.bungdz.Wizards_App.ui.Devices.DevicesFragment;
import com.bungdz.Wizards_App.ui.AlarmUI.ListAlarmFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new HomeFragment();
            case 1:
                return new MeshNetworkFragment();
            case 2:
                return new DevicesFragment();
            case 3:
                return new CreateAlarmFragment();
            case 4:
                return new ListAlarmFragment();
            default:
                return new HomeFragment();

        }
    }
    @Override
    public int getItemCount() {
        return 5;
    }
}
