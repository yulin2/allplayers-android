package com.allplayers.android;

import com.allplayers.objects.AlbumData;
import com.allplayers.objects.GroupData;
import com.allplayers.rest.RestApiV1;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class PhotosActivity  extends ListActivity {
    private ArrayList<AlbumData> albumList = new ArrayList<AlbumData>();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String jsonResult;

        if (LocalStorage.getTimeSinceLastModification("UserAlbums") / 1000 / 60 < 60) { //more recent than 60 minutes
            AlbumsMap albums;
            ArrayList<AlbumData> newAlbumList;

            String storedAlbums = LocalStorage.readUserAlbums(getBaseContext());

            String[] storedAlbumsList = storedAlbums.split("\n");

            for (int i = 0; i < storedAlbumsList.length; i++) {
                albums = new AlbumsMap(storedAlbumsList[i]);
                newAlbumList = albums.getAlbumData();

                if (!newAlbumList.isEmpty()) {
                    for (int j = 0; j < newAlbumList.size(); j++) {
                        albumList.add(newAlbumList.get(j));
                    }
                }
            }
        } else {
            //check local storage
            if (LocalStorage.getTimeSinceLastModification("UserGroups") / 1000 / 60 < 60) { //more recent than 60 minutes
                jsonResult = LocalStorage.readUserGroups(getBaseContext());
            } else {
                jsonResult = RestApiV1.getUserGroups();
                LocalStorage.writeUserGroups(getBaseContext(), jsonResult, false);
            }

            GroupsMap groups = new GroupsMap(jsonResult);
            ArrayList<GroupData> groupList = groups.getGroupData();

            if (!groupList.isEmpty()) {
                String group_uuid;
                AlbumsMap albums;
                ArrayList<AlbumData> newAlbumList;

                LocalStorage.writeUserAlbums(getBaseContext(), "", true);

                for (int i = 0; i < groupList.size(); i++) {
                    group_uuid = groupList.get(i).getUUID();
                    jsonResult = RestApiV1.getGroupAlbumsByGroupId(group_uuid);
                    LocalStorage.appendUserAlbums(getBaseContext(), jsonResult);
                    albums = new AlbumsMap(jsonResult);
                    newAlbumList = albums.getAlbumData();

                    if (!newAlbumList.isEmpty()) {
                        for (int j = 0; j < newAlbumList.size(); j++) {
                            albumList.add(newAlbumList.get(j));
                        }
                    }
                }
            }
        }

        if (albumList.isEmpty()) {
            String[] values = new String[] {"no albums to display"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, values);
            setListAdapter(adapter);
        } else {
            //Create a customized ArrayAdapter
            AlbumAdapter adapter = new AlbumAdapter(getApplicationContext(), R.layout.albumlistitem, albumList);
            setListAdapter(adapter);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (!albumList.isEmpty()) {
            // Display the photos for the selected album
            Intent intent = (new Router(this)).getAlbumPhotosActivityIntent(albumList.get(position));
            startActivity(intent);
        }
    }
}