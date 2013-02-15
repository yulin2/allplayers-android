package com.allplayers.android;

import com.allplayers.objects.AlbumData;
import com.allplayers.objects.DataObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class AlbumsMap {
    private ArrayList<AlbumData> albums = new ArrayList<AlbumData>();

    public AlbumsMap(String jsonResult) {
        try {
            JSONArray jsonArray = new JSONArray(jsonResult);

            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonResult.length(); i++) {
                    AlbumData album = new AlbumData(jsonArray.getString(i));

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
}