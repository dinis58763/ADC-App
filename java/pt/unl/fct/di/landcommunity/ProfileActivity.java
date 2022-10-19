package pt.unl.fct.di.landcommunity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ProfileActivity extends AppCompatActivity {

    TextView userId;
    TextView name;
    TextView numberVerified;
    TextView numberInVerification;

    ImageView imageView;

    Button settings;
    Button editProfile;
    Button goBack;
    SharedPreferences sharedPreferences;
    Bundle extras;

    private ImageLoader imageLoader;

    private static final String SHARED_PREF_NAME = "mypref";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userId = findViewById(R.id.tv_usernameProfile);
        name = findViewById(R.id.et_nameProfile);
        numberVerified = findViewById(R.id.et_numberVerifiedInstallments);
        numberInVerification = findViewById(R.id.et_numberInVerificationInstallments);

        imageView = (ImageView) findViewById(R.id.imageviewprofilepicture);

        settings = findViewById(R.id.btn_settingsProfile);
        editProfile = findViewById(R.id.btn_editProfile);
        goBack = findViewById(R.id.goBackMenu);

        /** Token information **/

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        extras = new Bundle();

        String username = sharedPreferences.getString("username", null);
        String tokenID = sharedPreferences.getString("tokenID", null);
        Long creationDate = sharedPreferences.getLong("creationDate", 0);
        Long expirationDate = sharedPreferences.getLong("expirationDate", 0);
        String role = sharedPreferences.getString("role", null);

        /** REST Request to get the username, name and profile image of the logged in user +
         * Gets the number of verified, in verif and not verif installments of the logged in user --> REST
         * ***/

        profilePhotoRequest(username, tokenID, creationDate, expirationDate, role);

        userInfoRequest(username, tokenID, creationDate, expirationDate, role);

        statsRequest(username, tokenID, creationDate, expirationDate, role);

        /** Buttons */

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, SettingsActivity.class)); // Register --> Login
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // go to right
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, UpdateActivity.class).putExtras(extras)); // Register --> Login
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // go to right
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, MainMenuActivity.class)); // Register --> Login
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // go to left
            }
        });
    }

    private void profilePhotoRequest(String username,String tokenID,Long creationDate,Long expirationDate,String role) {

        String url = "https://projeto-adc-353721.appspot.com/rest/" + username;

        RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);

        StringRequest request = new StringRequest(StringRequest.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String[] array = response.split("/");
                        String[] filename = array[array.length-1].split("\"");
                        String url2 =  "https://projeto-adc-353721.appspot.com/gcs/projeto-adc-353721.appspot.com/"+ filename[0];
                        new DownloadImageTask(imageView).execute(url2);
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
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
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
                    /* fill your json here */
                    return js.toString().getBytes("utf-8");
                } catch (Exception e) { }
                return null;
            }
        };
        queue.add(request);
    }

    private void userInfoRequest(String username,String tokenID,Long creationDate,Long expirationDate,String role) {

        String url = "https://projeto-adc-353721.appspot.com/rest/" + username + "/getUserLogin";

        RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);

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
                        System.out.println(response);
                        name.setText(response.optJSONObject("properties").optJSONObject("user_name").optString("value"));
                        extras.putString("username", username);
                        extras.putString("email", response.optJSONObject("properties").optJSONObject("user_email").optString("value"));
                        extras.putString("name", response.optJSONObject("properties").optJSONObject("user_name").optString("value"));
                        extras.putString("phone", response.optJSONObject("properties").optJSONObject("user_phone").optString("value"));
                        extras.putString("mobile", response.optJSONObject("properties").optJSONObject("user_mobile").optString("value"));
                        extras.putString("city", response.optJSONObject("properties").optJSONObject("user_city").optString("value"));
                        extras.putString("address", response.optJSONObject("properties").optJSONObject("user_addr").optString("value"));
                        extras.putString("postalCode", response.optJSONObject("properties").optJSONObject("user_cp").optString("value"));
                        extras.putString("nif", response.optJSONObject("properties").optJSONObject("user_nif").optString("value"));
                        extras.putString("img", response.optJSONObject("properties").optJSONObject("user_file").optString("value"));
                        userId.setText(username);
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
            }

        });
        //Add the request to the RequestQueue.
        queue.add(jsonRequest);
    }

    private void statsRequest(String username,String tokenID,Long creationDate,Long expirationDate,String role) {
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

        RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);

        CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.POST, url, js,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        System.out.println(response);
                        int verified = 0;
                        int inVerification = 0;

                        for(int i = 0; i < response.length(); i++) {
                            if (response.optJSONObject(i).optJSONObject("properties").optJSONObject("property_state").
                                    optString("value").equals("true"))
                                verified++;
                            else
                                inVerification++;
                            // mIdData.add(i, response.optJSONObject(i).optJSONObject("key").optJSONArray("path").optJSONObject(0).optString("id"));
                        }
                        numberVerified.setText(String.valueOf(verified));
                        numberInVerification.setText(String.valueOf(inVerification));

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

