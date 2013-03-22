package com.allplayers.android;

import java.util.ArrayList;
import java.util.Collection;

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
    private ArrayList<String> recipientNamesList = new ArrayList<String>();
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.selectmessagecontacts);
        
        Intent intent = getIntent();
                
        Gson gson = new Gson();
        
        if(intent.hasExtra("userData")) {
            recipientList.addAll((ArrayList<GroupMemberData>) gson.fromJson(intent.getStringExtra("userData"), ArrayList.class));     
        }
        
        for (int i = 0; i < recipientList.size(); i++) {
            recipientNamesList.set(i, recipientList.get(i).getName());
        }
       
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, recipientNamesList.toArray(new String[recipientNamesList.size()]));
        setListAdapter(adapter);
        
        final Button addRecipientButton = (Button)findViewById(R.id.addRecipientButton);
        addRecipientButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SelectMessageContacts.this, SelectUserContacts.class);
                startActivity(intent);
            }
        });
        
        final Button composeMessageButton = (Button)findViewById(R.id.composeMessageButton);
        composeMessageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Intent intent = new Intent(SelectMessageContacts.this, SelectGroupContacts.class);
                //startActivity(intent);
            }
        });
    }
    
    
}