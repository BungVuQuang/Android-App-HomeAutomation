package com.bungdz.Wizards_App;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;
import com.bungdz.Wizards_App.databinding.ActivityForgotMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotActivity extends AppCompatActivity {
    private ActivityForgotMainBinding binding;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot() ;
        setContentView(view);
        setContentView(R.layout.activity_forgot_main);
        auth=FirebaseAuth.getInstance();
        resetPassword();
        binding.btnReset.setOnClickListener(v -> resetPassword());

        binding.btnBack.setOnClickListener(v -> finish());
    }
    private void resetPassword() {
        String email=binding.edtGmail.getText().toString().trim();

        if(email.isEmpty()){
            binding.edtGmail.setError("Không điền gmail vào tìm lại bằng niềm tin ak??");
            binding.edtGmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.edtGmail.setError("Điền cái gì vậy ???");
            binding.edtGmail.requestFocus();
            return;
        }
        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(ForgotActivity.this,"Check gmail để reset password !",Toast.LENGTH_SHORT).show();
                ForgotActivity.this.finish();
            }else{
                Toast.makeText(ForgotActivity.this,"Thử lại đi !! Bị lỗi gì rồi..!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}