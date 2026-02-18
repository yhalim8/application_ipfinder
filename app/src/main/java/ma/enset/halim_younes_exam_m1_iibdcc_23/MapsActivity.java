package ma.enset.halim_younes_exam_m1_iibdcc_23;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private LatLng location;
    private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        String latLong = getIntent().getStringExtra("LatLand");
        cityName = getIntent().getStringExtra("CityName");
        location = parseLatLng(latLong);

        if (location == null) {
            Toast.makeText(this, R.string.location_not_available, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment == null) {
            Toast.makeText(this, R.string.map_unavailable, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        String markerTitle = TextUtils.isEmpty(cityName)
                ? getString(R.string.unknown_city)
                : cityName;

        googleMap.addMarker(new MarkerOptions()
                .position(location)
                .title(markerTitle));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 11f));
    }

    private LatLng parseLatLng(String latLong) {
        if (TextUtils.isEmpty(latLong) || !latLong.contains(",")) {
            return null;
        }

        String[] parts = latLong.split(",");
        if (parts.length != 2) {
            return null;
        }

        try {
            double latitude = Double.parseDouble(parts[0].trim());
            double longitude = Double.parseDouble(parts[1].trim());
            return new LatLng(latitude, longitude);
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
