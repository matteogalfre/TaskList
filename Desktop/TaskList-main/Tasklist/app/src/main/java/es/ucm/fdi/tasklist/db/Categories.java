package es.ucm.fdi.tasklist.db;

import android.os.Parcel;
import android.os.Parcelable;

public class Categories implements Parcelable {
    private int color;
    private String type;

    public Categories(String type, int color) {
        this.color = color;
        this.type = type;
    }

    protected Categories(Parcel in) {
        color = in.readInt();
        type = in.readString();
    }

    public static final Creator<Categories> CREATOR = new Creator<Categories>() {
        @Override
        public Categories createFromParcel(Parcel in) {
            return new Categories(in);
        }

        @Override
        public Categories[] newArray(int size) {
            return new Categories[size];
        }
    };

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categories that = (Categories) o;
        return type.equals(((Categories) o).getType());
    }

    @Override
    public String toString() {
        return type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeInt(color);
    }
}

