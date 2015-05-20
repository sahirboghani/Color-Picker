package gov.sahir.colorpicker.frags;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import gov.sahir.colorpicker.ColorAdapter;
import gov.sahir.colorpicker.ColorCallBack;
import gov.sahir.colorpicker.MainActivity;
import gov.sahir.colorpicker.R;

/**
 * Created by Sahir on 4/19/2015.
 */
public class HueScreen extends Fragment {

    private ColorAdapter mAdapter = null;
    private ColorCallBack mTarget;
    private List<GradientDrawable> drawables;

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
        mAdapter = null;
        mTarget = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.main_screen_updatable_list, container, false);

        drawables = new ArrayList<GradientDrawable>();

        for(int i = 360/ MainActivity.PARTITIONS; i <= 360; i+=360/MainActivity.PARTITIONS) {
            int colorLeft = Color.HSVToColor(new float[]{i - 360 / MainActivity.PARTITIONS, MainActivity.mSaturation, MainActivity.mValue});
            int colorRight = Color.HSVToColor(new float[] { i, MainActivity.mSaturation, MainActivity.mValue} );
            drawables.add(new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[] { colorLeft, colorRight }));
        }

        mAdapter = new ColorAdapter(((MainActivity)mTarget).getApplicationContext(), drawables);

        final ListView listView = (ListView) (rootview).findViewById(R.id.list);

        if(mAdapter != null)
            listView.setAdapter(mAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick( AdapterView<?> parent, final View view, final int position, long id) {
                    float leftHue = MainActivity.central_hue + (position*360/MainActivity.PARTITIONS) - 360/MainActivity.PARTITIONS/2;
                    if(leftHue < 0)
                        leftHue += 360;

                    mTarget.setHue(leftHue);
                    FragmentManager fm = getFragmentManager();
                    fm.beginTransaction().replace(R.id.container, new SaturationScreen(), MainActivity.SATURATION_TAG).addToBackStack(null).commit();
                }

        });
        return rootview;
    }

    public void updateData() {
        drawables.clear();
        for(int i = 0; i < MainActivity.PARTITIONS; ++i) {
            float leftHue = MainActivity.central_hue + (i*360/MainActivity.PARTITIONS) - 360/MainActivity.PARTITIONS/2;
            if(leftHue < 0)
                leftHue += 360;

            float rightHue = MainActivity.central_hue + (i*360/MainActivity.PARTITIONS) + 360/MainActivity.PARTITIONS/2;
            if(rightHue > 360)
                rightHue -= 360;

            //int diff = ((int)Math.abs(leftHue-rightHue));
            int diff = rightHue > leftHue ? (int)(rightHue-leftHue) : (int)(360-leftHue+rightHue);
            int[] colors;

            if(MainActivity.PARTITIONS < 10) {
                colors = new int[diff / 10];

                for (int j = 0; j < colors.length; ++j) {
                    float temphue = (leftHue + j*10)%360;
                    colors[j] = Color.HSVToColor(new float[] { temphue, MainActivity.mSaturation, MainActivity.mValue});
                }
            }
            else {
                int colorLeft = Color.HSVToColor(new float[]{leftHue, MainActivity.mSaturation, MainActivity.mValue});
                int colorRight = Color.HSVToColor(new float[] { rightHue, MainActivity.mSaturation, MainActivity.mValue} );
                colors = new int[] { colorLeft, colorRight };
            }

            drawables.add(new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors));
        }
        mAdapter.notifyDataSetChanged();
    }
}
