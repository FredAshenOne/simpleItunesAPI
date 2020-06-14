package com.example.salongtest.adapters;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.salongtest.R;
import com.example.salongtest.models.Song;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolderSong> implements View.OnClickListener {

    List<Song> songsList;
    private View.OnClickListener listener;
    public SongAdapter(List<Song> songsList) {
        this.songsList = songsList;
    }

    @NonNull
    @Override
    public ViewHolderSong onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_list,parent,false);
        view.setOnClickListener(this);
        return new ViewHolderSong(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderSong holder, int position) {
            holder.trackName.setText(songsList.get(position).getTrackName());
            holder.albumName.setText(songsList.get(position).getCollectionName());
            holder.artistName.setText(songsList.get(position).getArtistName());
        Glide.with(holder.albumCover.getContext())
                .load(songsList.get(position).getArtworkUrl100())
                .into(holder.albumCover);
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (listener!=null){
            listener.onClick(v);
        }
    }


    public class ViewHolderSong extends RecyclerView.ViewHolder {
        TextView  trackName,artistName,albumName;
        ImageView albumCover;

        public ViewHolderSong(@NonNull View itemView) {
            super(itemView);
            trackName = itemView.findViewById(R.id.idTrackName);
            artistName = itemView.findViewById(R.id.idArtist);
            albumName = itemView.findViewById(R.id.Album);
            albumCover = itemView.findViewById(R.id.idCover);
        }
    }
}
