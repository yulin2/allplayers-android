
package com.allplayers.android;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.view.Window;
import com.allplayers.android.activities.AllplayersSherlockFragmentActivity;
import com.allplayers.objects.GroupData;
import com.allplayers.rest.RestApiV1;
import com.google.gson.Gson;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/** 
 * Form to create an event for a group.
 */
public class CreateEventActivity extends AllplayersSherlockFragmentActivity {
    
    // Form elements
    private EditText mEventTitleEditText;
    private EditText mEventDescriptionEditText;
    private TextView mEventStartTimeAndDateHeader;
    private static Button mEventStartTimeButton;
    private DialogFragment mEventStartTimePickerFragment;
    private static Button mEventStartDateButton;
    private DialogFragment mEventStartDatePickerFragment;
    private TextView mEventStartTimeAndDateErrorMessage;
    private TextView mEventStartTimeAndDateErrorMessage2;
    private TextView mEventEndTimeAndDateHeader;
    private DialogFragment mEventEndTimePickerFragment;
    private static Button mEventEndTimeButton;
    private DialogFragment mEventEndDatePickerFragment;
    private static Button mEventEndDateButton;
    private TextView mEventEndTimeAndDateErrorMessage;
    private TextView mEventEndTimeAndDateErrorMessage2;
    private Button mCreateEventButton;
    
    // Form responses
    private String mEventTitle;
    private String mEventDescription;
    private String mEventStartDateTime;
    private static int mEventStartHourOfDay;
    private static int mEventStartMinute;
    private static int mEventStartYear;
    private static int mEventStartMonth;
    private static int mEventStartDay;
    private String mEventEndDateTime;
    private static int mEventEndHourOfDay;
    private static int mEventEndMinute;
    private static int mEventEndYear;
    private static int mEventEndMonth;
    private static int mEventEndDay;
    
    // Asynchronous Tasks
    private CreateEventTask mCreateEventTask;
    
    // Flags
    private boolean mFormCompletionState_Error = false;
    private boolean mFormCompletionState_date_error = false;
    
    // Other Data
    private GroupData mGroup;
    
    // This
    private Activity mActivity = this;
    
    // Toast
    private Toast mToast;
    
