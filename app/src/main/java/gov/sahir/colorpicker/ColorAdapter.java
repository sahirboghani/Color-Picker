package gov.sahir.colorpicker;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Sahir on 4/19/2015.
 */
public class ColorAdapter extends ArrayAdapter<GradientDrawable> {

    private final List<GradientDrawable> colorList;

    public ColorAdapter(Context context, List<GradientDrawable> objects) {
        super(context, 0, objects);

        colorList = objects;
    }

    private static class ViewHolder {
        TextView errything;
    }

    @Override
    public View getView(int position, View covertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(covertView == null) {
            viewHolder = new ViewHolder();

            covertView = LayoutInflater.from(getContext()).inflate(R.layout.swatch_list, parent, false);

            viewHolder.errything = (TextView) covertView.findViewById(R.id.swatch);

            // cache the files
            covertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) covertView.getTag();
        }

        // populate the sub-views
        GradientDrawable gD = getItem(position);
        viewHolder.errything.setBackground(gD);
        viewHolder.errything.setTextSize(26);


        return covertView;
    }
}
