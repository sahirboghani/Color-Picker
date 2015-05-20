package gov.sahir.colorpicker;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import gov.sahir.colorpicker.db.ColorTable;
import gov.sahir.colorpicker.db.DataBaseHelper;

/**
 * Created by Sahir on 4/19/2015.
 */
public class ColorCursorAdapter extends CursorAdapter {

    static private final int NAME = 0;
    static private final int HEX = 1;
    static private final int HUE = 2;
    static private final int SATURATION = 3;
    static private final int VALUE = 4;
    static private final int ID = 5;

    static public final String[] PROJECTION = new String[] {ColorTable.COLUMN_NAME, ColorTable.COLUMN_HEX,
                                                            ColorTable.COLUMN_HUE, ColorTable.COLUMN_SATURATION,
                                                            ColorTable.COLUMN_VALUE, ColorTable.COLUMN_ID};
    static public int currentOrder = -1;

    static public final String[] ORDER_BY = new String[] {
            ColorTable.COLUMN_HUE + "," + ColorTable.COLUMN_SATURATION + "," + ColorTable.COLUMN_VALUE,
            ColorTable.COLUMN_HUE + "," + ColorTable.COLUMN_VALUE + "," + ColorTable.COLUMN_SATURATION,
            ColorTable.COLUMN_SATURATION + "," + ColorTable.COLUMN_HUE + "," + ColorTable.COLUMN_VALUE,
            ColorTable.COLUMN_SATURATION + "," + ColorTable.COLUMN_VALUE + "," + ColorTable.COLUMN_HUE,
            ColorTable.COLUMN_VALUE + "," + ColorTable.COLUMN_HUE + "," + ColorTable.COLUMN_SATURATION,
            ColorTable.COLUMN_VALUE + "," + ColorTable.COLUMN_SATURATION + "," + ColorTable.COLUMN_HUE
    };

    static private class ViewHolder {
        TextView color_name;
        TextView color_preview;
        Map<String, String> toastHelper;
    }

    private LayoutInflater mInflater;

    public ColorCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View row = mInflater.inflate(R.layout.row_color, parent, false);

        // cache the Views
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.color_name = (TextView) row.findViewById(R.id.phase_2_color_text);
        viewHolder.color_preview = (TextView) row.findViewById(R.id.phase_2_color_preview);

        row.setTag(viewHolder);

        return row;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = updateViewHolderValues(view, cursor);
    }

    private ViewHolder updateViewHolderValues(View view, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder)view.getTag();

        viewHolder.color_preview.setBackgroundColor(Color.parseColor(cursor.getString(HEX)));
        viewHolder.color_name.setText(cursor.getString(NAME));

        viewHolder.toastHelper = new HashMap<String, String>();

        viewHolder.toastHelper.put("name", cursor.getString(NAME));
        viewHolder.toastHelper.put("hex", cursor.getString(HEX));
        viewHolder.toastHelper.put("hue", Integer.toString(cursor.getInt(HUE)));
        viewHolder.toastHelper.put("saturation", Float.toString(cursor.getFloat(SATURATION)));
        viewHolder.toastHelper.put("value", Float.toString(cursor.getFloat(VALUE)));

        return viewHolder;
    }

    public static Map<String, String> getToastHelper(View view) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        return viewHolder.toastHelper;
    }
}
