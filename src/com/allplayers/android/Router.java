package com.allplayers.android;

import com.allplayers.objects.GroupData;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Handle routing the user and data between activities by type matching
 * and casting Intent data in one place.
 */
public class Router {

    public static final String INTENT_PREFIX = "com.allplayers.android.";

    public static final String EXTRA_GROUP = INTENT_PREFIX + "GROUP";

    private Activity mActivity;

    public Router(Activity activity) {
        mActivity = activity;
    }

    public Intent getGroupAlbumsActivityIntent(GroupData group) {
        Intent intent = new Intent(mActivity, GroupEventsActivity.class);
        intent.putExtra(EXTRA_GROUP, group);
        return intent;
    }

    public Intent getGroupEventsActivityIntent(GroupData group) {
        Intent intent = new Intent(mActivity, GroupEventsActivity.class);
        intent.putExtra(EXTRA_GROUP, group);
        return intent;
    }

    public Intent getGroupMembersActivityIntent(GroupData group) {
        Intent intent = new Intent(mActivity, GroupMembersActivity.class);
        intent.putExtra(EXTRA_GROUP, group);
        return intent;
    }

    public Intent getGroupPageActivityIntent(GroupData group) {
        Intent intent = new Intent(mActivity, GroupPageActivity.class);
        intent.putExtra(EXTRA_GROUP, group);
        return intent;
    }

    public GroupData getIntentGroup() {
        return (GroupData) mActivity.getIntent().getSerializableExtra(
                EXTRA_GROUP);
    }
}
