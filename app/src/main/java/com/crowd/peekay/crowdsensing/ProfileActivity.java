package com.crowd.peekay.crowdsensing;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout mLayoutUsername, mLayoutPassword, mLayoutEmail;
    String mField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mLayoutUsername = (LinearLayout) findViewById(R.id.profile_lay_username);
        mLayoutPassword = (LinearLayout) findViewById(R.id.profile_lay_password);
        mLayoutEmail = (LinearLayout) findViewById(R.id.profile_lay_email);

        mLayoutUsername.setOnClickListener(this);
        mLayoutPassword.setOnClickListener(this);
        mLayoutEmail.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profile_lay_username:
                mField = "username";
                break;
            case R.id.profile_lay_password:
                mField = "password";
                break;
            case R.id.profile_lay_email:
                mField = "email";
                break;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);

        View v = View.inflate(ProfileActivity.this, R.layout.dialog_custom, null);
        TextView textHeader = (TextView) v.findViewById(R.id.custom_dialog_header);
        final EditText textInput = (EditText) v.findViewById(R.id.custom_dialog_text);

        textHeader.setText("Confirm Password");
        textInput.setHint("Password");
        builder.setView(v);
        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton(R.string.action_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ConfirmPasswordAsync confirmPassword = new ConfirmPasswordAsync(ProfileActivity.this,
                        textInput.getText().toString().trim(), mField);
                confirmPassword.execute();
                dialogInterface.dismiss();
            }
        });
        builder.show();
        // start-on positive buttons
    }
}
