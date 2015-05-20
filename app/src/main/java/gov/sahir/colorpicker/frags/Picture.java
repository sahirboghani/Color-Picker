package gov.sahir.colorpicker.frags;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import gov.sahir.colorpicker.ColorCallBack;
import gov.sahir.colorpicker.ColorCursorAdapter;
import gov.sahir.colorpicker.MainActivity;
import gov.sahir.colorpicker.R;
import gov.sahir.colorpicker.contentprovider.ColorContentProvider;
import gov.sahir.colorpicker.db.ColorTable;
import gov.sahir.colorpicker.db.DataBaseHelper;

/**
 * Created by Sahir on 4/23/2015.
 */
public class Picture extends Fragment {

    private ColorCallBack mTarget;
    public static final int REQUEST_CAPTURE = 1;
    private ImageView mImageView;
    private TextView mDominantColor;
    private TextView mFoundColor;

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        mTarget = (ColorCallBack) activity;
        dispatchTakePictureIntent();
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
        View view = inflater.inflate(R.layout.picture_fragment, container, false);
        mImageView = (ImageView)view.findViewById(R.id.taken_picture);
        mDominantColor = (TextView)view.findViewById(R.id.dominant_color);
        mFoundColor = (TextView)view.findViewById(R.id.match_found);
        return view;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(((MainActivity)mTarget).getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CAPTURE);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAPTURE && resultCode == ((MainActivity.RESULT_OK))) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);

            int[][] hsvTracker = new int[3][];
            hsvTracker[0] = new int[360];
            hsvTracker[1] = new int[100];
            hsvTracker[2] = new int[100];

            int pixel;
            float[] components = new float[3];

            for(int i = 0; i < imageBitmap.getWidth(); ++i) {
                for(int j = 0; j < imageBitmap.getHeight(); ++j) {
                    pixel = imageBitmap.getPixel(i, j);
                    Color.colorToHSV(pixel, components);
                    ++hsvTracker[0][(int)components[0]];

                    ++hsvTracker[1][(int)(components[1]*99)];
                    ++hsvTracker[2][(int)(components[2]*99)];
                }
            }
            int maxHue = 0;
            int maxSaturation = 0;
            int maxValue = 0;

            for(int i = 1; i < hsvTracker[0].length; ++i) {
                if(hsvTracker[0][maxHue] < hsvTracker[0][i])
                    maxHue = i;
            }
            for(int i = 1; i < hsvTracker[1].length; ++i) {
                if(hsvTracker[1][maxSaturation] < hsvTracker[1][i])
                    maxSaturation = i;
            }
            for(int i = 1; i < hsvTracker[2].length; ++i) {
                if(hsvTracker[2][maxValue] < hsvTracker[2][i])
                    maxValue = i;
            }

            float hue = maxHue*1.0f;
            float saturation = maxSaturation/100.0f;
            float value = maxValue/100.0f;

            MainActivity.isMatchScreen = true;
            MainActivity.mHue = hue;
            MainActivity.mSaturation = saturation;
            MainActivity.mValue = value;

            String SELECTION = "Hue == ? and Hue == ? and Saturation == ? and Saturation == ? and Value == ? and Value == ?";
            Cursor cursor = getActivity().getContentResolver().query(ColorContentProvider.CONTENT_URI, ColorCursorAdapter.PROJECTION,
                                                                    SELECTION, DataBaseHelper.getSelectionArgs(), MainActivity.sortSelectionString);
            if(cursor.getCount()==0) {
                mFoundColor.setText("No Match Found");
                mDominantColor.setBackgroundColor(Color.BLACK);
            }
            else {
                mFoundColor.setText(cursor.getString(0));
                mDominantColor.setBackgroundColor(Color.parseColor(cursor.getString(1)));
            }
        }
    }
}
