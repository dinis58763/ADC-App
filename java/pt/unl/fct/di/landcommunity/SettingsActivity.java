package pt.unl.fct.di.landcommunity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    private static final String SHARED_PREF_NAME = "mypref";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_user);

        //loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(LoginViewModel.class);

        // Delete variables

        final Button updatePassword = (Button) findViewById(R.id.btn_update_password);
        final Button logout = (Button) findViewById(R.id.btn_logout);
        final Button delete = (Button) findViewById(R.id.btn_delete_account);
        final Button backToMenu = (Button) findViewById(R.id.btn_goBackToMenu);

        updatePassword.setOnClickListener(new View.OnClickListener() { // Delete Account
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, UpdatePasswordActivity.class)); // Settings --> Update Pass
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // go to right
            }
        });

        logout.setOnClickListener(new View.OnClickListener() { // Reset Button
            // DeleteAccountGBO ----> SettingsGBO
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

                RequestQueue queue = Volley.newRequestQueue(SettingsActivity.this);

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

                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // go to right
            }
        });

        delete.setOnClickListener(new View.OnClickListener() { // Reset Button
            // DeleteAccountGBO ----> SettingsGBO
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, DeleteActivity.class)); // Settings --> Delete
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // go to right
            }
        });

        backToMenu.setOnClickListener(new View.OnClickListener() { // Reset Button
            // DeleteAccountGBO ----> SettingsGBO
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, ProfileActivity.class)); // Settings --> Menu
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // go to left
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // go to left
    }
}
