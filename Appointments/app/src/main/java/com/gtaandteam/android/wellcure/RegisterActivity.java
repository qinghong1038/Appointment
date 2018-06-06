package com.gtaandteam.android.wellcure;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    Button LoginButton;
    EditText editTextUserName;
    EditText editTextPass;
    private ProgressDialog progress;
    private FirebaseAuth firebaseAuth;
    Button registerButton;
    TextView textViewLog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null)
        {
            //user already logged in. go directly to doctor activity
            finish();
            startActivity(new Intent(getApplicationContext(),DoctorsActivity.class));
        }
        LoginButton = findViewById(R.id.login_button2);
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(RegisterActivity.this,MainActivity.class));

            }
        });
        editTextUserName=(EditText)findViewById(R.id.username_editText2);
        editTextPass=(EditText)findViewById(R.id.password_editText2);
        progress=new ProgressDialog(this);
        registerButton=findViewById(R.id.reg_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
        textViewLog = findViewById(R.id.textViewReg);
        textViewLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(RegisterActivity.this,MainActivity.class));
            }
        });

    }
    private void registerUser()
    {
        String email=editTextUserName.getText().toString().trim();
        String pass=editTextPass.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            // is empty
            Toast.makeText(this,"Please Enter Email Id",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(pass)){
            // is empty
            Toast.makeText(this,"Please Enter Password",Toast.LENGTH_SHORT).show();
            return;

        }

        // if validations are ok we show a progress bar
        progress.setMessage("Registering User..");
        progress.show();
        firebaseAuth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progress.dismiss();
                        if(task.isSuccessful())
                        {
                            //user successfully registered
                            //we start profile activity here
                            Toast.makeText(RegisterActivity.this,"Registered Successfully",Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));


                        }
                        else
                        {
                            Toast.makeText(RegisterActivity.this,"Couldnt Register. Please Try Again..",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
