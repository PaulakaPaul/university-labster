package ro.ianders.universitylabsterremake.mainactivityfragments.auxiliaractivitesandfragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import ro.ianders.universitylabsterremake.LabsterConstants;

/**
 * Created by paul.iusztin on 28.12.2017.
 */

public class CoursePagerAdapter extends FragmentStatePagerAdapter{

    private int mNumOfTabs;
    private ArrayList<String> checkins;
    private List<String> notes;


    public CoursePagerAdapter(FragmentManager fm, int NumOfTabs, ArrayList<String> checkins, List<String> notes) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.checkins = checkins;
        this.notes = notes;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Bundle checkinsBundle = new Bundle();
                checkinsBundle.putSerializable(LabsterConstants.PAGER_ADAPTER_CHECKINS_SEND_DATA_KEY, checkins);

                CheckinsFragment checkinsFragment = new CheckinsFragment();
                checkinsFragment.setArguments(checkinsBundle);

                return  checkinsFragment;
            case 1:
                return new NotesFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }



}
