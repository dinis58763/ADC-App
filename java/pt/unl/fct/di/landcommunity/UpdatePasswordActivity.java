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

public class UpdatePasswordActivity extends AppCompatActivity {

    EditText currentPassword;
    EditText newPassword;
    EditText reNewPassword;
    SharedPreferences sharedPreferences;

    Button updatePassword;

    CheckBox checkBoxPass;
    CheckBox checkBoxNewPass;

    private static final String SHARED_PREF_NAME = "mypref";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_update_password);

        // loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(LoginViewModel.class);

        // Register variables
        currentPassword = (EditText) findViewById(R.id.et_currentPasswordUpdatePassword);
        newPassword = (EditText) findViewById(R.id.et_newPasswordUpdatePassword);
        reNewPassword = (EditText) findViewById(R.id.et_reNewPasswordUpdatePassword);

        checkBoxPass = (CheckBox) findViewById(R.id.et_checkBoxSeeCurrentPasswordUpdatePassword);
        checkBoxNewPass = (CheckBox) findViewById(R.id.et_checkBoxSeeNewPasswordUpdatePassword);

        updatePassword = (Button) findViewById(R.id.btn_update_password);
        final Button resetUpdatePassword = (Button) findViewById(R.id.btn_resetUpdatePassword);

        checkBoxPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    // Show Password
                    currentPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                else
                    // Hide Password
                    currentPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        checkBoxNewPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    // Show Password
                    newPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                else
                    // Hide Password
                    newPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        resetUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UpdatePasswordActivity.this, SettingsActivity.class)); // Register --> Login
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // go to left
            }
        });
    }

    public void sendMessageUpdatePassword(View view) {
        updatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
                String username = sharedPreferences.getString("username", null);
                String tokenID = sharedPreferences.getString("tokenID", null);
                JSONObject js = new JSONObject();
                try {
                    js.put("oldPwd", currentPassword.getText().toString());
                    js.put("password", newPassword.getText().toString());
                    js.put("confirmPwd", reNewPassword.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String url = "https://projeto-adc-353721.appspot.com/rest/" + username +"/update/password?tokenID=" + tokenID;

                RequestQueue queue = Volley.newRequestQueue(UpdatePasswordActivity.this);

                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT, url, js,
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

                startActivity(new Intent(UpdatePasswordActivity.this, SettingsActivity.class)); // Register --> Login
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // go to right
            }
        });
    }

    public void sendMessageResetUpdatePassword(View view) {
        // Do something in response to button
        Button goToLoginButton = (Button) findViewById(R.id.goToLoginButton);

        goToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // startActivity(new Intent(RegisterActivity.this, LoginActivity.class)); // Register --> Login
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // go to left
    }
}
