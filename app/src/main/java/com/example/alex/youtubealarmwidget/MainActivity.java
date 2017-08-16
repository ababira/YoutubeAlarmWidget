package com.example.alex.youtubealarmwidget;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.media.AudioManager.FLAG_SHOW_UI;
import static android.media.AudioManager.STREAM_MUSIC;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import static android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
import static android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
import static com.example.alex.youtubealarmwidget.Config.YOUTUBE_API_KEY;

public class MainActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {


    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;

    private final String SEARCH_QUERY = "Pavel Stratan Dudu";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(FLAG_KEEP_SCREEN_ON);


        //TODO: remove this Handler initialization if not necessary
        handler = new Handler();


        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(YOUTUBE_API_KEY, this);


    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {

        /* TODO:
        * wasRestored - Whether the player was restored from a previously saved state, as part of the YouTubePlayerView or YouTubePlayerFragment
        * restoring its state. true usually means playback is resuming from where the user expects it would, and that a new video
        * should not be loaded.
        * */

        if (!wasRestored) {


            //TODO: slowly increase the volume in an attempt to wake the user up
            AudioManager audioManager =
                    (AudioManager)getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamVolume(STREAM_MUSIC,audioManager.getStreamMaxVolume(STREAM_MUSIC),FLAG_SHOW_UI );



            this.youTubePlayer = youTubePlayer;



            searchOnYoutube(SEARCH_QUERY);

            //youTubePlayer.loadVideo(results.get(0));
        }



    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format(getString(R.string.player_error), youTubeInitializationResult.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    /*
    TODO: check to see what happens if the user doesnt have the youtube app installed
    * */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(YOUTUBE_API_KEY, this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubeView;
    }




    private Handler handler;
    private  List<String> results;
    private YouTubePlayer youTubePlayer;

    private void searchOnYoutube(final String keywords){
        new Thread(){
            public void run(){

                initYoutubeConnection(MainActivity.this);
                 results = search(keywords);


                handler.post(new Runnable(){
                    public void run(){
                        youTubePlayer.loadVideo(results.get(0));
                    }
                });
            }
        }.start();
    }



    //TODO: move these in a separate class if need be

    private YouTube youtube;
    private YouTube.Search.List query;

    public void initYoutubeConnection(Context context) {
        youtube = new YouTube.Builder(new NetHttpTransport(),
                new JacksonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest hr) throws IOException {}
        }).setApplicationName(context.getString(R.string.app_name)).build();

        try{
            query = youtube.search().list("id,snippet");
            query.setKey(YOUTUBE_API_KEY);
            query.setType("video");
            query.setFields("items(id/videoId,snippet/title,snippet/description,snippet/thumbnails/default/url)");
        }catch(IOException e){
            Log.d("YC", "Could not initialize: "+e);
        }
    }

    public List<String> search(String keywords){
        query.setQ(keywords);
        try{
            SearchListResponse response = query.execute();
            List<SearchResult> results = response.getItems();

            List<String> items = new ArrayList<String>();
            for(SearchResult result:results){
                items.add(result.getId().getVideoId());
            }
            return items;
        }catch(IOException e){
            Log.d("YC", "Could not search: "+e);
            return null;
        }
    }


}
