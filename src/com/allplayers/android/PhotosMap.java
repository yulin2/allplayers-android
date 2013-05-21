package com.allplayers.android;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.allplayers.objects.PhotoData;
import com.google.gson.Gson;

public class PhotosMap {
    private ArrayList<PhotoData> photos = new ArrayList<PhotoData>();

    public PhotosMap(String jsonResult) {
        if (!jsonResult.equals("false")) {
            try {
                JSONArray jsonArray = new JSONArray(jsonResult);

                if (jsonArray.length() > 0) {
                    int numPhotos = 0;

                    // Used to create PhotoData objects from json.
                    Gson gson = new Gson();
                    for (int i = 0; i < jsonResult.length(); i++) {
                        PhotoData photo = gson.fromJson(jsonArray.getString(i), PhotoData.class);

                        if (photo.getPhotoFull() != null && !photo.getPhotoFull().trim().equals("")) {
                            if (photo.isNew(photos)) {
                                if (numPhotos > 0) {
                                    photo.setPreviousPhoto(photos.get(numPhotos - 1));
                                    photos.get(numPhotos - 1).setNextPhoto(photo);
                                }

                                photos.add(photo);
                                numPhotos++;
                            }
                        }
                    }
                }
            } catch (JSONException ex) {
                System.err.println("PhotosMap/" + ex);
            }
        }
    }

    public ArrayList<PhotoData> getPhotoData() {
        return photos;
    }
}