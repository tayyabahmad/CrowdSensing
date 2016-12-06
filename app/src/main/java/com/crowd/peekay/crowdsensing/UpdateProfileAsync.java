package com.crowd.peekay.crowdsensing;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import utils.MyPreference;
import utils.PostRequestData;
import utils.UrlString;

/**
 * Created by Samir KHan on 12/5/2016.
 */
public class UpdateProfileAsync extends AsyncTask {

    Context mContext;
    String mField, mValue, mUserMsg;
    ProgressDialog mProgressDialog;

    public UpdateProfileAsync(Context context, String field, String value) {
        this.mContext = context;
        this.mField = field;
        this.mValue = value;
        mProgressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog.setMessage("Updating user profile..");
        mProgressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        URL url;
        HttpURLConnection connection;
        String urlString = new UrlString(mContext).getRootUrl() + "update_field.php";
        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            OutputStream os = connection.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            HashMap<String, String> param = new HashMap<>();
            param.put("id", new MyPreference(mContext).getUserId() + "");
            param.put("value", mValue);
            param.put("field", mField);

            bw.write(PostRequestData.getData(param));
            bw.flush();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String data = "", line;
                while ((line = br.readLine()) != null) {
                    data += line;
                }
                return data;
            } else {
                mUserMsg = "Server Couldn't process the request";
            }

        } catch (IOException e) {
            mUserMsg = "Please make sure that Internet connection is available," +
                    " and server IP is inserted in settings";
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        mProgressDialog.hide();
        String data = (String) o;
        try {
            //connection isn't available or something is wrong with server address
            if (mUserMsg != null)
                throw new IOException();

            if (data == null || data.equals(""))
                throw new NullPointerException("Server response couldn't be empty");

            // convert jsonString into jsonArray and get first object
            JSONArray jsonArray = new JSONArray(data);
            JSONObject jsonObject = jsonArray.getJSONObject(0);

            // parse server response
            switch (jsonObject.getString("response")) {
                case "success":
                    mUserMsg = mField + " successfully updated";
                    break;
                case "taken":
                    mUserMsg = "username "+mValue + " is already taken. Please try another one";
                    break;
                default:
                    mUserMsg = "Something went wrong on Server";
                    break;
            }

        } catch (JSONException e) {
            mUserMsg = "Incorrect formatted data from server";
            e.printStackTrace();
        } catch (IOException e) {
            //if connection was available via connecting but
            //we can't get data from server..
            if (mUserMsg == null)
                mUserMsg = "Please check connection";
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
            mUserMsg = e.getMessage();
        } finally {
            if (mUserMsg != null)
                Toast.makeText(mContext, mUserMsg, Toast.LENGTH_SHORT).show();
        }

        super.onPostExecute(o);
    }
}
