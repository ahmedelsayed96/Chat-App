package ahmed.example.com.chatapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by root on 04/10/17.
 */

public class RoomData implements Parcelable {


    public static final Creator<RoomData> CREATOR = new Creator<RoomData>() {
        @Override
        public RoomData createFromParcel(Parcel in) {
            return new RoomData(in);
        }

        @Override
        public RoomData[] newArray(int size) {
            return new RoomData[size];
        }
    };
    private String lastMessage;
    private long lastDate;
    private FriendData friendData;

    public RoomData() {

    }

    protected RoomData(Parcel in) {
        lastMessage = in.readString();
        lastDate = in.readLong();
        friendData = in.readParcelable(FriendData.class.getClassLoader());
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastDate() {
        return lastDate;
    }

    public void setLastDate(long lastDate) {
        this.lastDate = lastDate;
    }

    public FriendData getFriendData() {
        return friendData;
    }

    public void setFriendData(FriendData friendData) {
        this.friendData = friendData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(lastMessage);
        parcel.writeLong(lastDate);
        parcel.writeParcelable(friendData, i);
    }
}
