package pt.unl.fct.di.landcommunity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class ListInstallmentActivity extends AppCompatActivity {

    TextView district;
    TextView county;
    TextView parish;
    TextView name;
    TextView soilType;
    TextView area;
    TextView previousUsage;
    TextView currentUsage;
    TextView description;
    TextView article;
    TextView section;

    Button openMap;
    Button editLandInstallment;
    Button reset;

    String encode;
    String propertyLink;
    String isTrue;
    ImageView imageView;
    TextView stateText;

    Double newArea;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_installment);

        // Register Land Installment variables

        name = findViewById(R.id.tv_list_installment);

        district = findViewById(R.id.et_districtLandList);
        county = findViewById(R.id.et_countyLandList);
        parish = findViewById(R.id.et_parishLandList);
        section = findViewById(R.id.et_sectionLandList);
        soilType = findViewById(R.id.et_soilTypeLandList);
        article = findViewById(R.id.et_articleLandList);
        area = findViewById(R.id.et_areaLandList);
        currentUsage = findViewById(R.id.et_currentUsageLandList);
        previousUsage = findViewById(R.id.et_previousUsageLandList);
        description = findViewById(R.id.et_textHelpLandInstallmentList);

        openMap = findViewById(R.id.btn_ListLandInstallmentOpenMaps);
        editLandInstallment = (Button) findViewById(R.id.btn_editLandInstallment);
        reset = (Button) findViewById(R.id.btn_resetListLandInstallment);

        imageView = findViewById(R.id.txt_statusInstallmentImageList);
        stateText = findViewById(R.id.txt_statusInstallmentTextList);

        DecimalFormat twoDForm = new DecimalFormat("#,######");

        /** REST to get all variables for that land installment name **/

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            name.setText(extras.getString("name"));
            district.setText(extras.getString("district"));
            county.setText(extras.getString("county"));
            parish.setText(extras.getString("parish"));
            section.setText(extras.getString("section"));
            article.setText(extras.getString("article"));
            soilType.setText(extras.getString("soilType"));
            // area.setText(twoDForm.format(Double.valueOf(extras.getString("area"))) + " ha");
            // System.out.println("ENTREI VALOR LIST INSTALLMENT --->>> " + extras.getString("area") );
            newArea = Double.valueOf(extras.getString("area"));
            System.out.println("ENTREI LIST INST NEW AREA --->> " + newArea);
            System.out.println("ENTREI LIST INST EXTRAS GET STRING --->> " + extras.getString("area"));
            area.setText(newArea + " ha");
            currentUsage.setText(extras.getString("currentUsage"));
            previousUsage.setText(extras.getString("previousUsage"));
            description.setText(extras.getString("description"));

            propertyLink = extras.getString("propertyLink");
            encode = extras.getString("encode");
            isTrue = extras.getString("state");
        }

        if(isTrue.equals("true")) {
            imageView.setImageResource(R.drawable.circlegreen);
            stateText.setText("  Verificado");
        }
        else {
            imageView.setImageResource(R.drawable.circle);
            stateText.setText("  Em Verificação");
        }


        openMap.setOnClickListener(new View.OnClickListener() { // LIST MAP
            @Override
            public void onClick(View view) {
                /** Intent **/
                Bundle extrasList = new Bundle();
                extrasList.putString("encodeMapList", encode);
                extrasList.putString("state", isTrue);
                startActivity(new Intent(ListInstallmentActivity.this, MapsListInstallmentActivity.class)
                        .putExtras(extrasList)); // Register --> Login
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // go to right
            }
        });

        editLandInstallment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(extras != null) {
                    Bundle extrasList = new Bundle();

                    extrasList.putString("nameL", extras.getString("name"));
                    extrasList.putString("districtL", extras.getString("district"));
                    extrasList.putString("countyL", extras.getString("county"));
                    extrasList.putString("parishL", extras.getString("parish"));
                    extrasList.putString("sectionL", extras.getString("section"));
                    extrasList.putString("articleL", extras.getString("article"));
                    extrasList.putString("soilTypeL", extras.getString("soilType"));
                    extrasList.putString("areaL", newArea.toString()); // extras.getString("area"));
                    extrasList.putString("currentUsageL", extras.getString("currentUsage"));
                    extrasList.putString("previousUsageL", extras.getString("previousUsage"));
                    extrasList.putString("descriptionL", extras.getString("description"));
                    extrasList.putString("encodeL", encode);
                    extrasList.putString("propertyIdL", extras.getString("propertyId"));
                    extrasList.putString("state", isTrue);
                    extrasList.putString("propertyLink", propertyLink);

                    startActivity(new Intent(ListInstallmentActivity.this, EditInstallmentActivity.class)
                            .putExtras(extrasList)); // Register --> Login
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // go to right
                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name.setText("");
                district.setText("");
                county.setText("");
                parish.setText("");
                section.setText("");
                soilType.setText("");
                article.setText("");
                currentUsage.setText("");
                previousUsage.setText("");
                description.setText("");
                startActivity(new Intent(ListInstallmentActivity.this, ListviewActivity.class)); // List Inst --> List View
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // go to left
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // go to left
    }
}

