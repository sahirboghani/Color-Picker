package gov.sahir.colorpicker.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.Console;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import gov.sahir.colorpicker.ColorCursorAdapter;
import gov.sahir.colorpicker.MainActivity;

/**
 * Created by Sahir on 4/19/2015.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    private static String TAG = "DataBaseHelper";
    private static String DB_PATH = "data/data/gov.sahir.colorpicker/databases/";
    private static final String DB_NAME = "ColorDB.sqlite";
    private static final int DATABASE_VERSION = 1;
    private static SQLiteDatabase mDataBase;
    private final Context mContext;

    public static final String SELECTION = "Hue >= ? and Hue <= ? and Saturation >= ? and Saturation <= ? and Value >= ? and Value <= ?";

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    public void createDataBase() throws IOException {

        if(!checkDataBase()) {
            this.getReadableDatabase();

            try {
                copyDataBase();
            } catch(IOException e) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    private boolean checkDataBase() throws IOException {

        SQLiteDatabase checkdb = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            checkdb = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch(SQLiteException e) {
            /* db doesn't exist */
        }

        if(checkdb != null) {
            checkdb.close();
        }
        return checkdb != null;
    }

    private void copyDataBase() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while((mLength = mInput.read(mBuffer))>0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    // open the Database so we can query it
    public void openDataBase() throws SQLException {
        String mPath = DB_PATH + DB_NAME;
        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    public static String[] getSelectionArgs() {
        float hueLeft = MainActivity.getLeftHue();
        float hueRight = MainActivity.getRightHue();
        float saturation = MainActivity.getSaturationValue();
        float value = MainActivity.getValueValue();
        float delta = .15f;

        float leftSaturation = saturation - delta;
        float rightSaturation = saturation + delta;
        float leftValue = value - delta;
        float rightValue = value + delta;

        List<String> selectionArgs = new ArrayList<>();
        selectionArgs.add(Float.toString(hueLeft));
        selectionArgs.add(Float.toString(hueRight));
        selectionArgs.add(Float.toString(leftSaturation));
        selectionArgs.add(Float.toString(rightSaturation));
        selectionArgs.add(Float.toString(leftValue));
        selectionArgs.add(Float.toString(rightValue));

        final String[] selection_args = new String[selectionArgs.size()];
        selectionArgs.toArray(selection_args);
        return selection_args;
    }

    public static String getOrder() {
        return ColorCursorAdapter.currentOrder != -1 ? ColorCursorAdapter.ORDER_BY[ColorCursorAdapter.currentOrder] : null;
    }

    @Override
    public synchronized void close() {
        if(mDataBase != null)
            mDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase database) {}

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i2) {

    }
/*
    public Cursor testing(){

        SQLiteDatabase checkdb = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
        return checkdb.rawQuery("select * from Color", null);
    }
*/
}
