package uk.droidcon.dina12.uk.droidcon.dina12.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class User implements Parcelable {

    public String email;
    public String token;

    public String uid;

    public User(String userInfo) throws InvalidUserInfo {
        String[] parts = userInfo.split("\\s");
        if (parts.length == 2) {
            email = parts[0];
            if (email.length() > 1) {
                //ignore the en token string
                email = email.substring(3);
            }
            token = parts[1];
        } else {
            throw new InvalidUserInfo();
        }

    }

    public static class InvalidUserInfo extends Exception {
    }

    public String getAvatarLink() {
        return String.format("http://www.appsrise.net/api/dina12/%s.jpg", uid);
    }

    public static User register() {
        // TODO Create a user
        return null;
    }

    public void update() {
        // TODO Update the user's private information
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.email);
        dest.writeString(this.token);
        dest.writeString(this.uid);
    }

    protected User(Parcel in) {
        this.email = in.readString();
        this.token = in.readString();
        this.uid = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
