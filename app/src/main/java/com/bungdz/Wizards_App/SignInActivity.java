package com.bungdz.Wizards_App;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;
import com.bungdz.Wizards_App.models.User;
import com.bungdz.Wizards_App.databinding.ActivitySigninMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {
    private ActivitySigninMainBinding binding;
    FirebaseAuth fAuth=FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySigninMainBinding.inflate(getLayoutInflater());
        // getting our root layout in our view.
        View view = binding.getRoot();
        setContentView(view);
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnSignIn.setOnClickListener(v -> {
            String email=binding.edtUserGmail.getText().toString().trim();
            String password=binding.edtPassword.getText().toString().trim();
            String num=binding.edtUserNum.getText().toString().trim();
            String name=binding.edtUserName.getText().toString().trim();
            if(TextUtils.isEmpty(name)){
                binding.edtUserNum.setError("Name Empty??");
                binding.edtUserNum.requestFocus();
                return;
            }
            if(TextUtils.isEmpty(email)){
                binding.edtUserGmail.setError("Gmail Empty??");
                binding.edtUserGmail.requestFocus();
                return;
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.edtUserGmail.setError("Enter Gmail, Please??");
                binding.edtUserGmail.requestFocus();
                return;
            }
            if(TextUtils.isEmpty(password)){
                binding.edtPassword.setError("Enter Password, Please??");
                binding.edtPassword.requestFocus();
                return;
            }
            if(TextUtils.isEmpty(num)){
                binding.edtUserNum.setError("Number Empty??");
                binding.edtUserNum.requestFocus();
                return;
            }
            if(password.length()<6){
                binding.edtPassword.setError("Too Sort (>6) !!");
                binding.edtPassword.requestFocus();
                return;
            }
            fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    User user=new User(name,email,num);
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(user.getEmail())
                            .setValue(user).addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()){
                                    Toast.makeText(SignInActivity.this,"Đăng ký tài khoản thành công rùi nha :>",Toast.LENGTH_SHORT).show();
                                    finish();

                                }
                                else {
                                    Toast.makeText(SignInActivity.this,"Bị lỗi rồi!! Đăng ký lại dùm cái",Toast.LENGTH_SHORT).show();

                                }
                            });
                }else {
                    Toast.makeText(SignInActivity.this,"Bị lỗi rồi!! Đăng ký lại dùm cái",Toast.LENGTH_SHORT).show();
                }
            });

        });
    }
}
