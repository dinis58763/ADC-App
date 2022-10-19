package pt.unl.fct.di.landcommunity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    EditText nameRegister;
    EditText emailRegister;
    EditText usernameRegister;
    EditText mobileRegister;
    EditText phoneRegister;
    EditText cityRegister;
    EditText addressRegister;
    EditText postalCodeRegister;
    EditText nifRegister;
    EditText passwordLogin;
    EditText repasswordLogin;
    Button registerButton;
    CheckBox checkBox;
    Switch switchButton;
    // private ProgressBar loadingPB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(LoginViewModel.class);

        // Register variables
        nameRegister = (EditText) findViewById(R.id.et_nameRegister);
        emailRegister = (EditText) findViewById(R.id.et_emailRegister);
        usernameRegister = (EditText) findViewById(R.id.et_usernameRegister);
        mobileRegister = (EditText) findViewById(R.id.et_mobilePhoneUpdate);
        phoneRegister = (EditText) findViewById(R.id.et_phoneRegister);
        cityRegister = (EditText) findViewById(R.id.et_cityRegister);
        addressRegister = (EditText) findViewById(R.id.et_addressRegister);
        postalCodeRegister = (EditText) findViewById(R.id.et_postalCodeRegister);
        nifRegister = (EditText) findViewById(R.id.et_nifRegister);
        passwordLogin = (EditText) findViewById(R.id.et_passwordRegister);
        repasswordLogin = (EditText) findViewById(R.id.et_repasswordRegister);

        mobileRegister.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        phoneRegister.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        nifRegister.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        checkBox = (CheckBox) findViewById(R.id.et_checkBoxSeePassword);
        registerButton = (Button) findViewById(R.id.btn_register);
        switchButton = (Switch) findViewById(R.id.et_switchRegister);
        final Button goToLoginButton = (Button) findViewById(R.id.goToLoginButton);

        registerButton.setEnabled(false);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    // Show Password
                    passwordLogin.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                else
                    // Hide Password
                    passwordLogin.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    switchButton.setText("Private Profile");
                    switchButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_privacy, 0, 0, 0);
                } else {
                    // When is nothing selected, means we have a public profile.
                    switchButton.setText("Public Profile");
                    switchButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_user, 0, 0, 0);
                }
            }
        });

        goToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class)); // Register --> Login
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // go to left
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              /** ADDED **/
              System.out.println("CHECK VALIDATION VALOR --->>>> " + checkDataEntered());
              System.out.println("CHECK NAME VALOR --->>>> " + isName(nameRegister));
              System.out.println("CHECK EMAIL VALOR --->>>> " + isEmail(emailRegister));
              System.out.println("CHECK USERNAME VALOR --->>>> " + isUsername(usernameRegister));
              System.out.println("CHECK PASSWORD VALOR --->>>> " + isValidPassword(passwordLogin));
              System.out.println("CHECK RETYPE PASSWORD VALOR --->>>> " + checkPasswords(passwordLogin, repasswordLogin));

              System.out.println("\n\n\n\n\n\n\nCHECK NAME VALOR --->>>> " + nameRegister.getText().toString());
              System.out.println("CHECK EMAIL VALOR --->>>> " + emailRegister.getText().toString());
              System.out.println("CHECK USERNAME VALOR --->>>> " + usernameRegister.getText().toString());
              System.out.println("CHECK PASSWORD VALOR --->>>> " + passwordLogin.getText().toString());
              System.out.println("CHECK RETYPE PASSWORD VALOR --->>>> " + passwordLogin.getText().toString());

              // checkDataEntered();
              checkOptionalDataEntered();
              System.out.println("CHECK VALIDATION VALOR --->>>> " + checkOptionalDataEntered());
              System.out.println("CHECK NAME VALOR --->>>> " + isNIF(nifRegister));

              // loadingPB.setVisibility(View.VISIBLE);

              String url = "https://projeto-adc-353721.appspot.com/rest/register";

              RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);

              StringRequest request = new StringRequest(StringRequest.Method.POST, url,
                      new Response.Listener<String>() {
                          @Override
                          public void onResponse(String response) {
                              usernameRegister.setText("");
                              passwordLogin.setText("");
                              repasswordLogin.setText("");
                              emailRegister.setText("");
                              nameRegister.setText("");
                              phoneRegister.setText("");
                              mobileRegister.setText("");
                              addressRegister.setText("");
                              cityRegister.setText("");
                              postalCodeRegister.setText("");
                              nifRegister.setText("");

                              startActivity(new Intent(RegisterActivity.this, LoginActivity.class)); // Register --> Login
                              overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // go to right
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
              }) {
                  @Override
                  public String getBodyContentType() {
                      return "application/json; charset=utf-8";
                  }

                  @Override
                  public byte[] getBody() {
                      try {
                          JSONObject js = new JSONObject();
                          try {
                              js.put("username", usernameRegister.getText().toString());
                              js.put("password", passwordLogin.getText().toString());
                              js.put("confirmPwd", repasswordLogin.getText().toString());
                              js.put("email", emailRegister.getText().toString());
                              js.put("name", nameRegister.getText().toString());
                              js.put("profilePrivacy", false);
                              js.put("mobileNr", mobileRegister.getText().toString());
                              js.put("phoneNr", phoneRegister.getText().toString());
                              js.put("mobileNr", mobileRegister.getText().toString());
                              js.put("mainAddr", addressRegister.getText().toString());
                              js.put("city", cityRegister.getText().toString());
                              js.put("cp", postalCodeRegister.getText().toString());
                              js.put("nif", nifRegister.getText().toString());
                              js.put("file", "/img/default-avatar.jpg");

                          } catch (JSONException e) {
                              e.printStackTrace();
                          }
                          /* fill your json here */
                          return js.toString().getBytes("utf-8");
                      } catch (Exception e) {
                      }
                      return null;
                  }
              };
              queue.add(request);
          }
      });

        // NAME
        nameRegister.addTextChangedListener(new TextWatcher() {
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
                if (!isName(nameRegister))
                    nameRegister.setError("Por favor insira um nome válido");
                else
                    checkValidation();
            }
        });

        // EMAIL
        emailRegister.addTextChangedListener(new TextWatcher() {
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
                if (!isEmail(emailRegister))
                    emailRegister.setError("Por favor insira um e-mail válido");
                else
                    checkValidation();
            }
        });

        // USERNAME
        usernameRegister.addTextChangedListener(new TextWatcher() {
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
                if (!isUsername(usernameRegister))
                    usernameRegister.setError("Por favor insira um Nome de Utilizador válido que não" +
                            " exceda os 20 caracteres");
                else
                    checkValidation();
            }
        });

        // PHONE NUMBER
        phoneRegister.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
                // checkOptionalValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!TextUtils.isEmpty(phoneRegister.getText().toString().trim())) {
                    if (!isHomePhone(phoneRegister)) {
                        phoneRegister.setError("Por favor insira um Telefone válido");
                        registerButton.setEnabled(false);
                    }
                    else {
                        if (checkDataEntered())
                            registerButton.setEnabled(true);
                    }
                }
            }
        });

        // MOBILE PHONE
        mobileRegister.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
                // checkOptionalValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!TextUtils.isEmpty(mobileRegister.getText().toString().trim())) {
                    if (!isMobilePhone(mobileRegister)) {
                        mobileRegister.setError("Por favor insira um Telemóvel válido");
                        registerButton.setEnabled(false);
                    }
                    else {
                        if (checkDataEntered())
                            registerButton.setEnabled(true);
                    }
                }
            }
        });

        // CITY
        cityRegister.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
                // checkOptionalValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!TextUtils.isEmpty(cityRegister.getText().toString().trim())) {
                    if (!isCity(cityRegister)) {
                        cityRegister.setError("Por favor insira uma cidade válida");
                    registerButton.setEnabled(false);
                    }
                    else {
                        if (checkDataEntered())
                            registerButton.setEnabled(true);
                    }
                }
            }
        });

        // ADDRESS
        addressRegister.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
                // checkOptionalValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!TextUtils.isEmpty(addressRegister.getText().toString().trim())) {
                    if (!isEmpty(addressRegister)) {
                        addressRegister.setError("Por favor insira uma Morada válida");
                        registerButton.setEnabled(false);
                    }
                    else {
                        if (checkDataEntered())
                            registerButton.setEnabled(true);
                    }
                }
            }
        });

        // POSTAL-CODE
        postalCodeRegister.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
                // checkOptionalValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!TextUtils.isEmpty(postalCodeRegister.getText().toString().trim())) {
                    if (!isPostalCode(postalCodeRegister)) {
                        postalCodeRegister.setError("Por favor insira um Código-Postal válido");
                    registerButton.setEnabled(false);
                    }
                    else {
                        if (checkDataEntered())
                            registerButton.setEnabled(true);
                    }
                }
            }
        });

        // NIF
        nifRegister.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
                // checkOptionalValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!TextUtils.isEmpty(nifRegister.getText().toString().trim())) {
                    if (!isNIF(nifRegister)) {
                        nifRegister.setError("Por favor insira um NIF válido");
                        registerButton.setEnabled(false);
                    }
                    else {
                        if (checkDataEntered())
                            registerButton.setEnabled(true);
                    }
                }
            }
        });

        // PASSWORD
        passwordLogin.addTextChangedListener(new TextWatcher() {
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
                if (passwordLogin.length() < 5)
                    passwordLogin.setError("A Palavra-Passe tem de ter no minímo 5 caracteres");
                else if (passwordLogin.length() > 12)
                    passwordLogin.setError("A Palavra-Passe só pode ter no máximo 12 caracteres");
                else if (!isValidPassword(passwordLogin))
                    passwordLogin.setError("Por favor insira pelo menos um caracter especial e não utilize espaços");
                else
                    checkValidation();
            }
        });

        // REPEAT PASSWORD
        repasswordLogin.addTextChangedListener(new TextWatcher() {
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
                if (!checkPasswords(passwordLogin, repasswordLogin))
                    repasswordLogin.setError("As Palavras-Passe não são iguais!");
                else
                    checkValidation();
            }
        });
    }

    boolean isEmail(EditText text) {
        String email = text.getText().toString();
        String regex ="^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return (!TextUtils.isEmpty(email.trim()) && Pattern.compile(regex).matcher(email).matches());
    }

    boolean isUsername(EditText text) {
        String username = text.getText().toString();
        return (!TextUtils.isEmpty(username.trim()) && username.length() <= 20 && !username.contains(" "));
    }

    boolean isMobilePhone(EditText text) {
        String phone = text.getText().toString();
        String regex ="^[9][1236]+[0-9]{7}$"; // Checks for portuguese mobile numbers.
        return (Pattern.compile(regex).matcher(phone).matches() && TextUtils.isDigitsOnly(phone));
    }

    boolean isHomePhone(EditText text) {
        String phone = text.getText().toString();
        String regex ="^[2]+[0-9]{8}$"; // Checks for portuguese mobile numbers.
        return (Pattern.compile(regex).matcher(phone).matches() && TextUtils.isDigitsOnly(phone));
    }

    boolean isEmpty(EditText text) {
        String str = text.getText().toString();
        return TextUtils.isEmpty(str.trim());
    }

    boolean isNIF(EditText text) {
        String nif = text.getText().toString();
        String regex ="[0-9]{9}";
        return (Pattern.compile(regex).matcher(nif).matches() && TextUtils.isDigitsOnly(nif));
    }

    boolean isCity(EditText text) {
        String city = text.getText().toString();
        String regex ="[A-Za-z ]*";
        return (Pattern.compile(regex).matcher(city).matches());
    }

    boolean isName(EditText text) {
        String name = text.getText().toString();
        String regex ="[A-Za-z ]*";
        return (!TextUtils.isEmpty(name.trim()) && Pattern.compile(regex).matcher(name).matches());
    }

    boolean isPostalCode(EditText text) {
        String cp = text.getText().toString();
        String regex = "^[0-9]{4}(?:-[0-9]{3})?$";
        return (Pattern.compile(regex).matcher(cp).matches() && Pattern.compile(regex).matcher(cp).matches());
    }

    boolean isValidPassword(EditText text) {
        String password = text.getText().toString();
        String regex = "^" +
                "(?=.*[!@#$%^&+=])" +     // at least 1 special character
                "(?=\\S+$)" +            // no white spaces
                ".{5,12}" +              // at least 5 characters
                "$"; // "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[-+_!@#$%^&*., ?]).+$";
        return (!TextUtils.isEmpty(password.trim()) && password.length() >= 5
                && password.length() <= 12 && Pattern.compile(regex).matcher(password).matches());
    }

    boolean checkPasswords(EditText text1, EditText text2) {
        String pass1 = text1.getText().toString();
        String pass2 = text2.getText().toString();
        return !TextUtils.isEmpty(pass2.trim()) && pass1.equals(pass2);
    }

    private void checkValidation() {
        if (checkDataEntered())
            registerButton.setEnabled(true);
        else
            registerButton.setEnabled(false);
    }

    boolean checkDataEntered() {

        if (!isName(nameRegister)) {
            System.out.println("ENTREI AQUI NAME ------>>>>>>>> " + !isName(nameRegister));
            return false;
        }

        if(!isUsername(usernameRegister))
            return false;

        if (!isEmail(emailRegister))
            return false;

        if(!isValidPassword(passwordLogin))
            return false;

        if (!checkPasswords(passwordLogin, repasswordLogin))
            return false;

        return true;
    }

    boolean checkOptionalDataEntered() {

        if(!TextUtils.isEmpty(phoneRegister.getText().toString().trim()))
            if (!isHomePhone(phoneRegister))
                return false;

        if(!TextUtils.isEmpty(mobileRegister.getText().toString().trim()))
            if (!isMobilePhone(mobileRegister))
                return false;

        if(!TextUtils.isEmpty(nifRegister.getText().toString().trim()))
            if (!isNIF(nifRegister))
                return false;

        if(!TextUtils.isEmpty(cityRegister.getText().toString().trim()))
            if (!isCity(cityRegister))
                return false;

        if(!TextUtils.isEmpty(postalCodeRegister.getText().toString().trim()))
            if (!isPostalCode(postalCodeRegister))
                return false;

        return true;
    }

     @Override
     public void finish() {
     super.finish();
     overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // go to left
     }
}