package com.crowd.peekay.crowdsensing;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import utils.MyPreference;
import utils.Post;

public class TimelineActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);


        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        ArrayList<Post> posts = new ArrayList<>();
        mAdapter = new CustomAdapter(TimelineActivity.this, posts);
        mRecyclerView.setAdapter(mAdapter);

        // start loading data from a server
        LoadDataAsync async = new LoadDataAsync(this, mRecyclerView);
        async.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_timeline, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_post) {
            NewPostDialog dialog = new NewPostDialog();
            dialog.show(getFragmentManager(), "add_post");
        } else if (id == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if(id == R.id.profile){
        Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Logout");
            builder.setMessage("Do you want to continue logging-out?");
            builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MyPreference preference = new MyPreference(TimelineActivity.this);
                    preference.removeUserId();
                    preference.removeUsername();

                    Intent intent = new Intent(TimelineActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }
}   // end of Class: TimelineActivity

/*
* ------------------------------ CLASS: CustomAdapter  ---------------------------
* */

class CustomAdapter extends RecyclerView.Adapter {
    Context mContext;
    List<Post> mPost;

    public CustomAdapter(Context context, List<Post> post) {
        this.mContext = context;
        this.mPost = post;
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_timeline, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final ViewHolder vh = (ViewHolder) holder;

        vh.textUsername.setText(mPost.get(position).username);
        vh.textAdd.setText(mPost.get(position).location);
        vh.textPost.setText(mPost.get(position).text);
        if(mPost.get(position).img != null)
        vh.imgPost.setImageBitmap(mPost.get(position).img);
        else
        vh.imgPost.setImageBitmap(null);
        vh.textUpvotes.setText(mPost.get(position).totalUpvotes + "");
        vh.textDownvotes.setText(mPost.get(position).totalDownvotes + "");

        int vote = mPost.get(position).vote;
        if (vote == 1) {
            vh.imgUpvote.setImageDrawable(mContext.getResources().getDrawable(R.drawable.up_arrow_red));
            vh.imgDownvote.setImageDrawable(mContext.getResources().getDrawable(R.drawable.down_arrow));
        } else if(vote == -1){
            vh.imgUpvote.setImageDrawable(mContext.getResources().getDrawable(R.drawable.up_arrow));
            vh.imgDownvote.setImageDrawable(mContext.getResources().getDrawable(R.drawable.down_arrow_red));
        } else {
            vh.imgUpvote.setImageDrawable(mContext.getResources().getDrawable(R.drawable.up_arrow));
            vh.imgDownvote.setImageDrawable(mContext.getResources().getDrawable(R.drawable.down_arrow));
        }

        vh.imgUpvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int voted;
                if(mPost.get(position).vote == 1){
                    voted = 0;
                } else
                    voted = 1;

                UpvoteAsync upvoteAsync = new UpvoteAsync(mContext, voted,
                        mPost.get(position), vh);
                upvoteAsync.execute();
            }
        });

        vh.imgDownvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int voted;
                if(mPost.get(position).vote == -1){
                    voted = 0;
                } else
                    voted = -1;

                UpvoteAsync upvoteAsync = new UpvoteAsync(mContext, voted,
                        mPost.get(position), vh);
                upvoteAsync.execute();
            }
        });
    }
}   //end of Class: Custom Adapter

/*
* ------------------------------ CLASS: ViewHolder  ---------------------------
* */

class ViewHolder extends RecyclerView.ViewHolder {
    public TextView textUsername, textAdd, textPost, textUpvotes, textDownvotes;
    public ImageView imgPost, imgUpvote, imgDownvote;

    public ViewHolder(View v) {
        super(v);

        textUsername = (TextView) v.findViewById(R.id.list_username);
        textAdd = (TextView) v.findViewById(R.id.list_address);
        textPost = (TextView) v.findViewById(R.id.list_post);
        imgPost = (ImageView) v.findViewById(R.id.list_img);
        imgUpvote = (ImageView) v.findViewById(R.id.list_upvote);
        imgDownvote = (ImageView) v.findViewById(R.id.list_downvote);
        textUpvotes = (TextView) v.findViewById(R.id.list_text_upvote);
        textDownvotes = (TextView) v.findViewById(R.id.list_text_downvote);
    }
}  // end of Class: ViewHolder
