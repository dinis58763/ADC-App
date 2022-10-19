package pt.unl.fct.di.landcommunity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsInactiveActivity extends AppCompatActivity {

    Switch switchButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_inactive);

        //loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(LoginViewModel.class);

        // Delete variables

        switchButton = (Switch) findViewById(R.id.et_switchInactiveProfile);

        final Button logout = (Button) findViewById(R.id.btn_logoutInactive);
        final Button delete = (Button) findViewById(R.id.btn_delete_inactive_account);
        final Button backToMenu = (Button) findViewById(R.id.btn_goBackToMenuInactive);

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    switchButton.setText("Active Profile");
                    /**
                     * updates in REST that is now active and returns to main Menu already active
                     */
                    startActivity(new Intent(SettingsInactiveActivity.this, MainMenuActivity.class)); // Register --> Login
                   //  switchButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_privacy, 0, 0, 0);
                }
                else {
                    // When is nothing selected, means we have a public profile.
                    switchButton.setText("Inactive Profile");
                    // startActivity(new Intent(RegisterActivity.this, LoginActivity.class)); // Register --> Login
                    // switchButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_user, 0, 0, 0);
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() { // Reset Button
            // DeleteAccountGBO ----> SettingsGBO
            @Override
            public void onClick(View view) {
                /** REST -> Even an inactive can logout **/
                startActivity(new Intent(SettingsInactiveActivity.this, LoginActivity.class)); // Register --> Login
            }
        });

        delete.setOnClickListener(new View.OnClickListener() { // Reset Button
            // DeleteAccountGBO ----> SettingsGBO
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsInactiveActivity.this, DeleteActivity.class)); // Register --> Login
            }
        });

        backToMenu.setOnClickListener(new View.OnClickListener() { // Reset Button
            // DeleteAccountGBO ----> SettingsGBO
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsInactiveActivity.this, MainMenuInactiveActivity.class)); // Register --> Login
            }
        });
    }
}
