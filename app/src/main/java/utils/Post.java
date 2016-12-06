package utils;

import android.graphics.Bitmap;

/**
 * Created by Peekay on 10/4/2016.
 */

public class Post {

    public String username, location, text, date, modifiedDate;
    public int id;      /*  post id  */
    public int userId;  /*  post-uploader    */
    public int trustedUserId;   /* trusted-user, may be the user it self - trustedUserId = userId */
    public int vote;    /* for Upvote it's 1, for downVote it's -1, 0 is for undo   */
    public int totalUpvotes, totalDownvotes;
    public Bitmap img;
}

