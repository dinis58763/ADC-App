package pt.unl.fct.di.landcommunity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Splashscreen extends AppCompatActivity {

    private static int SPLASH_TIMER=3000;
    SharedPreferences sharedPreferences;

    private static final String SHARED_PREF_NAME = "mypref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        String username = sharedPreferences.getString("username", null);
        String tokenID = sharedPreferences.getString("tokenID", null);
        Long creationDate = sharedPreferences.getLong("creationDate", 0);
        Long expirationDate = sharedPreferences.getLong("expirationDate", 0);
        String role = sharedPreferences.getString("role", null);

        String url = "https://projeto-adc-353721.appspot.com/rest/" + username + "/getUserLogin";

        RequestQueue queue = Volley.newRequestQueue(Splashscreen.this);

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

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Intent intent=new Intent(splashscreen.this,MainActivity.class);
                                Intent intent=new Intent(Splashscreen.this, MainMenuActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        },SPLASH_TIMER);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        // Intent intent=new Intent(splashscreen.this,MainActivity.class);
                        Intent intent=new Intent(Splashscreen.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                },SPLASH_TIMER);
            }

        });
        // Add the request to the RequestQueue.
        queue.add(jsonRequest);
    }
}