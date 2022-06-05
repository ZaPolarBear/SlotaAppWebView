package com.testapp.slotss;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class SlotActivity extends AppCompatActivity {

    private TextView msg;
    private ImageView img1, img2, img3;
    private Reel reel1, reel2, reel3;
    private Button btn, newgame, one, two, ten, sounds;
    private TextView curr_bet;
    private TextView curr_balance;
    private boolean isStarted, isPlaying;

    private MediaPlayer mp;
    private boolean isBetSet = false;
    private int dollarBet= 0;
    private int currentBalance = 1000;


    private SharedPreferences mSharedPrefs;

    public static final Random RANDOM = new Random();

    public static long randomLong(long lower, long upper) {
        return lower + (long) (RANDOM.nextDouble() * (upper - lower));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot);
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        btn = findViewById(R.id.btn);
        one = findViewById(R.id.one);
        two = findViewById(R.id.two);
        ten = findViewById(R.id.ten);
        sounds = findViewById(R.id.sound);
        newgame = findViewById(R.id.newgame);
        msg = findViewById(R.id.msg);
        curr_bet = findViewById(R.id.current_bet);
        curr_balance = findViewById(R.id.current_balance);
        isPlaying = true;

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mp.reset();
                mp.release();
                mp=null;
            }
        });

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean easyMode = mSharedPrefs.getBoolean("difficult",false);


        currentBalance = mSharedPrefs.getInt("balance",currentBalance);
        curr_balance.setText("Current Balance: $"+currentBalance);

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dollarBet += 1;
                curr_bet.setText("Current Bet: $"+ dollarBet);
                isBetSet = true;
            }
        });



        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dollarBet += 2;
                curr_bet.setText("Current Bet: $"+ dollarBet);
                isBetSet = true;
            }
        });


        ten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dollarBet += 10;
                curr_bet.setText("Current Bet: $"+ dollarBet);
                isBetSet = true;
            }
        });



        newgame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dollarBet = 0;
                currentBalance = mSharedPrefs.getInt("balance",currentBalance);
                curr_balance.setText("Current Balance: $"+currentBalance);
                curr_bet.setText("Current Bet: $"+ dollarBet);
                isBetSet = false;
            }
        });


        sounds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying){
                    Toast.makeText(SlotActivity.this, "Sound is off", Toast.LENGTH_SHORT).show();
                    isPlaying = false;
                }
                else{
                    Toast.makeText(SlotActivity.this, "Sound is on", Toast.LENGTH_SHORT).show();
                    isPlaying = true;
                }
            }
        });

        btn.setOnClickListener(view -> {
            //check if a bet is set
            // mp = MediaPlayer.create(getApplicationContext(), "");
            mp.start();
            if(isBetSet && dollarBet<=currentBalance) {
                //if btn pressed while reels are running
                //stop and display results
                if (isStarted) {
                    reel1.stopReel();
                    reel2.stopReel();
                    reel3.stopReel();


                    if (reel1.currentIndex == reel2.currentIndex && reel2.currentIndex == reel3.currentIndex) {
                        //JACKPOT
                        msg.setText("+$"+(dollarBet * 3));
                        currentBalance = currentBalance + (dollarBet * 3);
                        curr_balance.setText("Current Balance: $" + currentBalance);
                        Toast.makeText(SlotActivity.this, "JACKPOT!!!", Toast.LENGTH_LONG).show();
                    } else if (reel1.currentIndex == reel2.currentIndex || reel2.currentIndex == reel3.currentIndex
                            || reel1.currentIndex == reel3.currentIndex) {
                        //TWO HITS
                        msg.setText("+$"+(dollarBet ));
                        currentBalance = currentBalance + dollarBet;
                        curr_balance.setText("Current Balance: $" + currentBalance);
                        Toast.makeText(SlotActivity.this, "SMALL WIN!", Toast.LENGTH_LONG).show();
                    } else {
                        //LOSS
                        msg.setText("-$"+ (dollarBet));
                        currentBalance = currentBalance - dollarBet;
                        curr_balance.setText("Current Balance: $" + currentBalance);
                        Toast.makeText(SlotActivity.this, "TRY AGAIN!", Toast.LENGTH_LONG).show();
                    }


                    //save balance
                    SharedPreferences.Editor editor = mSharedPrefs.edit();
                    editor.putInt("balance",currentBalance);
                    editor.commit();

                    btn.setText("Start");
                    isStarted = false;


                } else { //else make reels run

                    //stop the mashing
                    btn.setEnabled(false);
                    Timer buttonTimer = new Timer();
                    buttonTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(() -> btn.setEnabled(true));
                        }
                    }, 3000); //3 sec delay

                if(!easyMode){
                    reel1 = new Reel(img -> runOnUiThread(() ->
                            img1.setImageResource(img)),
                            200, randomLong(0, 200));

                    reel1.start();

                    reel2 = new Reel(img -> runOnUiThread(() ->
                            img2.setImageResource(img)),
                            200, randomLong(150, 400));

                    reel2.start();

                    reel3 = new Reel(img -> runOnUiThread(() ->
                            img3.setImageResource(img)),
                            200, randomLong(300, 800));

                    reel3.start();
                } else{
                    //Log.d("DIFFICULTY","easy mode spin");
                    reel1 = new Reel(img -> runOnUiThread(() ->
                            img1.setImageResource(img)),
                            200, 10);

                    reel1.start();

                    reel2 = new Reel(img -> runOnUiThread(() ->
                            img2.setImageResource(img)),
                            200, 10);

                    reel2.start();

                    reel3 = new Reel(img -> runOnUiThread(() ->
                            img3.setImageResource(img)),
                            200, randomLong(0, 10));

                    reel3.start();
                }

                    btn.setText("Stop");
                    msg.setText("");
                    isStarted = true;
                }
            }
            else{ //notify to set a bet or balance too low
                if (dollarBet>=currentBalance){
                    Toast.makeText(SlotActivity.this, "Balance is too low!", Toast.LENGTH_SHORT).show();
                    msg.setText("");
                    curr_bet.setText("Current Bet: $0");
                }
                else{
                    Toast.makeText(SlotActivity.this, "Set a bet!", Toast.LENGTH_SHORT).show();
                    msg.setText("");
                }
            }
        });
    }


}