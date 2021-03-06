package org.nhnnext.mediaplayerfullsizeexample;

import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import org.nhnnext.mediaplayerexample.R;

public class MediaPlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    MediaPlayer mediaPlayer;
    Button playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediaplayer);

        surfaceView = (SurfaceView) findViewById(R.id.surface);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        playButton = (Button)findViewById(R.id.play_btn);
        playButton.setOnClickListener(clickListener);

        Button stopButton = (Button) findViewById(R.id.stop_btn);
        stopButton.setOnClickListener(clickListener);
    }

    Button.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.play_btn) {
                Log.i("main", "play");
                startOrPause();
            } else if (v.getId() == R.id.stop_btn) {
                stopNprepare();
            }
        }
    };

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        } else {
            mediaPlayer.reset();
        }

        try {
            // local resource
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/too_cute");
            mediaPlayer.setDataSource(this, uri);

            // external URL
//            String path = "http://techslides.com/demos/sample-videos/small.mp4";
//            mediaPlayer.setDataSource(path);

            mediaPlayer.setDisplay(holder);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(preparedListener);
            mediaPlayer.setOnCompletionListener(completionListener);
            mediaPlayer.setOnVideoSizeChangedListener(sizeChangeListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {
            Point size = new Point();
            int videoWidth = mediaPlayer.getVideoWidth();
            int videoHeight = mediaPlayer.getVideoHeight();
            float videoProportion = (float) videoWidth / (float) videoHeight;
            getWindowManager().getDefaultDisplay().getSize(size);
            int screenWidth = size.x;
            int screenHeight = size.y;
            float screenProportion = (float) screenWidth / (float) screenHeight;
            android.view.ViewGroup.LayoutParams layoutParams = surfaceView.getLayoutParams();

            if (videoProportion > screenProportion) {
                layoutParams.width = screenWidth;
                layoutParams.height = (int) ((float) screenWidth / videoProportion);
            } else {
                layoutParams.width = (int) (videoProportion * (float) screenHeight);
                layoutParams.height = screenHeight;
            }
            surfaceView.setLayoutParams(layoutParams);
        }
    };

    MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {
            playButton.setText("Play");
        }
    };

    MediaPlayer.OnVideoSizeChangedListener sizeChangeListener = new MediaPlayer.OnVideoSizeChangedListener() {

        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        }
    };

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void startOrPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playButton.setText("Play");
        } else {
            mediaPlayer.start();
            playButton.setText("Pause");
        }
    }

    private void stopNprepare() {
        mediaPlayer.stop();
        playButton.setText("Play");


        try {
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
