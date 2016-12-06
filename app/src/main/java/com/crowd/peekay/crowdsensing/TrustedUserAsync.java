package com.crowd.peekay.crowdsensing;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import utils.MyPreference;
import utils.Post;
import utils.UrlString;

/**
 * Created by Samir KHan on 12/2/2016.
 */

public class TrustedUserAsync extends AsyncTask {
    Context mContext;
    ProgressDialog progressDialog;
    Post mPost;
    String mUserMsg;
    ;

    public TrustedUserAsync(Context context, Post post) {
        this.mContext = context;
        this.mPost = post;
        progressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setMessage("Loading trusted users..");
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        URL url;
        HttpURLConnection connection;
        String urlString = new UrlString(mContext).getRootUrl() + "trusted_user.php";

        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(1000);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                return br;
            }
        } catch (MalformedURLException e) {
            mUserMsg = "Poorly URL formatted";
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

        //hide the progress-dialog
        progressDialog.hide();

        try {
            BufferedReader br = (BufferedReader) o;

            String line, data = "";
            while ((line = br.readLine()) != null) {
                data = data + line;
            }

            final JSONArray jsonArray = new JSONArray(data);
            JSONObject jsonObject = jsonArray.getJSONObject(0);

            //we don't need post button while posting via trusted-user
            boolean showPostBtn = false;

            //create an alert-dialog
            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Trusted Users");

            //if trusted-user exists, then show list
            // else if doesn't exist, proceed with-self
            // else if error occure, show error msg..
            if (jsonObject.getString("response").equals("success")) {
                final ArrayAdapter adapter = new
                        ArrayAdapter(mContext, android.R.layout.select_dialog_singlechoice);

                //get the top trusted-user from the same jsonObject as of 'response'
                adapter.add(jsonObject.getString("username"));

                //get remaining all trusted-users - put in adapter
                for (int i = 1; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    adapter.add(jsonObject.getString("username"));
                }

                //set alert adapter..
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {

                        //TODO: [should be a better way to do this, fuck]
                        //when user select trusted-user,
                        //find his id from json-array..
                        JSONObject jsonObject;
                        for (int i = 0; i < jsonArray.length(); i++) {

                            try {
                                jsonObject = jsonArray.getJSONObject(i);
                                if (jsonObject.getString("username").equals(adapter.getItem(which).toString())) {
                                    mPost.trustedUserId = jsonObject.getInt("id");
                                    break;
                                }
                            } catch (JSONException e) {
                                //Actually, this shouldn't be possible in any case
                                //I mean, if we have got username from this json-data, then
                                //why can't we get his ID?
                                e.printStackTrace();
                            }
                        }
                    }
                });

                // netural button for self-posting incase of having trusted-users though..
                builder.setNeutralButton("Post as Me", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        // set user-id
                        MyPreference prefs = new MyPreference(mContext);
                        mPost.trustedUserId = prefs.getUserId();

                        //start posting and finish the dialog
                        AddPostAsync async = new AddPostAsync(mContext, mPost);
                        async.execute();
                        dialogInterface.dismiss();

                        //set text for toast
                        mUserMsg = "Posting..";
                    }
                });

            } else if (jsonObject.getString("response").equals("no-user")) {
                showPostBtn = true;
                builder.setMessage("No trusted user exists, " +
                        "Are you agree to continue this post as your self?");
            } else if (jsonObject.getString("response").equals("error")) {
                showPostBtn = true;
                builder.setMessage("Couldn't get trusted-users." +
                        " Are you agree to continue this post as your self?");
            }

            // set user-id
            MyPreference prefs = new MyPreference(mContext);
            mPost.trustedUserId = prefs.getUserId();

            builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            //if trusted-user list exists then don't add button +ive
            if (showPostBtn)
                builder.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //start posting and finish the dialog
                        AddPostAsync async = new AddPostAsync(mContext, mPost);
                        async.execute();
                        dialog.dismiss();

                        //set text for toast
                        mUserMsg = "Posting..";
                    }
                });

            builder.show();
        } catch (IOException e) {
            mUserMsg = "Error via Posting, Please check connection and try again";
            e.printStackTrace();
        } catch (JSONException e) {
            mUserMsg = "Incorrect response from server, please try again";
            e.printStackTrace();
        } finally {
            if (mUserMsg != null) {
                Toast.makeText(mContext, mUserMsg, Toast.LENGTH_SHORT).show();
            }

            super.onPostExecute(o);
        }
    }
}
