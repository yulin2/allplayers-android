package com.allplayers.android;

import com.allplayers.objects.AlbumData;
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

    public static final String EXTRA_ALBUM = INTENT_PREFIX + "ALBUM";

    public static final String EXTRA_GROUP = INTENT_PREFIX + "GROUP";

    private Activity mActivity;

    public Router(Activity activity) {
        mActivity = activity;
    }

    private Intent getGroupIntent(Class<?> cls, GroupData group) {
        Intent intent = new Intent(mActivity, cls);
        intent.putExtra(EXTRA_GROUP, group);
        return intent;
    }

    private Intent getAlbumIntent(Class<?> cls, AlbumData album) {
        Intent intent = new Intent(mActivity, cls);
        intent.putExtra(EXTRA_ALBUM, album);
        return intent;
    }

    public Intent getAlbumPhotosActivityIntent(AlbumData album) {
        return getAlbumIntent(AlbumPhotosActivity.class, album);
    }

    public Intent getGroupAlbumsActivityIntent(GroupData group) {
        return getGroupIntent(GroupAlbumsActivity.class, group);
    }

    public Intent getGroupEventsActivityIntent(GroupData group) {
        return getGroupIntent(GroupEventsActivity.class, group);
    }

    public Intent getGroupMembersActivityIntent(GroupData group) {
        return getGroupIntent(GroupMembersActivity.class, group);
    }

    public Intent getGroupPageActivityIntent(GroupData group) {
        return getGroupIntent(GroupPageActivity.class, group);
    }

    public AlbumData getIntentAlbum() {
        return (AlbumData) mActivity.getIntent().getSerializableExtra(EXTRA_ALBUM);
    }

    public GroupData getIntentGroup() {
        return (GroupData) mActivity.getIntent().getSerializableExtra(EXTRA_GROUP);
    }
}
