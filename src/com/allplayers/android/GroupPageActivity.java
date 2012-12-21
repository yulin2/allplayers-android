package com.allplayers.android;

import com.allplayers.objects.GroupData;
import com.allplayers.rest.RestApiV1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GroupPageActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grouppage);

        final GroupData group = (new Router(this)).getIntentGroup();
        String title = group.getTitle();
        String desc = group.getDescription();
        String logoURL = group.getLogo();
        String uuid = group.getUUID();

        boolean isMember = isMember(uuid);

        Bitmap logo = RestApiV1.getRemoteImage(logoURL);

        ImageView imView = (ImageView)findViewById(R.id.groupLogo);
        imView.setImageBitmap(logo);

        TextView groupInfo = (TextView)findViewById(R.id.groupDetails);
        groupInfo.setText("Title: " + title + "\n\nDescription: " + desc);

        final Button groupMembersButton = (Button)findViewById(R.id.groupMembersButton);
        groupMembersButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = (new Router(GroupPageActivity.this)).getGroupMembersActivityIntent(group);
                startActivity(intent);
            }
        });

        final Button groupEventsButton = (Button)findViewById(R.id.groupEventsButton);
        groupEventsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = (new Router(GroupPageActivity.this)).getGroupEventsActivityIntent(group);
                startActivity(intent);
            }
        });

        final Button groupPhotosButton = (Button)findViewById(R.id.groupPhotosButton);
        groupPhotosButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = (new Router(GroupPageActivity.this)).getGroupAlbumsActivityIntent(group);
                startActivity(intent);
            }
        });

        if (isMember) {
            groupMembersButton.setVisibility(View.VISIBLE);
            groupEventsButton.setVisibility(View.VISIBLE);
            groupPhotosButton.setVisibility(View.VISIBLE);
        } else {
            groupMembersButton.setVisibility(View.GONE);
            groupEventsButton.setVisibility(View.GONE);
            groupPhotosButton.setVisibility(View.GONE);
        }
    }

    private boolean isMember(String group_uuid) {
        String jsonResult = RestApiV1.getGroupMembersByGroupId(group_uuid);

        //If a result is not returned, the user is not an authenticated group member
        if (jsonResult.trim().equals("null") || jsonResult.trim().equals("error") ||
                jsonResult.equals("You are not logged in")) {
            return false;
        }

        return true;
    }
}