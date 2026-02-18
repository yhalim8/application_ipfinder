package ma.enset.halim_younes_exam_m1_iibdcc_23;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IpFinderActivity extends AppCompatActivity {
    private EditText ipInput;
    private ProgressBar loading;
    private TextView errorText;
    private TextView ipValue;
    private TextView cityValue;
    private TextView regionValue;
    private TextView countryValue;
    private TextView orgValue;
    private Button showMapBtn;
    private Button copyBtn;
    private Button shareBtn;
    private LinearLayout recentContainer;

    private String latLong = "";
    private String latestSummary = "";

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "ipFinderPrefs";
    private static final String RECENT_KEY = "recentIps";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_finder);

        ipInput = findViewById(R.id.IpInput);
        loading = findViewById(R.id.loading);
        errorText = findViewById(R.id.errorText);
        ipValue = findViewById(R.id.ipValue);
        cityValue = findViewById(R.id.cityValue);
        regionValue = findViewById(R.id.regionValue);
        countryValue = findViewById(R.id.countryValue);
        orgValue = findViewById(R.id.orgValue);
        showMapBtn = findViewById(R.id.showMapBtn);
        copyBtn = findViewById(R.id.copyBtn);
        shareBtn = findViewById(R.id.shareBtn);
        recentContainer = findViewById(R.id.recentContainer);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        findViewById(R.id.button).setOnClickListener(view -> searchIp(ipInput.getText().toString().trim()));

        showMapBtn.setOnClickListener(view -> {
            if (latLong.isEmpty()) {
                Toast.makeText(this, "Search first to view on map", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent mapIntent = new Intent(this, MapsActivity.class);
            mapIntent.putExtra("LatLand", latLong);
            startActivity(mapIntent);
        });

        copyBtn.setOnClickListener(view -> {
            if (latestSummary.isEmpty()) {
                Toast.makeText(this, "Nothing to copy", Toast.LENGTH_SHORT).show();
                return;
            }
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("IP details", latestSummary);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Details copied", Toast.LENGTH_SHORT).show();
        });

        shareBtn.setOnClickListener(view -> {
            if (latestSummary.isEmpty()) {
                Toast.makeText(this, "Nothing to share", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, latestSummary);
            startActivity(Intent.createChooser(sendIntent, "Share IP details"));
        });

        loadRecentIps();
        setEmptyState();
    }

    private void searchIp(String input) {
        errorText.setVisibility(View.GONE);

        if (!input.isEmpty() && !Patterns.IP_ADDRESS.matcher(input).matches()) {
            showError("Please enter a valid IPv4/IPv6 address.");
            return;
        }

        setLoading(true);
        String endpoint = input.isEmpty() ? "https://ipinfo.io/json" : "https://ipinfo.io/" + input + "/geo";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, endpoint,
                response -> {
                    setLoading(false);
                    bindResult(response);
                },
                error -> {
                    setLoading(false);
                    showError("Unable to fetch IP info. Please check your network and try again.");
                });

        requestQueue.add(request);
    }

    private void bindResult(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String ip = jsonObject.optString("ip", "-");
            String city = jsonObject.optString("city", "-");
            String region = jsonObject.optString("region", "-");
            String country = jsonObject.optString("country", "-");
            String org = jsonObject.optString("org", "-");
            latLong = jsonObject.optString("loc", "");

            ipValue.setText("IP: " + ip);
            cityValue.setText("City: " + city);
            regionValue.setText("Region: " + region);
            countryValue.setText("Country: " + country);
            orgValue.setText("Provider: " + org);

            latestSummary = "IP: " + ip + "\n"
                    + "City: " + city + "\n"
                    + "Region: " + region + "\n"
                    + "Country: " + country + "\n"
                    + "Provider: " + org;

            if (!ip.equals("-")) {
                saveRecentIp(ip);
                loadRecentIps();
            }
        } catch (Exception exception) {
            showError("Unexpected response from server.");
        }
    }

    private void setLoading(boolean isLoading) {
        loading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void setEmptyState() {
        ipValue.setText("IP: -");
        cityValue.setText("City: -");
        regionValue.setText("Region: -");
        countryValue.setText("Country: -");
        orgValue.setText("Provider: -");
    }

    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
    }

    private void saveRecentIp(String ip) {
        List<String> recent = getRecentIps();
        recent.remove(ip);
        recent.add(0, ip);

        if (recent.size() > 5) {
            recent = recent.subList(0, 5);
        }

        sharedPreferences.edit().putString(RECENT_KEY, String.join(",", recent)).apply();
    }

    private List<String> getRecentIps() {
        String saved = sharedPreferences.getString(RECENT_KEY, "");
        if (saved == null || saved.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(saved.split(",")));
    }

    private void loadRecentIps() {
        recentContainer.removeAllViews();
        List<String> recentIps = getRecentIps();

        if (recentIps.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No searches yet.");
            empty.setTextColor(getColor(R.color.secondary_text));
            recentContainer.addView(empty);
            return;
        }

        for (String recentIp : recentIps) {
            Button button = new Button(this);
            button.setAllCaps(false);
            button.setText(recentIp);
            button.setBackgroundResource(R.drawable.input_background);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 8);
            button.setLayoutParams(params);
            button.setOnClickListener(view -> {
                ipInput.setText(recentIp);
                searchIp(recentIp);
            });
            recentContainer.addView(button);
        }
    }
}
