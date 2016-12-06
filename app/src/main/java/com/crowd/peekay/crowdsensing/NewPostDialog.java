package com.crowd.peekay.crowdsensing;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import utils.MyPreference;
import utils.Post;
import utils.PostRequestData;
import utils.UrlString;

public class NewPostDialog extends DialogFragment {
    EditText mTextPost, mTextLoc;
    ImageView mImgPost;
    Button mBtnPost;
    Bitmap mBitmap;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_new_post, null);
        mTextPost = (EditText) v.findViewById(R.id.post_text);
        mTextLoc = (EditText) v.findViewById(R.id.post_loc);
        mImgPost = (ImageView) v.findViewById(R.id.post_img);
        mBtnPost = (Button) v.findViewById(R.id.post_btn_upload);

        //action listener
        mBtnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // set dialog positve button
        builder.setPositiveButton("Post", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //check user input validation
                String msg = Validate();
                if (msg != null) {
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    return;
                }

                String text = mTextPost.getText().toString().trim();
                String loc = mTextLoc.getText().toString().trim();

                //initialize post instance
                Post p = new Post();
                p.text = text;
                p.location = loc;
                p.img = mBitmap;

                //check for trusted-users
                TrustedUserAsync async = new TrustedUserAsync(getActivity(), p);
                async.execute();
            }
        });
        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {

                Uri uri = data.getData();
                String mUserMsg = null;

                try {
                    ParcelFileDescriptor parcelFileDescriptor =
                            getActivity().getContentResolver().openFileDescriptor(uri, "r");

                    //get bitmap from the intent result..
                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                    Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                    parcelFileDescriptor.close();

                    //resize image height and width..
                    /*ViewGroup.LayoutParams params = mImgPost.getLayoutParams();
                    params.height = 300;
                    mImgPost.setLayoutParams(params);*/

                    mImgPost.setImageBitmap(bitmap);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                   // mBaseString = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    mBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                } catch (FileNotFoundException e) {
                    mUserMsg = "The file cannot exist on this device.";
                    e.printStackTrace();
                } catch (IOException e) {
                    mUserMsg = "Unable to read the selected image";
                    e.printStackTrace();
                }

                //if error occure while getting a picture
                //create and show alert-dialog accordingly
                if (mUserMsg != null) {
                    AlertDialog.Builder dialogError = new AlertDialog.Builder(getActivity());
                    dialogError.setTitle("Error");
                    dialogError.setMessage(mUserMsg);

                    dialogError.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    dialogError.show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Method: validate user-input
    public String Validate() {
        String msg = null;

        if (mTextPost.getText().toString().trim().length() < 3) {
            msg = "Post should be larger than 3 characters.";
        } else if (mTextLoc.getText().toString().trim().length() < 3) {
            msg = "Location should be larger than 3 characters.";
        }
        return msg;
    }
}   // end of Class: NewPostDialog

/*
* ------------------------------ CLASS: AddPostAsync ASYNC ---------------------------
* */

class AddPostAsync extends AsyncTask {
    Context mContext;
    ProgressDialog mProgressDialog;
    Post mPost;
    String mUserMsg;

    public AddPostAsync(Context context, Post post) {
        this.mContext = context;
        this.mPost = post;
        mProgressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog.setMessage("Posting..");
        mProgressDialog.show();

        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        URL url;
        HttpURLConnection connection;
        String urlString = new UrlString(mContext).getRootUrl() + "post.php";
        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(1000);

            OutputStreamWriter sw = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            BufferedWriter bw = new BufferedWriter(sw);

            // add parameters to POST request
            HashMap<String, String> param = new HashMap<String, String>();
            param.put("text", mPost.text);
            param.put("loc", mPost.location);
            param.put("user_id", new MyPreference(mContext).getUserId() + "");
            param.put("upload_user", mPost.trustedUserId + "");

            if (mPost.img != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                mPost.img.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] ba = stream.toByteArray();
               String base64String =  Base64.encodeToString(ba, Base64.DEFAULT);
                param.put("img", base64String);
            }


            bw.write(PostRequestData.getData(param));
            bw.flush();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                return br;
            } else {
                mUserMsg = "Server Couldn't process the request";
            }
        } catch (MalformedURLException e) {
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

            // json-string to json-array then get response from that
            JSONArray jsonArray = new JSONArray(data);
            JSONObject jsonObject = jsonArray.getJSONObject(0);

            switch (jsonObject.getString("response")) {
                case "success":
                    // redirect to another from here..
                    Intent intent = new Intent(((Activity) mContext), NewPostDialog.class);
                    ((Activity) mContext).recreate();
                    break;

                case "error":
                    mUserMsg = "Something went wrong on server";
                    break;
                case "img-error":
                    mUserMsg = "Couldn't post an Image.";
                    break;
            }
        } catch (JSONException e) {
            mUserMsg = "Incorrect formatted data from server";
            e.printStackTrace();
        } catch (IOException e) {
            //if connection lost while posting..
            if (mUserMsg != null)
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