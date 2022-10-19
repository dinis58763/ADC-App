package pt.unl.fct.di.landcommunity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class StatsActivity extends AppCompatActivity {

    private PieChart pieChart;

    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "mypref";

    List<String> areas = new ArrayList<>();
    List<Double> intAreas = new ArrayList<Double>();

    List<Double> until2000Array = new ArrayList<Double>();
    List<Double> until4000Array = new ArrayList<Double>();
    List<Double> until6000Array = new ArrayList<Double>();
    List<Double> until8000Array  = new ArrayList<Double>();
    List<Double> until10000Array = new ArrayList<Double>();
    List<Double> moreThan10000Array = new ArrayList<Double>();

    int counter2 = 0;
    int counter4 = 0;
    int counter6 = 0;
    int counter8 = 0;
    int counter10 = 0;
    int counterM10 = 0;

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        pieChart = findViewById(R.id.activity_main_piechart);

        textView = findViewById(R.id.tv_numberOfInstallments);


        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        String tokenID = sharedPreferences.getString("tokenID", null);
        Long creationDate = sharedPreferences.getLong("creationDate", 0);
        Long expirationDate = sharedPreferences.getLong("expirationDate", 0);
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

        String url = "https://projeto-adc-353721.appspot.com/rest/"+ username +"/properties?userId=" + username;

        RequestQueue queue = Volley.newRequestQueue(StatsActivity.this);

        CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.POST, url, js,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for(int i = 0; i < response.length(); i++)
                            areas.add(i ,response.optJSONObject(i).optJSONObject("properties").optJSONObject("property_area").optString("value"));

                        for(int i = 0; i < areas.size(); i++) {
                            intAreas.add(i, Double.valueOf(((areas.get(i))))); //Integer.valueOf((areas.get(i)))
                            System.out.println("DOUBLE VALOR ---->>>> " + Double.valueOf(((areas.get(i)))));
                        }

                        textView.setText(String.valueOf(areas.size()) + " Parcelas");
                        setupPieChart();
                        loadPieChartData();
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

    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.TRANSPARENT);
        pieChart.setCenterText("Áreas das suas Parcelas");
        pieChart.setCenterTextSize(24);
        pieChart.getDescription().setEnabled(false);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    private void loadPieChartData() {

        for(int i = 0; i < intAreas.size(); i++) {

            if(intAreas.get(i) <= 0.5000)
                until2000Array.add(counter2++, intAreas.get(i));

            else if(intAreas.get(i) > 0.5000 && intAreas.get(i) <= 1.0000)
                until4000Array.add(counter4++, intAreas.get(i));

            else if(intAreas.get(i) > 1.0000 && intAreas.get(i) <= 1.5000)
                until6000Array.add(counter6++, intAreas.get(i));

            else if(intAreas.get(i) > 1.5000 && intAreas.get(i) <= 2.0000)
                until8000Array.add(counter8++, intAreas.get(i));

            else if(intAreas.get(i) > 2.0000 && intAreas.get(i) <= 3.0000)
                until10000Array.add(counter10++, intAreas.get(i));

            else
                moreThan10000Array.add(counterM10++, intAreas.get(i));
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) counter2/intAreas.size(), "Menos de meio hectare"));
        entries.add(new PieEntry((float) counter4/intAreas.size(), "Entre meio hectare e 1 hectare"));
        entries.add(new PieEntry((float) counter6/intAreas.size(), "Entre 1 hectare e hectare e meio"));
        entries.add(new PieEntry((float) counter8/intAreas.size(), "Entre hectare e meio e 2 hectares"));
        entries.add(new PieEntry((float) counter10/intAreas.size(), "Entre 2 hectares e 3 hectares"));
        entries.add(new PieEntry((float) counterM10/intAreas.size(), "Mais de 3 hectares"));

        ArrayList<Integer> colors = new ArrayList<>();
        for (int color: ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for (int color: ColorTemplate.PASTEL_COLORS) { //VORDIPLOM_COLORS COLORFUL_COLORS
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "ÁREAS DAS PARCELAS");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
        pieChart.invalidate();

        pieChart.animateY(1400, Easing.EaseInOutQuad);
        }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // go to left
    }
}
