package com.example.heartbox;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    TextView textView;
    Button btnNext, btnPrevious, btnPause;
    SeekBar seekBar;
    static MediaPlayer myMediaPlayer;
    int posiition;
    String sname;
    ArrayList<File> mySongs;
    Thread updateSeekBar;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        btnPrevious = (Button) findViewById(R.id.prev);
        btnNext = (Button) findViewById(R.id.next);
        btnPause = (Button) findViewById(R.id.pause);
        textView = (TextView) findViewById(R.id.songView);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        updateSeekBar = new Thread() {
            @Override
            public void run() {
                int totalSuration = myMediaPlayer.getDuration();
                int currentPosition = 0;

                while (currentPosition < totalSuration) {
                    try {
                        sleep(500);
                        currentPosition = myMediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                    } catch (InterruptedException ex) {

                        ex.printStackTrace();
                    }
                }
            }
        };

        if (myMediaPlayer != null) {
            myMediaPlayer.stop();
            myMediaPlayer.release();
        }

        Intent i = getIntent();
        Bundle b = i.getExtras();
        mySongs = (ArrayList) b.getParcelableArrayList("songs");
        sname = mySongs.get(posiition).getName().toString();
        final String songName = i.getStringExtra("songname");

        textView.setText(songName);
        textView.setSelected(true);
        posiition = b.getInt("pos", 0);
        Uri u = Uri.parse(mySongs.get(posiition).toString());
        myMediaPlayer = MediaPlayer.create(getApplicationContext(), u);
        myMediaPlayer.start();
        seekBar.setMax(myMediaPlayer.getDuration());
        updateSeekBar.start();

        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);

        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myMediaPlayer.seekTo(seekBar.getProgress());
            }
        });


        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekBar.setMax(myMediaPlayer.getDuration());
                if (myMediaPlayer.isPlaying()) {
                    btnPause.setBackgroundResource(R.drawable.icon_play);
                    myMediaPlayer.pause();
                } else {
                    btnPause.setBackgroundResource(R.drawable.icon_pause);
                    myMediaPlayer.start();
                }
            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myMediaPlayer.stop();
                myMediaPlayer.release();
                posiition = ((posiition + 1) % mySongs.size());
                Uri u = Uri.parse(mySongs.get(posiition).toString());
                myMediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                sname = mySongs.get(posiition).toString();
                textView.setText(sname);
                myMediaPlayer.start();
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myMediaPlayer.stop();
                myMediaPlayer.release();
                posiition = ((posiition - 1) < 0) ? (mySongs.size() - 1) : (posiition - 1);
                Uri u = Uri.parse(mySongs.get(posiition).toString());
                myMediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                sname = mySongs.get(posiition).toString();
                textView.setText(sname);
                myMediaPlayer.start();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();


        return super.onOptionsItemSelected(item);
    }
}
