package com.example.salongtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.salongtest.adapters.SongAdapter;
import com.example.salongtest.interfaces.postService;
import com.example.salongtest.models.Itunes;
import com.example.salongtest.models.Song;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private ImageView playplause;
    private RecyclerView recyclerSongs;
    private EditText search;
    private TextView txtCurrent,txtTotal,trackPlaying,artistPlaying,albumPlaying;
    private SeekBar playerSeekbar;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private ImageView stop;
    private ImageView currentCover;
    private LinearLayout footer;
    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        setContentView(R.layout.activity_main);
        recyclerSongs = findViewById(R.id.songsRecycler);
        recyclerSongs.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        search = findViewById(R.id.txtSearch);
        txtTotal = findViewById(R.id.txtTotalDuration);
        txtCurrent = findViewById(R.id.txtCurrentTime);
        playplause = findViewById(R.id.imgPlayPause);
        playerSeekbar = findViewById(R.id.playerSeekbar);
        trackPlaying = findViewById(R.id.trackPlaying);
        artistPlaying = findViewById(R.id.artistPlaying);
        albumPlaying = findViewById(R.id.albumPlaying);
        footer = findViewById(R.id.footer);
        currentCover = findViewById(R.id.imgCurrentSong);
        stop = findViewById(R.id.imgStop);
        mediaPlayer = new MediaPlayer();
        playerSeekbar.setMax(100);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getSongs(search.getText().toString());
            }
        });
    }



    public void getSongs(String searchTerm){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://itunes.apple.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        postService service = retrofit.create(postService.class);

        Call<Itunes> call = service.getSearchResults(searchTerm,postService.ENTITY_TYPE_MUSIC);
        call.enqueue(new Callback<Itunes>() {
            @Override
            public void onResponse(Call<Itunes> call, Response<Itunes> response) {
                Log.d("Response",response.message());
                final List<Song> songList = response.body().getResultModels();
                SongAdapter adapter = new SongAdapter(songList);
                adapter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        footer.setVisibility(View.VISIBLE);
                        prepareMediaPlayer(songList.get(recyclerSongs.getChildAdapterPosition(v)).getPreviewUrl());
                        trackPlaying.setText(songList.get(recyclerSongs.getChildAdapterPosition(v)).getTrackName());
                        artistPlaying.setText(songList.get(recyclerSongs.getChildAdapterPosition(v)).getArtistName());
                        albumPlaying.setText(songList.get(recyclerSongs.getChildAdapterPosition(v)).getCollectionName());
                        Glide.with(getApplicationContext())
                                .load(songList.get(recyclerSongs.getChildAdapterPosition(v)).getArtworkUrl100())
                                .into(currentCover);
                    }
                });
                recyclerSongs.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<Itunes> call, Throwable t) {


            }
        });



        playplause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    handler.removeCallbacks(updater);
                    mediaPlayer.pause();
                    playplause.setImageResource(R.drawable.ic_play);
                }else {
                    mediaPlayer.start();
                    playplause.setImageResource(R.drawable.ic_pause);
                    updateSeekBar();
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    handler.removeCallbacks(updater);
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    footer.setVisibility(View.GONE);

            }
        });

    }
    private void prepareMediaPlayer(String url){
        try {

            if(mediaPlayer.isPlaying()) {
                handler.removeCallbacks(updater);
                mediaPlayer.stop();
                mediaPlayer.reset();

            }
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
            playplause.setImageResource(R.drawable.ic_pause);
            txtTotal.setText(milliSecondsToTimer(mediaPlayer.getDuration()));
            updateSeekBar();

        }catch (Exception ex){
            Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
            long currentDuration = mediaPlayer.getCurrentPosition();
            txtCurrent.setText(milliSecondsToTimer(currentDuration));
        }
    };
    private void updateSeekBar(){
        if (mediaPlayer.isPlaying()){
            playerSeekbar.setProgress((int) (((float)mediaPlayer.getCurrentPosition()/mediaPlayer.getDuration())*100));
            handler.postDelayed(updater,1000);

        }else{
            playplause.setImageResource(R.drawable.ic_play);
        }
    }

    private String milliSecondsToTimer(long milliseconds){
         String timerString = "";
         String secondsString;
         int hours = (int) (milliseconds / (1000*60*60));
         int minutes = (int)(milliseconds %(1000*60*60))/ (1000*60);
         int seconds = (int)((milliseconds%(1000*60*60)) %(1000*60)/1000);
         if (hours> 0){
             timerString = hours+":";
         }
         if (seconds<10){
             secondsString ="0"+seconds;
         }else{
             secondsString = "" + seconds;
         }

         timerString = timerString + minutes + ":" + secondsString;
         return timerString;
    }

}
