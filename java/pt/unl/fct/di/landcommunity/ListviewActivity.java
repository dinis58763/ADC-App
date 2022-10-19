package pt.unl.fct.di.landcommunity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ListviewActivity extends AppCompatActivity {

    private ListView mListview;

    private ArrayList<String> mArrData;
    // private ArrayList<String> mIdData;

    private SchoolAdapter mAdapter;
    SharedPreferences sharedPreferences;

    Button openMap;
    Button goBack;

    private static final String SHARED_PREF_NAME = "mypref";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        mListview = (ListView) findViewById(R.id.listSchool);
        openMap = findViewById(R.id.btn_listViewLandInstallmentOpenMaps);
        goBack = findViewById(R.id.btn_resetlistView);

        // Set some data to array list
        mArrData = new ArrayList<String>();
        // mIdData = new ArrayList<String>();

        // startActivity(new Intent(LogoutActivity.this, SettingsActivity.class)); // Register --> Login

        /** REST Request to get the array of properties' names of the logged in user **/

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        String username = sharedPreferences.getString("username", null);
        String tokenID = sharedPreferences.getString("tokenID", null);
        Long creationDate = sharedPreferences.getLong("creationDate", 0);
        Long expirationDate = sharedPreferences.getLong("expirationDate", 0);
        String role = sharedPreferences.getString("role", null);

        openMap.setOnClickListener(new View.OnClickListener() { // Delete Account
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListviewActivity.this, MapsListAllInstallmentsActivity.class)); // Settings --> Update Pass
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // go to right
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() { // Delete Account
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListviewActivity.this, MainMenuActivity.class)); // Settings --> Update Pass
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // go to left
            }
        });

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

        String url = "https://projeto-adc-353721.appspot.com/rest/"+ username +"/properties?userId=" + username;

        RequestQueue queue = Volley.newRequestQueue(ListviewActivity.this);

        CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.POST, url, js,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for(int i = 0; i < response.length(); i++) {
                            mArrData.add(i ,response.optJSONObject(i).optJSONObject("properties").optJSONObject("property_name").optString("value"));
                            // mIdData.add(i, response.optJSONObject(i).optJSONObject("key").optJSONArray("path").optJSONObject(0).optString("id"));
                        }

                        // Collections.sort(mArrData, String.CASE_INSENSITIVE_ORDER);
                        // Initialize adapter and set adapter to list view
                        mAdapter = new SchoolAdapter(ListviewActivity.this, mArrData, response);
                        mListview.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();

                        // Display the first 500 characters of the response string.
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    Toast.makeText(getApplicationContext(), responseBody, Toast.LENGTH_LONG).show();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }});

        queue.add(jsonRequest);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // go to left
    }
}
