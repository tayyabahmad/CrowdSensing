package com.crowd.peekay.crowdsensing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import utils.Post;
import utils.UrlString;

/**
 * Created by Samir KHan on 12/4/2016.
 */
public class ImageDownloadAsync extends AsyncTask {
    Context mContext;
    Post mPost;
    String mUrl;
    ImageView mImg;

    public ImageDownloadAsync(Context context, Post post, String url) {
        this.mContext = context;
        this.mPost = post;
        this.mUrl = url;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        URL url;
        HttpURLConnection connection;
        try {
            // set URL
            mUrl = new UrlString(mContext).getRootUrl() + mUrl;

            url = new URL(mUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(1000);

            mPost.img = BitmapFactory.decodeStream(url.openConnection().getInputStream());

        } catch (MalformedURLException e) {
            // informatted URL
            e.printStackTrace();
        } catch (IOException e) {
            //connection not found..
            e.printStackTrace();
        }
        return null;
    }
}
