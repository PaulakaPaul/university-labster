package ro.ianders.universitylabsterremake.mainactivityfragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ro.ianders.universitylabsterremake.LabsterConstants;
import ro.ianders.universitylabsterremake.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddCourseProfessorFragment extends Fragment {


    public AddCourseProfessorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_course_professor, container, false);
    }

}
