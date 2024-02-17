package com.bungdz.Wizards_App;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import com.bungdz.Wizards_App.databinding.ActivityLoginMainBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity   {
    private ActivityLoginMainBinding binding;
    private SharedPreferences.Editor loginPrefsEditor;

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert=new AlertDialog.Builder(LoginActivity.this);
        alert.setCancelable(true);
        alert.setIcon(android.R.drawable.ic_dialog_alert);
        alert.setTitle("Wizards App");
        alert.setMessage("Are you sure you want to close this activity?");
        alert.setPositiveButton("Yes", (dialog, which) -> {
            finishAffinity();
            System.exit(0);
        });
        alert.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog=alert.create();
        alertDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityLoginMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        FirebaseAuth fAuth=FirebaseAuth.getInstance();
        SharedPreferences loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        boolean saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin) {
            binding.edtUser.setText(loginPreferences.getString("email", ""));
            binding.edtPassword.setText(loginPreferences.getString("password", ""));
            binding.btnRemember.setChecked(true);
        }
        binding.btnLogin.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            String email=binding.edtUser.getText().toString().trim();
            String password=binding.edtPassword.getText().toString().trim();
            if(TextUtils.isEmpty(email)){
                Toast.makeText(getApplicationContext(),"Empty??",Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(password)){
                Toast.makeText(getApplicationContext(),"Empty??",Toast.LENGTH_SHORT).show();
                return;
            }
            if(password.length()<6){
                Toast.makeText(getApplicationContext(),"Too sort??",Toast.LENGTH_SHORT).show();
                return;
            }
            fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    binding.progressBar.setVisibility(View.GONE);
                    if (binding.btnRemember.isChecked()) {
                        loginPrefsEditor.putBoolean("saveLogin", true);
                        loginPrefsEditor.putString("email", email);
                        loginPrefsEditor.putString("password", password);
                        loginPrefsEditor.commit();
                    } else {
                        loginPrefsEditor.clear();
                        loginPrefsEditor.commit();
                    }
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!!!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("USER GMAIL", email); // Đưa dữ liệu vào Intent với key là "STRING_DATA"
                    startActivity(intent);
                    LoginActivity.this.finish();
                }else {
                    Toast.makeText(LoginActivity.this,  Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }

            });
        });
        binding.btnSignin.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this,SignInActivity.class)));

        binding.btnForgot.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this,ForgotActivity.class)));

    }
}
