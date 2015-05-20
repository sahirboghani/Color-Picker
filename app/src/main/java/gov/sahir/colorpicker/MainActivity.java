package gov.sahir.colorpicker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.List;

import gov.sahir.colorpicker.db.DataBaseHelper;
import gov.sahir.colorpicker.frags.Choose;
import gov.sahir.colorpicker.frags.ColorList;
import gov.sahir.colorpicker.frags.HueScreen;
import gov.sahir.colorpicker.frags.Picture;
import gov.sahir.colorpicker.frags.SaturationScreen;
import gov.sahir.colorpicker.frags.ValueScreen;

public class MainActivity extends ActionBarActivity implements ColorCallBack {

    public static final String PREFS = "Prefs";
    public static final String PREF_SORT_ORDER = "sort_order";
    public static final String PREF_SORT_PLACE = "sort_order_location";
    public static final String PREF_SATURATION_COUNT = "sat_cout";
    public static final String PREF_VALUE_COUNT = "val_count";

    SharedPreferences sharedPreferences;

    public static float mHue = 1.0f;
    public static float mSaturation = 1.0f;
    public static float mValue = 1.0f;

    public static int PARTITIONS = 10;
    private static int sortSelection = -1;
    public static int saturation_swatch_length = 10;
    public static int value_swatch_length = 10;
    public static int central_hue = 70;
    public static boolean isMatchScreen = false;
    public static boolean isSimilarScreen = false;

    public final static String HUE_TAG = "hey";
    public final static String SATURATION_TAG = "you";
    public final static String VALUE_TAG = "there";
    public final static String FINAL_TAG = "!";
    public final static String CHOOSE_TAG = "!!!";
    public final static String PICTURE_TAG = "!!!!";

    public static boolean isValueScreen = false;
    private static int temp;
    private static int temp2;

    private static String[] choices = {
            "Hue, Saturation, Value",
            "Hue, Value, Saturation",
            "Saturation, Hue, Value",
            "Saturation, Value, Hue",
            "Value, Hue, Saturation",
            "Value, Saturation, Hue"
    };

    public static String sortSelectionString = choices[0];

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        if(sharedPreferences.contains(PREF_SORT_ORDER))
            sortSelectionString = sharedPreferences.getString(PREF_SORT_ORDER, "Hue, Saturation, Value");
        if(sharedPreferences.contains(PREF_SATURATION_COUNT))
            saturation_swatch_length = sharedPreferences.getInt(PREF_SATURATION_COUNT, 10);
        if(sharedPreferences.contains(PREF_VALUE_COUNT))
            value_swatch_length = sharedPreferences.getInt(PREF_VALUE_COUNT, 10);
        if(sharedPreferences.contains(PREF_SORT_PLACE))
            sortSelection = sharedPreferences.getInt(PREF_SORT_PLACE, 0);

        DataBaseHelper myDB = new DataBaseHelper(this);
        try {
            myDB.createDataBase();
        } catch(IOException e) {
            throw new Error("Unable to create database");
        }

        try {
            myDB.openDataBase();
        } catch(SQLException e) {
            try {
                throw e;
            } catch(SQLException el) {
                el.printStackTrace();
            }
        }

