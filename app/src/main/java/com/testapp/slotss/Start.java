package com.testapp.slotss;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.onesignal.OneSignal;

public class Start extends Activity {
    Button startBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);
        startBtn = findViewById(R.id.btnStart);
        OneSignal.initWithContext(this);
        OneSignal.setAppId("03d44663-4ac3-4603-b120-4fd040c173f7");
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity();
            }
        });
    }
    public void openActivity() {
        Intent i = new Intent(this, SlotActivity.class);
        startActivity(i);
    }
}
