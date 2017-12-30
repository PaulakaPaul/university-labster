package ro.ianders.universitylabsterremake.mainactivityfragments.auxiliaractivitesandfragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ro.ianders.universitylabsterremake.LabsterConstants;
import ro.ianders.universitylabsterremake.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckinsFragment extends Fragment {

    private LinearLayout linCheckins;


    public CheckinsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkins, container, false);

        linCheckins = view.findViewById(R.id.linCheckins);


        Bundle args = getArguments();
        try {
            ArrayList<String> checkins = (ArrayList<String>) args.getSerializable(LabsterConstants.PAGER_ADAPTER_CHECKINS_SEND_DATA_KEY);
            if(checkins != null)
                generateCheckins(checkins);
        } catch (ClassCastException e) {
            Log.e("ClassCastException", e.getMessage());
        } catch (NullPointerException e) {
            Log.e("NullPointerException", e.getMessage());
        }


        return view;
    }


    private void generateCheckins(@NonNull ArrayList<String> checkins) {

        int i = 1;
        for(String s : checkins) {

            TextView checkin = new TextView(getContext());
            checkin.setText(String.format("%d. %s",i, s));
            checkin.setTextColor(Color.parseColor("#000000"));
            checkin.setTextSize(18f);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10,8,10,8);
            checkin.setLayoutParams(params); //setting margins and height and width

            linCheckins.addView(checkin);
            i++;
        }


    }

}
