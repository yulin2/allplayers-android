package com.allplayers.android;

import android.app.Activity;
import android.content.Intent;

import com.allplayers.objects.AlbumData;
import com.allplayers.objects.EventData;
import com.allplayers.objects.GroupData;
import com.allplayers.objects.MessageData;
import com.allplayers.objects.MessageThreadData;
import com.allplayers.objects.PhotoData;

/**
 * Handle routing the user and data between activities by type matching
 * and casting Intent data in one place.
 */
public class Router {

    private Activity mActivity;
    
    private static final String INTENT_PREFIX = "com.allplayers.android.";
    private static final String EXTRA_ALBUM = INTENT_PREFIX + "ALBUM";
    private static final String EXTRA_EVENT = INTENT_PREFIX + "EVENT";
    private static final String EXTRA_GROUP = INTENT_PREFIX + "GROUP";
    private static final String EXTRA_MESSAGE = INTENT_PREFIX + "MESSAGE";
    private static final String EXTRA_MESSAGE_THREAD = INTENT_PREFIX + "MESSAGE_THREAD";
    private static final String EXTRA_PHOTO = INTENT_PREFIX + "PHOTO";
    private static final String EXTRA_GROUP_SEARCH_QUERY = INTENT_PREFIX + "GROUP_SEARCH_QUERY";
    private static final String EXTRA_GROUP_SEARCH_ZIPCODE = INTENT_PREFIX + "GROUP_SEARCH_ZIPCODE";
    private static final String EXTRA_GROUP_SEARCH_DISTANCE = INTENT_PREFIX + "GROUP_SEARCH_DISTANCE";

    /**
     * Constructor.
     * 
     * @param activity The current Activity.
     */
    public Router(Activity activity) {
        mActivity = activity;
    }

    /**
     * Returns an intent with attached AlbumData.
     * 
     * @param cls The class that the intent will lead to.
     * @param album The AlbumData object to be attached to the intent.
     * @return An intent with attached AlbumData.
     */
    private Intent getAlbumIntent(Class<?> cls, AlbumData album) {
        Intent intent = new Intent(mActivity, cls);
        intent.putExtra(EXTRA_ALBUM, album);
        return intent;
    }

    /**
     * Returns an intent for EventDisplayActivity with attached EventData.
     * 
     * @param event The EventData object to be attached to the intent.
     * @return An intent for EventDisplayActivity with attached EventData.
     */
    public Intent getEventDisplayActivityIntent(EventData event) {
        Intent intent = new Intent(mActivity, EventDisplayActivity.class);
        intent.putExtra(EXTRA_EVENT, event);
        return intent;
    }

    /**
     * Returns an intent for EventDetailActivity with attached EventData.
     * 
     * @param event The EventData object to be attached to the intent.
     * @return An intent for EventDetailActivity with attached EventData.
     */
    public Intent getEventDetailActivityIntent(EventData event) {
        Intent intent = new Intent(mActivity, EventDetailActivity.class);
        intent.putExtra(EXTRA_EVENT, event);
        return intent;
    }

    /**
     * Returns an intent with attached GroupData.
     * 
     * @param cls The class that the intent will lead to.
     * @param group The GroupData object to be attached to the intent.
     * @return An intent with attached GroupData.
     */
    private Intent getGroupIntent(Class<?> cls, GroupData group) {
        Intent intent = new Intent(mActivity, cls);
        intent.putExtra(EXTRA_GROUP, group);
        return intent;
    }

    /**
     * Returns an intent with attached MessageData.
     * 
     * @param cls The class that the intent will lead to.
     * @param message The MessageData object to be attached to the intent.
     * @return An intent with attached MessageData.
     */
    private Intent getMessageIntent(Class<?> cls, MessageData message) {
        Intent intent = new Intent(mActivity, cls);
        intent.putExtra(EXTRA_MESSAGE, message);
        return intent;
    }

    /**
     * Returns an intent for EventDetailActivity with attached EventData.
     * 
     * @param album The EventData object to be attached to the intent.
     * @return An intent for EventDetailActivity with attached EventData.
     */
    public Intent getAlbumPhotosActivityIntent(AlbumData album) {
        return getAlbumIntent(AlbumPhotosActivity.class, album);
    }

    /**
     * Returns an intent for GroupAlbumsActivity with attached GroupData.
     * 
     * @param group The GroupData object to be attached to the intent.
     * @return An intent for GroupAlbumsActivity with attached GroupData.
     */
    public Intent getGroupAlbumsActivityIntent(GroupData group) {
        return getGroupIntent(GroupAlbumsActivity.class, group);
    }

    /**
     * Returns an intent for GroupLocationActivity with attached GroupData.
     * 
     * @param group The GroupData object to be attached to the intent.
     * @return An intent for GroupLocationActivity with attached GroupData.
     */
    public Intent getGroupLocationActivityIntent(GroupData group) {
        return getGroupIntent(GroupLocationActivity.class, group);
    }

    /**
     * Returns an intent for GroupEventsActivity with attached GroupData.
     * 
     * @param group The GroupData object to be attached to the intent.
     * @return An intent for GroupEventsActivity with attached GroupData.
     */
    public Intent getGroupEventsActivityIntent(GroupData group) {
        return getGroupIntent(GroupEventsActivity.class, group);
    }

    /**
     * Returns an intent for GroupMembersActivity with attached GroupData.
     * 
     * @param group The GroupData object to be attached to the intent.
     * @return An intent for GroupMembersActivity with attached GroupData.
     */
    public Intent getGroupMembersActivityIntent(GroupData group) {
        return getGroupIntent(GroupMembersActivity.class, group);
    }

