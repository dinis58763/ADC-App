package pt.unl.fct.di.landcommunity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LogoutActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    CheckBox checkBox;
    Button logoutButton;
    SharedPreferences sharedPreferences;

    private static final String SHARED_PREF_NAME = "mypref";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        //loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(LoginViewModel.class);

        // Delete variables
        username = (EditText) findViewById(R.id.et_usernameLogout);
        password = (EditText) findViewById(R.id.et_passwordLogout);

        checkBox = (CheckBox) findViewById(R.id.et_checkBoxSeePassword_logout);
        logoutButton = (Button) findViewById(R.id.btn_logout);
        final Button resetButton = (Button) findViewById(R.id.btn_resetLogout);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    // Show Password
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                else
                    // Hide Password
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() { // Reset Button
            // DeleteAccountGBO ----> SettingsGBO
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogoutActivity.this, SettingsActivity.class)); // Logout --> Settings
            }
        });
    }

   public void sendMessageLogout(View view) {
                logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
                    String username = sharedPreferences.getString("username", null);
                    String tokenID = sharedPreferences.getString("tokenID", null);
                    long creationDate = sharedPreferences.getLong("creationDate", -1);
                    long expirationDate = sharedPreferences.getLong("expirationDate", -1);
                    String role = sharedPreferences.getString("role", null);

                    JSONObject js = new JSONObject();
                    try {
                        js.put("username", username);
                        js.put("tokenID", tokenID);
                        js.put("creationDate", creationDate);
                        js.put("expirationDate", expirationDate);
                        js.put("role", role);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String url = "https://projeto-adc-353721.appspot.com/rest/" + username +"/logout";

                    RequestQueue queue = Volley.newRequestQueue(LogoutActivity.this);

                    JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, js,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.remove("username");
                                    editor.remove("tokenID");
                                    editor.remove("creationDate");
                                    editor.remove("expirationDate");
                                    editor.remove("role");
                                    editor.commit();
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.getStackTrace();
                        }
                    });
                    queue.add(jsonRequest);

                    startActivity(new Intent(LogoutActivity.this, LoginActivity.class));
                }
            });
        }

    public void sendMessageResetLogout(View view) {
        // Do something in response to button
        Button resetButton = (Button) findViewById(R.id.btn_resetDelete);
        // DeleteAccountGBO ----> SettingsGBO
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LogoutActivity.this, SettingsActivity.class)); // Register --> Login
            }
        });
    }
}
