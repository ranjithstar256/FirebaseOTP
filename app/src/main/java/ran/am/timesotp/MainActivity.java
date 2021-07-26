package ran.am.timesotp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import ran.am.timesotp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding am;
    FirebaseAuth mAuth;
    String codeSent;
    PhoneAuthProvider.ForceResendingToken tokenone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        am = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        am.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("321abcd", "sendotp: " + "+91" + am.editTextPhone.getText().toString());
                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(mAuth)
                                .setPhoneNumber("+91" + am.editTextPhone.getText().toString())       // Phone number to verify
                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                .setActivity(MainActivity.this)                 // Activity (for callback binding)
                                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                .build();
            //    PhoneAuthProvider.verifyPhoneNumber(options);      // OnVerificationStateChangedCallbacks


                Thread t = new Thread() {
                    public void run() {
                        for (int i = 30; i > 0; i--) {
                            try {
                                final int a = i;
                                int finalI = i;
                                runOnUiThread(() -> {
                                    am.textView2.setText("Please Wait "+ finalI +" seconds to Resend OTP");
                                    if (finalI==2){
                                        am.button3.setVisibility(View.VISIBLE);
                                    }
                                });
                                sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                t.start();
            }
        });


        am.button3.setOnClickListener(view -> {
            resendVerificationCode("+91" + am.editTextPhone.getText().toString(),tokenone);
        });
    }

    public void sgn(View view) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, am.editTextTextPersonName.getText().toString());
        signInWithPhoneAuthCredential(credential);
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSent = s;
            tokenone=forceResendingToken;
            Log.d("132abcd", "sendotp: " + s + "\n\n\n" + codeSent);
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Toast.makeText(MainActivity.this, "Completed!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
            Log.d("321TAG", "onVerificationFailed: " + e.getMessage().toString());
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            am.textView.setText("Success!!");
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(), "Incorrect Verification code", Toast.LENGTH_SHORT).show();
                                am.textView.setText("RETRY Please !");
                            }
                        }
                    }
                });
    }


    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
}