    /**
     * Returns an intent for GroupPageActivity with attached GroupData.
     * 
     * @param group The GroupData object to be attached to the intent.
     * @return An intent for GroupPageActivity with attached GroupData.
     */
    public Intent getGroupPageActivityIntent(GroupData group) {
        return getGroupIntent(GroupPageActivity.class, group);
    }

    /**
     * Returns an intent for MessageReply with attached MessageData.
     * 
     * @param message The MessageData object to be attached to the intent.
     * @return An intent for MessageReply with attached MessageData.
     */
    public Intent getMessagReplyIntent(MessageData message) {
        return getMessageIntent(MessageReply.class, message);
    }

    /**
     * Returns an intent for MessageThread with attached MessageData.
     * 
     * @param message The MessageData object to be attached to the intent.
     * @return An intent for MessageThread with attached MessageData.
     */
    public Intent getMessageThreadIntent(MessageData message) {
        return getMessageIntent(MessageThread.class, message);
    }

    /**
     * Returns an intent for MessageViewSingle with attached MessageData and ThreadData.
     * 
     * @param message The MessageData object to be attached to the intent.
     * @param thread The MessageThreadData object to be attached to the intent.
     * @return An intent for MessageViewSingle with attached MessageData and ThreadData.
     */
    public Intent getMessageViewSingleIntent(MessageData message, MessageThreadData thread) {
        Intent intent = getMessageIntent(MessageViewSingle.class, message);
        intent.putExtra(EXTRA_MESSAGE_THREAD, thread);
        return intent;
    }

    /**
     * Returns an intent for PhotoPager with attached PhotoData.
     * 
     * @param photo The PhotoData object to be attached to the intent.
     * @return An intent for PhotoPager with attached PhotoData.
     */
    public Intent getPhotoPagerActivityIntent(PhotoData photo) {
        Intent intent = new Intent(mActivity, PhotoPager.class);
        intent.putExtra(EXTRA_PHOTO, photo);
        return intent;
    }

    /**
     * Returns an intent for SearchGroupsListActivity with attached query, zipcode, and distance.
     * 
     * @param query The query to be attached to the intent.
     * @param zipcode The zip code to be attached to the intent.
     * @param zipcode The distance to be attached to the intent.
     * @return An intent for SearchGroupsListActivity with attached query, zipcode, and distance.
     */
    public Intent getSearchGroupsListActivityIntent(String query, int zipcode, int distance) {
        Intent intent = new Intent(mActivity, SearchGroupsListActivity.class);
        intent.putExtra(EXTRA_GROUP_SEARCH_QUERY, query);
        intent.putExtra(EXTRA_GROUP_SEARCH_ZIPCODE, zipcode);
        intent.putExtra(EXTRA_GROUP_SEARCH_DISTANCE, distance);
        return intent;
    }

    /**
     * Returns AlbumData from the current activitie's intent.
     * 
     * @return AlbumData from the current activitie's intent.
     */
    public AlbumData getIntentAlbum() {
        return (AlbumData) mActivity.getIntent().getSerializableExtra(EXTRA_ALBUM);
    }

    /**
     * Returns EventData from the current activitie's intent.
     * 
     * @return EventData from the current activitie's intent.
     */
    public EventData getIntentEvent() {
        return (EventData) mActivity.getIntent().getSerializableExtra(EXTRA_EVENT);
    }

    /**
     * Returns GroupData from the current activitie's intent.
     * 
     * @return GroupData from the current activitie's intent.
     */
    public GroupData getIntentGroup() {
        return (GroupData) mActivity.getIntent().getSerializableExtra(EXTRA_GROUP);
    }

    /**
     * Returns MessageData from the current activitie's intent.
     * 
     * @return MessageData from the current activitie's intent.
     */
    public MessageData getIntentMessage() {
        return (MessageData) mActivity.getIntent().getSerializableExtra(EXTRA_MESSAGE);
    }

    /**
     * Returns MessageThreadData from the current activitie's intent.
     * 
     * @return MessageThreadData from the current activitie's intent.
     */
    public MessageThreadData getIntentMessageThread() {
        return (MessageThreadData) mActivity.getIntent().getSerializableExtra(EXTRA_MESSAGE_THREAD);
    }

    /**
     * Returns PhotoData from the current activitie's intent.
     * 
     * @return PhotoData from the current activitie's intent.
     */
    public PhotoData getIntentPhoto() {
        return (PhotoData) mActivity.getIntent().getSerializableExtra(EXTRA_PHOTO);
    }

    /**
     * Returns The search query from the current activitie's intent.
     * 
     * @return The search query from the current activitie's intent.
     */
    public String getIntentSearchQuery() {
        return mActivity.getIntent().getExtras().getString(EXTRA_GROUP_SEARCH_QUERY);
    }

    /**
     * Returns The search zip code from the current activitie's intent.
     * 
     * @return The search zip code from the current activitie's intent.
     */
    public int getIntentSearchZipcode() {
        return mActivity.getIntent().getExtras().getInt(EXTRA_GROUP_SEARCH_ZIPCODE);
    }

    /**
     * Returns The search distance from the current activitie's intent.
     * 
     * @return The search distance from the current activitie's intent.
     */
    public int getIntentSearchDistance() {
        return mActivity.getIntent().getExtras().getInt(EXTRA_GROUP_SEARCH_DISTANCE);
    }
}
