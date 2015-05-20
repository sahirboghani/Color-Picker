package gov.sahir.colorpicker.frags;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gov.sahir.colorpicker.ColorAdapter;
import gov.sahir.colorpicker.ColorCallBack;
import gov.sahir.colorpicker.ColorCursorAdapter;
import gov.sahir.colorpicker.MainActivity;
import gov.sahir.colorpicker.R;
import gov.sahir.colorpicker.contentprovider.ColorContentProvider;
import gov.sahir.colorpicker.db.DataBaseHelper;

/**
 * Created by Sahir on 4/19/2015.
 */
public class ColorList extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ColorCursorAdapter mAdapter;
    private ColorCallBack mTarget;
    public static boolean containsOne = false;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.sort_order_view, container, false);

        final ListView listView = (ListView) (rootView).findViewById(R.id.list);

        getLoaderManager().initLoader(0, null, this);

        mAdapter = new ColorCursorAdapter(getActivity(), null, 0);

        if(listView != null) {
            listView.setAdapter(mAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Map<String, String> toaster = ColorCursorAdapter.getToastHelper(view);
                    String temp = "Hue: " + toaster.get("hue") + "\nSaturation: " + toaster.get("saturation") + "\nValue: " + toaster.get("value");
                    Toast.makeText(rootView.getContext(), temp, Toast.LENGTH_SHORT).show();
                }
            });
        }

        return rootView;
    }

/*

        LoaderManager.LoaderCallbacks

 */



    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), ColorContentProvider.CONTENT_URI, ColorCursorAdapter.PROJECTION,
                                DataBaseHelper.SELECTION, DataBaseHelper.getSelectionArgs(), DataBaseHelper.getOrder());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }

    public void updateData() {
        onLoadFinished(null, getActivity().getContentResolver().query(ColorContentProvider.CONTENT_URI, ColorCursorAdapter.PROJECTION,
                                                                      DataBaseHelper.SELECTION, DataBaseHelper.getSelectionArgs(), MainActivity.sortSelectionString));
        mAdapter.notifyDataSetChanged();
    }
}
