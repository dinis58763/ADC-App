package pt.unl.fct.di.landcommunity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HelpActivity extends AppCompatActivity {

    EditText et_subject;
    EditText et_message;
    Button sendEmail;
    Button reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        et_subject = findViewById(R.id.et_emailHelp);
        et_message = findViewById(R.id.et_textHelp);
        sendEmail = findViewById(R.id.btn_send_email);
        reset = findViewById(R.id.btn_resetHelp);

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject = et_subject.getText().toString().trim();
                String message = et_message.getText().toString().trim();
                String email = "dinisilvestre@gmail.com";

                if (subject.isEmpty())
                    Toast.makeText(HelpActivity.this, "Please add an Email", Toast.LENGTH_SHORT).show();

                else if (message.isEmpty())
                    Toast.makeText(HelpActivity.this, "Please add some Message", Toast.LENGTH_SHORT).show();

                else {
                    String mail = "mailto:" + email +
                            "?&subject=" + Uri.encode(subject) +
                            "&body=" + Uri.encode(message);
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse(mail));
                    try {
                        startActivity(Intent.createChooser(intent, "Send Email.."));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // go to right
                    } catch (Exception e) {
                        Toast.makeText(HelpActivity.this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() { // Reset Button
            @Override
            public void onClick(View view) {
                /** MUDADO URGENTEMENTE DEPOIS DO PROFILE ESTAR FEITO **/
                startActivity(new Intent(HelpActivity.this, MainMenuActivity.class)); // Help --> Settings
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
