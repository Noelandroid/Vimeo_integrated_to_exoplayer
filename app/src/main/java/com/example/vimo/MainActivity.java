package com.example.vimo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import uk.breedrapps.vimeoextractor.OnVimeoExtractionListener;
import uk.breedrapps.vimeoextractor.VimeoExtractor;
import uk.breedrapps.vimeoextractor.VimeoVideo;

public class MainActivity extends AppCompatActivity {
   // VideoView videoView;

    PlayerView playerView;
    ProgressBar progressBar;
    ImageView btfullscreen,btquality;
    SimpleExoPlayer simpleExoPlayer;

    boolean flag=false;

    public  static  final  String VIMEO_URL="https://player.vimeo.com/video/437729786";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mains);
        //videoView=findViewById(R.id.videoView);

        playerView=findViewById(R.id.player_view);
        progressBar=findViewById(R.id.progress_bar);
        btfullscreen=findViewById(R.id.bt_fullscreen);



        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        initialize();


    }

    private void initialize() {

        VimeoExtractor.getInstance().fetchVideoWithURL(VIMEO_URL, null, new OnVimeoExtractionListener() {
            @Override
            public void onSuccess(VimeoVideo video) {
                String hdStream = video.getStreams().get("480p");
                System.out.println("VIMEO VIDEO STREAM" + hdStream);
                if (hdStream != null) {
                    playVideo(hdStream);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }

    private void playVideo(final String hdStream) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
               /* final VideoView videoView = findViewById(R.id.videoView);
                final MediaController mediacontroller = new MediaController(MainActivity.this);
                mediacontroller.setAnchorView(videoView);
                videoView.setMediaController(mediacontroller);

                videoView.setBackgroundColor(Color.TRANSPARENT);
                Uri video = Uri.parse(hdStream);
                videoView.setVideoURI(video);
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        videoView.requestFocus();
                        videoView.start();
                    }
                });
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                        System.out.println("Video Finish");
                        finish();
                    }
                });*/




                Uri videouri= Uri.parse(hdStream);

                LoadControl loadControl=new DefaultLoadControl();

                BandwidthMeter bandwidthMeter=new DefaultBandwidthMeter();

                TrackSelector trackSelector= new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));




                simpleExoPlayer= ExoPlayerFactory.newSimpleInstance(MainActivity.this,trackSelector,loadControl);

                DefaultHttpDataSourceFactory dataSourceFactory=new DefaultHttpDataSourceFactory("exoplayer_video");

                ExtractorsFactory extractorsFactory=new DefaultExtractorsFactory();



                MediaSource mediaSource=new ExtractorMediaSource(videouri,dataSourceFactory,extractorsFactory,null,null);

                playerView.setPlayer(simpleExoPlayer);




                playerView.getKeepScreenOn();
                simpleExoPlayer.prepare(mediaSource);
                simpleExoPlayer.setPlayWhenReady(true);
                simpleExoPlayer.addListener(new Player.EventListener() {
                    @Override
                    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

                    }

                    @Override
                    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                    }

                    @Override
                    public void onLoadingChanged(boolean isLoading) {

                    }

                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {


                        if (playbackState==Player.STATE_BUFFERING){

                            progressBar.setVisibility(View.VISIBLE);

                        }else  if (playbackState==Player.STATE_READY){
                            progressBar.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onRepeatModeChanged(int repeatMode) {

                    }

                    @Override
                    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                    }

                    @Override
                    public void onPlayerError(ExoPlaybackException error) {

                    }

                    @Override
                    public void onPositionDiscontinuity(int reason) {

                    }

                    @Override
                    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                    }

                    @Override
                    public void onSeekProcessed() {

                    }
                });


                btfullscreen.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("SourceLockedOrientationActivity")
                    @Override
                    public void onClick(View view) {
                        if (flag){

                            btfullscreen.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen));

                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                            flag=false;
                        } else {
                            btfullscreen.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen_exit));

                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                            flag=true;
                        }
                    }
                });
            }
        });




    }


}

