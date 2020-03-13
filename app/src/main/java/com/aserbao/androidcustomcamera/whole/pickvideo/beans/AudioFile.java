package com.aserbao.androidcustomcamera.whole.pickvideo.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Vincent Woo
 * Date: 2016/10/11
 * Time: 15:52
 */

public class AudioFile extends BaseFile implements Parcelable {
    private long duration;

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getId());
        dest.writeString(getName());
        dest.writeString(getPath());
        dest.writeLong(getSize());
        dest.writeString(getBucketId());
        dest.writeString(getBucketName());
        dest.writeLong(getDate());
        dest.writeByte((byte) (isSelected() ? 1 : 0));
        dest.writeLong(getDuration());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AudioFile> CREATOR = new Creator<AudioFile>() {
        @Override
        public AudioFile[] newArray(int size) {
            return new AudioFile[size];
        }

        @Override
        public AudioFile createFromParcel(Parcel in) {
            AudioFile file = new AudioFile();
            file.setId(in.readLong());
            file.setName(in.readString());
            file.setPath(in.readString());
            file.setSize(in.readLong());
            file.setBucketId(in.readString());
            file.setBucketName(in.readString());
            file.setDate(in.readLong());
            file.setSelected(in.readByte() != 0);
            file.setDuration(in.readLong());
            return file;
        }
    };
}
