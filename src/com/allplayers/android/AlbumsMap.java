package com.allplayers.android;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.allplayers.objects.AlbumData;
import com.google.gson.Gson;

public class AlbumsMap {
    private ArrayList<AlbumData> albums = new ArrayList<AlbumData>();

    public AlbumsMap(String jsonResult) {

        // Used to create AlbumData objects from json.
        Gson gson = new Gson();
        try {
            JSONArray jsonArray = new JSONArray(jsonResult);

            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    AlbumData album = gson.fromJson(jsonArray.getString(i), AlbumData.class);

                    if (album.isNew(albums)) {
                        albums.add(album);
                    }
                }
            }
        } catch (JSONException ex) {
            System.err.println("AlbumsMap/" + ex);
        }
    }

    public ArrayList<AlbumData> getAlbumData() {
        return albums;
    }

    public int size() {
        return albums.size();
    }
}