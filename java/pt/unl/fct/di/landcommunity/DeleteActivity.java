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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class DeleteActivity extends AppCompatActivity {

    EditText password;
    CheckBox checkBox;
    EditText deleteMessage;
    Button deleteAccountButton;
    SharedPreferences sharedPreferences;

    private static final String SHARED_PREF_NAME = "mypref";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);

        //loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(LoginViewModel.class);

        //password = (EditText) findViewById(R.id.et_passwordDelete);
        deleteMessage = (EditText) findViewById(R.id.et_deleteMessage); // Has to check to delete

        checkBox = (CheckBox) findViewById(R.id.et_checkBoxSeePassword_delete);
        deleteAccountButton = (Button) findViewById(R.id.btn_deleteAccount);
        final Button resetButton = (Button) findViewById(R.id.btn_resetDelete);

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
                startActivity(new Intent(DeleteActivity.this, SettingsActivity.class)); // Register --> Login
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // go to left
            }
        });
    }

    public void sendMessageDelete(View view) {
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

                String userId = sharedPreferences.getString("username", null);
                String tokenID = sharedPreferences.getString("tokenID", null);

                String url = "https://projeto-adc-353721.appspot.com/rest/" + userId + "?userId=" + userId + "&tokenID=" + tokenID;

                if (!deleteMessage.getText().toString().equals("apagar")) {
                    password.setText("");
                    Toast.makeText(getApplicationContext(), "Por favor escreva \"apagar\"", Toast.LENGTH_LONG).show();
                }

                else {

                    RequestQueue queue = Volley.newRequestQueue(DeleteActivity.this);

                    JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    startActivity(new Intent(DeleteActivity.this, RegisterActivity.class));
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // go to right
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try {
                                password.setText("");
                                String responseBody = new String(error.networkResponse.data, "utf-8");
                                Toast.makeText(getApplicationContext(), responseBody, Toast.LENGTH_LONG).show();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    queue.add(jsonRequest);
                }
            }

        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // go to left
    }

    public void sendMessageResetDelete(View view) {
        // Do something in response to button
        Button resetButton = (Button) findViewById(R.id.btn_resetDelete);
        // DeleteAccountGBO ----> SettingsGBO
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DeleteActivity.this, SettingsActivity.class)); // Register --> Login
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // go to left
            }
        });
    }
}

