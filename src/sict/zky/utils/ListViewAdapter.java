package sict.zky.utils;

import java.util.ArrayList;

import sict.zky.deskclock.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
 
/**
 *   ≈‰∆˜
 * @author Administrator
 *
 */
public class ListViewAdapter extends BaseAdapter{
     
    private LayoutInflater inflater;
     
    private ArrayList<String> list; 
     
     
 
    public ListViewAdapter(Context context, ArrayList<String> list) {
        super();
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }
 
    public int getCount() {
        return list.size();
    }
 
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }
 
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.lv_items, null);
        }
        TextView tv = (TextView)convertView.findViewById(R.id.text);
        tv.setText(list.get(position));
         
        return convertView;
    }
 
}
