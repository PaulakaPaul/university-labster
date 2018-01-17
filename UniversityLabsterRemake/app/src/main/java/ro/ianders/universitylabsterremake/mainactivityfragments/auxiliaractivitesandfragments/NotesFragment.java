package ro.ianders.universitylabsterremake.mainactivityfragments.auxiliaractivitesandfragments;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ro.ianders.universitylabsterremake.LabsterApplication;
import ro.ianders.universitylabsterremake.LabsterConstants;
import ro.ianders.universitylabsterremake.R;
import ro.ianders.universitylabsterremake.datatypes.Message;
import ro.ianders.universitylabsterremake.datatypes.Student;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotesFragment extends Fragment {

    private EditText etCourseMessage;
    private LinearLayout linCourseMessage;

    private NotesFragmentCallbacks notesFragmentCallbacks;

    public NotesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        etCourseMessage = view.findViewById(R.id.etCourseMessage);
        linCourseMessage = view.findViewById(R.id.linCourseMessages);

        Bundle args = getArguments();
        try {
            ArrayList<Message> messages = (ArrayList<Message>) args.getSerializable(LabsterConstants.PAGER_ADAPTER_NOTES_SEND_DATA_KEY);
            if(messages != null)
               createTextViewNotes(messages);
        } catch (ClassCastException e) {
            Log.e("ClassCastException", e.getMessage());
        } catch (NullPointerException e) {
            Log.e("NullPointerException", e.getMessage());
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // getting reference to callbacks from the courseActivity
        try {
            notesFragmentCallbacks = (NotesFragmentCallbacks) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement the interface " +
                    "called: ChangeProfilePicHeader");
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        etCourseMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String message = etCourseMessage.getText().toString().trim();

                    if(!TextUtils.isEmpty(message)) {
                        notesFragmentCallbacks.bindDataWithAdapter(message); // we save the message and recreate the view from the CourseActivity
                    } else {
                        Toast.makeText(getContext(), "Please introduce something!", Toast.LENGTH_SHORT).show();
                    }
                    handled = true;
                }
                return handled;
            }
        });

    }

    private void createTextViewNotes(ArrayList<Message> messages) { // we create and show all the right notes


        for(Message m : messages) {

            //important references
            TextView newMessage = new TextView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            Student student = new Student(m.getUserUID()); //grab the student
            int indexOfStudent = LabsterApplication.getInstace().getStudents().indexOf(student);
            student = LabsterApplication.getInstace().getStudents().get(indexOfStudent);


            Context context = getContext();
            if(context!= null) {
                if (student.equals(LabsterApplication.getCurrentStudent())) { //if the student is the current student we customize it different
                   // Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.message_shape); // one method to get a drawable
                    newMessage.setBackground(getResources().getDrawable(R.drawable.message_shape_current_person, getContext().getTheme()));
                    params.gravity = Gravity.END;
                    newMessage.setText(m.getMessage());
                } else { // this is for any other student
                    newMessage.setBackground(getResources().getDrawable(R.drawable.message_shape, getContext().getTheme())); // another method to
                    // get a drawable
                    newMessage.setText(String.format("%s %s: %s",student.getProfile().getLastName(), student.getProfile().getFirstName(), m.getMessage()));
                }
            }

            //customization for all the text
            newMessage.setTextColor(Color.parseColor("#000000"));
            newMessage.setTextSize(18f);
            params.setMargins(10,8,10,8);
            newMessage.setLayoutParams(params); //setting margins and height and width

            linCourseMessage.addView(newMessage);
        }
    }
}
