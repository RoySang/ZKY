package sict.zky.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.testin.agent.TestinAgent;

import sict.zky.deskclock.R;
import sict.zky.domain.Pc_data;
import sict.zky.service.Pc_bgdataService;
import sict.zky.service.Pc_dataService;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
// test
public class datalistActivity extends Activity {
	private ListView listView;
	private Pc_dataService pc_dataService;
	private Pc_bgdataService pc_bgdataservice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.inputdata);
		pc_dataService = new Pc_dataService(getApplicationContext());
		pc_bgdataservice = new Pc_bgdataService(getApplicationContext());
//		listView = (ListView) this.findViewById(R.id.uploaddata_listview);
		show();
	}
	
	
	@Override
	protected void onStart()
	{
		super.onStart();
		TestinAgent.onStart(this);
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		TestinAgent.onStop(this);
	}

	private void show() {// ��ʽ���
		List<Pc_data> pc_datas = pc_dataService.selectAll();

		List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

//		for (Pc_data pc_data : pc_datas) {
//			HashMap<String, Object> item = new HashMap<String, Object>();
//			item.put("userId", pc_data.getUserId());
//			item.put("systolicPressure", pc_data.getSystolicPressure());
//			item.put("diastolicPressure", pc_data.getDiastolicPressure());
//			item.put("pluse", pc_data.getPulse());
//			item.put("uploadTime", pc_data.getUploadTime());
//			item.put("familyMember", pc_data.getFamilyMember());
//
//			data.add(item);  
//		}
		for (Pc_data pc_data : pc_datas) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("userId", pc_data.getUserId());
			item.put("uploadTime", pc_data.getUploadTime());
			item.put("systolicPressure", pc_data.getSystolicPressure());
			item.put("diastolicPressure", pc_data.getDiastolicPressure());
			item.put("pluse", pc_data.getPulse());
			
			item.put("familyMember", pc_data.getFamilyMember());

			data.add(item);
		}

		SimpleAdapter adapter = new SimpleAdapter(this, data,
				R.layout.pc_dataitem, new String[] { "userId","uploadTime",
						"systolicPressure", "diastolicPressure", "pluse",
						 "familyMember" }, new int[] {
						R.id.userId, R.id.uploadTime,R.id.systolicPressure,
						R.id.diastolicPressure, R.id.pluse, R.id.familyMember });
		listView.setAdapter(adapter);
	}

}
