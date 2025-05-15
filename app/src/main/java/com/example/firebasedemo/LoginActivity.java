package com.example.firebasedemo;

import static com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL;

import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.credentials.ClearCredentialStateRequest;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.ClearCredentialException;
import androidx.credentials.exceptions.GetCredentialException;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "GoogleActivity";
    private FirebaseAuth mAuth;
    private CredentialManager credentialManager;

    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Objects.requireNonNull(getSupportActionBar()).hide();
        email = findViewById(R.id.emailfield);
        password = findViewById(R.id.passwordfield);
        Button login = findViewById(R.id.loginbutton);
        ImageView google = findViewById(R.id.google);

        mAuth = FirebaseAuth.getInstance();
        credentialManager = CredentialManager.create(getBaseContext());

        login.setOnClickListener(v -> {
            String mail = email.getText().toString();
            String pass = password.getText().toString();

            if (mail.isBlank() || pass.isBlank()) {
                Toast.makeText(LoginActivity.this, "Email and Password cannot be empty.", Toast.LENGTH_SHORT).show();
            } else {
                signIn(mail, pass);
            }
        });

        google.setOnClickListener(v -> {
            launchCredentialManager();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }
    }

    private void launchCredentialManager() {
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder().setFilterByAuthorizedAccounts(false).setServerClientId(getString(R.string.default_web_client_id)).build();

        GetCredentialRequest request = new GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build();

        credentialManager.getCredentialAsync(getBaseContext(), request, new CancellationSignal(), Executors.newSingleThreadExecutor(), new CredentialManagerCallback<>() {
            @Override
            public void onResult(GetCredentialResponse result) {
                handleSignIn(result.getCredential());
            }

            @Override
            public void onError(@NonNull GetCredentialException e) {
                updateUI(null);
            }
        });
    }

    private void handleSignIn(Credential credential) {
        if (credential instanceof CustomCredential customCredential && credential.getType().equals(TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
            Bundle credentialData = customCredential.getData();
            GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credentialData);

            firebaseAuthWithGoogle(googleIdTokenCredential.getIdToken());
        } else {
            Toast.makeText(LoginActivity.this, "Credential is not of type Google ID!", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                updateUI(user);
            } else {
                updateUI(null);
            }
        });
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    updateUI(null);
                }
            }
        });
    }

    private void signOut() {
        mAuth.signOut();

        ClearCredentialStateRequest clearRequest = new ClearCredentialStateRequest();
        credentialManager.clearCredentialStateAsync(clearRequest, new CancellationSignal(), Executors.newSingleThreadExecutor(), new CredentialManagerCallback<>() {
            @Override
            public void onResult(@NonNull Void result) {
                Toast.makeText(LoginActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull ClearCredentialException e) {
                updateUI(null);
            }
        });
    }

    private void updateUI(final FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(LoginActivity.this, ContactActivity.class);
            startActivity(intent);
            finish();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        signOut();
                    } catch (Exception ignored) {
                    }
                }
            });
        }
    }
}