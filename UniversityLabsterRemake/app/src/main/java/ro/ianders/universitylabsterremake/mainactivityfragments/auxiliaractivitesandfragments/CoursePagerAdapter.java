package ro.ianders.universitylabsterremake.mainactivityfragments.auxiliaractivitesandfragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import ro.ianders.universitylabsterremake.LabsterConstants;
import ro.ianders.universitylabsterremake.datatypes.Message;

/**
 * Created by paul.iusztin on 28.12.2017.
 */

public class CoursePagerAdapter extends FragmentStatePagerAdapter{

    private int mNumOfTabs;
    private ArrayList<String> checkins;
    private ArrayList<Message> notes;


    public CoursePagerAdapter(FragmentManager fm, int NumOfTabs, ArrayList<String> checkins, ArrayList<Message> notes) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.checkins = checkins;
        this.notes = notes;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) { // if we use pager with tabs we can set the tabs title with this function

        switch (position) {

            case 0:
                return LabsterConstants.TAB_COURSE_CHECKINS;
            case 1:
                return LabsterConstants.TAB_COURSE_NOTES;
            default:
                return null;
        }

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

                Bundle notesBundle = new Bundle();
                notesBundle.putSerializable(LabsterConstants.PAGER_ADAPTER_NOTES_SEND_DATA_KEY, notes);

                NotesFragment notesFragment = new NotesFragment();
                notesFragment.setArguments(notesBundle);

                return notesFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }



}
