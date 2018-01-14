package ro.ianders.universitylabsterremake.mainactivityfragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ro.ianders.universitylabsterremake.LabsterApplication;
import ro.ianders.universitylabsterremake.R;
import ro.ianders.universitylabsterremake.datatypes.ActivityCourse;
import ro.ianders.universitylabsterremake.datatypes.Course;
import ro.ianders.universitylabsterremake.datatypes.Schedule;
import ro.ianders.universitylabsterremake.datatypes.TimetableData;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimetableFragment extends Fragment {

    // we will keep the data for the timetable in those lists
    private List<TimetableData> monday;
    private List<TimetableData> tuesday;
    private List<TimetableData> wednesday;
    private List<TimetableData> thursday;
    private List<TimetableData> friday;

    private TableRow trMonday;
    private TableRow trTuesday;
    private TableRow trWednesday;
    private TableRow trThursday;
    private TableRow trFriday;

    private final int[] hours = new int[]{8, 10, 12, 14, 16, 18}; // we use this array to create every day of the table dynamically


    public TimetableFragment() {
        // Required empty public constructor

        monday = new ArrayList<>();
        tuesday = new ArrayList<>();
        wednesday = new ArrayList<>();
        thursday = new ArrayList<>();
        friday = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timetable, container, false);

        trMonday = view.findViewById(R.id.trMonday);
        trTuesday = view.findViewById(R.id.trTuesday);
        trWednesday = view.findViewById(R.id.trWednesday);
        trThursday = view.findViewById(R.id.trThursday);
        trFriday = view.findViewById(R.id.trFriday);

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fillListsWithData();

        fillTableRowWithData(monday, trMonday);
        fillTableRowWithData(tuesday, trTuesday);
        fillTableRowWithData(wednesday, trWednesday);
        fillTableRowWithData(thursday, trThursday);
        fillTableRowWithData(friday, trFriday);

    }



    private void fillTableRowWithData(List<TimetableData> timetableDatas, TableRow tableRow) throws RuntimeException {

         timetableDatas = timetableDatas.stream() // we filter only for unique data and after we sort the list
                                                        .distinct() // you also need to implement the HashCode() of the class for this to work !!!
                                                        .sorted(Comparator.comparing(TimetableData::getPeriodOfTime))
                                                        .collect(Collectors.toList());

        Supplier<IntStream> intStreamSupplier = () -> IntStream.of(hours); // supplier for stream to check if the startHour from the timetableDatas is correct

        int i,hoursIndex, numberOfViewsAddedInFor = 0; // we use the last variable to fill with empty text views in the end(until the table is filled)
        TableRow.LayoutParams params = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);


        for(i = 0, hoursIndex = 0; i < timetableDatas.size() && hoursIndex < hours.length; i++, hoursIndex++) {

            final int startHour = grabStartHour(timetableDatas.get(i).getPeriodOfTime()); // we fill the table compared to the start hour of the course/activity course
            boolean correctValue = intStreamSupplier.get().anyMatch( x -> startHour == x);

            if(hoursIndex < hours.length && correctValue) { // in case of some extra data that passed all the filters
                if (startHour == hours[hoursIndex]) {

                    //put the data in the table
                    TextView timetableData = new TextView(getContext());

                    String dataToShow = timetableDatas.get(i).getName() + "\n" + timetableDatas.get(i).getType();

                    timetableData.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    timetableData.setGravity(Gravity.CENTER_VERTICAL);
                    timetableData.setPadding(8,8,8,8);
                    timetableData.setText(dataToShow);
                    timetableData.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.table_cell_shape));
                    timetableData.setLayoutParams(params);


                    final String startTime = timetableDatas.get(i).getPeriodOfTime().split(" - ")[0];
                    final String endTime = timetableDatas.get(i).getPeriodOfTime().split(" - ")[1];
                    final String type = timetableDatas.get(i).getType();
                    final String name = timetableDatas.get(i).getName();

                    timetableData.setOnClickListener((view) -> { // listener to add event on calender within the timetable
                        String date = LabsterApplication.generateTodayDate();
                        LabsterApplication.getInstace().putDataInCalendar(TimetableFragment.this.getContext(), date, startTime, endTime, name,
                                type, "");
                    });

                    tableRow.addView(timetableData);

                    numberOfViewsAddedInFor++;

                } else {

                    //we need to fill the empty spaces so the data will distribute properly
                    TextView emptyTextView = new TextView(getContext());

                    emptyTextView.setText("");
                    tableRow.addView(emptyTextView);
                    emptyTextView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.table_cell_shape));
                    emptyTextView.setLayoutParams(params);

                    i--; // we want to put all the data in the table, so we need to remain on the same data until we find a match
                    numberOfViewsAddedInFor++;
                }
            }

        }

        while (numberOfViewsAddedInFor < hours.length) {
            //we need to fill the empty spaces so the data will distribute properly
            TextView emptyTextView = new TextView(getContext());
            Log.e("EMPTY", numberOfViewsAddedInFor + "");
            emptyTextView.setText("");
            tableRow.addView(emptyTextView);
            emptyTextView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.table_cell_shape));
            emptyTextView.setLayoutParams(params);
            numberOfViewsAddedInFor++;
        }

    }

    private int grabStartHour(String periodOfTime) throws RuntimeException {

        String[] hours = periodOfTime.split(" - "); //period of time is like : 12:00 - 14:00

        if(hours.length != 2) throw  new RuntimeException("Data provided to this method is wrong!");


        String[] firstHour = hours[0].split(":"); // now we split the 12:00 to get most significant part of the number

        try {
            return Integer.parseInt(firstHour[0]);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Data provided to this method is wrong! -> course hour are not introduced correctly");
        }
    }

    private void fillListsWithData() {
        looperForFillLists(LabsterApplication.getInstace().getCourses());
        looperForFillLists(LabsterApplication.getInstace().getActivities());
    }


    private void looperForFillLists(List<? extends Course> courses) {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

        for(Course c : courses)
            for(Schedule s : c.getSchedules()) {

                try {

                    calendar.setTime(simpleDateFormat.parse(s.getDate()));
                    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                    String type;

                    try { // we will use this function for both Courses and ActivityCourses
                        type = ((ActivityCourse) c).getType();
                    } catch (ClassCastException e) {
                        type = "course";
                    }

                    String startTime = s.getStartTime();
                    if(startTime.length() == 4) // 4:00
                        startTime += "0" + startTime; // -> 04:00 -> we need this so we can compare the data in the string form (04:00 < 10:00)

                    TimetableData timetableData = new TimetableData(c.getCourseData().getNameCourse(), type, startTime + " - " + s.getEndTime());

                    if(dayOfWeek == Calendar.MONDAY)
                        monday.add(timetableData);
                    else if(dayOfWeek == Calendar.TUESDAY)
                        tuesday.add(timetableData);
                    else if(dayOfWeek == Calendar.WEDNESDAY)
                        wednesday.add(timetableData);
                    else if(dayOfWeek == Calendar.THURSDAY)
                        thursday.add(timetableData);
                    else if(dayOfWeek == Calendar.FRIDAY)
                        friday.add(timetableData);


                } catch (ParseException e) {
                    Log.e("PARSEEXCEPTION", "we couldn't parse data from the main data types: " + e.getMessage());
                }

            }
    }

}
