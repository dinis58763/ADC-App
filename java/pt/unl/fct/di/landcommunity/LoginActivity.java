package pt.unl.fct.di.landcommunity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    EditText usernameLogin;
    EditText passwordLogin;
    Button loginButton;
    GoogleSignInClient mGoogleSignInClient;
    SharedPreferences sharedPreferences;

    private static final int RC_SIGN_IN = 100;
    private static final String SHARED_PREF_NAME = "mypref";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        // Login variables
        usernameLogin = (EditText) findViewById(R.id.et_nameLogin);
        passwordLogin = (EditText) findViewById(R.id.et_passwordLogin);
        loginButton = (Button) findViewById(R.id.btn_login);
        final Button goToRegisterButton = (Button) findViewById(R.id.goToRegisterButton);

        Log.i("EMAIL LOGIN ---> ", "LOGIN ON CREATE --->  " + usernameLogin);
        Log.i("PASSWORD LOGIN ---> ", "LOGIN ON CREATE --->  " + passwordLogin);

        loginButton.setEnabled(false);

        /** GOOGLE ***/
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        // updateUI(account);

        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        // USERNAME
        usernameLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
                // checkValidation();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isUsername(usernameLogin))
                    usernameLogin.setError("Por favor insira um Nome de Utilizador válido");
                else
                    checkValidation();
            }
        });

        // PASSWORD
        passwordLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
                // checkValidation();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (passwordLogin.length() < 3)
                    passwordLogin.setError("Por favor insira uma Palavra-Passe válida");
                else if (passwordLogin.length() > 15)
                    passwordLogin.setError("Por favor insira uma Palavra-Passe válida");
                else if (!isValidPassword(passwordLogin))
                    passwordLogin.setError("Por favor insira uma Palavra-Passe válida");
                else
                    checkValidation();
            }
        });

        passwordLogin.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameLogin.getText().toString(),
                            usernameLogin.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginViewModel.login(usernameLogin.getText().toString(),
                        passwordLogin.getText().toString());

                JSONObject js = new JSONObject();
                try {
                    js.put("username", usernameLogin.getText().toString());
                    js.put("password", passwordLogin.getText().toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String url = "https://projeto-adc-353721.appspot.com/rest/login";

                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, js,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
                                try {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("username", response.getString("username"));
                                    editor.putString("tokenID", response.getString("tokenID"));
                                    editor.putLong("creationDate", response.getLong("creationDate"));
                                    editor.putLong("expirationDate", response.getLong("expirationDate"));
                                    editor.putString("role", response.getString("role"));
                                    editor.commit();
                                }
                                catch (JSONException e) {}
                                Toast.makeText(getApplicationContext(), "Welcome!", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(LoginActivity.this, MainMenuActivity.class)); // Register --> Menu
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // go to right
                                // Display the first 500 characters of the response string.
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            usernameLogin.setText("");
                            passwordLogin.setText("");
                            Toast.makeText(getApplicationContext(), responseBody, Toast.LENGTH_LONG).show();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                });
                queue.add(jsonRequest);

                /**
                 * Checks if the user is Active or Inactive --> REST Request
                 */

            }
        });

        goToRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)); // Login --> Register
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // go to left
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();

                Toast.makeText(this, "User email: " + personEmail, Toast.LENGTH_SHORT).show();
            }
            // Signed in successfully, show authenticated UI.
            startActivity(new Intent(LoginActivity.this, MainMenuActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // go to right
            // updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            // updateUI(null);
            Log.d("Message", e.toString());
        }
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    public void sendMessageToRegister(View view) {
        // Do something in response to button
        Button goToRegisterButton = (Button) findViewById(R.id.goToRegisterButton);

        goToRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)); // Login --> Register
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // go to left
            }
        });
    }

    boolean isUsername(EditText text) {
        String username = text.getText().toString();
        return (!TextUtils.isEmpty(username.trim()) && username.length() <= 20 && !username.contains(" "));
    }

    boolean isValidPassword(EditText text) {
        String password = text.getText().toString();
        /**
        String regex = "^" +
                "(?=.*[!@#$%^&+=])" +     // at least 1 special character
                "(?=\\S+$)" +            // no white spaces
                ".{5,12}" +              // at least 5 characters
                "$"; // "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[-+_!@#$%^&*., ?]).+$"; **/
        return (!TextUtils.isEmpty(password.trim())); // && password.length() >= 5 && password.length() <= 12 && Pattern.compile(regex).matcher(password).matches());
    }

    private void checkValidation() {
        if (checkDataEntered())
            loginButton.setEnabled(true);
        else
            loginButton.setEnabled(false);
    }

    boolean checkDataEntered() {

        if(!isUsername(usernameLogin))
            return false;

        if(!isValidPassword(passwordLogin))
            return false;

        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // go to left
    }
}
