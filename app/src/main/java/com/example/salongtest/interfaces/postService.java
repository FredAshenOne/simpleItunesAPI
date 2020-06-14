package com.example.salongtest.interfaces;

import com.example.salongtest.models.Itunes;
import com.example.salongtest.models.Song;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface postService {
    String ENTITY_TYPE_MUSIC = "music";

    @GET("search")
    Call<Itunes> getSearchResults(
            @Query("term") CharSequence searchTerm,
            @Query("media") String media
    );
}
