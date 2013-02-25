package com.allplayers.android;

import com.allplayers.android.fragments.GroupsActivityFragment;
import com.allplayers.objects.GroupData;
import com.allplayers.rest.RestApiV1;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class GroupsActivity extends ListActivity {

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GroupsActivityFragment frag = GroupsActivityFragment.newInstance();
        
	}
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            startActivity(new Intent(GroupsActivity.this, FindGroupsActivity.class));
        }

        return super.onKeyUp(keyCode, event);
    }
}