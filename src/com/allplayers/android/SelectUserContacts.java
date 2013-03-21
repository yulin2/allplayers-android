
package com.allplayers.android;

import com.allplayers.objects.GroupData;
import com.allplayers.objects.GroupMemberData;
import com.allplayers.rest.RestApiV1;
import com.google.gson.Gson;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class SelectUserContacts extends ListActivity {
    
    private ArrayList<GroupMemberData> membersList;
    private ArrayList<GroupMemberData> selectedMembers;
    private Intent selectMessageContactsIntent;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        selectedMembers = new ArrayList<GroupMemberData>();
        setContentView(R.layout.selectusercontacts);

        GetUserGroupmatesTask helper = new GetUserGroupmatesTask();
        helper.execute();
        
        final Button doneButton = (Button)findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectMessageContactsIntent = new Intent(SelectUserContacts.this, SelectMessageContacts.class);
                
                Gson gson = new Gson();
                String userData = gson.toJson(selectedMembers);
                
                selectMessageContactsIntent.putExtra("userData", userData);
                
                startActivity(selectMessageContactsIntent);
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if(selectedMembers.get(position) == null) {
            v.setBackgroundResource(R.color.android_blue);
            selectedMembers.add(membersList.get(position));
        } else {
            v.setBackgroundResource(Color.BLACK);
            selectedMembers.remove(position);
        }
    }

    /*
     * Gets a group's members using a rest call and populates an array with the
     * data.
     */
    public class GetUserGroupmatesTask extends AsyncTask<Void, Void, String> {

        protected String doInBackground(Void... args) {
            return RestApiV1.getUserGroupmates();
        }

        protected void onPostExecute(String jsonResult) {
            jsonResult = jsonResult.replace("firstname", "fname");
            jsonResult = jsonResult.replace("lastname", "lname");
            GroupMembersMap groupMembers = new GroupMembersMap(jsonResult);
            membersList = groupMembers.getGroupMemberData();
            String[] values;

            if (!membersList.isEmpty()) {
                values = new String[membersList.size()];

                for (int i = 0; i < membersList.size(); i++) {
                    values[i] = membersList.get(i).getName();
                }
            } else {
                values = new String[] {
                    "No members to display"
                };
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectUserContacts.this,
                    android.R.layout.simple_list_item_1, values);
            setListAdapter(adapter);
        }
    }
}
