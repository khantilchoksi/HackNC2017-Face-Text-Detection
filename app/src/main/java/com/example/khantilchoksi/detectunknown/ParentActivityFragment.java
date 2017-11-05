package com.example.khantilchoksi.detectunknown;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A placeholder fragment containing a simple view.
 */
public class ParentActivityFragment extends Fragment {

    private View mRootView;

    public ParentActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_parent, container, false);

        Button personButton = (Button)mRootView.findViewById(R.id.btn_person);
        personButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(getActivity(), MainActivity.class);
                getActivity().startActivity(mainIntent);
            }
        });

        Button objectButton = (Button)mRootView.findViewById(R.id.btn_object);
        objectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(getActivity(), RecognizeObjectActivity.class);
                getActivity().startActivity(mainIntent);
            }
        });

        return mRootView;
    }
}
