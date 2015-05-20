package gov.sahir.colorpicker.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import org.w3c.dom.Text;

import gov.sahir.colorpicker.db.ColorTable;
import gov.sahir.colorpicker.db.DataBaseHelper;

/**
 * Created by Sahir on 4/19/2015.
 */
public class ColorContentProvider extends ContentProvider {

    // database
    private DataBaseHelper database;

    private static final String AUTHORITY = "gov.sahir.colorpicker.provider";
    private static final String BASE_PATH = "Colors";

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/Colors";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/Color";

    public static final String CONTENT_UR_PREFIX = "content://" + AUTHORITY + "/" + BASE_PATH;
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    // URI Matcher
    private static final UriMatcher urimatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int TASKS = 1;
    private static final int TASK_ID = 2;

    static {
        urimatcher.addURI(AUTHORITY, BASE_PATH, TASKS);
        urimatcher.addURI(AUTHORITY, BASE_PATH + "/#", TASK_ID);
    }

    @Override
    public boolean onCreate() {
        database = new DataBaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        ColorTable.validateProjection(projection);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        queryBuilder.setTables(ColorTable.COLOR_TABLE);

        switch (urimatcher.match(uri)) {
            case TASKS:
                break;
            case TASK_ID:
                queryBuilder.appendWhere(ColorTable.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }

        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
