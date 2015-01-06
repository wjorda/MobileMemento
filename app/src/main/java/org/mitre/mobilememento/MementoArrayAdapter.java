package org.mitre.mobilememento;

/**
 * Created by wes on 12/18/14.
 */

import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * TODO:Document my code? Why do you think they call it "code"?
 */
public class MementoArrayAdapter extends BaseAdapter {

    private ArrayList<Memento> mementos;
    private ViewArchiveActivity context;

    public MementoArrayAdapter(ViewArchiveActivity context) {
        Log.d("AchiveSelectDialog", "C");
        this.context = context;

        if(context.getTimeMap() != null) mementos = context.getTimeMap().getMementos();
        else mementos = new ArrayList<Memento>();
    }

    public void setTimeMap(TimeMap timeMap) {
        mementos = timeMap.getMementos();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mementos.size();
    }

    @Override
    public Object getItem(int i) {
        return mementos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View oldView, ViewGroup viewGroup) {
        Log.d("AchiveSelectDialog", "D");
        Memento memento = mementos.get(i);

        View v = oldView;
        if(v == null) {
            v = View.inflate(context, R.layout.archive_list_element, null);
        }

        Log.d("AchiveSelectDialog", "E");

        ((TextView) v.findViewById(R.id.element_title)).setText(memento.getTime(DateFormat.getDateFormat(context)));
        ((ImageView) v.findViewById(R.id.screenBadge)).setImageDrawable(memento.getScreenType().makeDrawable(context));

        Log.d("AchiveSelectDialog", "F");

        return v;
    }
}
