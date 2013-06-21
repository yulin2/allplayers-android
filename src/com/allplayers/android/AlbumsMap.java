package com.allplayers.android;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.allplayers.objects.AlbumData;
import com.google.gson.Gson;

/**
 * Custom ArrayList used to hold AlbumDal
 */
public class AlbumsMap {
    private ArrayList<AlbumData> albums = new ArrayList<AlbumData>();

    /**
     * Constructor.
     * 
     * @param jsonResult Result from API call. Used directly to populate the ArrayList.
     */
    public AlbumsMap(String jsonResult) {

        Gson gson = new Gson();
        
        // Go through the albums in the API result and check if they are already in the list. If
        // not, add them.
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

    /**
     * Returns an ArrayList of the API result.
     *
     * @return An ArrayList of the API result.
     */
    public ArrayList<AlbumData> getAlbumData() {
        return albums;
    }

    /**
     * Returns the number of photos in the album.
     * 
     * @return The number of photos in the album.
     */
    public int size() {
        return albums.size();
    }
}
