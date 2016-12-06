package utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Samir KHan on 10/3/2016.
 */
public class MyPreference {

    private final String FILE = "com.samirkhan.crowd.file";
    private final String USERNAME = "username";
    private final String USER_ID = "userId";
    private final String SERVER_URL = "serverUrl";
    private final String LOCATION = "location";
    private final String POST_TEXT = "postText";

    Context mContext;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public MyPreference(Context c) {
        this.mContext = c;
        preferences = mContext.getSharedPreferences(FILE, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    /*  USERNAME*/
    public void setUsername(String username) {
        editor.putString(USERNAME, username);
        editor.apply();
    }

    public String getUsername() {
        return preferences.getString(USERNAME, null);
    }

    public void removeUsername() {
        editor.remove(USERNAME);
        editor.apply();
    }

    /*  USER ID*/
    public void setUserId(int addUserId) {
        editor.putInt(USER_ID, addUserId);
        editor.apply();
    }

    public int getUserId() {
        return preferences.getInt(USER_ID, -1);
    }

    public void removeUserId() {
        editor.remove(USER_ID);
        editor.apply();
    }

    /*  SERVER URL  */
    public void setServerUrl(String url) {
        editor.putString(SERVER_URL, url);
        editor.apply();
    }

    public String getServerUrl() {
        return preferences.getString(SERVER_URL, null);
    }

    public void removeServerUrl() {
        editor.remove(SERVER_URL);
        editor.apply();
    }

    /*  Post Location  */
    public void setLocation(String location) {
        editor.putString(LOCATION, location);
        editor.apply();
    }

    public String getLocation() {
        return preferences.getString(LOCATION, null);
    }

    public void removeLocation() {
        editor.remove(LOCATION);
        editor.apply();
    }

    /*  POST TEXT  */
    public void setPostText(String text) {
        editor.putString(POST_TEXT, text);
        editor.apply();
    }

    public String getPostText() {
        return preferences.getString(POST_TEXT, null);
    }

    public void removePostText() {
        editor.remove(SERVER_URL);
        editor.apply();
    }

}
