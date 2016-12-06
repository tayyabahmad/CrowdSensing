package utils;

import android.content.Context;

/**
 * Created by Samir KHan on 10/3/2016.
 */
public class UrlString {

    private Context mContext;
    private final String PATH = "/crowd/";
    public UrlString(Context context) {
        this.mContext = context;
    }

    //Method: get root-url
    public String getRootUrl() {
        MyPreference preference = new MyPreference(mContext);
        return preference.getServerUrl()+PATH;
    }
}
