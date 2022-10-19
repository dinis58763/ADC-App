package pt.unl.fct.di.landcommunity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MainMenuActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "mypref";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_active_user);

        //loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(LoginViewModel.class);

        // Delete variables
        final Button profile = (Button) findViewById(R.id.btn_profileMenu);
        final Button registInstallment = (Button) findViewById(R.id.btn_openMapMenu);
        final Button seeInstallment = (Button) findViewById(R.id.btn_seeInstallmentMenu);
        final Button chat = (Button) findViewById(R.id.btn_conversationMenu);
        final Button stats = (Button) findViewById(R.id.btn_statsMenu);
        final Button help = (Button) findViewById(R.id.btn_helpMenu);

        profile.setOnClickListener(new View.OnClickListener() { // Delete Account
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainMenuActivity.this, ProfileActivity.class)); // Menu --> ProfileActivity
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // go to right
            }
        });

        registInstallment.setOnClickListener(new View.OnClickListener() { // Delete Account
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainMenuActivity.this, MapsActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // go to right
            }
        });

        seeInstallment.setOnClickListener(new View.OnClickListener() { // Delete Account
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

                String url = "https://projeto-adc-353721.appspot.com/rest/" + username +"/properties?userId=" + username;

                RequestQueue queue = Volley.newRequestQueue(MainMenuActivity.this);

                Log.d("ENTREI", ""+1);

                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, js,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
                                Log.d("ENTREI", ""+2);
                                Log.d("ENTREI", ""+sharedPreferences);

                                // parse JSON
                                JSONObject object = new JSONObject((Map) response);
                                // for(int i = 0; i < response.length(); i++)
                                    // string name = object[i].getString("property_name");
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ENTREI", ""+3);
                        error.getStackTrace();
                    }
                });
                queue.add(jsonRequest);
                // queue.start();

                startActivity(new Intent(MainMenuActivity.this, ListviewActivity.class)); // Menu --> Login
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // go to right
            }
        });

        chat.setOnClickListener(new View.OnClickListener() { // Delete Account
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainMenuActivity.this, ChatActivity.class)); // Menu --> Login
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // go to right
            }
        });

        stats.setOnClickListener(new View.OnClickListener() { // Delete Account
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainMenuActivity.this, StatsActivity.class)); // Menu --> Login
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // go to right
            }
        });

        help.setOnClickListener(new View.OnClickListener() { // Delete Account
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainMenuActivity.this, HelpActivity.class)); // Menu --> Login
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // go to right
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // go to left
    }
}