    /** 
     * Called when the activity is starting.
     * 
     * @param savedInstanceState If the activity is being re-initialized after previously being 
     * shut down then this Bundle contains the data it most recently supplied in 
     * onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        
        // Grab the group from the intent.
        Gson gson = new Gson();
        mGroup = gson.fromJson(getIntent().getStringExtra("Current Group"), GroupData.class);
        
        // Set up the ActionBar.
        mActionBar.setTitle("Create an Event for " + mGroup.getTitle());
        mActionBar.setDisplayShowHomeEnabled(false);
        
        // Link up the form elements to their xml companions.
        mEventTitleEditText = (EditText) findViewById(R.id.event_title_edit_text);
        mEventDescriptionEditText = (EditText) findViewById(R.id.event_description_edit_text);
        mEventStartTimeAndDateHeader = (TextView) findViewById(R.id.event_start_time_and_date_header);
        mEventStartTimeButton = (Button) findViewById(R.id.event_start_time_button);
        mEventStartDateButton = (Button) findViewById(R.id.event_start_date_button);
        mEventStartTimeAndDateErrorMessage = (TextView) findViewById(R.id.event_start_time_and_date_error_message);
        mEventStartTimeAndDateErrorMessage2 = (TextView) findViewById(R.id.event_start_time_and_date_error_message_2);
        mEventEndTimeAndDateHeader = (TextView) findViewById(R.id.event_end_time_and_date_header);
        mEventEndTimeButton = (Button) findViewById(R.id.event_end_time_button);
        mEventEndDateButton = (Button) findViewById(R.id.event_end_date_button);
        mEventEndTimeAndDateErrorMessage = (TextView) findViewById(R.id.event_end_time_and_date_error_message);
        mEventEndTimeAndDateErrorMessage2 = (TextView) findViewById(R.id.event_end_time_and_date_error_message_2);
        mCreateEventButton = (Button) findViewById(R.id.done_button);
        
        // Set up listeners for the time and date picker fragment buttons.
        mEventStartTimeButton.setOnClickListener(new OnClickListener() {

            /**
             * Called when a view has been clicked.
             * 
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                mEventStartTimePickerFragment = new StartTimePickerFragment();
                mEventStartTimePickerFragment.show(getSupportFragmentManager(), "Start Time Picker");
            }
        });
        
        mEventStartDateButton.setOnClickListener(new OnClickListener() {

            /**
             * Called when a view has been clicked.
             * 
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                mEventStartDatePickerFragment = new StartDatePickerFragment();
                mEventStartDatePickerFragment.show(getSupportFragmentManager(), "Start Date Picker");
            }
            
        });
        
        mEventEndTimeButton.setOnClickListener(new OnClickListener() {

            /**
             * Called when a view has been clicked.
             * 
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                mEventEndTimePickerFragment = new EndTimePickerFragment();
                mEventEndTimePickerFragment.show(getSupportFragmentManager(), "End Time Picker");
            }
            
        });
        
        mEventEndDateButton.setOnClickListener(new OnClickListener() {

            /**
             * Called when a view has been clicked.
             * 
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                mEventEndDatePickerFragment = new EndDatePickerFragment();
                mEventEndDatePickerFragment.show(getSupportFragmentManager(), "End Date Picker");
            }
            
        });
        
        // Set up the listener for the "Create Event" (done) button.
        mCreateEventButton.setOnClickListener(new OnClickListener() {

            /** 
             * Called when a view has been clicked.
             * @param v The view that was clicked.
             */
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View v) {
                
                // Reset mFormCompletionState_Error and mFormCompletionState_date_error.
                mFormCompletionState_Error = false;
                mFormCompletionState_date_error = false;
                
                // Reset any showing error messages.
                mEventStartTimeAndDateHeader.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                mEventEndTimeAndDateHeader.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                mEventStartTimeButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                mEventStartDateButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                mEventEndTimeButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                mEventEndDateButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

                mEventStartTimeAndDateErrorMessage.setVisibility(View.GONE);
                mEventEndTimeAndDateErrorMessage.setVisibility(View.GONE);
                mEventStartTimeAndDateErrorMessage2.setVisibility(View.GONE);
                mEventEndTimeAndDateErrorMessage2.setVisibility(View.GONE);
                
                // Get the text from the Title and Description forms.
                mEventTitle = mEventTitleEditText.getText().toString();
                mEventDescription = mEventDescriptionEditText.getText().toString();
                
                // Check to make sure that the Title and Description fields have content. If not,
                // show the fields as errored and set mFormCompletionState_Error to true.
                if (mEventTitle.equals("")) {
                    mEventTitleEditText.setError("This field cannot be left blank.");
                    mFormCompletionState_Error = true;
                }
                if (mEventDescription.equals("")) {
                    mEventDescriptionEditText.setError("This field cannot be left blank.");
                    mFormCompletionState_Error = true;
                }
                
                // Make sure that the start and end date and time have been set. (if the buttons'
                // text has not been changed, we know the time has not been set).
                if (mEventStartTimeButton.getText().equals("Select Start Time")) {
                    mEventStartTimeAndDateErrorMessage2.setVisibility(View.VISIBLE);
                    mEventStartTimeButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.drawable.indicator_input_error));
                    mFormCompletionState_Error = true;
                    mFormCompletionState_date_error = true;
                }
                if (mEventStartDateButton.getText().equals("Select Start Date")) {
                    mEventStartTimeAndDateErrorMessage2.setVisibility(View.VISIBLE);
                    mEventStartDateButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.drawable.indicator_input_error));
                    mFormCompletionState_Error = true;
                    mFormCompletionState_date_error = true;
                }
                if (mEventEndTimeButton.getText().equals("Select End Time")) {
                    mEventEndTimeAndDateErrorMessage2.setVisibility(View.VISIBLE);
                    mEventEndTimeButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.drawable.indicator_input_error));
                    mFormCompletionState_Error = true;
                    mFormCompletionState_date_error = true;
                }
                if (mEventEndDateButton.getText().equals("Select End Date")) {
                    mEventEndTimeAndDateErrorMessage2.setVisibility(View.VISIBLE);
                    mEventEndDateButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.drawable.indicator_input_error));
                    mFormCompletionState_Error = true;
                    mFormCompletionState_date_error = true;
                }
                
                // Get the event start and end dates (unless we've already found an error with the 
                // dates and times, in that case, don't even bother).
                if (mFormCompletionState_date_error == false) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss");
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    
                    // TODO: Research alternatives to "Date" that arent depracated
                    @SuppressWarnings("deprecation")
                    Date startDate = new Date(mEventStartYear - 1900, 
                            mEventStartMonth, 
                            mEventStartDay,
                            mEventStartHourOfDay, 
                            mEventStartMinute,
                            0);
                    
                    // TODO: Research alternatives to "Date" that arent depracated
                    @SuppressWarnings("deprecation")
                    Date endDate = new Date(mEventEndYear - 1900, 
                            mEventEndMonth, 
                            mEventEndDay,
                            mEventEndHourOfDay, 
                            mEventEndMinute,
                            0);
                    
                    mEventStartDateTime = sdf.format(startDate);
                    mEventEndDateTime = sdf.format(endDate);
                    
                    // Make sure that the start time is before the end time. If not, display an
                    // error message.
                    if (startDate.compareTo(endDate) > 0) {
                        mFormCompletionState_Error = true;
                        mEventStartTimeAndDateHeader.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.indicator_input_error), null);
                        mEventEndTimeAndDateHeader.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.indicator_input_error), null);
                        mEventStartTimeAndDateErrorMessage.setVisibility(View.VISIBLE);
                        mEventEndTimeAndDateErrorMessage.setVisibility(View.VISIBLE);
                    }
                }
                
                // Check if there were any errors in the form submission, if not, proceed with 
                // creating the event.
                if (mFormCompletionState_Error == false) {
                    mCreateEventTask = new CreateEventTask();
                    mCreateEventTask.execute();
                }  else {
                    mToast = Toast.makeText(mActivity, "There was an error in your submission.", Toast.LENGTH_LONG);
                    mToast.setGravity(Gravity.CENTER, 0, 0);
                    mToast.show();
                }
            }
            
        });
    }
    
    /**
     * Called when you are no longer visible to the user. You will next receive either onRestart(),
     * onDestroy(), or nothing, depending on later user activity.
     */
    @Override
    public void onStop() {
        super.onStop();
        
        if (mCreateEventTask != null) {
            mCreateEventTask.cancel(true);
        }
    }
    
    /** 
     * Sends an API call to have an event be created.
     */
    public class CreateEventTask extends AsyncTask<Void, Void, String> {

        private Toast toast;
        
        /** 
         * Runs on the UI thread before doInBackground(Params...).
         */
        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
            toast = Toast.makeText(getBaseContext(), "Creating Event", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        
        /** 
         * Performs a computation on a background thread;
         */
        @Override
        protected String doInBackground(Void... params) {
            return RestApiV1.createEvent(mGroup.getUUID(), mEventTitle, mEventDescription, mEventStartDateTime, mEventEndDateTime);
        }
        
        /** 
         * Runs on the UI thread after doInBackground(Params...). The specified result is the value
         * returned by doInBackground(Params...).
         */
        @Override
        protected void onPostExecute(String jsonResult) {
            setProgressBarIndeterminateVisibility(false);
            toast.cancel();
            
            // We should never enter the catch. If we do then there is an issue with the API.
            try {
                JSONObject response = new JSONObject(jsonResult);
                
                // If the response has a uuid in it, we know the event creation was successful.
                if (response.has("uuid")) {
                    toast = Toast.makeText(getBaseContext(), "Event Created", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    mActivity.finish();
                } 
            } catch (JSONException e) {
                toast = Toast.makeText(getBaseContext(), "There was an error creating this event, please try again later.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                e.printStackTrace();
            }
        }
    }
    
    /**
     * A fragment that displays a dialog window, floating on top of its activity's window. Contains
     * a time picker for the user to use in selecting the starting time of their event.
     */
    public static class StartTimePickerFragment extends DialogFragment implements OnTimeSetListener {
    
        /**
         * Override to build your own custom Dialog container. This is typically used to show an
         * AlertDialog instead of a generic Dialog; when doing so, onCreateView(LayoutInflater,
         * ViewGroup, Bundle) does not need to be implemented since the AlertDialog takes care of
         * its own content.
         * 
         * @param savedInstanceState The last saved instance state of the Fragment, or null if this
         * is a freshly created Fragment.
         * @return Return a new Dialog instance to be displayed by the Fragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
        
            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }
        
        /**
         * The callback used to indicate the user is done filling in the time (they clicked on the
         * 'Set' button).
         * 
         * @param view The view associated with this listener.
         * @param hourOfDay The hour that was set.
         * @param minute The minute that was set.
         */
        @SuppressLint("SimpleDateFormat")
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mEventStartHourOfDay = hourOfDay;
            mEventStartMinute = minute;
            
            // TODO: Research alternatives to "Date" that arent depracated
            @SuppressWarnings("deprecation")
            Date date = new Date(0, 0, 0, hourOfDay, minute, 0);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:aa");
            mEventStartTimeButton.setText(sdf.format(date));
        }
    }
    
    /**
     * A fragment that displays a dialog window, floating on top of its activity's window. Contains
     * a date picker for the user to use in selecting the starting date of their event.
     */
    public static class StartDatePickerFragment extends DialogFragment implements OnDateSetListener {
        
        /**
         * Override to build your own custom Dialog container. This is typically used to show an
         * AlertDialog instead of a generic Dialog; when doing so, onCreateView(LayoutInflater,
         * ViewGroup, Bundle) does not need to be implemented since the AlertDialog takes care of
         * its own content.
         * 
         * @param savedInstanceState The last saved instance state of the Fragment, or null if this
         * is a freshly created Fragment.
         * @return Return a new Dialog instance to be displayed by the Fragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
        
            // Create a new instance of TimePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month,day);
        }
        
        /**
         * The callback used to indicate the user is done filling in the date.
         * 
         * @param view The view associated with this listener.
         * @param year The year that was set.
         * @param monthOfYear The month that was set (0-11 for compatibility with Calendar.class).
         * @param dayOfMonth The day of the month that was set.
         */
        @SuppressLint("SimpleDateFormat")
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mEventStartYear = year;
            mEventStartMonth = monthOfYear;
            mEventStartDay = dayOfMonth;
            
            // TODO: Research alternatives to "Date" that arent depracated
            @SuppressWarnings("deprecation")
            Date date = new Date(year - 1900, monthOfYear, dayOfMonth, 0, 0, 0);
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
            mEventStartDateButton.setText(sdf.format(date));
        }
    }
    
    /**
     * A fragment that displays a dialog window, floating on top of its activity's window. Contains
     * a time picker for the user to use in selecting the ending time of their event.
     */
    public static class EndTimePickerFragment extends DialogFragment implements OnTimeSetListener {
        
        /**
         * Override to build your own custom Dialog container. This is typically used to show an
         * AlertDialog instead of a generic Dialog; when doing so, onCreateView(LayoutInflater,
         * ViewGroup, Bundle) does not need to be implemented since the AlertDialog takes care of
         * its own content.
         * 
         * @param savedInstanceState The last saved instance state of the Fragment, or null if this
         * is a freshly created Fragment.
         * @return Return a new Dialog instance to be displayed by the Fragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
        
            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }
        
        /**
         * The callback used to indicate the user is done filling in the time (they clicked on the
         * 'Set' button).
         * 
         * @param view The view associated with this listener.
         * @param hourOfDay The hour that was set.
         * @param minute The minute that was set.
         */
        @SuppressLint("SimpleDateFormat")
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mEventEndHourOfDay = hourOfDay;
            mEventEndMinute = minute;
            
            // TODO: Research alternatives to "Date" that arent depracated
            @SuppressWarnings("deprecation")
            Date date = new Date(0, 0, 0, hourOfDay, minute, 0);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:aa");
            mEventEndTimeButton.setText(sdf.format(date));
        }
    }
    
    /**
     * A fragment that displays a dialog window, floating on top of its activity's window. Contains
     * a date picker for the user to use in selecting the ending date of their event.
     */
    public static class EndDatePickerFragment extends DialogFragment implements OnDateSetListener {
        
        /**
         * Override to build your own custom Dialog container. This is typically used to show an
         * AlertDialog instead of a generic Dialog; when doing so, onCreateView(LayoutInflater,
         * ViewGroup, Bundle) does not need to be implemented since the AlertDialog takes care of
         * its own content.
         * 
         * @param savedInstanceState The last saved instance state of the Fragment, or null if this
         * is a freshly created Fragment.
         * @return Return a new Dialog instance to be displayed by the Fragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
        
            // Create a new instance of TimePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month,day);
        }
        
        /**
         * The callback used to indicate the user is done filling in the date.
         * 
         * @param view The view associated with this listener.
         * @param year The year that was set.
         * @param monthOfYear The month that was set (0-11 for compatibility with Calendar.class).
         * @param dayOfMonth The day of the month that was set.
         */
        @SuppressLint("SimpleDateFormat")
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mEventEndYear = year;
            mEventEndMonth = monthOfYear;
            mEventEndDay = dayOfMonth;
            
            // TODO: Research alternatives to "Date" that arent depracated
            @SuppressWarnings("deprecation")
            Date date = new Date(year - 1900, monthOfYear, dayOfMonth, 0, 0, 0);
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
            mEventEndDateButton.setText(sdf.format(date));
        }
    }
}
