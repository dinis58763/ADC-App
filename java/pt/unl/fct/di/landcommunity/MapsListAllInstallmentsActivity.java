package pt.unl.fct.di.landcommunity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MapsListAllInstallmentsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnPolygonClickListener {

    private Context mContext;
    private GoogleMap mMap;

    Polygon encodedPolygon = null;

    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "mypref";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_list_all_installments);

        // checkBox.setTextColor(Color.rgb(255,255,255));

        mContext = MapsListAllInstallmentsActivity.this;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.list_all_installments_map);

        mapFragment.getMapAsync(this);

        /** REST **/
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        System.out.println(sharedPreferences.getAll());

        String username = sharedPreferences.getString("username", null);
        String tokenID = sharedPreferences.getString("tokenID", null);
        Long creationDate = sharedPreferences.getLong("creationDate", 0);
        Long expirationDate = sharedPreferences.getLong("expirationDate", 0);
        String role = sharedPreferences.getString("role", null);

        String url = "https://projeto-adc-353721.appspot.com/rest/properties";

        RequestQueue queue = Volley.newRequestQueue(MapsListAllInstallmentsActivity.this);

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

        CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.POST, url, js,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        System.out.println("\n\n\n "+ response);
                        for(int i = 0; i < response.length(); i ++) {
                            System.out.println();
                            String encodeMessage = response.optJSONObject(i).optJSONObject("properties").optJSONObject("property_encode").optString("value");
                            String userId = response.optJSONObject(i).optJSONObject("properties").optJSONObject("user_id").optString("value");
                            String state = response.optJSONObject(i).optJSONObject("properties").optJSONObject("property_state").optString("value");
                            String nameProperty = response.optJSONObject(i).optJSONObject("properties").optJSONObject("property_name").optString("value");

                            List<LatLng> encode = PolyUtil.decode(encodeMessage);

                            if(!encode.isEmpty() && userId.equals(username)) {

                                    // Set Polygon Stroke Color
                                    if (state.equals("true")) {
                                        PolygonOptions polygonOptions = new PolygonOptions().addAll(encode).clickable(true);
                                        encodedPolygon = mMap.addPolygon(polygonOptions);
                                        encodedPolygon.setTag(nameProperty);

                                        encodedPolygon.setStrokeColor(Color.rgb(0, 153, 76)); // solid green
                                        encodedPolygon.setFillColor(Color.rgb(102, 255, 178)); // green
                                    }

                                    else if (state.equals("false")){
                                        PolygonOptions polygonOptions = new PolygonOptions().addAll(encode).clickable(true);
                                        encodedPolygon = mMap.addPolygon(polygonOptions);
                                        encodedPolygon.setTag(nameProperty);

                                        encodedPolygon.setStrokeColor(Color.rgb(0, 76, 153)); // blue
                                        encodedPolygon.setFillColor(Color.rgb(51, 153, 255)); // light blue
                                    }
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getStackTrace();
            }
        });
        queue.add(jsonRequest);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID); // mMap.setMapType(mMap.MAP_TYPE_HYBRID); // Here is where you set the map type

        /** ADDED **/
        mMap.setOnPolygonClickListener(this);

        /**
         * Camera start in Macao
         */
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.557244, -7.995710), 15));

        // Sets the map type to be "hybrid"
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    @Override
    public void onPolygonClick(@NonNull Polygon polygon) {
        // polygon.setTag("User/Entity");
        Toast.makeText(this, "Nome da Parcela: " + polygon.getTag().toString(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // go to left
    }
}