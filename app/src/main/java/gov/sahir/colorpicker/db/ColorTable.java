package gov.sahir.colorpicker.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Sahir on 4/20/2015.
 */

public class ColorTable {

    // Column names
    public static final String COLOR_TABLE = "Color";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_HEX = "hex";
    public static final String COLUMN_HUE = "hue";
    public static final String COLUMN_SATURATION = "saturation";
    public static final String COLUMN_VALUE = "value";

    private static HashSet<String> VALID_COLUMN_NAMES;

    static {
        String[] validNames = {
                COLUMN_ID,
                COLUMN_NAME,
                COLUMN_HEX,
                COLUMN_HUE,
                COLUMN_SATURATION,
                COLUMN_VALUE
        };

        VALID_COLUMN_NAMES = new HashSet<String>(Arrays.asList(validNames));
    }

    public static void validateProjection(String[] projection) {

        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));

            // check if all columns which are requested are available
            if (!VALID_COLUMN_NAMES.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}
