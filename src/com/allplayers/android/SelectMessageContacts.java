package com.allplayers.android;

import java.util.ArrayList;

import com.allplayers.objects.GroupMemberData;
import com.google.gson.Gson;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

/**
 * 
 */
public class SelectMessageContacts extends ListActivity {

    private ArrayList<GroupMemberData> recipientList = new ArrayList<GroupMemberData>();
    
    private Button addRecipientButton;
    private Button composeMessageButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_message_contacts);
        Intent intent = getIntent();
        Gson gson = new Gson();
        if(intent.hasExtra("selected user")) {
        recipientList.add(gson.fromJson(intent.getStringExtra("selected user"),
                GroupMemberData.class));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, recipientList.toArray(new String[recipientList
                        .size()]));
        setListAdapter(adapter);
        
        addRecipientButton = (Button)findViewById(R.id.addRecipientButton);
        composeMessageButton = (Button)findViewById(R.id.composeMessageButton);
        addRecipientButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent(SelectMessageContacts.this, SelectUserContacts.class);
                startActivity(intent);
            }
        });
        
        composeMessageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                
            }
        });
    }
    
    
}