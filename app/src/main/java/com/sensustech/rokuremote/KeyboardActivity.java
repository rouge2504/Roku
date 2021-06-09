package com.sensustech.rokuremote;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.sensustech.rokuremote.Utils.AppPreferences;
import com.sensustech.rokuremote.Utils.RokuControl;

public class KeyboardActivity extends AppCompatActivity {

    private EditText et_text;
    private ImageButton btn_close;
    private ImageButton btn_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);

        et_text = findViewById(R.id.et_text);
        btn_close = findViewById(R.id.btn_close);
        btn_send = findViewById(R.id.btn_send);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        et_text.addTextChangedListener (new TextWatcher() {
            private int previousLength;
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousLength = s.length();
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String letter = s.subSequence(start, start + count).toString();
                keyPress(letter);
            }

            public void afterTextChanged(Editable s) {
                boolean backSpace = previousLength > s.length();
                if (backSpace) {
                    buttonPress("Backspace");
                }
            }
        });

        et_text.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    buttonPress("Select");
                    return true;
                }
                return false;
            }
        });
    }

    public void buttonPress(String buttonCode) {
        String deviceIp = AppPreferences.getInstance(this).getString("deviceIp");
        if(deviceIp != null && deviceIp.length() > 0){
            RokuControl.getInstance().sendCommandPressHTTP(deviceIp,buttonCode);
        }
    }


    public void keyPress(String buttonCode) {
        String deviceIp = AppPreferences.getInstance(this).getString("deviceIp");
        if(deviceIp != null && deviceIp.length() > 0){
            RokuControl.getInstance().sendCommandLitHTTP(deviceIp,buttonCode);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}