package org.overlake.mat803.criminalintent;

import android.net.Uri;

import java.util.Date;
import java.util.UUID;

public class Crime {

    private UUID mID;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private int mSuspectId;
    private String mSuspectName;
    private String mSupsectPhone;

    public int getSuspectId() {
        return mSuspectId;
    }

    public void setSuspectId(int suspectId) {
        mSuspectId = suspectId;
    }

    public String getPhotoFilename(){
        return "IMG_" + getID() + ".jpg";
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

    public String getSuspectName() {
        return mSuspectName;
    }

    public void setSuspectName(String suspectName) {
        mSuspectName = suspectName;
    }

    public String getSuspectPhone() {
        return mSupsectPhone;
    }

    public void setSupsectPhone(String supsectPhone) {
        mSupsectPhone = supsectPhone;
    }


}

