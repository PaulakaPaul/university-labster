package ro.ianders.universitylabsterremake.mainactivityfragments;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ro.ianders.universitylabsterremake.AddCourseActivity;
import ro.ianders.universitylabsterremake.LabsterApplication;
import ro.ianders.universitylabsterremake.R;
import ro.ianders.universitylabsterremake.datatypes.PendingActivityCourse;
import ro.ianders.universitylabsterremake.datatypes.PendingCourse;
import ro.ianders.universitylabsterremake.datatypes.PendingListData;
import ro.ianders.universitylabsterremake.datatypes.Student;
import ro.ianders.universitylabsterremake.mainactivityfragments.auxiliaractivitesandfragments.PendingCourseActivity;

public class PendingCoursesFragment extends Fragment {

    private RecyclerView lvRecyclerPendingCourses;
    private FloatingActionButton fbtnAddCourse;

    private List<PendingListData> pendingListDataToShow;

    public static final int REQUEST_TO_PENDING_COURSE_ACTIVITY = 2; // for onActivityResult constant
    public static final int REQUEST_TO_ADD_PENDING_COURSE = 3; //for ActivityAddCourse


    public PendingCoursesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_courses_list, container, false);

        lvRecyclerPendingCourses = view.findViewById(R.id.lvRecyclerCourses);
        fbtnAddCourse = view.findViewById(R.id.fbtnAddCourse);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fbtnAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getContext(), AddCourseActivity.class), REQUEST_TO_ADD_PENDING_COURSE);
            }
        });


        bindData(); // implemented in this class -> it generates the data to show and it binds it with the recycler list
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity()); //manager for the recycler list view
        lvRecyclerPendingCourses.setLayoutManager(layoutManager);
    }

    private void generatePendingListData() {
        pendingListDataToShow = new ArrayList<>();
        PendingListData pendingListData;
        String name;
        Student currentStudent = LabsterApplication.getCurrentStudent(); //static method implemented in the LABSTER

        // we show only the course from today and with the same section and year with the student
        // we check the year, section and faculty to be the same

        for (PendingCourse c : LabsterApplication.getInstace().getPendingCourses()) {
            if ((c.getCourseData().getYear() == currentStudent.getYear()) &
                    (c.getCourseData().getFaculty()).equalsIgnoreCase(currentStudent.getFaculty()) &
                    (c.getCourseData().getSection().equalsIgnoreCase(currentStudent.getSection()))) {

                        name = c.getCourseData().getNameCourse();

                        pendingListData = new PendingListData(c.getValidations().size(), R.drawable.course, Color.parseColor(FragmentConstants.COLOR_COURSE), null, name);
                        pendingListDataToShow.add(pendingListData);
                }
        }

        for (PendingActivityCourse ac : LabsterApplication.getInstace().getPendingActivityCourses()) {
            if ((ac.getCourseData().getYear() == currentStudent.getYear()) & (ac.getCourseData().getSection().equalsIgnoreCase(currentStudent.getSection()))
                    & ac.getCourseData().getFaculty().equalsIgnoreCase(currentStudent.getFaculty())) {

                        name = ac.getCourseData().getNameCourse();

                        if (ac.getType().charAt(0) == 'l') //laboratory
                            pendingListData = new PendingListData(ac.getValidations().size(), R.drawable.laboratory, Color.parseColor(FragmentConstants.COLOR_LABORATORY), null, name);
                        else //seminary
                            pendingListData = new PendingListData(ac.getValidations().size(), R.drawable.seminary, Color.parseColor(FragmentConstants.COLOR_SEMINARY), null, name);

                        pendingListDataToShow.add(pendingListData);
                    }
                }
        }

    private void bindData() {
        generatePendingListData();
        CoursesFragment.CourseOnItemClickListener<PendingListData> listener = (pendingListData) ->
                startActivityForResult(new Intent(getContext(), PendingCourseActivity.class).putExtra("data", pendingListData), REQUEST_TO_PENDING_COURSE_ACTIVITY);
        // we use this listener to go to a new CourseActivity when a item is clicked
        // we can't call startActivity() function from the CoursePagerAdapter
        RecyclerView.Adapter adapter = new PendingCoursesListAdapter( pendingListDataToShow, listener);
        lvRecyclerPendingCourses.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_TO_PENDING_COURSE_ACTIVITY && resultCode == Activity.RESULT_OK) {

            int position = data.getIntExtra("position",-1);

            if(position != -1) { // we update and recreate the view when we come back from the PENDING COURSE ACTIVITY after we validate
                pendingListDataToShow.get(position).incrementNumberOfValidations();
                bindData();
            }
        } else if (requestCode == REQUEST_TO_ADD_PENDING_COURSE && resultCode == Activity.RESULT_OK) {
            bindData();
        }

    }
}