        FragmentManager fm  = getFragmentManager();
        fm.beginTransaction().add(R.id.container, new Choose(), CHOOSE_TAG).commit();
    }

    public void mainButtonHandler(View view) {
        FragmentManager fm  = getFragmentManager();
        fm.beginTransaction().replace(R.id.container, new HueScreen(), HUE_TAG).commit();
    }

    public void pictureButtonHandler(View view) {
        FragmentManager fm  = getFragmentManager();
        fm.beginTransaction().replace(R.id.container, new Picture(), PICTURE_TAG).commit();
    }

    public void matchColorButtonHandler(View view) {
        getFragmentManager().beginTransaction().replace(R.id.container, new ColorList(), MainActivity.FINAL_TAG).commit();
    }

    public void sortButtonHandler(View view) {

        dialogHelper().show();

    }

    public void mainScreenSwatchLengthButtonHandler(View view) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.front_screen_dialog_slider, (ViewGroup)findViewById(R.id.front_screen_dialog_slider_root_element));

        SeekBar numSwatches = (SeekBar) layout.findViewById(R.id.number_of_swatches);
        final TextView numSwatchesView = (TextView) layout.findViewById(R.id.current_swatch_length);

        SeekBar centralSwatch = (SeekBar) layout.findViewById(R.id.central_swatch_color);
        final TextView centralSwatchView = (TextView) layout.findViewById(R.id.current_cental_hue);


        //
        //  sharedprefs read
        //
        numSwatchesView.setText(String.valueOf(PARTITIONS));
        numSwatches.setProgress(PARTITIONS+1);

        centralSwatchView.setBackgroundColor(Color.HSVToColor(new float[]{1.0f * central_hue, 1.0f, 1.0f}));
        centralSwatch.setProgress(central_hue);

        numSwatches.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                numSwatchesView.setText(String.valueOf(i+1));
                temp = i+1;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        centralSwatch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                centralSwatchView.setBackgroundColor(Color.HSVToColor(new float[] {1.0f*i, 1.0f,1.0f}));
                temp2 = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the dialog title
        builder.setTitle("Sort")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setView(layout)
                        // Set the action buttons
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                        /*SaturationScreen sS = (SaturationScreen) getFragmentManager().findFragmentByTag(SATURATION_TAG);
                        if (isValueScreen) {
                            value_swatch_length = temp;
                            mValue = 1.0f * temp / value_swatch_length;
                            ValueScreen vS = (ValueScreen) getFragmentManager().findFragmentByTag(VALUE_TAG);
                            vS.updateData();
                        } else {
                            saturation_swatch_length = temp;
                            mSaturation = 1.0f * temp / saturation_swatch_length;
                            sS.updateData();
                        }*/
                        PARTITIONS = temp;
                        central_hue = temp2;
                        HueScreen hS = (HueScreen) getFragmentManager().findFragmentByTag(HUE_TAG);
                        hS.updateData();
                        updatePrefs();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        builder.create().show();
    }

    public void swatchLengthButtonHandler(View view) {

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_slider, (ViewGroup)findViewById(R.id.dialog_slider_root_element));

        SeekBar seekBar = (SeekBar)layout.findViewById(R.id.number_of_swatches);
        final TextView textView = (TextView)layout.findViewById(R.id.current_swatch_length);

        if(isValueScreen) {
            textView.setText(String.valueOf(value_swatch_length));
            seekBar.setProgress(value_swatch_length-1);
        }
        else {
            textView.setText(String.valueOf(saturation_swatch_length));
            seekBar.setProgress(saturation_swatch_length);
        }
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textView.setText(String.valueOf(i+1));
                temp = i+1;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the dialog title
        builder.setTitle("Sort")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setView(layout)
                        // Set the action buttons
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                        SaturationScreen sS = (SaturationScreen) getFragmentManager().findFragmentByTag(SATURATION_TAG);
                        if (isValueScreen) {
                            value_swatch_length = temp;
                            mValue = 1.0f * temp / value_swatch_length;
                            ValueScreen vS = (ValueScreen) getFragmentManager().findFragmentByTag(VALUE_TAG);
                            vS.updateData();
                        } else {
                            saturation_swatch_length = temp;
                            mSaturation = 1.0f * temp / saturation_swatch_length;
                            sS.updateData();
                        }
                        updatePrefs();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        builder.create().show();
    }


    public Dialog dialogHelper() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the dialog title
        builder.setTitle("Sort")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setSingleChoiceItems(choices, sortSelection,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                temp = which;
                            }
                        })
                        // Set the action buttons
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                        sortSelectionString = choices[temp];
                        sortSelection = temp;
                        FragmentManager fm = getFragmentManager();
                        ColorList frag = (ColorList) fm.findFragmentByTag(FINAL_TAG);
                        updatePrefs();
                        frag.updateData();
                     }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home) {
            //mSaturation = mValue = 1.0f;
            //getFragmentManager().beginTransaction().replace(R.id.container, new HueScreen()).commit();
            Toast.makeText(this, "No Going Back", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updatePrefs() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_SORT_ORDER, sortSelectionString);
        editor.putInt(PREF_SATURATION_COUNT, saturation_swatch_length);
        editor.putInt(PREF_VALUE_COUNT, value_swatch_length);
        editor.putInt(PREF_SORT_PLACE, sortSelection);
        editor.commit();
    }

    public void setHue(float hue) { mHue = hue; }
    public void setSaturation(float saturation) { this.mSaturation = saturation; }
    public void setValue(float value) { this.mValue = value; }
    public float getHue() { return this.mHue; }
    public float getSaturation() { return this.mSaturation; }
    public float getValue() { return this.mValue; }

    public static float getLeftHue() {
        return mHue;
    }
    public static float getRightHue() {
        return mHue + 360/PARTITIONS;
    }
    public static float getValueValue() {
        return mValue;
    }
    public static float getSaturationValue() {
        return mSaturation;
    }

}
