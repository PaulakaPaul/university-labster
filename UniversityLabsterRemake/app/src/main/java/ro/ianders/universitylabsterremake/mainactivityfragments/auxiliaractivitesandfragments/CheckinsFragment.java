package ro.ianders.universitylabsterremake.mainactivityfragments.auxiliaractivitesandfragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ro.ianders.universitylabsterremake.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckinsFragment extends Fragment {


    public CheckinsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_checkins, container, false);
    }

}
