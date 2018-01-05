package ro.ianders.universitylabsterremake.datatypes;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by paul.iusztin on 05.01.2018.
 */

public class PendingListData extends ListData {

    //used by the Parcelable interface
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @NonNull
        public PendingListData createFromParcel(Parcel in) {
            return new PendingListData(in);
        }

        public PendingListData[] newArray(int size) {
            return new PendingListData[size];
        }
    };

    private int numberOfValidations;

    public PendingListData(int numberOfValidations, int type, int color, String schedule, String name) {
        super(type, color, schedule, name);
        this.numberOfValidations = numberOfValidations;
    }

    public int getNumberOfValidations() {
        return numberOfValidations;
    }

    public void incrementNumberOfValidations() {
        if(numberOfValidations < PendingCourse.NUMBER_OF_VALIDATIONS)
            numberOfValidations++;
    }

    public void setPosition(int position) { // we don't use the schedule field at the pendingListData, but we need a position field so we use this one for that
        schedule = "" + position;
    }

    public int getPosition() { // we need it as a int, even if it's a String we cover that in the implementation so we can use it easily as a int
        return Integer.parseInt(schedule);
    }

    //used by the Parcelable interface
    private PendingListData(Parcel parcel) { // for the parcelable interface (the rest of the interface it is implemented in the ListData class)
        super(parcel);
        this.numberOfValidations = parcel.readInt();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
       super.writeToParcel(parcel, i);
       parcel.writeInt(this.numberOfValidations);
    }

}
