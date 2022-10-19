package pt.unl.fct.di.landcommunity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;


public class MapsListInstallmentActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnPolygonClickListener  {

    private Context mContext;
    private GoogleMap mMap;

    private List<LatLng> list = new ArrayList<>();
    private String encode;
    private String isTrue;

    Polygon polygon = null;

    final int POLYGON_PADDING_PREFERENCE = 200;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_list_installment);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            encode = extras.getString("encodeMapList");
            isTrue = extras.getString("state");
        }

        // checkBox.setTextColor(Color.rgb(255,255,255));
        mContext = MapsListInstallmentActivity.this;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.list_installment_map);

        mapFragment.getMapAsync(this);
        mContext = MapsListInstallmentActivity.this;

        list = PolyUtil.decode(encode);

        /** REST Request to get the encoded Message **/
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Sets the map type to be "hybrid"
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        final LatLngBounds latLngBounds = getPolygonLatLngBounds(list);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, POLYGON_PADDING_PREFERENCE));

        // Create PolygonOptions
        if(!list.isEmpty()) {
            PolygonOptions polygonOptions = new PolygonOptions().addAll(list).clickable(true);
            polygon = mMap.addPolygon(polygonOptions);
            polygon.setTag("A");


            // Set Polygon Stroke Color
            if (isTrue.equals("true")) {
                polygon.setStrokeColor(Color.rgb(0, 153, 76)); // solid green
                polygon.setFillColor(Color.rgb(102, 255, 178)); // green
            }

            else if (isTrue.equals("false")){
                polygon.setStrokeColor(Color.rgb(0, 76, 153)); // blue
                polygon.setFillColor(Color.rgb(51, 153, 255)); // light blue
            }
        }
    }

    private static LatLngBounds getPolygonLatLngBounds(final List<LatLng> newList) {
        final LatLngBounds.Builder centerBuilder = LatLngBounds.builder();
        for (LatLng point : newList) {
            centerBuilder.include(point);
        }
        return centerBuilder.build();
    }

    @Override
    public void onPolygonClick(@NonNull Polygon polygon) {
        polygon.setTag("User/Entity");
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // go to left
    }
}
