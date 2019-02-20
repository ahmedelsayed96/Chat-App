package ahmed.example.com.chatapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by root on 05/10/17.
 */

public class FriendData implements Parcelable {

    public static final Creator<FriendData> CREATOR = new Creator<FriendData>() {
        @Override
        public FriendData createFromParcel(Parcel in) {
            return new FriendData(in);
        }

        @Override
        public FriendData[] newArray(int size) {
            return new FriendData[size];
        }
    };
    String Name;
    String Image;

    public FriendData(String name, String image) {
        Name = name;
        Image = image;
    }

    protected FriendData(Parcel in) {
        Name = in.readString();
        Image = in.readString();
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Name);
        parcel.writeString(Image);
    }
}


