package pt.unl.fct.di.landcommunity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.ArrayList;

public class SchoolAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> mArrSchoolData;
    private JSONArray response;
    Activity activity;

    public SchoolAdapter(Context context, ArrayList arrSchoolData, JSONArray response) {
        super();
        this.response = response;
        mContext = context;
        mArrSchoolData = arrSchoolData;
        activity = (Activity) context;
    }

    public int getCount() {
        // return the number of records
        return mArrSchoolData.size();
    }

    // getView method is called for each item of ListView
    public View getView(int position, View view, ViewGroup parent) {
        // inflate the layout for each item of listView
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.view_listview_row, parent, false);

        // get the reference of textView and button
        TextView txtSchoolTitle = (TextView) view.findViewById(R.id.txtSchoolTitle);
        Button btnAction = (Button) view.findViewById(R.id.btnAction);
        ImageView imageView = view.findViewById(R.id.txt_statusInstallmentImage);
        TextView textView = view.findViewById(R.id.txt_statusInstallmentText);

        String isTrue = response.optJSONObject(position).optJSONObject("properties").optJSONObject("property_state").optString("value");
        // Set the title and button name
        txtSchoolTitle.setText(mArrSchoolData.get(position));
        // btnAction.setText("Action " + position);
        if(isTrue.equals("true")) {
            imageView.setImageResource(R.drawable.circlegreen);
            textView.setText("  Verificado");
        }
        else {
            imageView.setImageResource(R.drawable.circle);
            textView.setText("  Em Verificação");
        }

        // Click listener of button
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Logic goes here
                Bundle extras = new Bundle();

                System.out.println(response);

                extras.putString("name", response.optJSONObject(position).optJSONObject("properties").optJSONObject("property_name").optString("value"));
                extras.putString("district", response.optJSONObject(position).optJSONObject("properties").optJSONObject("property_dist").optString("value"));
                extras.putString("county", response.optJSONObject(position).optJSONObject("properties").optJSONObject("property_conc").optString("value"));
                extras.putString("parish", response.optJSONObject(position).optJSONObject("properties").optJSONObject("property_freg").optString("value"));
                extras.putString("section", response.optJSONObject(position).optJSONObject("properties").optJSONObject("property_secc").optString("value"));
                extras.putString("article", response.optJSONObject(position).optJSONObject("properties").optJSONObject("property_art").optString("value"));
                extras.putString("soilType", response.optJSONObject(position).optJSONObject("properties").optJSONObject("property_cober_solo").optString("value"));
                extras.putString("area", response.optJSONObject(position).optJSONObject("properties").optJSONObject("property_area").optString("value"));
                extras.putString("currentUsage", response.optJSONObject(position).optJSONObject("properties").optJSONObject("property_uti_atu").optString("value"));
                extras.putString("previousUsage", response.optJSONObject(position).optJSONObject("properties").optJSONObject("property_uti_ant").optString("value"));
                extras.putString("description", response.optJSONObject(position).optJSONObject("properties").optJSONObject("property_carac").optString("value"));
                extras.putString("encode", response.optJSONObject(position).optJSONObject("properties").optJSONObject("property_encode").optString("value"));
                extras.putString("propertyLink", response.optJSONObject(position).optJSONObject("properties").optJSONObject("property_file_link").optString("value"));
                extras.putString("propertyId", response.optJSONObject(position).optJSONObject("key").optJSONArray("path").optJSONObject(0).optString("id"));
                extras.putString("state", isTrue);
                /**
                 * Estado da Propriedade
                 */
                System.out.println("\n\n\n\n" + response.optJSONObject(position).optJSONObject("properties").optJSONObject("property_state").optString("value"));

                activity.startActivity(new Intent(mContext,ListInstallmentActivity.class).putExtras(extras));
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // go to right
            }
        });
        return view;
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
}
