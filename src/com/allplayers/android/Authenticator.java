/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.allplayers.android;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;

/**
 * Custom account authenticator.
 */
class Authenticator extends AbstractAccountAuthenticator {

    /**
     * Constructor
     * 
     * @param context Context of the authenticator.
     */
    public Authenticator(Context context) {
        super(context);
    }

    /**
     * Adds an account of the specified accountType.
     * 
     * @param response Used to send the result back to the AccountManager, will never be null.
     * @param accountType The type of account to add, will never be null.
     * @param authTokenType The type of auth token to retrieve after adding the account, may be
     * null.
     * @param requiredFeatures A String array of authenticator-specific features that the added
     * account must support, may be null.
     * @param options A Bundle of authenticator-specific options, may be null.
     * @return a Bundle result or null if the result is to be returned via the response. 
     * The result will contain either:
     *      • KEY_INTENT.
     *      • KEY_ACCOUNT_NAME and KEY_ACCOUNT_TYPE of the account that was added.
     *      • KEY_ERROR_CODE and KEY_ERROR_MESSAGE to indicate an error.
     */
    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
                             String authTokenType, String[] requiredFeatures, Bundle options) {
        return null;
    }

    /**
     * Checks that the user knows the credentials of an account.
     * 
     * @param response Used to send the result back to the AccountManager, will never be null.
     * @param account The account whose credentials are to be checked, will never be null.
     * @param options A Bundle of authenticator-specific options, may be null.
     * @return a Bundle result or null if the result is to be returned via the response. 
     * The result will contain either:
     *      • KEY_INTENT.
     *      • KEY_BOOLEAN_RESULT, true if the check succeeded, false otherwise.
     *      • KEY_ERROR_CODE and KEY_ERROR_MESSAGE to indicate an error.
     */
    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) {
        return null;
    }

    /**
     * Returns a Bundle that contains the Intent of the activity that can be used to edit the
     * properties. In order to indicate success the activity should call response.setResult() with a
     * non-null Bundle.
     * 
     * @param response Response used to set the result for the request. If the Constants.INTENT_KEY
     * is set in the bundle then this response field is to be used for sending future results if and
     * when the Intent is started.
     * @param accountType The AccountType whose properties are to be edited.
     * @return A Bundle containing the result or the Intent to start to continue the request. If
     * this is null then the request is considered to still be active and the result should sent
     * later using response.
     */
    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the authtoken for an account.
     * 
     * @param response Used to send the result back to the AccountManager, will never be null.
     * @param account The account whose credentials are to be retrieved, will never be null.
     * @param authTokenType The type of auth token to retrieve, will never be null.
     * @param loginOptions A Bundle of authenticator-specific options, may be null.
     * @return A Bundle result or null if the result is to be returned via the response.
     * The result will contain either:
     *      • KEY_INTENT.
     *      • KEY_ACCOUNT_NAME, KEY_ACCOUNT_TYPE, and KEY_AUTHTOKEN.
     *      • KEY_ERROR_CODE and KEY_ERROR_MESSAGE to indicate an error.
     */
    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
                               String authTokenType, Bundle loginOptions) throws NetworkErrorException {
        return null;
    }

    /**
     * Ask the authenticator for a localized label for the given authTokenType.
     * 
     * @param authTokenType The authTokenType whose label is to be returned, will never be null.
     * @return The localized label of the auth token type, may be null if the type isn't known.
     */
    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return null;
    }

    /**
     * Checks if the account supports all the specified authenticator specific features.
     * 
     * @param response Used to send the result back to the AccountManager, will never be null.
     * @param account The account to check, will never be null.
     * @param features An array of features to check, will never be null.
     * @return A Bundle result or null if the result is to be returned via the response.
     * The result will contain either:
     *      • KEY_INTENT.
     *      • KEY_BOOLEAN_RESULT, true if the account has all the features, false otherwise.
     *      • KEY_ERROR_CODE and KEY_ERROR_MESSAGE to indicate an error.
     */
    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) {
        return null;
    }

    /**
     * Update the locally stored credentials for an account.
     * 
     * @param response Used to send the result back to the AccountManager, will never be null.
     * @param account The account whose credentials are to be updated, will never be null.
     * @param authTokenType The type of auth token to retrieve after updating the credentials, may
     * be null.
     * @param options A Bundle of authenticator-specific options, may be null.
     * @return A Bundle result or null if the result is to be returned via the response.
     * The result will contain either:
     *      • KEY_INTENT.
     *      • KEY_ACCOUNT_NAME and KEY_ACCOUNT_TYPE of the account that was added.
     *      • KEY_ERROR_CODE and KEY_ERROR_MESSAGE to indicate an error.
     */
    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account,
                                    String authTokenType, Bundle loginOptions) {
        return null;
    }
}
