package com.allplayers.rest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * App side of the API.
 */
public class RestApiV1 {
    
    private static CookieHandler sCookieHandler = new CookieManager();

    private static final String CAP_SOLUTION_NAME = "X-ALLPLAYERS-CAPTCHA-SOLUTION";
    private static final String CAP_TOKEN_NAME = "X-ALLPLAYERS-CAPTCHA-TOKEN";
    private static final String ENDPOINT = "https://www.pdup.allplayers.com/?q=api/v1/rest/";
    private static String sCurrentUserUUID = "";

    /**
     * Default Constructor.
     */
    public RestApiV1() {
        
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { 
            new X509TrustManager() {
            
                /**
                 * Returns the list of certificate issuer authorities which are trusted for
                 * authentication of peers.
                 */
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    // Unused
                    return null;
                }

                /**
                 * Returns the list of certificate issuer authorities which are trusted for
                 * authentication of peers.
                 */
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    // Unused
                }

                /**
                 * Checks whether the specified certificate chain (partial or complete) can be
                 * validated and is trusted for server authentication for the specified key exchange
                 * algorithm.
                 */
                @Override
                public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType) {
                    // Unused
                }
            }    
        };  

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception ex) {
            System.err.println("APCI_RestServices/constructor/" + ex);
        }

        // Install CookieHandler
        CookieHandler.setDefault(sCookieHandler);
    }

    /**
     * Checks if the user is currently logged in.
     * 
     * @return True if the current user is logged in, False if not.
     */
    public static boolean isLoggedIn() {
        
        // If we don't have the user's UUID stored locally, we know that nobody is logged in. We can
        // stop checking right here.
        if (sCurrentUserUUID.equals("") || sCurrentUserUUID.equals(null)) {
            logOut();
            return false;
        }

        // If we make it this far, we know that the app has the UUID saved, now we need to check if
        // the API says that we are logged in.
        try {
            URL url = new URL(ENDPOINT + "users/" + sCurrentUserUUID + ".json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            InputStream inStream = connection.getInputStream();
            BufferedReader input = new BufferedReader(new InputStreamReader(
                        inStream));
            String line = "";
            String result = "";
            while ((line = input.readLine()) != null) {
                result += line;
            }
            JSONObject jsonResult = new JSONObject(result);
            String retrievedUUID = jsonResult.getString("uuid");

            // Check if the the logged in user in the API is the same one that we have stored in the
            // app. If not, something weird happened so we should log out both the app and the API.
            if (retrievedUUID.equals(sCurrentUserUUID)) {
                return true;
            } else {
                logOut();
                return false;
            }
        } catch (Exception ex) {
            System.err.println("APCI_RestServices/isLoggedIn/" + ex);
            return false;
        }
    }

    /**
     * Check if the user is logged in, then perform a GET call.
     * 
     * @param urlString The url of the GET call.
     * @return Result from API.
     */
    private static String makeAuthenticatedGet(String urlString) {
        
        // Check if the user is logged in.
        Log.d("IC", urlString);
        if (!isLoggedIn()) {
            return "You are not logged in";
        }

        // Make and return from authenticated get call
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url
                                           .openConnection();
            connection.setDoInput(true);
            InputStream inStream = connection.getInputStream();
            if (connection.getResponseCode() == 204) {
                return "error";
            }
            BufferedReader input = new BufferedReader(new InputStreamReader(
                        inStream));

            String line = "";
            String result = "";
            while ((line = input.readLine()) != null) {
                result += line;
            }

            return result;
        } catch (Exception ex) {
            System.err.println("APCI_RestServices/makeAuthenticatedGet/" + ex);
            return "error";
        }
    }

    /**
     * Check if the user is logged in, then perform a DELETE call.
     * 
     * @param urlString The url of the DELETE call.
     * @return Result from API.
     */
    private static String makeAuthenticatedDelete(String urlString) {
        if (!isLoggedIn()) {
            return "You are not logged in";
        }

        // Make and return from authenticated delete call
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url
                                           .openConnection();

            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type",
                                          "application/x-www-form-urlencoded");
            connection.getResponseCode();

            return "done";
        } catch (Exception ex) {
            System.err.println("APCI_RestServices/makeAuthenticatedDelete/"
                               + ex);
            return "Failed to complete the delete.";
        }
    }
    
    /**
     * Check if the user is logged in, then perform a POST call.
     * 
     * @param urlString The url of the POST call.
     * @return Result from API.
     */
    private static String makeAuthenticatedPost(String urlString,
            String[][] contents) {
        
        // Make and return from authenticated post call
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url
                                           .openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                                          "application/x-www-form-urlencoded");

            // If not logging in, set the cookies in the header
            if (!urlString.equals(ENDPOINT + "users/login.json")) {
                if (!isLoggedIn()) {
                    return "You are not logged in";
                }
            }

            DataOutputStream printout = new DataOutputStream(
                connection.getOutputStream());

            // Send POST output.
            String content = "";
            if (contents.length > 0) {
                for (int i = 0; i < contents.length; i++) {
                    if (i > 0) {
                        content += "&";
                    }

                    content += contents[i][0] + "="
                               + URLEncoder.encode(contents[i][1], "UTF-8");
                }
            }

            printout.writeBytes(content);
            printout.flush();
            printout.close();

            // Get response data.
            BufferedReader input = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
            String str;

            String result = "";
            while ((str = input.readLine()) != null) {
                result += str;
            }

            input.close();

            return result;
        } catch (Exception ex) {
            System.err.println("APCI_RestServices/makeAuthenticatedPost/" + ex);
            return "error";
        }
    }

    /**
     * Check if the user is logged in, then perform a PUT call.
     * 
     * @param urlString The url of the GET call.
     * @return Result from API.
     */
    private static String makeAuthenticatedPut(String urlString,
            String[][] contents) {
        if (!isLoggedIn()) {
            return "You are not logged in";
        }

        // Make and return from authenticated put call
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url
                                           .openConnection();

            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type",
                                          "application/x-www-form-urlencoded");

            DataOutputStream printout = new DataOutputStream(
                connection.getOutputStream());

            // Send PUT output.
            String content = "";
            if (contents.length > 0) {
                for (int i = 0; i < contents.length; i++) {
                    if (i > 0) {
                        content += "&";
                    }

                    content += contents[i][0] + "="
                               + URLEncoder.encode(contents[i][1], "UTF-8");
                }
            }

            printout.writeBytes(content);
            printout.flush();
            printout.close();
            return connection.getResponseMessage();
        } catch (Exception ex) {
            System.err.println("APCI_RestServices/makeAuthenticatedPut/" + ex);
            return ex.toString();
        }
    }

    /**
     * Perform a GET call.
     * 
     * @param urlString The url of the GET call.
     * @return Result from API.
     */
    private static String makeUnauthenticatedGet(String urlString) {
        
        // Make and return from unauthenticated get call
        try {
            URL url = new URL(urlString);
            Log.d("IC", urlString + "\n" + url);
            HttpURLConnection connection = (HttpURLConnection) url
                                           .openConnection();
            connection.setDoInput(true);
            InputStream inStream = connection.getInputStream();
            if (connection.getResponseCode() == 204) {
                return "error";
            }
            BufferedReader input = new BufferedReader(new InputStreamReader(
                        inStream));

            String line = "";
            String result = "";
            while ((line = input.readLine()) != null) {
                result += line;
            }

            return result;
        } catch (Exception ex) {
            System.err
            .println("APCI_RestServices/makeUnauthenticatedGet/" + ex);
            return ex.toString();
        }
    }

    /**
     * Perform a POST call.
     * 
     * @param urlString The url of the POST call.
     * @return Result from API.
     */
    private static String makeUnauthenticatedPost(String urlString,
            String[][] contents, String captchaToken, String captchaResponse) {

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(urlString);
        List<NameValuePair> request = new ArrayList<NameValuePair>();
        for (int i = 0; i < contents.length; i++) {
            request.add(new BasicNameValuePair(contents[i][0], contents[i][1]));
        }

        try {
            post.setEntity(new UrlEncodedFormEntity(request));
            if (captchaToken != null) {
                post.addHeader(CAP_TOKEN_NAME, captchaToken);
                post.addHeader(CAP_SOLUTION_NAME, captchaResponse);
            }
            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                String text = EntityUtils.toString(entity);
                return text;
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "error";
    }

    /**
     * Get a Bitmap from a URL.
     * 
     * @param urlString The URL where the image is stored.
     * @return The bitmap stored at the passed URL.
     */
    public static Bitmap getRemoteImage(final String urlString) {
        try {
            HttpGet httpRequest = null;

            try {
                httpRequest = new HttpGet(new URL(urlString).toURI());
            } catch (URISyntaxException ex) {
                System.err.println("RestApiV1/getRemoteImage/" + ex);
            }

            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(httpRequest);
            HttpEntity entity = response.getEntity();
            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
            InputStream instream = bufHttpEntity.getContent();
            return BitmapFactory.decodeStream(instream);
        } catch (IOException ex) {
            System.err.println("RestApiV1/getRemoteImage/" + ex);
        }

        return null;
    }
    
    /**
     * Log the user out of the app and the API.
     */
    public static void logOut() {
        try {
            CookieManager cm = ((CookieManager) CookieHandler.getDefault());
            URI uri = new URI(ENDPOINT + "users/login.json");
            List<HttpCookie> myCookies = cm.getCookieStore().get(uri);
            for (int i = 0; i < myCookies.size(); i++) {
                cm.getCookieStore().remove(uri, myCookies.get(i));
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        sCurrentUserUUID = "";
    }

    /**
     * Set sCurrentUserUUID to the passed value.
     * 
     * @param uuid The new value of sCurrentUserUUID
     */
    public static void setCurrentUserUUID(String uuid) {
        sCurrentUserUUID = uuid;
    }

    /**
     * Returns the value of sCurrentUserUUID.
     * 
     * @return The value of sCurrentUserUUID.
     */
    public static String getCurrentUserUUID() {
        return sCurrentUserUUID;
    }

    /**
     * Set sCookieHandler to the passed value.
     * 
     * @param cookieHandler The new value of sCookieHandler.
     */
    public static void setCookieHandler(CookieHandler cookieHandler) {
        sCookieHandler = cookieHandler;
    }

    /**
     * Returns the value of sCookieHandler.
     * 
     * @return The value of sCookieHandler.
     */
    public static CookieHandler getCookieHandler() {
        return sCookieHandler;
    }
    
////////////////////////////////////////////////////////////////////////////////////////////////////
    // GET Calls (Albums Endpoint)
////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * API call to fetch an album by its UUID.
     * 
     * @param albumUuid The UUID of the album that is being fetched.
     * @return Result from API.
     */
    public static String getAlbumByAlbumId(String albumUuid) {
        
        String query = ENDPOINT + "albums/" + albumUuid + ".json";
        
        return makeAuthenticatedGet(query);
    }

    /**
     * API call to fetch an album's photos.
     *
     * @param albumUuid The UUID of the album whos photos are being fetched.
     * @param offset Determines at what point the API returns data (starts after 'offset' results).
     * @param limit The number of results the API will return.
     * @return: Result from API.
     */
    public static String getAlbumPhotosByAlbumId(String albumUuid, int offset, int limit) {
        
        String query = ENDPOINT + "albums/" + albumUuid + "/photos.json";
        
        if (offset > 0) {
            query += ("&offset=" + offset);
        }
        if (limit > -1) {
            query += ("&limit=" + limit);
        }
        
        return makeAuthenticatedGet(query);
    }
    
////////////////////////////////////////////////////////////////////////////////////////////////////
    // GET Calls (Events Endpoint)
////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * API call to fetch an event by its ID.
     * 
     * @param eventId The ID of the event that is being fetched.
     * @return Result from API.
     */
    public static String getEventByEventId(String eventId) {
        
        String query = ENDPOINT + "events/" + eventId + ".json";
        
        return makeAuthenticatedGet(query);
    }
    
////////////////////////////////////////////////////////////////////////////////////////////////////
// POST Calls (Events Endpoint)
////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /** 
     * API call to create an event.
     * 
     * @param groupUuid The UUID of the group that the event should be created for.
     * @param eventTitle The title of the event.
     * @param eventDescription The description of the event.
     * @param startDateTime The start time of the event (in the format YYYY-MM-DDTHH:MM:SS).
     * @param endDateTime The end time of the event (in the format YYYY-MM-DDTHH:MM:SS).
     * @return Result from API.
    */
    public static String createEvent(String groupUuid, String eventTitle, String eventDescription, String startDateTime, String endDateTime) {
       
        String query = ENDPOINT + "events/";
        String[][] contents = new String[5][2];

        // Set group
        contents[0][0] = "groups[0]";
        contents[0][1] = groupUuid;

        // Set eventTitle
        contents[1][0] = "title";
        contents[1][1] = eventTitle;

        // Set eventDescription
        contents[2][0] = "description";
        contents[2][1] = eventDescription;

        // Set eventStartDateTime
        contents[3][0] = "date_time[start]";
        contents[3][1] = startDateTime;

        // Set eventEndDateTime
        contents[4][0] = "date_time[end]";
        contents[4][1] = endDateTime;

        return makeAuthenticatedPost(query, contents);
    }
   
////////////////////////////////////////////////////////////////////////////////////////////////////
    // GET Calls (Groups Endpoint)
////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * API call to fetch the passed group's albums.
     *
     * @param groupUuid The UUID of the group whos data is being fetched.
     * @param offset Determines at what point the API returns data (starts after 'offset' results).
     * @param limit The number of results the API will return.
     * @return Result from API.
     */
    public static String getGroupAlbumsByGroupId(String groupUuid, int offset, int limit) {
        
        String query = ENDPOINT + "groups/" + groupUuid + "/albums.json";
        
        if (offset > 0) {
            query += ("&offset=" + offset);
        }
        if (limit > -1) {
            query += ("&limit=" + limit);
        }
        
        return makeAuthenticatedGet(query);
    }
    
    /**
     * API call to fetch a group's upcoming events.
     *
     * @param groupUuid The UUID of the group whos data is being fetched.
     * @param offset Determines at what point the API returns data (starts after 'offset' results).
     * @param limit The number of results the API will return.
     * @return Result from API.
     */
    public static String getGroupEventsUpcomingByGroupId(String groupUuid, int offset, int limit) {
        
        String query = ENDPOINT + "groups/" + groupUuid + "/events/upcoming.json";
        
        if (offset > 0) {
            query += ("&offset=" + offset);
        }
        if (limit > -1) {
            query += ("&limit=" + limit);
        }
        
        return makeAuthenticatedGet(query);
    }
    
    /**
     * API call to fetch the passed group's information.
     *
     * @param groupUuid The UUID of the group whos data is being fetched.
     * @return Result from API.
     */
    public static String getGroupInformationByGroupId(String groupUuid) {
        
        String query = ENDPOINT + "groups/" + groupUuid + ".json";
        
        return makeAuthenticatedGet(query);
    }

    /**
     * API call to fetch the passed group's members.
     *
     * @param groupUuid The UUID of the group whos data is being fetched.
     * @param offset Determines at what point the API returns data (starts after 'offset' results).
     * @param limit The number of results the API will return.
     * @return Result from API.
     */
    public static String getGroupMembersByGroupId(String groupUuid, int offset, int limit) {
        
        String query = ENDPOINT + "groups/" + groupUuid + "/members.json";
        
        if (offset > 0) {
            query += ("&offset=" + offset);
        }
        if (limit > -1) {
            query += ("&limit=" + limit);
        }
        
        return makeAuthenticatedGet(query);
    }

    /**
     * API call to fetch the passed group's photos.
     *
     * @param groupUuid The UUID of the group whos data is being fetched.
     * @return Result from API.
     */
    public static String getGroupPhotosByGroupId(String groupUuid) {
        
        String query = ENDPOINT + "groups/" + groupUuid + "/photos.json";
        
        return makeAuthenticatedGet(query);
    }
    
    /**
     * API call to fetch a list of groups that match the passed in search criteria.
     * 
     * @param search The search query for the groups.
     * @param zipcode The zip code query for the groups.
     * @param distance The distance query for the groups.
     * @return Result from API.
     */
    public static String searchGroups(String search, int zipcode, int distance, int offset, int limit) {
        
        String query = ENDPOINT + "groups.json";
        
        if (search.length() > 0) {
            try {
                search = URLEncoder.encode(search, "utf-8");
            } catch (UnsupportedEncodingException e) {
                // There is no reason that this should happen.
            }
            query += ("&search=\"" + search + "\"");
        }
        
        // The input distance will only matter if a zipcode is given.
        if (zipcode > 0) {
            query += ("&distance[postal_code]=" + zipcode
                            + "&distance[search_distance]=" + distance 
                            + "&distance[search_units]=mile");
        }
        
        if (offset > 0) {
            query += ("&offset=" + offset);
        }
        if (limit > -1) {
            query += ("&limit=" + limit);
        }
        
        return makeUnauthenticatedGet(query);
    }
    
////////////////////////////////////////////////////////////////////////////////////////////////////
    // Delete Calls (Messages Endpoint)
////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * API call to delete a message or message thread.
     *
     * @param id The unique id of the thread or message.
     * @param type Whether you want to delete a message or thread.
     *      "thread": Thread.
     *      "msg": Message.
     * @return The response after making the API call.
     */
    public static String deleteMessage(String id, String type) {
        
        String query = ENDPOINT + "messages/" + id + "&type=" + type;
        
        return makeAuthenticatedDelete(query);
    }
    
////////////////////////////////////////////////////////////////////////////////////////////////////
    // GET Calls (Messages Endpoint)
////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * API call to fetch the currently logged in user's message inbox.
     *
     * @param offset Determines at what point the API returns data (starts after 'offset' results).
     * @param limit The number of results the API will return.
     * @return Result from API.
     */
    public static String getUserInbox(int offset, int limit) {
        
        String query = ENDPOINT + "messages.json&box=inbox";
        
        if (offset > 0) {
            query += ("&offset=" + offset);
        }
        if (limit > -1) {
            query += ("&limit=" + limit);
        }
        
        return makeAuthenticatedGet(query);
    }

    /**
     * API call to fetch the messages in a specific thread.
     *
     * @param threadId The unique id of the thread to fetch.
     * @return Result from API.
     */
    public static String getUserMessagesByThreadId(String threadId) {
        
        String query = ENDPOINT + "messages/" + threadId + ".json";
        
        return makeAuthenticatedGet(query);
    }
    
    /**
     * API call to fetch the currently logged in user's message sent box.
     *
     * @param offset Determines at what point the API returns data (starts after 'offset' results).
     * @param limit The number of results the API will return.
     *
     * @return: Result from API.
     */
    public static String getUserSentBox(int offset, int limit) {
        
        String query = ENDPOINT + "messages.json&box=sent";
        
        if (offset > 0) {
            query += ("&offset=" + offset);
        }
        if (limit > -1) {
            query += ("&limit=" + limit);
        }
        
        return makeAuthenticatedGet(query);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
    // Post Calls (Messages Endpoint)
////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * API call to create a new message.
     *
     * @param uuids An array of recipient uuids.
     * @param subject The subject of the message.
     * @param body The body of the message.
     * @return Result from API.
     */
    public static String createMessageNew(String[] uuids, String subject, String body) {
        
        String query = ENDPOINT + "messages.json";
        String[][] contents = new String[uuids.length + 2][2];
        
        for (int i = 0; i < uuids.length; i++) {
            contents[i][0] = "recipients[" + i + "]";
            contents[i][1] = uuids[i];
        }
        
        contents[uuids.length][0] = "subject";
        contents[uuids.length][1] = subject;
        contents[uuids.length + 1][0] = "body";
        contents[uuids.length + 1][1] = body;
        
        return makeAuthenticatedPost(query, contents);
    }
    
    /**
     * API call to create a message reply.
     *
     * @param threadId The id of the thread to reply to.
     * @param body The body of the message.
     * @return Result from API.
     */
    public static String createMessageReply (int threadId, String body) {
        
        String query = ENDPOINT + "messages.json";
        String[][] contents = new String[2][2];
        
        contents[0][0] = "thread_id";
        contents[0][1] = "" + threadId;
        contents[1][0] = "body";
        contents[1][1] = body;

        return makeAuthenticatedPost(query, contents);
    }
    
////////////////////////////////////////////////////////////////////////////////////////////////////
    // Put Calls (Messages Endpoint)
////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /** 
     * API call to update a message or message thread status.
     *
     * @param id The unique id of the thread or message.
     * @param status The status you want to update to.
     *      "1": Unread.
     *      "2": Read.
     * @param type Whether a message or thread is to be updated.
     *      "thread": Thread.
     *      "msg": Message.
     * @return: Result from API.
     */
    public static String putMessage(int id, int status, String type) {
        
        String query = ENDPOINT + "messages/" + id + ".json";
        String[][] contents = new String[2][2];

        contents[0][0] = "status";
        contents[0][1] = "" + status;
        contents[1][0] = "type";
        contents[1][1] = type;
        
        return makeAuthenticatedPut(query, contents);
    }
    
////////////////////////////////////////////////////////////////////////////////////////////////////
    // GET Calls (Photos Endpoint)
////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * API call to fetch a photo by its UUID.
     * 
     * @param photoUuid The UUID of the photo that is being fetched.
     * @return Result from API.
     */
    public static String getPhotoByPhotoId(String photoUuid) {
        
        String query = ENDPOINT + "photos/" + photoUuid + ".json";
        
        return makeAuthenticatedGet(query);
    }
    
////////////////////////////////////////////////////////////////////////////////////////////////////
    // GET Calls (Resources Endpoint)
////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * API call to fetch a resource by its ID.
     * 
     * @param resourceId The ID of the photo that is being fetched.
     * @return Result from API.
     */
    public static String getUserResourceByResourceId(String resourceId) {
        
        String query = ENDPOINT + "resources/" + resourceId + ".json";
        
        return makeAuthenticatedGet(query);
    }
    
////////////////////////////////////////////////////////////////////////////////////////////////////
    // GET Calls (Users Endpoint)
////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * API call to fetch the currently logged in user's children.
     *
     * @param offset Determines at what point the API returns data (starts after 'offset' results).
     * @param limit The number of results the API will return.
     * @return Result from API.
     */
    public static String getUserChildren(int offset, int limit) {
        
        String query = ENDPOINT + "users/" + sCurrentUserUUID + "/children.json";
        
        if (offset > 0) {
            query += ("&offset=" + offset);
        }
        if (limit > -1) {
            query += ("&limit=" + limit);
        }
        
        return makeAuthenticatedGet(query);
    }
    
    /**
     * API call to fetch the currently logged in user's upcoming events.
     *
     * @param offset Determines at what point the API returns data (starts after 'offset' results).
     * @param limit The number of results the API will return.
     * @return Result from API.
     */
    public static String getUserEventsUpcoming(int offset, int limit) {
        
        String query = ENDPOINT + "users/" + sCurrentUserUUID + "/events/upcoming.json";
        
        if (offset > 0) {
            query += ("&offset=" + offset);
        }
        if (limit > -1) {
            query += ("&limit=" + limit);
        }
        
        return makeAuthenticatedGet(query);
    }
    
    /**
     * API call to fetch the currently logged in user's friends.
     *
     * @param offset Determines at what point the API returns data (starts after 'offset' results).
     * @param limit The number of results the API will return.
     * @return Result from API.
     */
    public static String getUserFriends(int offset, int limit) {
        
        String query = ENDPOINT + "users/" + sCurrentUserUUID + "/friends.json";
        
        if (offset > 0) {
            query += ("&offset=" + offset);
        }
        if (limit > -1) {
            query += ("&limit=" + limit);
        }
        
        return makeAuthenticatedGet(query).replaceAll("&#039;", "'");
    }
    
    /**
     * API call to fetch the currently logged in user's friends.
     *
     * @param offset Determines at what point the API returns data (starts after 'offset' results).
     * @param limit The number of results the API will return.
     * @return Result from API.
     */
    public static String getUserGroupmates(int offset, int limit) {
        
        String query = ENDPOINT + "users/" + sCurrentUserUUID + "/groupmates.json";
        
        if (offset > 0) {
            query += ("&offset=" + offset);
        }
        if (limit > -1) {
            query += ("&limit=" + limit);
        }
        
        return makeAuthenticatedGet(query);
    }
    
    /**
     * API call to fetch the currently logged in user's groups.
     *
     * @param offset Determines at what point the API returns data (starts after 'offset' results).
     * @param limit The number of results the API will return.
     * @param sort The method that the data should be sorted by:
     *      "radioactivity": Sort the data by group activity (radioactivity).
     *      "alphabetical_ascending": Sort the data in alphabetically ascending order.
     *      "alphabetical_descending": Sort the data in alphabetically descending order.
     * @return Result from API.
     */
    public static String getUserGroups(int offset, int limit, String sort) {
        
        String query = ENDPOINT + "users/" + sCurrentUserUUID + "/groups.json";
        
        if (offset > 0) {
            query += ("&offset=" + offset);
        }
        if (limit > -1) {
            query += ("&limit=" + limit);
        }
        if (sort != null && !sort.equals("")) {
            query += ("&sort=" + sort);
        }
        
        return makeAuthenticatedGet(query);
    }

    /**
     * API call to fetch the currently logged in user's guardians.
     *
     * @param offset Determines at what point the API returns data (starts after 'offset' results).
     * @param limit The number of results the API will return.
     * @return Result from API.
     */
    public static String getUserGuardians(int offset, int limit) {
        
        String query = ENDPOINT + "users/" + sCurrentUserUUID + "/guardians.json";
        
        if (offset > 0) {
            query += ("&offset=" + offset);
        }
        if (limit > -1) {
            query += ("&limit=" + limit);
        }
        
        return makeAuthenticatedGet(query);
    }
    
////////////////////////////////////////////////////////////////////////////////////////////////////
    // POST Calls (Users Endpoint)
////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * API call to create a new account on AllPlayers.com.
     * 
     * @param firstName Registrant's first name.
     * @param lastName Registrant's last name.
     * @param email Registrant's email.
     * @param gender Registrant's gender (M or F).
     * @param birthday Registrant's birthday (YYYY-MM-DD.
     * @param password Registrant's password.
     * @param capToken Captcha Token.
     * @param capResponse Captcha Response.
     * @return The response from the API call.
     */
    public static String createNewUser(String firstName, String lastName, String email,
                                       String gender, String birthday,String password,
                                       String capToken, String capResponse) {

        String query = "https://www.pdup.allplayers.com/api/v1/rest/users.json";
        String[][] contents = new String[6][2];

        // Set firstName
        contents[0][0] = "firstname";
        contents[0][1] = firstName;

        // Set lastName
        contents[1][0] = "lastname";
        contents[1][1] = lastName;

        // Set email
        contents[2][0] = "email";
        contents[2][1] = email;

        // Set gender (M or F)
        contents[3][0] = "gender";
        contents[3][1] = gender;

        // Set birthday (YYYY-MM-DD)
        contents[4][0] = "birthday";
        contents[4][1] = birthday;

        // Set password
        contents[5][0] = "password";
        contents[5][1] = password;

        return makeUnauthenticatedPost(query, contents, capToken, capResponse);
    }

    /**
     * API call to log the user into the API.
     * 
     * @param username The user's entered username (or email).
     * @param password The user's entered password.
     * @return The response after making the API call.
     */
    public String validateLogin(String username, String password) {
        
        String query = ENDPOINT + "users/login.json";
        String[][] contents = new String[2][2];
        
        contents[0][0] = "username";
        contents[0][1] = username;
        contents[1][0] = "password";
        contents[1][1] = password;

        return makeAuthenticatedPost(query, contents);
    }  
}
