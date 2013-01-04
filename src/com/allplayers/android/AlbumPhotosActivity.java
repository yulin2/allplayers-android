package com.allplayers.android;
 
import com.allplayers.rest.RestApiV1;
import com.allplayers.objects.AlbumData;
import com.allplayers.objects.PhotoData;
 
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
 
import java.util.ArrayList;
 
import org.json.JSONException;
import org.json.JSONObject;
 
public class AlbumPhotosActivity extends ListActivity {
    private ArrayList<PhotoData> photoList;
 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlbumData album = (new Router(this)).getIntentAlbum();
        AlbumDataTask helper = new AlbumDataTask();
        helper.execute(album);
    }
 
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
 
        if (!photoList.isEmpty()) {
            // Display the group page for the selected group
            Intent intent = (new Router(this)).getPhotoDisplayActivityIntent(photoList.get(position));
            startActivity(intent);
        }
    }
    
    public class AlbumDataTask extends AsyncTask<AlbumData, Void, String> {
       
        protected String doInBackground(AlbumData... album) {
          return RestApiV1.getAlbumPhotosByAlbumId(album[0].getUUID());
        }
        
 		protected void onPostExecute(String jsonResult) {
 			PhotosMap photos = new PhotosMap(jsonResult);
 	        photoList = photos.getPhotoData();
 	        if (photoList.isEmpty()) {
 	            String[] values = new String[] {"no photos to display"};
 	 
 	            ArrayAdapter<String> adapter = new ArrayAdapter<String>(AlbumPhotosActivity.this,
 	                    android.R.layout.simple_list_item_1, values);
 	            setListAdapter(adapter);
 	        } else {
 	            //Create a customized ArrayAdapter
 	            PhotoAdapter adapter = new PhotoAdapter(getApplicationContext(), R.layout.photolistitem, photoList);
 	            setListAdapter(adapter);
 	        }
     	}
     }
}
