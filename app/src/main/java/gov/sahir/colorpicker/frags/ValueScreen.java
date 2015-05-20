package gov.sahir.colorpicker.frags;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
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
public class ValueScreen extends Fragment {

    private ColorAdapter mAdapter;
    private ColorCallBack mTarget;
    List<GradientDrawable> drawables;

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        MainActivity.isValueScreen = true;
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
        MainActivity.isValueScreen = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.saturation_value_updatable_list, container, false);

        drawables = new ArrayList<GradientDrawable>();

        float hue = mTarget.getHue();

        for(int i = MainActivity.value_swatch_length; i > 0; --i) {


            int colorLeft = Color.HSVToColor(new float[]{hue, MainActivity.mSaturation, 1.0f*i / MainActivity.value_swatch_length});
            int colorRight = Color.HSVToColor(new float[] { hue+360/ MainActivity.PARTITIONS, MainActivity.mSaturation, 1.0f*i / MainActivity.value_swatch_length} );
            drawables.add(new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[] { colorLeft, colorRight }));

            /*
            int colorLeft = Color.HSVToColor(new float[]{hue, MainActivity.mSaturation, i / 10.0f});
            int colorRight = Color.HSVToColor(new float[] { hue+360/ MainActivity.PARTITIONS, MainActivity.mSaturation, i / 10.0f} );
            drawables.add(new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[] { colorLeft, colorRight }));*/
        }

        mAdapter = new ColorAdapter(((MainActivity)mTarget).getApplicationContext(), drawables);

        final ListView listView = (ListView) (rootview).findViewById(R.id.list);

        if(mAdapter != null)
            listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick( AdapterView<?> parent, final View view, final int position, long id) {
                mTarget.setValue(1 - (position * .1f));
                getFragmentManager().beginTransaction().replace(R.id.container, new ColorList(), MainActivity.FINAL_TAG).addToBackStack(null).commit();
            }

        });
        return rootview;
    }

    public void updateData() {
        drawables.clear();
        float hue = mTarget.getHue();
        for(int i = MainActivity.value_swatch_length; i >= 1; --i) {
            int colorLeft = Color.HSVToColor(new float[]{hue, MainActivity.mSaturation, 1.0f*i / MainActivity.value_swatch_length});
            int colorRight = Color.HSVToColor(new float[] { hue+360/ MainActivity.PARTITIONS, MainActivity.mSaturation, 1.0f*i / MainActivity.value_swatch_length} );
            drawables.add(new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[] { colorLeft, colorRight }));
        }
        mAdapter.notifyDataSetChanged();
    }
}





