package com.crowd.peekay.crowdsensing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import utils.MyPreference;
import utils.Post;
import utils.UrlString;

/**
 * Created by Samir KHan on 12/4/2016.
 */
public class LoadDataAsync extends AsyncTask {
    Context mContext;
    String mUserMsg;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;

    public LoadDataAsync(Context context, RecyclerView recyclerView) {
        this.mContext = context;
        this.mRecyclerView = recyclerView;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        URL url;
        HttpURLConnection connection;
        String urlString = new UrlString(mContext).getRootUrl() + "get_data.php";
        try {
            // url including parameter
            urlString = String.format(urlString + "?id=%s",
                    URLEncoder.encode(new MyPreference(mContext).getUserId() + "", "UTF-8"));

            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(1000);

            InputStream inputStream = connection.getInputStream();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder("");
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            } else {
                mUserMsg = "Server Couldn't process the request";
            }
        } catch (MalformedURLException e) {
            mUserMsg = "Incorrect URL formatted";
            e.printStackTrace();
        } catch (IOException e) {
            mUserMsg = "Please make sure that Internet Connection is available," +
                    " and server IP is inserted in settings";
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        List<Post> posts = new ArrayList<>();

        try {
            //connection isn't available or something is wrong with server address
            if (mUserMsg != null)
                throw new IOException();

            String data = (String) o;
            if (data == null || data.equals(""))
                throw new NullPointerException("Server response couldn't be empty");

            // json-string to json-array then get response from first object
            JSONArray jsonArray = new JSONArray(data);
            JSONObject jsonObject = jsonArray.getJSONObject(0);

            if (jsonObject.getString("response").equals("success")) {

                Post post = null;
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    post = new Post();

                    post.id = jsonObject.getInt("id");
                    post.userId = jsonObject.getInt("user_id");
                    post.username = jsonObject.getString("username");
                    post.text = jsonObject.getString("text");
                    post.location = jsonObject.getString("loc");
                    post.date = jsonObject.getString("date");
                    post.modifiedDate = jsonObject.getString("mod_date");
                    post.totalUpvotes = jsonObject.getInt("upvotes");
                    post.totalDownvotes = jsonObject.getInt("downvotes");
                    post.vote = jsonObject.getInt("voted");

                    if (!jsonObject.getString("img").equals("")) {
                        post.img = BitmapFactory.decodeResource(mContext.getResources(),
                                R.drawable.ic_default);

                        ImageDownloadAsync imageDownload = new ImageDownloadAsync(mContext, post,
                                jsonObject.getString("img"));
                        imageDownload.execute();
                    }

                    posts.add(post);
                }
            } else {
                mUserMsg = "Something went wrong on server, please confirm connectivity";
            }
        } catch (JSONException e) {
            mUserMsg = "Incorrect formatted data from server";
            e.printStackTrace();
        } catch (NullPointerException e) {
            mUserMsg = e.getMessage();
            e.printStackTrace();
        } catch (IOException e) {
            //if connection was available via connecting but
            //we can't get data from server..
            if (mUserMsg == null)
                mUserMsg = "Please check connection";
            e.printStackTrace();
        } finally {
            if (mUserMsg != null || posts.size() < 1) {
                Toast.makeText(mContext, mUserMsg, Toast.LENGTH_SHORT).show();
            } else {
                mAdapter = new CustomAdapter(mContext, posts);
                if (mAdapter != null)
                    mRecyclerView.setAdapter(mAdapter);
            }
        }
        super.onPostExecute(o);
    }
}