package com.evelyn.evelynaidlserver;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Student implements Parcelable {

    private int mId;
    private String mName;

    private Student(Parcel source) {
        mId = source.readInt();
        mName = source.readString();
    }

    public Student(int id, String name) {
        this.mId = id;
        this.mName = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mName);
    }

    public static final Parcelable.Creator<Student> CREATOR = new Parcelable.Creator<Student>() {
        @Override public Student createFromParcel(Parcel source) {
            return new Student(source);
        }

        @Override public Student[] newArray(int size) {
            return new Student[size];
        }
    };

    @Override public String toString() {
        return "ID: " + mId + ", Name: " + mName;
    }
}

