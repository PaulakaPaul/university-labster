package ro.ianders.universitylabsterremake.mainactivityfragments.auxiliaractivitesandfragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by paul.iusztin on 28.12.2017.
 */

public class CoursePagerAdapter extends FragmentStatePagerAdapter{

    int mNumOfTabs;

    public CoursePagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return  new CheckinsFragment();
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
