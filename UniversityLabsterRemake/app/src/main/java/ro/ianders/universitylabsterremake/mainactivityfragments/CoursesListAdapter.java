package ro.ianders.universitylabsterremake.mainactivityfragments;

import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ro.ianders.universitylabsterremake.R;
import ro.ianders.universitylabsterremake.datatypes.ListData;

/**
 * Created by paul.iusztin on 27.12.2017.
 */

public class CoursesListAdapter extends RecyclerView.Adapter<CoursesListAdapter.CourseListHolder>{ //adapter for the courses list

    private ArrayList<ListData> dataToShow; //used to fill data only with today's date
    private CoursesFragment.CourseOnItemClickListener listener; // we have to change activities which can be implemented just from outside


    CoursesListAdapter(ArrayList<ListData> dataToShow, CoursesFragment.CourseOnItemClickListener listener) {
        this.dataToShow = dataToShow;
        this.listener = listener;
    }

    @Override
    public CourseListHolder onCreateViewHolder(ViewGroup parent, int viewType) { //creates a item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_list_item, parent, false);
        return new CourseListHolder(view);
    }

    @Override
    public void onBindViewHolder(CourseListHolder holder, int position) {
         holder.bindView(position); //binds data
    }

    @Override
    public int getItemCount() { //number of items
        return dataToShow.size();
    }




    public class CourseListHolder extends RecyclerView.ViewHolder implements View.OnClickListener { //data item structure used by the adapter

        private ImageView ivType;
        private TextView tvSchedule;
        private TextView tvName;
        private LinearLayout linItemHolder;

        private ListData currentListData; // we need this for the onClick event

        CourseListHolder(View itemView) {
            super(itemView);
            ivType = itemView.findViewById(R.id.ivType);
            tvSchedule = itemView.findViewById(R.id.tvSchedule);
            tvName = itemView.findViewById(R.id.tvName);
            linItemHolder = itemView.findViewById(R.id.linItemHolder);
            itemView.setOnClickListener(this);
        }

        void bindView(int position) {

            currentListData = dataToShow.get(position);

            ((GradientDrawable) linItemHolder.getBackground()).setColor(dataToShow.get(position).getColor()); //setting background within the drawable set
            // as background -> we keep the drawable and change only the colour
            ivType.setImageResource(dataToShow.get(position).getType());  //setting picture

            String schedule = dataToShow.get(position).getSchedule();
            if((schedule.charAt(0) == '0' ) &&  schedule.charAt(1) !=  '0' ) //remove the '0'es added to compare the schedules ( 8:00 -> 08:00)
                schedule = dataToShow.get(position).getSchedule().substring(1, schedule.length());
            tvSchedule.setText(schedule); //setting schedule

            tvName.setText(dataToShow.get(position).getName()); //setting name

        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(currentListData);
        }

    }
}
