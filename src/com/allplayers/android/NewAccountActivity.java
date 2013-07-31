package com.allplayers.android;

import java.util.Calendar;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import com.allplayers.rest.RestApiV1;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NewAccountActivity extends Activity {

    private AccountRegistrationTask mAccountRegistrationTask;
    private Activity mActivity = this;
    private GetNewCaptchaTask mGetNewCaptchaTask;

    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mVerifyPasswordEditText;
    private Spinner mGenderSpinner;
    private TextView mGenderSpinnerError;
    private DatePicker mDatePicker;
    private TextView mDatePickerError;
    private TextView mCaptchaTextView;
    private EditText mCaptchaEditText;
    private Button mSubmit;
    private ProgressBar mProgressBar;
    private Toast mToast;

    private int mGeneratedCaptchaAnswerInt;
    private String mFirstName;
    private String mLastName;
    private String mEmail;
    private String mPassword;
    private String mVerifyPassword;
    private String mApiCaptchaToken;
    private String mApiCaptchaProblem;
    private String mApiCaptchaResponse;
    private String mGeneratedCaptchaProblem;
    private String mGeneratedCaptchaResponse;
    private String mGender = "U";
    private String mBirthDate;

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     * down then this Bundle contains the data it most recently supplied in
     * onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_new_account);

        // Set up the captcha on the page for the user to solve. This one is generated in the app
        // using the same random number ranges used on the  site. This is done to avoid sending more
        // network calls than we need to.
        setCaptcha();

        // Get a handle on all of the page elements in the XML.
        mFirstNameEditText = (EditText)findViewById(R.id.edit_first_name);
        mLastNameEditText = (EditText)findViewById(R.id.edit_last_name);
        mEmailEditText = (EditText)findViewById(R.id.edit_email);
        mPasswordEditText = (EditText)findViewById(R.id.edit_pass_one);
        mVerifyPasswordEditText = (EditText)findViewById(R.id.edit_pass_two);
        mGenderSpinner = (Spinner)findViewById(R.id.gender_picker);
        mGenderSpinnerError = (TextView)findViewById(R.id.gender_spinner_error);
        mDatePicker = (DatePicker)findViewById(R.id.date_picker);
        mDatePickerError = (TextView)findViewById(R.id.date_picker_error);
        mCaptchaTextView = (TextView)findViewById(R.id.captcha);
        mCaptchaEditText = (EditText)findViewById(R.id.captcha_answer);
        mSubmit = (Button)findViewById(R.id.submit);
        mProgressBar = (ProgressBar)findViewById(R.id.create_account_progress_bar);

        // Setup the text view for the captcha.
        mCaptchaTextView.setText(mGeneratedCaptchaProblem + " = ");

        // Set up the gender spinner.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender_spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGenderSpinner.setAdapter(adapter);
        mGenderSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            /**
             * Callback method to be invoked when an item in this view has been selected. This
             * callback is invoked only when the newly selected position is different from the
             * previously selected position or if there was no selected item.
             * 
             * @param parent The AdapterView where the selection happened.
             * @param view The view within the AdapterView that was clicked.
             * @param position The position of the view in the adapter.
             * @param id The row id of the item that is selected.
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                switch (pos) {
                case 0:
                    mGender = "U";
                    break;
                case 1:
                    mGender = "M";
                    break;
                case 2:
                    mGender = "F";
                }
            }

            /**
             * Callback method to be invoked when the selection disappears from this view. The
             * selection can disappear for instance when touch is activated or when the adapter
             * becomes empty.
             * 
             * @param parent The AdapterView that now contains no selected item.
             */
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // Do nothing
            }
        });

        // Set up the birth date picker. Max date can only be set in API 11 and up. We will check
        // later to make sure the date isn't set into the future.
        mDatePicker.setCalendarViewShown(false);
        if (Build.VERSION.SDK_INT > 10) {
            mDatePicker.setMaxDate(Calendar.getInstance().getTimeInMillis());
        }

        // Set up the submit button.
        mSubmit.setOnClickListener(new View.OnClickListener() {

            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                // Hide the keyboard.
                InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                // Set to true if any of the fields are incorrect.
                boolean errorOccured = false;

                // Make sure error messages are gone. They will be reinstated if necesary later in
                // this method.
                mDatePickerError.setVisibility(TextView.GONE);
                mGenderSpinnerError.setVisibility(TextView.GONE);

                // Get the text from the forms (mGender is set by a listener).
                mFirstName = mFirstNameEditText.getText().toString();
                mLastName = mLastNameEditText.getText().toString();
                mEmail = mEmailEditText.getText().toString();
                mPassword = mPasswordEditText.getText().toString();
                mVerifyPassword = mVerifyPasswordEditText.getText().toString();
                mGeneratedCaptchaResponse = mCaptchaEditText.getText().toString();

                String birthDateYear = mDatePicker.getYear() + "";
                String birthDateMonth = (mDatePicker.getMonth() + 1) + "";
                String birthDateDay = mDatePicker.getDayOfMonth() + "";

                // Add a "0" onto the front of any single digit number to fit the YYYY-MM-DD format.
                if (mDatePicker.getMonth() < 10) {
                    birthDateMonth = "0" + birthDateMonth;
                }
                if (mDatePicker.getDayOfMonth() < 10) {
                    birthDateDay = "0" + birthDateDay;
                }
                mBirthDate = birthDateYear + "-" + birthDateMonth + "-" + birthDateDay;

                // Check that the first name field is not blank.
                if (mFirstName.equals("")) {
                    errorOccured = true;
                    firstNameError();
                }

                // Check that the last name is not blank.
                if (mLastName.equals("")) {
                    errorOccured = true;
                    lastNameError();
                }

                // Check that the birth date is valid (must be over 13 years old to register), cant
                // be born in the future.
                Calendar now = Calendar.getInstance();
                Calendar dob = Calendar.getInstance();
                dob.set(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth());

                // Make sure that the birth date is not in the future. It is only possible for this
                // to happen on pre-API11 devices since we can set the maximum date for the date
                // picker in API11 and up.
                if (dob.after(now)) {
                    errorOccured = true;
                    Toast.makeText(NewAccountActivity.this, "I'm sorry, but you have entered a date in the future.", Toast.LENGTH_LONG).show();
                }
                int year1 = now.get(Calendar.YEAR);
                int year2 = dob.get(Calendar.YEAR);
                int age = year1 - year2;
                int month1 = now.get(Calendar.MONTH);
                int month2 = dob.get(Calendar.MONTH);
                if (month2 > month1) {
                    age--;
                } else if (month1 == month2) {
                    int day1 = now.get(Calendar.DAY_OF_MONTH);
                    int day2 = dob.get(Calendar.DAY_OF_MONTH);
                    if (day2 > day1) {
                        age--;
                    }
                }
                if (age < 13) {
                    errorOccured = true;
                    birthDateError();
                }

                // Check that the email field is not blank, if not, check that the email is valid.
                String validEmailRegex = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
                if (mEmail.equals("")) {
                    errorOccured = true;
                    emailError(1);
                } else if (!(mEmail.matches(validEmailRegex))) {
                    errorOccured = true;
                    emailError(2);
                }

                // Check that the password field is not blank, if not, check that the password
                // fields match, if so, check that the password is valid.
                if (mPassword.equals("") || mVerifyPassword.equals("")) {
                    errorOccured = true;
                    if (mPassword.equals("")) {
                        passwordError(1);
                    }
                    if (mVerifyPassword.equals("")) {
                        verifyPasswordError(1);
                    }
                } else if (!mPassword.equals(mVerifyPassword)) {
                    errorOccured = true;
                    passwordError(2);
                    verifyPasswordError(2);
                } else if (!(passwordMatchesCriteria(mPassword))) {
                    errorOccured = true;
                    passwordError(3);
                    verifyPasswordError(3);
                }

                // Check that the captcha field is not blank, if not, check that the answer is
                // correct.
                if (mGeneratedCaptchaResponse.equals("")) {
                    errorOccured = true;
                    captchaError(1);
                } else if (!(Integer.parseInt(mGeneratedCaptchaResponse) == mGeneratedCaptchaAnswerInt)) {
                    errorOccured = true;
                    captchaError(2);
                }

                // Check that the gender field is not blank.
                if (mGender.equals("U")) {
                    errorOccured = true;
                    genderError();
                }

                // Check if there were any errors with the fields. If so, we don't even want to
                // bother with sending the post with the information because we know it will not
                // work.
                if (errorOccured) {
                    Toast.makeText(getApplicationContext(),
                                   "There was an error in your submission.",
                                   Toast.LENGTH_LONG).show();
                } else {
                    mAccountRegistrationTask = new AccountRegistrationTask();
                    mAccountRegistrationTask.execute(new String[] {
                                                              mFirstName, mLastName, mEmail, mGender, mBirthDate, mPassword,
                                                              mApiCaptchaToken, mApiCaptchaResponse
                                                          });
                }

            }
        });

        // We need to get a captcha token and problem. Because of the way the API is set up, we need
        // to make an API call before sending any data to get the captcha token and problem. To do
        // that we will just send some dummy data.
        mGetNewCaptchaTask = new GetNewCaptchaTask();
        mGetNewCaptchaTask.execute();
    }
    
    /**
     * Called when you are no longer visible to the user. You will next receive either onRestart(),
     * onDestroy(), or nothing, depending on later user activity.
     */
    @Override
    public void onStop() {
        super.onStop();
        
        if (mAccountRegistrationTask != null) {
            mAccountRegistrationTask.cancel(true);
        }
        
        if (mGetNewCaptchaTask != null) {
            mGetNewCaptchaTask.cancel(true);
        }
        
        if (mToast != null) {
            mToast.cancel();
        }
    }

    /**
     * Creates a captcha problem and solution and assigns the resulting values to the class
     * variables mGeneratedCaptchaProblem and mGeneratedCaptchaAnswer.
     */
    private void setCaptcha() {
        int num1;
        int num2;
        int captchaAnswer;

        Random randomGenerator = new Random();

        // Create a pair of numbers whose sum <= 20 && sum > 0.
        do {
            num1 = randomGenerator.nextInt(21);
            num2 = randomGenerator.nextInt(21);
            captchaAnswer = num1 + num2;
        } while (!((captchaAnswer > 0) && (captchaAnswer <= 20)));

        // Set the class variables.
        mGeneratedCaptchaProblem = num1 + " + " + num2;
        mGeneratedCaptchaAnswerInt = captchaAnswer;
    }

    /**
     * Checks if the user entered password matches the criteria required for a password.
     *    Password must contain at least one letter.
     *    Password must be at least 6 characters in length.
     *    Password must contain at least 5 alphanumeric (letter or number) characters.
     *    Password must contain at least 2 characters of different types (lowercase, uppercase,
     *     digit, or punctuation.
     *
     * @param pass: The password to check for the correct criteria.
     * @return Whether or not the passed password meets the requirements.
     */
    private boolean passwordMatchesCriteria(String pass) {
        boolean hasLower = false;
        boolean hasUpper = false;
        boolean hasDigit = false;
        boolean hasPunct = false;
        int lowerCount = 0;
        int upperCount = 0;
        int digitCount = 0;
        int typeCount = 0;

        int length = pass.length();
        if (length < 6) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            char c = pass.charAt(i);
            if (c > 64 && c < 91) {
                hasUpper = true;
                upperCount++;
                continue;
            } else if (c > 96 && c < 123) {
                hasLower = true;
                lowerCount++;
                continue;
            } else if (c > 47 && c < 58) {
                hasDigit = true;
                digitCount++;
                continue;
            } else {
                hasPunct = true;
            }
        }

        if (!hasUpper && !hasLower) {
            return false;
        }
        if ((lowerCount + upperCount + digitCount) <  5) return false;
        if (hasLower) typeCount++;
        if (hasUpper) typeCount++;
        if (hasDigit) typeCount++;
        if (hasPunct) typeCount++;
        if (typeCount < 2) return false;
        return true;
    }

    /**
     * Called when an account is successfully created. Handles sending the data to the login page
     * and starting that activity.
     */
    private void onAccountCreateSuccess() {
        Intent intent = new Intent();
        intent.putExtra("login credentials", new String[] {mEmail, mPassword});
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    /**
     * Called if there is an error with the entered firstName. Will mark it as an errored field.
     */
    private void firstNameError() {
        mFirstNameEditText.setError("This field cannot be left blank.");
    }

    /**
     * Called if there is an error with the entered lastName. Will mark it as an errored field.
     */
    private void lastNameError() {
        mLastNameEditText.setError("This field cannot be left blank.");
    }

    /**
     * Called if there is an error with the entered email. Will mark it as an errored field.
     *
     * @param errorType: The type of error on the field.
     *      Acceptable Values:
     *          1: Email field left blank.
     *          2: Not a valid email address.
     *          3: Email address already in use.
     */
    private void emailError(int errorType) {
        switch (errorType) {
        case 1:
            mEmailEditText.setError("This field cannot be left blank.");
            break;
        case 2:
            mEmailEditText.setError("This is not a valid email address.");
            break;
        case 3:
            mEmailEditText.setError("This email address is already in use.");
            break;
        default:
            mEmailEditText.setError("This is not a valid email address.");
        }
    }

    /**
     * If there is an error with the birth date (registrant age < 13), display the error message.
     */
    private void birthDateError() {
        mDatePickerError.setVisibility(TextView.VISIBLE);
    }

    /**
     * Called if there is an error with the entered password. Will mark it as an errored field.
     *
     * @param errorType: The type of error on the field.
     *      Acceptable Values:
     *          1: Password field left blank.
     *          2: Password and verify password don't match.
     *          3: Password is not valid.
     */
    private void passwordError(int errorType) {
        mPasswordEditText.setText("");
        mVerifyPasswordEditText.setText("");
        switch (errorType) {
        case 1:
            mPasswordEditText.setError("This field cannot be left blank.");
            break;
        case 2:
            mPasswordEditText.setError("The passwords you entered do not match.");
            break;
        case 3:
            mPasswordEditText.setError("Your password must contain at least:" +
                                       "\n • 1 letter." +
                                       "\n • 6 characters." +
                                       "\n • 5 alphanumeric characters" +
                                       "\n • 2 characters of different types");
            break;
        default:
            mPasswordEditText.setError("This is not a valid password.");
        }
    }

    /**
     * Called if there is an error with the entered password. Will mark it as an errored field.
     *
     * @param errorType: The type of error on the field.
     *      Acceptable Values:
     *          1: Password field left blank.
     *          2: Password and verify password don't match.
     *          3: Password is not valid.
     */
    private void verifyPasswordError(int errorType) {
        mPasswordEditText.setText("");
        mVerifyPasswordEditText.setText("");
        switch (errorType) {
        case 1:
            mVerifyPasswordEditText.setError("This field cannot be left blank.");
            break;
        case 2:
            mVerifyPasswordEditText.setError("The passwords you entered do not match.");
            break;
        case 3:
            mVerifyPasswordEditText.setError("Your password must contain at least:" +
                                             "\n • 1 letter." +
                                             "\n • 6 characters." +
                                             "\n • 5 alphanumeric characters" +
                                             "\n • 2 characters of different types");
            break;
        default:
            mVerifyPasswordEditText.setError("This is not a valid email password.");
        }
    }

    /**
     * Called if there is an error with the entered Gender. Will mark the spinner with an error.
     */
    private void genderError() {
        mGenderSpinnerError.setVisibility(TextView.VISIBLE);
    }

    /**
     * Called if there is an error with the entered captcha. Will mark it as an errored field.
     *
     * @param errorType: The type of error on the field.
     *      Acceptable Values:
     *          1: Captcha field left blank.
     *          2: Captcha incorrect.
     */
    private void captchaError(int errorType) {
        mCaptchaEditText.setText("");
        switch (errorType) {
        case 1:
            mCaptchaEditText.setError("This field cannot be left blank.");
            break;
        case 2:
            mCaptchaEditText.setError("This answer is incorrect.");
            break;
        default:
            mCaptchaEditText.setError("This is not a valid solution.");
        }
    }

    /**
     * Registers the user for an AllPlayers.com account.
     */
    public class AccountRegistrationTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            mToast = Toast.makeText(mActivity, "Creating account...", Toast.LENGTH_LONG);
            mToast.show();

        }
        /**
         * Performs a computation on a background thread. In this case, makes an API call.
         *
         * @param signupInformation: The necessary information for a user to register an account.
         *  [0]: first name
         *  [1]: last name
         *  [2]: email
         *  [3]: gender
         *  [4]: birthday
         *  [5]: password
         *  [6]: captcha token
         *  [7]: captcha response
         *  @return The result of the API call.
         */
        @Override
        protected String doInBackground(String... signupInformation) {

            String jsonResult = RestApiV1.createNewUser(signupInformation[0], signupInformation[1],
                                signupInformation[2], signupInformation[3], signupInformation[4],
                                signupInformation[5], signupInformation[6], signupInformation[7]);
            return jsonResult;
        }

        /**
         * Runs on the UI thread after doInBackground(Params...). The specified result is the value
         * returned by doInBackground(Params...).
         * 
         * @param jsonResult The result of the API call.
         */
        @Override
        protected void onPostExecute(String jsonResult) {
            mProgressBar.setVisibility(View.GONE);
            mToast.cancel();
            if (jsonResult.equals("error")) {
                Toast toast = Toast.makeText(NewAccountActivity.this, "It appears that you are having connection issues, please check your network settings", Toast.LENGTH_LONG);
                
                // You cannot specify a length value for toast apart from LENGTH_LONG (~3.5s) and
                // LENGTH_SHORT (~2s). Calling show multiple times though extends the time. This
                // will make the toast show for ~10.5s which should be more suitable.
                for (int i = 0; i < 3; i++) {
                    toast.show();
                }
            }
            
            try {
                JSONObject response = new JSONObject(jsonResult);
                
                // If the result gives us a uuid, we know that the account has been created
                // successfully.
                if (response.has("uuid")) {
                    onAccountCreateSuccess();
                }

                // We should never get any form errors because we check for all of those conditions
                // locally. This is just here as a safeguard and should temporarily handle any
                // changes to the requirements of these fields.
                else if (response.has("form_errors")) {
                    JSONObject errors = response.getJSONObject("form_errors");
                    if (errors.has("mail")) {
                        emailError(2);
                    }
                    if (errors.has("pass")) {
                        passwordError(3);
                    }
                    if (errors.has("field_firstname")) {
                        firstNameError();
                    }
                    if (errors.has("field_lastname")) {
                        lastNameError();
                    }
                    if (errors.has("field_birth_date")) {
                        birthDateError();
                    }

                    // If there was a form error found by the server, we will need a new captcha.
                    // This should never occur though because we check for all of the registration
                    // conditions locally.
                    mGetNewCaptchaTask = new GetNewCaptchaTask();
                    mGetNewCaptchaTask.execute();
                } else {

                    // Its bad news if we get an API response that isn't one of these errors. It
                    // probably is a sign that there is an issue with the API. We should probably
                    // find a better way to handle this issue should it occur, but for now, we will
                    // throw an exception.
                    // TODO Find a better way to handle an invalid API response.
                    throw new Exception("The API returned an invalid response.");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }

        }
    }

    /**
     * Sends a blank registration form through the API to recieve a new captcha. This needs to be
     * done at the beginning of registration and then every time that there is a form error caught
     * by the server (this should never happen because we check all of the registration conditions
     * locally).
     */
    public class GetNewCaptchaTask extends AsyncTask<Void, Void, String> {

        /**
         * Send a blank registration form with no headers through the API. This will get us a fresh
         * captcha to solve.
         *
         * @param arg0: An array of nothing.
         * @return: The response from the API call.
         */
        @Override
        protected String doInBackground(Void... arg0) {
            String jsonResult = RestApiV1.createNewUser("", "", "", "", "", "", null, null);
            return jsonResult;
        }

        /**
         * Take the result from the API call and if it is a "captcha_error" (which it always should
         * be), parse out the math problem and captcha token, solVve the math problem, and save the
         * token and math problem answer to be sent with the next API call.
         *
         * @param jsonResult: The result of the API call made in doInBackground().
         */
        @Override
        protected void onPostExecute(String jsonResult) {       
            JSONObject response;
            
            try {
                response = new JSONObject(jsonResult);
                
                // We should only ever get a captcha_error here
                if (response.has("captcha_error")) {
                    mApiCaptchaToken = response.getJSONObject("captcha_error")
                                       .getString("captcha_token");
                    mApiCaptchaProblem = response.getJSONObject("captcha_error")
                                         .getString("captcha_problem");
                    mApiCaptchaResponse = (Integer.parseInt(mApiCaptchaProblem.substring(0,
                                                            mApiCaptchaProblem.indexOf("+") - 1)) + Integer
                                           .parseInt(mApiCaptchaProblem.substring(
                                                         mApiCaptchaProblem.indexOf("+") + 2,
                                                         mApiCaptchaProblem.indexOf("=") - 1))) + "";
                } else {

                    // Its bad news if we get an API response that isn't a captcha_error. It
                    // probably is a sign that there is an issue with the API. We should probably
                    // find a better way to handle this issue should it occur, but for now, we will
                    // throw an exception.
                    // TODO Find a better way to handle an invalid API response.
                    throw new Exception("The API returned an invalid response.");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}