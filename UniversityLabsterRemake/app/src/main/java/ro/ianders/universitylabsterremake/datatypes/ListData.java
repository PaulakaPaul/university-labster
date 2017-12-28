package ro.ianders.universitylabsterremake.datatypes;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by paul.iusztin on 28.12.2017.
 */

public class ListData implements Parcelable { // we need to implement the Parcelable interface so we can send ListData objects threw intents

    //used by the Parcelable interface
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @NonNull
        public ListData createFromParcel(Parcel in) {
            return new ListData(in);
        }

        public ListData[] newArray(int size) {
            return new ListData[size];
        }
    };

    // normal part of the data structure
    private int type; //resource for photo to show
    private int color; //background color
    private String schedule;
    private String name;


    public ListData(int type, int color, String schedule, String name) {
        this.type = type;
        this.color = color;
        this.schedule = schedule;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getSchedule() {
        return schedule;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    //used by the Parcelable interface
    private ListData(Parcel in) {
        this.type = in.readInt();
        this.color = in.readInt();
        this.schedule = in.readString();
        this.name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.type);
        parcel.writeInt(this.color);
        parcel.writeString(this.schedule);
        parcel.writeString(this.name);
    }
}
