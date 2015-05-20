package gov.sahir.colorpicker.frags;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import gov.sahir.colorpicker.ColorAdapter;
import gov.sahir.colorpicker.ColorCallBack;
import gov.sahir.colorpicker.R;

/**
 * Created by Sahir on 4/23/2015.
 */
public class Choose extends Fragment {
    private ColorCallBack mTarget;

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        mTarget = (ColorCallBack) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mTarget = null;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_menu, container, false);
    }
}

