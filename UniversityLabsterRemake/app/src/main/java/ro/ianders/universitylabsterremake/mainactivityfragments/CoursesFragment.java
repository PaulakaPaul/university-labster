package ro.ianders.universitylabsterremake.mainactivityfragments;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;

import ro.ianders.universitylabsterremake.datatypes.ListData;
import ro.ianders.universitylabsterremake.mainactivityfragments.auxiliaractivitesandfragments.CourseActivity;
import ro.ianders.universitylabsterremake.LabsterApplication;
import ro.ianders.universitylabsterremake.R;
import ro.ianders.universitylabsterremake.datatypes.ActivityCourse;
import ro.ianders.universitylabsterremake.datatypes.Course;
import ro.ianders.universitylabsterremake.datatypes.Schedule;
import ro.ianders.universitylabsterremake.datatypes.Student;


/**
 * A simple {@link Fragment} subclass.
 */
public class CoursesFragment extends Fragment {

    private RecyclerView lvRecyclerCourse;
    private ArrayList<ListData> dataToShow;
    private String todayDate;
    private FirebaseUser currentUser;
    private Student currentStudent;


    public CoursesFragment() {
        // Required empty public constructor
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        todayDate = LabsterApplication.generateTodayDate();
        findCurrentStudent();
        generateDataToShow();
    }

    private void findCurrentStudent() {

        for (Student s : LabsterApplication.getInstace().getStudents())
            if (s.getUserUID().equals(currentUser.getUid())) {
                currentStudent = s;
                break;
            }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_courses_list, container, false);

        lvRecyclerCourse = view.findViewById(R.id.lvRecyclerCourses);

        CourseOnItemClickListener listener = new CourseOnItemClickListener() { // we use this listener to go to a new CourseActivity when a item is clicked
            @Override
            public void onItemClick(ListData listData) {
                    startActivity(new Intent(getContext(), CourseActivity.class).putExtra("data", listData));
                }
        };

        RecyclerView.Adapter adapter = new CoursesListAdapter(dataToShow, listener);
        lvRecyclerCourse.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        lvRecyclerCourse.setLayoutManager(layoutManager);

        return view;
    }

    private void generateDataToShow() {
        dataToShow = new ArrayList<>();
        ListData listData;
        String startHour;
        String finishHour;
        String name;

        // we show only the course from today and with the same section and year with the student
        // we check only the section cuz if the section is not equal -> the faculty is not the same

        for (Course c : LabsterApplication.getInstace().getCourses()) {
            if ((c.getCourseData().getYear() == currentStudent.getYear()) && (c.getCourseData().getSection().equals(currentStudent.getSection())))
                for (Schedule s : c.getSchedules()) {
                    if (todayDate.equals(s.getDate())) {
                        startHour = s.getStartTime();
                        if (startHour.length() == 4) // we want format of type 09:00 so we can compare it with the ascii code (10 bigger than 09)
                            startHour = "0" + startHour;
                        finishHour = s.getEndTime();
                        name = c.getCourseData().getNameCourse();
                        listData = new ListData(R.drawable.course, Color.parseColor(FragmentConstants.COLOR_COURSE), startHour + " - " + finishHour, name);

                        dataToShow.add(listData);
                    }
                }
        }

        for (ActivityCourse ac : LabsterApplication.getInstace().getActivities()) {
            if ((ac.getCourseData().getYear() == currentStudent.getYear()) && (ac.getCourseData().getSection().equals(currentStudent.getSection())))
                for (Schedule s : ac.getSchedules()) {
                    if (todayDate.equals(s.getDate())) {
                        startHour = s.getStartTime();
                        if (startHour.length() == 4) // we want format of type 09:00 so we can compare it with the ascii code (10 bigger than 09)
                            startHour = "0" + startHour;
                        finishHour = s.getEndTime();
                        name = ac.getCourseData().getNameCourse();

                        if (ac.getType().charAt(0) == 'l') //laboratory
                            listData = new ListData(R.drawable.laboratory, Color.parseColor(FragmentConstants.COLOR_LABORATORY), startHour + " - " + finishHour, name);
                        else //seminary
                            listData = new ListData(R.drawable.seminary, Color.parseColor(FragmentConstants.COLOR_SEMINARY), startHour + " - " + finishHour, name);

                        dataToShow.add(listData);
                    }
                }
        }

        Collections.sort(dataToShow, (ld1, ld2) -> ld1.getSchedule().compareTo(ld2.getSchedule())); // we compare it lexicographically by the first number
        // ( 01:00 < 10:00 ) ; (08:00 < 22:00)
    }




    interface CourseOnItemClickListener { //listener for the CoursesListAdapter
        void onItemClick(ListData listData);
    }

}
