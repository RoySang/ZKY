package sict.zky.main;

import com.testin.agent.TestinAgent;

import sict.zky.deskclock.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Binding_EquipmentActivity extends Activity {

	private int userId;
	private String screenName;
	private SharedPreferences sp;
	private EditText SN;
	private String SN_;
	private Button bindingSN;
	private Button clearandreturn;
	private TextView binding_textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.binding_equipmentactivity);

		sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);

		Intent intent = getIntent();
		userId = intent.getIntExtra("userId", 0);
		screenName=intent.getStringExtra("screenName");
		
		SN = (EditText) findViewById(R.id.shebeima);
		bindingSN = (Button) findViewById(R.id.bindingSN);
		binding_textView = (TextView) findViewById(R.id.binding_textView);
		clearandreturn = (Button) findViewById(R.id.clearandreturn);

		SN_ = sp.getString("SN", "null");

		if (!SN_.equals("null")) {
			bindingSN.setBackgroundResource(R.drawable.btn_removebinding);
			clearandreturn.setBackgroundResource(R.drawable.fanhui);
			binding_textView.setText("您的设备号是:");
			SN.setText(SN_);
			SN.setEnabled(false);
		}

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

	public void bingdingSN(View v) {
		if (SN_.equals("null")) {
			SN_ = SN.getText().toString().trim();
			if (SN_.length() == 9) {
				Editor editor = sp.edit();
				editor.putString("SN", SN_);
				editor.commit();
				Intent intent4 = new Intent(Binding_EquipmentActivity.this,
						Binding_EquipmentActivity.class);
				intent4.putExtra("userId", userId);
				Toast.makeText(getApplicationContext(), "绑定成功", 0).show();
				startActivity(intent4);
				finish();
			} else {
				Toast.makeText(getApplicationContext(), "请输入正确的设备号", 1).show();
			}

		} else {

			Editor editor = sp.edit();
			editor.remove("SN");
			editor.commit();
			Intent intent4 = new Intent(Binding_EquipmentActivity.this,
					Binding_EquipmentActivity.class);
			intent4.putExtra("userId", userId);
			startActivity(intent4);
			finish();
		}

	}

	public void cleartheSN(View v) {
		if (SN_.equals("null")) {
			SN.setText("");
		} else {
			// bindingSN.setBackgroundResource(R.drawable.ic_list_nobinding);
			// Editor editor = sp.edit();
			// editor.remove("SN");
			// editor.commit();
			// Intent intent4 = new Intent(Binding_EquipmentActivity.this,
			// MainActivity.class);
			// intent4.putExtra("userId", userId);
			// startActivity(intent4);
			finish();
		}

	}
}
