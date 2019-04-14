package org.overlake.mat803.criminalintent;

import android.net.Uri;

import java.util.Date;
import java.util.UUID;

public class Crime {

    private UUID mID;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private Uri mSuspectUri;

    public Uri getSuspectUri() {
        return mSuspectUri;
    }

    public void setSuspectUri(Uri suspectUri) {
        mSuspectUri = suspectUri;
    }



    public Crime(){
        this(UUID.randomUUID());
    }

    public Crime(UUID id){
        mID = id;
        mDate = new Date();
    }

    public UUID getID() {
        return mID;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }
}
