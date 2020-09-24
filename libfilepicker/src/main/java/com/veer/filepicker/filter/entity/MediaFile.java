package com.veer.filepicker.filter.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <li>Author:Veer</li>
 * <li>Date:  2020/3/6</li>
 * <li>Description: </li>
 */
public class MediaFile extends BaseFile implements Parcelable {
    private long duration;
    private String thumbnail;
    private String mediaType;
    private int width;
    private int height;

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
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
        dest.writeString(getThumbnail());
        dest.writeString(getMediaType());
        dest.writeInt(getWidth());
        dest.writeInt(getHeight());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MediaFile> CREATOR = new Creator<MediaFile>() {
        @Override
        public MediaFile[] newArray(int size) {
            return new MediaFile[size];
        }

        @Override
        public MediaFile createFromParcel(Parcel in) {
            MediaFile file = new MediaFile();
            file.setId(in.readLong());
            file.setName(in.readString());
            file.setPath(in.readString());
            file.setSize(in.readLong());
            file.setBucketId(in.readString());
            file.setBucketName(in.readString());
            file.setDate(in.readLong());
            file.setSelected(in.readByte() != 0);
            file.setDuration(in.readLong());
            file.setThumbnail(in.readString());
            file.setMediaType(in.readString());
            file.setWidth(in.readInt());
            file.setHeight(in.readInt());
            return file;
        }
    };
}
