package com.crowd.peekay.crowdsensing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.zip.Inflater;

import utils.MyPreference;
import utils.PostRequestData;
import utils.UrlString;

/**
 * Created by Samir KHan on 12/4/2016.
 */
public class ConfirmPasswordAsync extends AsyncTask {
    Context mContext;
    ProgressDialog mProgressDialog;
    String mPassword, mUserMsg, mField;

    public ConfirmPasswordAsync(Context context, String password, String field) {
        this.mContext = context;
        this.mPassword = password;
        this.mField = field;
        mProgressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog.setMessage("Checking password..");
        mProgressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        URL url;
        HttpURLConnection connection;
        String urlString = new UrlString(mContext).getRootUrl() + "confirm_password.php";

        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(1000);

            OutputStream os = connection.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            HashMap<String, String> param = new HashMap<String, String>();
            param.put("id", new MyPreference(mContext).getUserId() + "");
            param.put("password", mPassword);

            bw.write(PostRequestData.getData(param));
            bw.flush();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                return br;
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
        try {
            //connection isn't available or something is wrong with server address
            if (mUserMsg != null)
                throw new IOException();

            BufferedReader br = (BufferedReader) o;
            if (br == null)
                throw new NullPointerException("BufferedReader instance couldn't be NULL");

            String data = "", line;
            while ((line = br.readLine()) != null) {
                data += line;
            }

            if (data == null || data.equals(""))
                throw new NullPointerException("Server response couldn't be empty");

            // convert jsonString into jsonArray and get first object
            JSONArray jsonArray = new JSONArray(data);
            JSONObject jsonObject = jsonArray.getJSONObject(0);

            // parse server response
            switch (jsonObject.getString("response")) {
                case "success":
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    View v = View.inflate(mContext, R.layout.dialog_custom, null);
                    TextView textHeader = (TextView) v.findViewById(R.id.custom_dialog_header);
                    final EditText textInput = (EditText) v.findViewById(R.id.custom_dialog_text);

                    //capitalize First Character
                    textHeader.setText("Update " + mField.replaceFirst(mField.charAt(0) + "",
                            Character.toUpperCase(mField.charAt(0)) + ""));

                    textInput.setHint("New " + mField.replaceFirst(mField.charAt(0) + "",
                            Character.toUpperCase(mField.charAt(0)) + ""));
                    builder.setView(v);
                    builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setPositiveButton(R.string.action_save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            UpdateProfileAsync updateProfile = new UpdateProfileAsync(mContext,
                                    mField, textInput.getText().toString().trim());
                            updateProfile.execute();
                            dialogInterface.dismiss();

                        }
                    });
                    builder.show();
                    break;
                case "incorrect":
                    mUserMsg = "Incorrect password, please try again..";
                    break;
                default:
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
