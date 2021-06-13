package com.example.instagram.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.databinding.ActivitySignUpBinding;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;

    public static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        super.onCreate(savedInstanceState);
        setContentView(view);

        binding.ivLogo.setImageResource(R.mipmap.logo_foreground);

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.etSUUsername.getText().toString();
                String password = binding.etSUPassword.getText().toString();
                String confirmPassword = binding.etSUConfirmPassword.getText().toString();
                if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "One or more of your fields are empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignUpActivity.this, "Your password do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                ParseUser user = new ParseUser();
                user.setUsername(username);
                user.setPassword(password);

                user.signUpInBackground(e -> {
                    if (e != null) {
                        Log.i(TAG, "Sign up successful");
                        Toast.makeText(SignUpActivity.this, "Sign up success!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Sign up failed: " + e);
                    }
                });

                finish();
            }
        });

    }
}