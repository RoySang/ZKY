package sict.zky.service;

import java.util.List;

import sict.zky.deskclock.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class UserNameAdapter extends BaseAdapter {
	private List<String>userNames;
	private Context context;
	public UserNameAdapter(Context pContext,List<String> pList){
		this.context=pContext;
		this.userNames=pList;	
	}
	
	public int getCount() {
		return userNames.size();
	}


	public Object getItem(int position) {
		return userNames.get(position);
	}

	
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater layoutInflater=LayoutInflater.from(context);
		convertView=layoutInflater.inflate(R.layout.username_item, null);
		if(convertView!=null){
			TextView textView =(TextView)convertView.findViewById(R.id.userName_item);
			textView.setText(userNames.get(position));
		}
		return convertView;
	}

}
