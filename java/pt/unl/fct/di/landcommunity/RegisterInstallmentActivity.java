package pt.unl.fct.di.landcommunity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

public class RegisterInstallmentActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
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
    String encodeMessage;
    TextView textView;
    SharedPreferences sharedPreferences;

    Intent intent;

    Button uploadImages;
    Button registerInstallment;
    Button reset;

    private static final String SHARED_PREF_NAME = "mypref";
    private static final String DELIMITER = "___";

    private double newArea;

    private static final int REQUEST_PERMISSIONS = 100;
    private static final int PICK_IMAGE_REQUEST = 1 ;
    private String filePath;
    private static final String ROOT_URL = "https://projeto-adc-353721.appspot.com/rest/filename";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_installment);

        // Register Land Installment variables
        district = findViewById(R.id.et_districtLandRegister);
        county = findViewById(R.id.et_countyLandRegister);
        parish = findViewById(R.id.et_parishLandRegister);
        section = (EditText) findViewById(R.id.et_sectionLandRegister);
        name = (EditText) findViewById(R.id.et_nameLandRegister);
        soilType = (EditText) findViewById(R.id.et_soilTypeLandRegister);
        article = (EditText) findViewById(R.id.et_articleLandRegister);
        area = (TextView) findViewById(R.id.et_areaLandRegister);
        currentUsage = (EditText) findViewById(R.id.et_currentUsageLandRegister);
        previousUsage = (EditText) findViewById(R.id.et_previousUsageLandRegister);

        // Faltam cenas---->>
        description = (EditText) findViewById(R.id.et_textHelpLandInstallment);

        //initialising views
        textView =  findViewById(R.id.textviewLandInstallment);

        uploadImages = findViewById(R.id.btnSelectFilesLandInstallment);
        registerInstallment = (Button) findViewById(R.id.btn_registerLandInstallment);
        reset = (Button) findViewById(R.id.btn_resetRegisterLandInstallment);

        article.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        registerInstallment.setEnabled(false);

        /**
         * In order to retrieve the Encoded message from the Maps Activity
         * Now I have the coordinates to send to the datastore with the other values
         */
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            encodeMessage = extras.getString("encode"); //The argument here must match that used in the other activity
            newArea = extras.getDouble("area");
            DecimalFormat twoDForm = new DecimalFormat("#,######");
            area.setText(Double.valueOf(twoDForm.format(newArea)) + " ha"); //ha
            district.setText(extras.getString("district"));
            county.setText(extras.getString("county"));
            parish.setText(extras.getString("parish"));
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

        ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                            if
                            ((ActivityCompat.shouldShowRequestPermissionRationale(RegisterInstallmentActivity.this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(RegisterInstallmentActivity.this,
                                    Manifest.permission.READ_EXTERNAL_STORAGE))) {
                            }
                            else
                                ActivityCompat.requestPermissions(RegisterInstallmentActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                        REQUEST_PERMISSIONS);
                        } else {

                            if (result.getResultCode() == Activity.RESULT_OK) {
                                Intent data = result.getData();
                                Uri uri = data.getData();
                                //path = getFileName(uri, getApplicationContext());
                                if (data != null) {
                                    try {
                                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                                getContentResolver(), data.getData());
                                        // imageView.setImageBitmap(bitmap);
                                        // uploadBitmap(bitmap);
                                        textView.setText("File Selected");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                });

        uploadImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                resultLauncher.launch(intent);
            }
        });


        registerInstallment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /** REST **/

                sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
                System.out.println(sharedPreferences.getAll());
                String username = sharedPreferences.getString("username", null);
                String tokenID = sharedPreferences.getString("tokenID", null);

                String url = "https://projeto-adc-353721.appspot.com/rest/" + username + "/property?tokenID="+ tokenID;

                RequestQueue queue = Volley.newRequestQueue(RegisterInstallmentActivity.this);

                JSONObject js = new JSONObject();
                try {
                    js.put("propertyEncode", encodeMessage);
                    js.put("propertyDist", district.getText().toString());
                    js.put("propertyConc", county.getText().toString());
                    js.put("propertyFreg", parish.getText().toString());
                    js.put("propertySecc", section.getText().toString());
                    js.put("propertyArt", article.getText().toString());
                    js.put("propertyName", name.getText().toString());
                    js.put("propertyCarac", description.getText().toString());
                    js.put("propertyCoberSolo", soilType.getText().toString());
                    js.put("propertyUtiAtu", currentUsage.getText().toString());
                    js.put("propertyUtiAnt", previousUsage.getText().toString());
                    js.put("propertyArea", newArea); // String.valueOf((int) newArea));
                    js.put("fromEntity", false);
                    js.put("propertyFileLink", "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, js,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(getApplicationContext(), "Propriedade registada!", Toast.LENGTH_LONG).show();
                                startActivity(new Intent( RegisterInstallmentActivity.this, MapsActivity.class));
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
                });
                queue.add(jsonRequest);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterInstallmentActivity.this, MapsActivity.class)); // Register --> Login
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // go to left
            }
        });
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri, Context context) {
        String res = null;
        if(uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    res = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
            if (res == null)
                res = uri.getPath();
            int cutt = res.lastIndexOf('/');
            if (cutt != -1) {
                res = res.substring(cutt + 1);
            }
        }
        return res;
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
            registerInstallment.setEnabled(true);
        else
            registerInstallment.setEnabled(false);
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
}