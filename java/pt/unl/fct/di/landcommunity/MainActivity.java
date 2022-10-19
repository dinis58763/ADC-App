package pt.unl.fct.di.landcommunity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button loginButtonMenu;
    Button registerButtonMenu;
    Button aboutUsMenu;
    Button contactUsMenu;
    Button termsConditionsMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get buttons
        View mlayout = findViewById(R.id.layout);
        loginButtonMenu = (Button) findViewById(R.id.btn_loginMenu);
        registerButtonMenu = (Button) findViewById(R.id.btn_registerMenu);
        aboutUsMenu = (Button) findViewById(R.id.btn_aboutUsMenu);
        contactUsMenu = (Button) findViewById(R.id.btn_contactsMenu);
        termsConditionsMenu = (Button) findViewById(R.id.btn_termsAndConditions);

        mlayout.setBackgroundColor(Color.rgb(255,255,255)); // White Background

        loginButtonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class)); // Main --> Login
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // go to right
            }
        });

        registerButtonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class)); // Main --> Register
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // go to right
            }
        });

        aboutUsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // About Us --> About Us Website (Maybe later needs change)
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://projeto-adc-353721.appspot.com/about/about.html")));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // go to right
            }
        });

        contactUsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // Contact Us --> Contact Us Website (Maybe later needs change)
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://projeto-adc-353721.appspot.com/contacts/contact.html")));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // go to right
            }
        });

        termsConditionsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                https://policies.google.com/terms?hl=en
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://policies.google.com/terms?hl=en")));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // go to right
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // go to left
    }
}
