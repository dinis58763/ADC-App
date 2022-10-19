package pt.unl.fct.di.landcommunity;

import android.Manifest.permission;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnPolylineClickListener, GoogleMap.OnPolygonClickListener, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, ActivityCompat.OnRequestPermissionsResultCallback  {

    private Context mContext;
    private GoogleMap mMap;
    SearchView searchView;
    Button btnDraw;
    Button btnClear;
    Button btnRegisterLandInstallment;
    Button btnGeoRegisterLandInstallment;
    Button goBack;

    Polygon polygon = null;
    Polygon encodedPolygon = null;

    List<LatLng> latLngList = new ArrayList<>();
    List<LatLng> locationList = new ArrayList<>();

    List<Marker> markerList = new ArrayList<>();
    List<Marker> locationMarkerList = new ArrayList<>();

    // Encoded Polygon
    private String encodeMessage = "";

    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);

    // Create a stroke pattern of a gap followed by a dot.
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied = false;
    LatLng userLatLng;

    private double area;
    int red = 255, green = 255, blue = 255;

    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "mypref";

    /** LOCATION **/
    LocationManager locationManager;
    // LocationListener locationListener;

    private static final int LOCATION_MIN_UPDATE_TIME = 5; // 5 segundos
    private static final int LOCATION_MIN_UPDATE_DISTANCE = 0; //1000; metros
    private MapView mapView;
    private Location location = null;

    // public static boolean isFromSetting=false;

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // drawMarker(location, getText(R.string.i_am_here).toString());
            // locationManager.removeUpdates(locationListener);
            btnGeoRegisterLandInstallment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // btnGeoRegisterLandInstallment.setBackgroundColor(getResources().getColor(R.color.transparent));
                    drawMarker(location, getText(R.string.i_am_here).toString());
                }
            });
            // locationManager.removeUpdates(locationListener);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // checkBox = (CheckBox) findViewById(R.id.check_box);
        btnDraw = (Button) findViewById(R.id.btn_draw);
        btnClear = (Button) findViewById(R.id.btn_clear);
        btnRegisterLandInstallment = (Button) findViewById(R.id.btn_registerLandInstallmentMaps);
        btnGeoRegisterLandInstallment = (Button) findViewById(R.id.btn_geoLocationRegisterLandInstallmentMaps);
        goBack = findViewById(R.id.btn_goBackRegisterLandInstallmentMaps);

        // checkBox.setTextColor(Color.rgb(255,255,255));

        mContext = MapsActivity.this;
        searchView = findViewById(R.id.idSearchView);
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.white));
        searchEditText.setHintTextColor(getResources().getColor(R.color.white));
        btnDraw.setTextColor(Color.rgb(255, 255, 255));
        btnClear.setTextColor(Color.rgb(255, 255, 255));

        List<Geocoder> geocoders = new ArrayList<>(); // list which keep the geocoders

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mContext = this;

        getCurrentLocation();

        isLocationEnabled();

        /**
         * Rest get all properties
         */

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        System.out.println(sharedPreferences.getAll());

        String username = sharedPreferences.getString("username", null);
        String tokenID = sharedPreferences.getString("tokenID", null);
        Long creationDate = sharedPreferences.getLong("creationDate", 0);
        Long expirationDate = sharedPreferences.getLong("expirationDate", 0);
        String role = sharedPreferences.getString("role", null);

        String url = "https://projeto-adc-353721.appspot.com/rest/properties";

        RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);

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

                            if(!encode.isEmpty()) {
                                if(userId.equals(username)) {

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
                                else if(!userId.equals(username) && state.equals("true")) {

                                    PolygonOptions polygonOptions = new PolygonOptions().addAll(encode).clickable(true);
                                    encodedPolygon = mMap.addPolygon(polygonOptions);
                                    encodedPolygon.setTag(nameProperty);

                                    encodedPolygon.setStrokeColor(Color.rgb(255, 255, 255));
                                    encodedPolygon.setFillColor(Color.argb(255, 0, 255, 0));

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

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MapsActivity.this, MainMenuActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // go to left
            }
        });

        btnRegisterLandInstallment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // btnGeoRegisterLandInstallment.setBackgroundColor(getResources().getColor(R.color.transparent));

                // Draw Polyline on Map
                if (polygon != null)
                    polygon.remove();

                // Create PolygonOptions
                if (!latLngList.isEmpty() && locationList.isEmpty()) {
                    PolygonOptions polygonOptions = new PolygonOptions().addAll(latLngList).clickable(true);
                    polygon = mMap.addPolygon(polygonOptions);
                    polygon.setTag("A");

                    encodeMessage = PolyUtil.encode(latLngList);

                    calculateArea();

                    // Set Polygon Stroke Color
                    polygon.setStrokeColor(Color.rgb(red, green, blue));
                    polygon.setFillColor(Color.argb(255, 255, 0, 0));

                    if (encodeMessage == "")
                        Snackbar.make(view, "Por favor, desenhe o polígono primeiro!", Snackbar.LENGTH_LONG).show();

                    else {
                        LatLng point = latLngList.get(0);
                        locationRequest(point.latitude, point.longitude);
                    }
                }

                else if (latLngList.isEmpty() && !locationList.isEmpty()) {
                    PolygonOptions polygonOptions = new PolygonOptions().addAll(locationList).clickable(true);
                    polygon = mMap.addPolygon(polygonOptions);
                    polygon.setTag("A");

                    encodeMessage = PolyUtil.encode(locationList);
                    calculateArea();

                    // Set Polygon Stroke Color
                    polygon.setStrokeColor(Color.rgb(red, green, blue));
                    polygon.setFillColor(Color.argb(255, 255, 0, 0));

                    if (encodeMessage == "")
                        Snackbar.make(view, "Por favor, desenhe o polígono primeiro!", Snackbar.LENGTH_LONG).show();

                    else {
                        LatLng point = locationList.get(0);
                        locationRequest(point.latitude, point.longitude);
                    }
                }

                else {
                    Toast.makeText(getApplicationContext(), "Por favor, escolha fazer o polígono através do desenho das " +
                            "parcelas ou através da localização!", Toast.LENGTH_LONG).show();

                    for (Marker marker : markerList)
                        marker.remove();

                    for (Marker marker : locationMarkerList)
                        marker.remove();

                    latLngList.clear();
                    markerList.clear();
                    locationList.clear();
                    locationMarkerList.clear();
                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnRegisterLandInstallment.setEnabled(true);
                // btnClear.setBackgroundColor(getResources().getColor(R.color.transparent));

                // Clear All
                if (polygon != null)
                    polygon.remove();

                if (!markerList.isEmpty() && !latLngList.isEmpty() && locationMarkerList.isEmpty() && locationList.isEmpty()) {
                    for (Marker marker : markerList)
                        marker.remove();

                    latLngList.clear();
                    markerList.clear();
                }

                else if (markerList.isEmpty() && latLngList.isEmpty() && !locationMarkerList.isEmpty() && !locationList.isEmpty()) {
                    for (Marker marker : locationMarkerList)
                        marker.remove();

                    locationList.clear();
                    locationMarkerList.clear();
                }
                encodeMessage = "";

                btnClear.getBackground().clearColorFilter();
            }
        });

        // Adding on query listener for our search view.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // on below line we are getting the
                // location name from search view.
                String location = searchView.getQuery().toString();

                // below line is to create a list of address
                // where we will store the list of all address.
                List<Address> addressList = null;

                // checking if the entered location is null or not.
                if (location != null || location.equals("")) {

                    if(!geocoders.isEmpty()) {

                        // clear the geocoder from the map
                        mMap.clear();
                        geocoders.remove(0);

                        // on below line we are creating and initializing a geo coder.
                        Geocoder geocoder = new Geocoder(MapsActivity.this);
                        geocoders.add(geocoder);

                        try {
                            // on below line we are getting location from the
                            // location name and adding that location to address list.
                            addressList = geocoder.getFromLocationName(location, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // on below line we are getting the location
                        // from our list a first position.
                        Address address = addressList.get(0);

                        // on below line we are creating a variable for our location
                        // where we will add our locations latitude and longitude.
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                        // on below line we are adding marker to that position.
                        mMap.addMarker(new MarkerOptions().position(latLng).title(location));

                        // below line is to animate camera to that position.
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    }

                    else if(geocoders.isEmpty()) {

                        // on below line we are creating and initializing a geo coder.
                        Geocoder geocoder = new Geocoder(MapsActivity.this);
                        geocoders.add(geocoder);

                        try {
                            // on below line we are getting location from the
                            // location name and adding that location to address list.
                            addressList = geocoder.getFromLocationName(location, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // on below line we are getting the location
                        // from our list a first position.
                        Address address = addressList.get(0);

                        // on below line we are creating a variable for our location
                        // where we will add our locations latitude and longitude.
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                        // on below line we are adding marker to that position.
                        mMap.addMarker(new MarkerOptions().position(latLng).title(location));

                        // below line is to animate camera to that position.
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID); // mMap.setMapType(mMap.MAP_TYPE_HYBRID); // Here is where you set the map type
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        /** ADDED **/
        mMap.setOnPolygonClickListener(this);

        // getCurrentLocation();

        /**
         * Camera start in Macao
         */
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.557244, -7.995710), 15));

        // Sets the map type to be "hybrid"
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        btnDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // btnDraw.setBackgroundColor(getResources().getColor(R.color.transparent));

                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {

                        // Create Marker Options
                        MarkerOptions markerOptions = new MarkerOptions().position(latLng);

                        // Create Marker
                        Marker marker = mMap.addMarker(markerOptions);

                        // Add LatLng and Marker
                        latLngList.add(latLng);
                        /** ADDED**/

                        encodeMessage = PolyUtil.encode(latLngList);
                        Log.i("-->POLY UTIL DRAW----> ", encodeMessage);
                        markerList.add(marker);
                    }
                });
                btnDraw.getBackground().clearColorFilter();
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng objLoc = marker.getPosition();
                return false;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                LatLng objLoc = marker.getPosition();
                float zoomLevel = 16.0f; //This goes up to 21
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(objLoc, zoomLevel));
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            }
        });
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                Toast.makeText(getApplicationContext(), "Por favor ligue-se ao WI-FI e ao GPS!", Toast.LENGTH_LONG).show();
            }

            else if (isGPSEnabled && !isNetworkEnabled) {
                Toast.makeText(getApplicationContext(), "Por favor ligue ao WI-FI!", Toast.LENGTH_LONG).show();
            }

            else if (!isGPSEnabled && isNetworkEnabled) {
                Toast.makeText(getApplicationContext(), "Por favor ligue ao GPS!", Toast.LENGTH_LONG).show();
            }

            else {
                location = null;
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_MIN_UPDATE_TIME, LOCATION_MIN_UPDATE_DISTANCE, locationListener);
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_MIN_UPDATE_TIME, LOCATION_MIN_UPDATE_DISTANCE, locationListener);
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                if (location != null) {
                    drawMarker(location, getText(R.string.i_am_here).toString());
                }
            }
        }

        else {
            if (ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{permission.ACCESS_FINE_LOCATION}, 12);
            }
            if (ContextCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{permission.ACCESS_COARSE_LOCATION}, 13);
            }
        }
    }

    private void isLocationEnabled() {
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(mContext);
            alertDialog.setTitle("Localização Inativa");
            alertDialog.setMessage("A sua localização não está de momento ativa. Por favor ligue-a" +
                    " de modo a poder registar uma parcela através da sua localização atual.");
/**
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    // isFromSetting = true;
                    // startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    dialog.cancel();
                }
            });

            alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                    startActivity(new Intent(MapsActivity.this, MainMenuActivity.class));
                }
            });  **/
            AlertDialog alert=alertDialog.create();
            alert.show();
        }
    }

    private void drawMarker(Location location, String title) {
        if (this.mMap != null) {

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(title);

            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            Marker marker = mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

            // Add LatLng and Marker
            locationList.add(latLng);
            locationMarkerList.add(marker);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        /**
        if(isFromSetting == true) {
            finish();
            startActivity(getIntent());
            isFromSetting = false;
        }
**/
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }

    /**
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isFromSetting == true) {
            finish();
            startActivity(getIntent());
            isFromSetting=false;
        }
    } **/

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onPolygonClick(@NonNull Polygon polygon) {
        // polygon.setTag("User/Entity");
        Toast.makeText(this, "Nome da Parcela: " + polygon.getTag().toString(),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Listens for clicks on a polyline.
     * @param polyline The polyline object that the user has clicked.
     */
    @Override
    public void onPolylineClick(@NonNull Polyline polyline) {
        // Flip from solid stroke to dotted stroke pattern.
        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT)))
            polyline.setPattern(PATTERN_POLYLINE_DOTTED);

        else
            // The default pattern is a solid stroke.
            polyline.setPattern(null);

        Toast.makeText(this, "Route type " + polyline.getTag().toString(),
                Toast.LENGTH_SHORT).show();
    }

    private void calculateArea() {

        area = SphericalUtil.computeArea(latLngList);

        area = area/(double) 10000; // in square meters --> hectare

    }

    private void locationRequest(double lat, double lng) {

        String[] res = new String[3];

        String url = "https://geoapi.pt/gps?lat=" + lat + "&lon=" + lng + "&json=1";

        RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Bundle extras = new Bundle();

                //Adding key value pairs to this bundle
                //there are quite a lot data types you can store in a bundle
                extras.putString("encode", encodeMessage); // encoded message
                extras.putDouble("area", area); // area
                extras.putString("district", response.optString("distrito"));
                extras.putString("county", response.optString("concelho"));
                extras.putString("parish", response.optString("freguesia"));

                startActivity(new Intent(MapsActivity.this, RegisterInstallmentActivity.class)
                        .putExtras(extras)); // Maps --> Register Inst + Extra Value
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                btnRegisterLandInstallment.setEnabled(false);
                Toast.makeText(getApplicationContext(), "Ponto inválido!" , Toast.LENGTH_LONG).show();
            }
        });
        queue.add(request);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // go to left
    }
}