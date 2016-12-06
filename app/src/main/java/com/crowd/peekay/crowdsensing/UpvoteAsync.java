package com.crowd.peekay.crowdsensing;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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

import utils.MyPreference;
import utils.Post;
import utils.UrlString;

/**
 * Created by Samir KHan on 12/4/2016.
 */
public class UpvoteAsync extends AsyncTask {
    Context mContext;
    ViewHolder mVh;
    Post mPost;
    int mVote;
    String mUserMsg;

    public UpvoteAsync(Context context, int vote, Post post, ViewHolder vh) {
        this.mContext = context;
        this.mVote = vote;
        this.mPost = post;
        this.mVh = vh;
    }

    @Override
    protected void onPreExecute() {
        mVh.imgUpvote.setEnabled(false);
        mVh.imgDownvote.setEnabled(false);

        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        URL url;
        HttpURLConnection connection;
        String urlString = new UrlString(mContext).getRootUrl() + "upvote.php";

        try {
            MyPreference preference = new MyPreference(mContext);
            String userId = preference.getUserId() + "";

            // add parameters to GET request
            urlString = String.format(urlString + "?vote=%s&user_id=%s&post_id=%s",
                    URLEncoder.encode(mVote + "", "UTF8"),
                    URLEncoder.encode(userId + "", "UTF8"), URLEncoder.encode(mPost.id + "", "UTF8"));

            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(1000);

            InputStream inputStream = connection.getInputStream();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                return br;
            } else {
                mUserMsg = "Sorry, server Couldn't process the request";
            }
        } catch (MalformedURLException e) {
            mUserMsg = "Cannot upvote/downvote, incorrect format of URL";
            e.printStackTrace();
        } catch (IOException e) {
            mUserMsg = "Please make sure the connection is available";
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {

        try {
            BufferedReader br = (BufferedReader) o;
            if (br == null)
                throw new NullPointerException("Please make sure the connection is available");

            StringBuilder sb = new StringBuilder("");
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            JSONArray jsonArray = new JSONArray(sb.toString());
            final JSONObject jsonObject = jsonArray.getJSONObject(0);

            switch (jsonObject.getString("response")) {
                case "success":
                    Drawable upvoteDrawable = null, downvoteDrawable = null;
                    String totalUpvotes = jsonObject.getString("upvotes");
                    String totalDownvotes = jsonObject.getString("downvotes");

                    if (mPost.vote == 0 && mVote == 1) {
                        mPost.vote = 1;
                        upvoteDrawable = mContext.getResources().getDrawable(R.drawable.up_arrow_red);
                        downvoteDrawable = mContext.getResources().getDrawable(R.drawable.down_arrow);

                    } else if (mPost.vote == 0 && mVote == -1) {
                        mPost.vote = -1;
                        upvoteDrawable = mContext.getResources().getDrawable(R.drawable.up_arrow);
                        downvoteDrawable = mContext.getResources().getDrawable(R.drawable.down_arrow_red);

                    } else if (mPost.vote == 1 && mVote == 1) {
                        mPost.vote = 0;
                        upvoteDrawable = mContext.getResources().getDrawable(R.drawable.up_arrow);
                        downvoteDrawable = mContext.getResources().getDrawable(R.drawable.down_arrow);
                    } else if (mPost.vote == 1 && mVote == -1) {
                        mPost.vote = -1;
                        upvoteDrawable = mContext.getResources().getDrawable(R.drawable.up_arrow);
                        downvoteDrawable = mContext.getResources().getDrawable(R.drawable.down_arrow_red);

                    } else if (mPost.vote == -1 && mVote == -1) {
                        mPost.vote = 0;
                        upvoteDrawable = mContext.getResources().getDrawable(R.drawable.up_arrow);
                        downvoteDrawable = mContext.getResources().getDrawable(R.drawable.down_arrow);

                    } else if (mPost.vote == -1 && mVote == 1) {
                        mPost.vote = 1;
                        upvoteDrawable = mContext.getResources().getDrawable(R.drawable.up_arrow_red);
                        downvoteDrawable = mContext.getResources().getDrawable(R.drawable.down_arrow);

                    } else {
                        mPost.vote = 0;
                        upvoteDrawable = mContext.getResources().getDrawable(R.drawable.up_arrow);
                        downvoteDrawable = mContext.getResources().getDrawable(R.drawable.down_arrow);
                    }

                    mPost.totalUpvotes = Integer.parseInt(totalUpvotes);
                    mPost.totalDownvotes = Integer.parseInt(totalDownvotes);

                    mVh.textUpvotes.setText(totalUpvotes);
                    mVh.textDownvotes.setText(totalDownvotes);
                    mVh.imgUpvote.setImageDrawable(upvoteDrawable);
                    mVh.imgDownvote.setImageDrawable(downvoteDrawable);

                    break;
                default:
                    //if there is any SEVER error while updating upvote & downvote
                    mUserMsg = "Error occured while upvoting/downvoting the post";
                    break;
            }
        } catch (JSONException e) {
            mUserMsg = "Error occured while upvoting/downvoting the post";
            e.printStackTrace();
        } catch (IOException e) {
            mUserMsg = "Please make sure the connection is available";
            e.printStackTrace();
        } catch (NullPointerException e) {
            mUserMsg = e.getMessage();
            e.printStackTrace();
        }

        if (mUserMsg != null) {
            Toast.makeText(mContext, mUserMsg, Toast.LENGTH_SHORT).show();
        }
        //after vote/downvote make these Enable
        mVh.imgUpvote.setEnabled(true);
        mVh.imgDownvote.setEnabled(true);

        super.onPostExecute(o);
    }
}
