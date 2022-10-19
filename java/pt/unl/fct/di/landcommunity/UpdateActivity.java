package pt.unl.fct.di.landcommunity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class UpdateActivity extends AppCompatActivity {

    EditText nameRegister;
    TextView usernameRegister;
    TextView emailRegister;
    EditText phoneRegister;
    EditText mobileRegister;
    EditText cityRegister;
    EditText addressRegister;
    EditText postalCodeRegister;
    EditText nifRegister;
    Button registerButton;
    Button resetUpdateInfo;
    ImageView imageView;
    Switch switchButton;
    SharedPreferences sharedPreferences;

    private static final String SHARED_PREF_NAME = "mypref";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_update_info);

        // loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(LoginViewModel.class);

        // Register variables
        usernameRegister = (TextView) findViewById(R.id.et_usernameUpdate);
        emailRegister = (TextView) findViewById(R.id.et_emailUpdate);
        nameRegister = (EditText) findViewById(R.id.et_nameUpdate);
        phoneRegister = (EditText) findViewById(R.id.et_phoneUpdate);
        mobileRegister = (EditText) findViewById(R.id.et_mobilePhoneUpdate);
        cityRegister = (EditText) findViewById(R.id.et_cityUpdate);
        addressRegister = (EditText) findViewById(R.id.et_addressUpdate);
        postalCodeRegister = (EditText) findViewById(R.id.et_postalCodeUpdate);
        nifRegister = (EditText) findViewById(R.id.et_nifUpdate);

        imageView = (ImageView) findViewById(R.id.imageviewprofilepicture);

        registerButton = (Button) findViewById(R.id.btn_Update);
        switchButton = (Switch) findViewById(R.id.et_switchUpdate);
        resetUpdateInfo = (Button) findViewById(R.id.btn_resetUpdateInfo);

        Bundle extras = getIntent().getExtras();

        usernameRegister.setText(extras.getString("username"));
        emailRegister.setText(extras.getString("email"));
        nameRegister.setText(extras.getString("name"));
        phoneRegister.setText(extras.getString("phone"));
        mobileRegister.setText(extras.getString("mobile"));
        cityRegister.setText(extras.getString("city"));
        addressRegister.setText(extras.getString("address"));
        postalCodeRegister.setText(extras.getString("postalCode"));
        nifRegister.setText(extras.getString("nif"));
        String img = extras.getString("img");
        System.out.println(img);

        if (img.contains("/gcs"))
           new DownloadImageTask(imageView).execute("https://projeto-adc-353721.appspot.com" + img);
        else {
            String[] filename = img.split("/");
            new DownloadImageTask(imageView).execute("https://projeto-adc-353721.appspot.com/gcs/projeto-adc-353721.appspot.com/" + filename[filename.length-1]);
        }

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    switchButton.setText("Private Profile");
                    switchButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_privacy, 0, 0, 0);
                }
                else {
                    // When is nothing selected, means we have a public profile.
                    switchButton.setText("Public Profile");
                    switchButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_user, 0, 0, 0);
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
                System.out.println(sharedPreferences.getAll());
                String tokenID = sharedPreferences.getString("tokenID", null);

                updateRequest(tokenID);
            }
        });

        resetUpdateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UpdateActivity.this, ProfileActivity.class)); // Register --> Login
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // go to left
            }
        });
    }

    public void sendMessageResetUpdateInfo(View view) {
        // Do something in response to button
        Button goToLoginButton = (Button) findViewById(R.id.goToLoginButton);

        goToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // startActivity(new Intent(RegisterActivity.this, LoginActivity.class)); // Register --> Login
            }
        });
    }

    private void updateRequest(String tokenID) {

        String url = "https://projeto-adc-353721.appspot.com/rest/" +  usernameRegister.getText().toString() + "/update/user?tokenID="+ tokenID;

        RequestQueue queue = Volley.newRequestQueue(UpdateActivity.this);

        StringRequest request = new StringRequest(StringRequest.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(UpdateActivity.this, ProfileActivity.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // go to right
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
                        js.put("username", usernameRegister.getText().toString());
                        js.put("email", emailRegister.getText().toString());
                        js.put("name", nameRegister.getText().toString());
                        js.put("profilePrivacy", false);
                        js.put("phoneNr", phoneRegister.getText().toString());
                        js.put("mobileNr", mobileRegister.getText().toString());
                        js.put("mainAddr", addressRegister.getText().toString());
                        js.put("city", cityRegister.getText().toString());
                        js.put("cp", postalCodeRegister.getText().toString());
                        js.put("nif", nifRegister.getText().toString());
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // go to left
    }
}
