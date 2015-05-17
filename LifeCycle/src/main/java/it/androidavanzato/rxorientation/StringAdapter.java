package it.androidavanzato.rxorientation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StringAdapter extends BaseAdapter {

    private ArrayList<String> items = new ArrayList<>();

    private Set<Integer> updating = new HashSet<>();

    private Context context;

    public StringAdapter(Context context) {
        this.context = context;
    }

    @Override public int getCount() {
        return items.size();
    }

    @Override public String getItem(int position) {
        return items.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            textView = (TextView) LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        } else {
            textView = (TextView) convertView;
        }
        textView.setText(getItem(position));
        textView.setBackgroundColor(updating.contains(position) ? 0xFFCCCCCC : 0x00000000);
        return textView;
    }

    public void replaceData(List<String> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public ArrayList<String> getItems() {
        return items;
    }

    public void startUpdate(int position) {
        updating.add(position);
        notifyDataSetChanged();
    }

    public void endUpdate(int position) {
        updating.remove(position);
        notifyDataSetChanged();
    }

    public void update(int position, String s) {
        items.set(position, s);
        endUpdate(position);
    }
}
