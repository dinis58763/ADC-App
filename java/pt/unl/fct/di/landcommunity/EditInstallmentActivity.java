package pt.unl.fct.di.landcommunity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

public class EditInstallmentActivity extends AppCompatActivity {

    TextView titleName;
    TextView district;
    TextView county;
    TextView parish;
    EditText name;
    EditText soilType;
    TextView area;
    EditText previousUsage;
    EditText currentUsage;
    EditText description;
    EditText article;
    EditText section;

    ImageView imageView;
    TextView textView;

    Button openMap;
    Button save;
    Button reset;

    String encode;
    String isTrue;
    String propertyLink;

    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "mypref";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_installment);

        // Register Land Installment variables
        titleName = findViewById(R.id.tv_edit_installment);

        name = findViewById(R.id.et_nameEditLandRegister);
        district = findViewById(R.id.et_districtEditLandList);
        county = findViewById(R.id.et_countyEditLandList);
        parish = findViewById(R.id.et_parishEditLandList);
        section = findViewById(R.id.et_sectionEditLandList);
        soilType = findViewById(R.id.et_soilEditTypeLandList);
        article = findViewById(R.id.et_articleEditLandList);
        area = findViewById(R.id.et_areaEditLandList);
        currentUsage = findViewById(R.id.et_currentUsageEditLandList);
        previousUsage = findViewById(R.id.et_previousUsageEditLandList);
        description = findViewById(R.id.et_textHelpEditLandInstallmentList);

        imageView = findViewById(R.id.txt_statusInstallmentImageEdit);
        textView = findViewById(R.id.txt_statusInstallmentTextEdit);

        openMap = findViewById(R.id.btn_EditLandInstallmentOpenMaps);

        DecimalFormat twoDForm = new DecimalFormat("#,######");

        /** REST Request **/
        Bundle extras = getIntent().getExtras();

        if(extras != null){
            name.setText(extras.getString("nameL"));
            district.setText(extras.getString("districtL"));
            county.setText(extras.getString("countyL"));
            parish.setText(extras.getString("parishL"));
            section.setText(extras.getString("sectionL"));
            article.setText(extras.getString("articleL"));
            soilType.setText(extras.getString("soilTypeL"));
            // area.setText(twoDForm.format(Double.valueOf(extras.getString("areaL"))));
            System.out.println("ENTREI EDIT INSTALLMENT ---->>>> " + extras.getString("areaL"));
            area.setText(extras.getString("areaL"));
            currentUsage.setText(extras.getString("currentUsageL"));
            previousUsage.setText(extras.getString("previousUsageL"));
            description.setText(extras.getString("descriptionL"));

            isTrue = extras.getString("state");
            encode = extras.getString("encodeL");
            propertyLink = extras.getString("propertyLink");
        }

        name.setText(extras.getString("nameL"), TextView.BufferType.EDITABLE);
        district.setText(extras.getString("districtL"), TextView.BufferType.EDITABLE);
        county.setText(extras.getString("countyL"), TextView.BufferType.EDITABLE);
        parish.setText(extras.getString("parishL"), TextView.BufferType.EDITABLE);
        section.setText(extras.getString("sectionL"), TextView.BufferType.EDITABLE);
        article.setText(extras.getString("articleL"), TextView.BufferType.EDITABLE);
        soilType.setText(extras.getString("soilTypeL"), TextView.BufferType.EDITABLE);
        area.setText(extras.getString("areaL") + " ha");  //twoDForm.format(Double.valueOf(extras.getString("areaL"))) + " ha");
        currentUsage.setText(extras.getString("currentUsageL"), TextView.BufferType.EDITABLE);
        previousUsage.setText(extras.getString("previousUsageL"), TextView.BufferType.EDITABLE);
        description.setText(extras.getString("descriptionL"), TextView.BufferType.EDITABLE);

        if(isTrue.equals("true")) {
            imageView.setImageResource(R.drawable.circlegreen);
            textView.setText("  Verificado");
        }
        else {
            imageView.setImageResource(R.drawable.circle);
            textView.setText("  Em Verificação");
        }

        // NAME
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
                // checkValidation();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isName(name))
                    name.setError("O nome da parcela não é válido! O máximo de caracteres que pode usar são 15 e no mínimo 3!");
                else
                    checkValidation();
            }
        });

        // NAME
        section.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
                // checkValidation();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isSection(section))
                    section.setError("Por favor inclua entre 1 a 2 letras em Caps-Lock!");
                else
                    checkValidation();
            }
        });

        // NAME
        article.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
                // checkValidation();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isArticle(article))
                    article.setError("Por favor inclua entre 1 a 3 números!");
                else
                    checkValidation();
            }
        });

        // NAME
        soilType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
                // checkValidation();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isSoilType(soilType))
                    soilType.setError("O tipo de solo não é válido!");
                else
                    checkValidation();
            }
        });

        // NAME
        currentUsage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
                // checkValidation();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isText(currentUsage))
                    currentUsage.setError("Por favor complete a caixa de texto!");
                else
                    checkValidation();
            }
        });

        // NAME
        previousUsage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
                // checkValidation();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isText(previousUsage))
                    previousUsage.setError("Por favor complete a caixa de texto!");
                else
                    checkValidation();
            }
        });

        // NAME
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
                // checkValidation();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isText(description))
                    description.setError("Por favor complete a caixa de texto!");
                else
                    checkValidation();
            }
        });

        /** REST to get all variables for that land installment name **/
        // setText -->

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateTextView();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();

        openMap.setOnClickListener(new View.OnClickListener() { // EDIT MAP
            @Override
            public void onClick(View view) {

                checkDataEntered();

                /** Intent **/
                if(extras != null) {

                    Bundle extrasList = new Bundle();

                    extrasList.putString("nameE", name.getText().toString());
                    extrasList.putString("districtE", district.getText().toString());
                    extrasList.putString("countyE", county.getText().toString());
                    extrasList.putString("parishE", parish.getText().toString());
                    extrasList.putString("sectionE", section.getText().toString());
                    extrasList.putString("articleE", article.getText().toString());
                    extrasList.putString("soilTypeE", soilType.getText().toString());
                    extrasList.putString("areaE", area.getText().toString()); // twoDForm.format(Double.valueOf(extras.getString("areaL"))));
                    extrasList.putString("currentUsageE", currentUsage.getText().toString());
                    extrasList.putString("previousUsageE", previousUsage.getText().toString());
                    extrasList.putString("descriptionE", description.getText().toString());
                    extrasList.putString("encodeE", encode);
                    extrasList.putString("propertyIdE", extras.getString("propertyIdL"));
                    extrasList.putString("state", isTrue);
                    extrasList.putString("propertyLink", propertyLink);

                    startActivity(new Intent(EditInstallmentActivity.this, MapsEditInstallmentActivity.class)
                            .putExtras(extrasList)); // Register --> Login
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // go to right
                }
            }
        });
    }

    boolean isSoilType(EditText text) {
        String soil = text.getText().toString();
        String regex ="[A-Za-z ]*";
        return (!TextUtils.isEmpty(soil.trim()) && Pattern.compile(regex).matcher(soil).matches());
    }

    boolean isSection(EditText text) {
        String section = text.getText().toString();
        String regex ="[A-Z]{1,2}";
        return (!TextUtils.isEmpty(section.trim()) && Pattern.compile(regex).matcher(section).matches()
                && section.length() >= 1 && section.length() <= 2);
    }

    boolean isArticle(EditText text) {
        String article = text.getText().toString();
        String regex ="[0-9]{1,3}";
        return (!TextUtils.isEmpty(article.trim()) && Pattern.compile(regex).matcher(article).matches()
                && article.length() >= 1 && article.length() <= 3);
    }

    boolean isName(EditText text) {
        String name = text.getText().toString();
        String regex ="[A-Za-z0-9 ]*";
        return (!TextUtils.isEmpty(name.trim()) && Pattern.compile(regex).matcher(name).matches() && name.length() < 15
                && name.length() >= 3);
    }

    boolean isText(EditText text) {
        String newText = text.getText().toString();
        String regex = "[a-zA-Z0-9 ]*";
        return (!TextUtils.isEmpty(newText.trim()) && Pattern.compile(regex).matcher(newText).matches());
    }

    private void checkValidation() {
        if (checkDataEntered())
            openMap.setEnabled(true);
        else
            openMap.setEnabled(false);
    }

    boolean checkDataEntered() {

        if (!isName(name))
            return false;

        if(!isSection(section))
            return false;

        if (!isArticle(article))
            return false;

        if(!isSoilType(soilType))
            return false;

        if (!isText(currentUsage))
            return false;

        if (!isText(previousUsage))
            return false;

        if (!isText(description))
            return false;

        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // go to left
    }


    private void updateTextView() {
        titleName.setText(name.getText().toString());
    }
}
