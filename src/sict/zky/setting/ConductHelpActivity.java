package sict.zky.setting;


import sict.zky.deskclock.DeskClockMainActivity;
import sict.zky.deskclock.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class ConductHelpActivity extends Activity {

	private LinearLayout gsmButton;
//	private LinearLayout lanyaXueTangButton;
	private LinearLayout lanyaXueYaButton;
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.conducthelp);
		gsmButton = (LinearLayout) this.findViewById(R.id.gsmhelp);
//		lanyaXueTangButton = (LinearLayout) this.findViewById(R.id.bluetoothhelp);
		lanyaXueYaButton = (LinearLayout) this.findViewById(R.id.xuetanghelp);
		
		
		gsmButton.setOnClickListener(onclicklistener);
		lanyaXueYaButton.setOnClickListener(onclicklistener);
		
	}
	public OnClickListener onclicklistener = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.gsmhelp:// GSM �豸����
				Intent intent1 = new Intent();
				intent1.setClass(ConductHelpActivity.this,
						ConductHelpGSMActivity.class);
				ConductHelpActivity.this.startActivity(intent1);

				break;
//			case R.id.bluetoothhelp://����Ѫѹ�豸������Ϣ
//				Intent intent2 = new Intent();
//				intent2.setClass(ConductHelpActivity.this,
//						HelpProductActivity.class);
//				ConductHelpActivity.this.startActivity(intent2);
//
//				break;
		   case R.id.xuetanghelp://����2.0�豸Ѫ�ǰ����ĵ�
			   Intent intent3= new Intent();
				intent3.setClass(ConductHelpActivity.this,
						ConductHelpXuetang.class);
				ConductHelpActivity.this.startActivity(intent3);

				break;
				
			}
		}
	};
	
}
