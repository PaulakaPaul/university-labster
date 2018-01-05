package ro.ianders.universitylabsterremake.mainactivityfragments;

import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ro.ianders.universitylabsterremake.LabsterApplication;
import ro.ianders.universitylabsterremake.R;
import ro.ianders.universitylabsterremake.datatypes.PendingCourse;
import ro.ianders.universitylabsterremake.datatypes.PendingListData;

/**
 * Created by paul.iusztin on 05.01.2018.
 */

public class PendingCoursesListAdapter extends RecyclerView.Adapter<PendingCoursesListAdapter.PendingCourseListHolder> {

    private List<PendingListData> dataToShow; //used to fill data only with today's date
    private CoursesFragment.CourseOnItemClickListener listener; // we have to change activities which can be implemented just from outside


    PendingCoursesListAdapter(List<PendingListData> dataToShow, CoursesFragment.CourseOnItemClickListener listener) {
        this.dataToShow = dataToShow;
        this.listener = listener;
    }

    @Override
    public PendingCoursesListAdapter.PendingCourseListHolder onCreateViewHolder(ViewGroup parent, int viewType) { //creates a item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_course_list_item, parent, false);
        return new PendingCoursesListAdapter.PendingCourseListHolder(view);
    }

    @Override
    public void onBindViewHolder(PendingCoursesListAdapter.PendingCourseListHolder holder, int position) {
        holder.bindView(position); //binds data
    }

    @Override
    public int getItemCount() { //number of items
        return dataToShow.size();
    }



    public class PendingCourseListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView ivType;
        private TextView tvName;
        private TextView tvNumberValidations;
        private LinearLayout linItemHolder;


        private PendingListData currentPendingListData; // we need this for the onClick event

        PendingCourseListHolder(View itemView) {
            super(itemView);
            ivType = itemView.findViewById(R.id.ivType);
            tvName = itemView.findViewById(R.id.tvName);
            linItemHolder = itemView.findViewById(R.id.linItemHolder);
            tvNumberValidations = itemView.findViewById(R.id.tvNumberValidations);
            itemView.setOnClickListener(this);
        }

        void bindView(int position) {

            currentPendingListData = dataToShow.get(position);
            currentPendingListData.setPosition(position); // we need the position to pass it to the PendingCourseActivity threw the listener callback

            ((GradientDrawable) linItemHolder.getBackground()).setColor(dataToShow.get(position).getColor()); //setting background within the drawable set
            // as background -> we keep the drawable and change only the colour
            ivType.setImageResource(dataToShow.get(position).getType());  //setting picture

            String name = dataToShow.get(position).getName();
            tvName.setText(name); //setting name

            if(PendingCourse.NUMBER_OF_VALIDATIONS - currentPendingListData.getNumberOfValidations() == 0) {
                tvNumberValidations.setText("VALIDATED"); // this is the case when we validate for the last time and come back from the PendingCourseActivity (in the case where it still exists dynamically)
            } else {
                tvNumberValidations.setText(String.format("%d", PendingCourse.NUMBER_OF_VALIDATIONS - currentPendingListData.getNumberOfValidations()));
                //setting the number of validations remained until it's still in the pending fragment
            }

        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(currentPendingListData);
        }
    }
}
