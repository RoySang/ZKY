package sict.zky.datacurve;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import sict.zky.deskclock.R;
import sict.zky.domain.Pc_bgdata;
import sict.zky.domain.Pc_data;
import sict.zky.service.Pc_bgdataService;
import sict.zky.service.Pc_dataService;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;

@SuppressLint("NewApi")
public class ActivityMainDiYa extends Fragment {
	private String titles12[] = { "����ѹ mmHg","",""};
	private int colors12[] = { Color.rgb(0, 173, 239),Color.rgb(151, 208, 91), Color.rgb(243, 64, 70)};
	private PointStyle pss12[] = { PointStyle.CIRCLE,PointStyle.POINT,PointStyle.POINT };
	private Pc_dataService pc_dataService;
	private GraphicalView mChartView12;
	private String userName;
	private String starttime;
	private String endtime;
	private int timeorci;
	private LinearLayout linearLayout1;
	private View view;
	private Integer year = 0, month = 0, day = 0, hour = 0, minute = 0;
	
	public ActivityMainDiYa() {
		super();
	}
	
	
	public void onActivityCreated(Bundle savedInstanceState) {
		Calendar c = Calendar.getInstance();//�õ���ǰʱ��
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);		
		
		//ǿ��ת���õ�����Ҫ�Ĳ���ֵ
	    userName=((DatacurvePressure)getActivity()).getUserName();
		starttime=((DatacurvePressure)getActivity()).getStarttime();
		endtime=((DatacurvePressure)getActivity()).getEndtime();
		timeorci=((DatacurvePressure)getActivity()).getTimeorci();

		
		pc_dataService=new Pc_dataService(getActivity());
		   if(timeorci==0){//����׷��
				showpc_databyci();
			}else{//��ʱ��׷��
				showpc_databytime();
			}
		   super.onActivityCreated(savedInstanceState);
		
	}
	public void showpc_databyci() {//������ʾ
		List<Pc_data> pc_datas;
		if(starttime.equals("0")){//��ʾȫ��
			pc_datas = pc_dataService.selectData(userName);
		}else{//��ʱ�䷶Χ��ʾ
			pc_datas = pc_dataService.selectDatabynameandtimerange(userName, starttime, endtime);
		}
		int a = 0;
	  if (pc_datas != null) {
			a = pc_datas.size();// �ܹ����ݵĸ���
		}
//		String[] data1 = new String[a];
	  double[] y2Values = new double[a];
	  double[] y21Values = new double[a];
	  double[] y22Values = new double[a];
		
		for (int i = 0; i < a; i++) {
//			data1[i] = pc_datas.get(i).getUploadTime();//�õ��ϴ�ʱ��
			y2Values[i] = pc_datas.get(i).getDiastolicPressure();//�õ�����ѹ
			y21Values[i] = 60;
			y22Values[i] = 90;
		}
		
		List<double[]> yValues2 = new ArrayList<double[]>();//�������е��yֵ
		yValues2.add(y2Values);
		yValues2.add(y21Values);
		yValues2.add(y22Values);
		
		List<double[]> xValues = new ArrayList<double[]>();//�������е��xֵ
		double[] datearr1 = new double[a];
		for (int k = 0; k < a; k++) {//���Σ�0��a
			datearr1[k] = k;
		}
		xValues.add(datearr1);

		//����LineChar��ͼ
		mChartView12 = ChartFactory.getLineChartView(getActivity(),
				buildDateDatasetbyci(titles12, xValues, yValues2),
				buildRendererbyci(colors12, pss12));
		linearLayout1.removeAllViews(); // ��remove��add����ʵ��ͳ��ͼ����
		linearLayout1.addView(mChartView12, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

	}
	private XYMultipleSeriesDataset buildDateDatasetbyci(String[] titles12,
			List<double[]> xValues, List<double[]> yValues1) {
		XYMultipleSeriesDataset dataset1 = new XYMultipleSeriesDataset();
		int length = titles12.length;
		for (int i = 0; i < length; i++) {
			XYSeries series1 = new XYSeries(titles12[i]);
			double[] xV = xValues.get(0); // ��TimeChart��������ҪΪDate��������
			double[] yV1 = yValues1.get(i);
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series1.add(xV[k], yV1[k]);

			}
			dataset1.addSeries(series1);
		}
		return dataset1;

	}
	private XYMultipleSeriesRenderer buildRendererbyci(int[] colors12,
			PointStyle[] pss12) {

		XYMultipleSeriesRenderer renderer1 = new XYMultipleSeriesRenderer();

		renderer1.setApplyBackgroundColor(true);
		renderer1.setBackgroundColor(Color.TRANSPARENT);
		renderer1.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
		renderer1.setAxesColor(Color.BLACK);

		renderer1.setAxisTitleTextSize(20); // ��������������С
		renderer1.setChartTitleTextSize(30);// ͼ����������С
//		renderer1.setChartTitle("");

		renderer1.setLabelsTextSize(30);// ���ǩ�����С
		renderer1.setLegendTextSize(40);// ͼ�������С
		renderer1.setPointSize(7f);//
		renderer1.setYLabels(8);// Sets the approximate number of labels for
								// the Y axis.
		renderer1.setXLabels(10);// Sets the approximate number of labels for
									// the
									// X axis.
									// renderer1.setLabelsColor(Color.rgb(0, 0,
									// 0));
		renderer1.setLabelsColor(Color.rgb(60, 60, 60));
//		renderer1.setShowGrid(true);// �Ƿ���ʾ����
		renderer1.setYLabelsAlign(Align.RIGHT);// ���ÿ̶�����Y��֮������λ�ù�ϵ
		renderer1.setGridColor(Color.LTGRAY);// �������ɫ
		renderer1.setPanLimits(new double[] { 0, 10000, 0, 0 });// �����϶�ʱX��Y����������ֵ��Сֵ

		// renderer1.setA
		renderer1.setZoomEnabled(true, false);
		renderer1.setPanEnabled(true, false);
		// �˴������С��ʾ���һ�죬���ź��������װɣ������date�Ĳ���ֵ�йأ��������Լ���ĥ���Գ����ģ��ٷ���Ȩ����û���ҵ���Ŀǰ����
		// ���Զ��޸ļ������ԣ�ͬʱע������˵�Ĳ�����һ����Լ������Ծ�ok��
		// renderer1.setXAxisMin(new Date(year - 1900, month, day-7, hour,
		// minute).getTime());
		// renderer1.setXAxisMax(new Date(year - 1900, month, day+1, hour,
		// minute).getTime());

		renderer1.setXAxisMax(10);
		renderer1.setXAxisMin(0);

		renderer1.setYAxisMax(150);
		renderer1.setYAxisMin(50);
		renderer1.setMargins(new int[] { 40, 50, 40, 0 }); // ����ͼ�����ܵ�����
		int length1 = colors12.length;
		for (int i = 0; i < length1; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors12[i]);
			r.setPointStyle(pss12[i]);
			r.setFillPoints(true);
			r.setLineWidth(3.0f);
//			r.setDisplayChartValues(true);//��������ʾ����
			r.setChartValuesSpacing(10);
			r.setChartValuesTextSize(20);
			renderer1.addSeriesRenderer(r);
			
		}
		renderer1.getSeriesRendererAt(0).setDisplayChartValues(true);
		return renderer1;
	};
	
	
	
	
	
	public void showpc_databytime() {//��ʱ��׷��
		// if (pc_dataService.ishavepc_data(name)) {
		List<Pc_data> pc_datas;
		if(starttime.equals("0")){//��ʾȫ��
			pc_datas = pc_dataService.selectData(userName);
		}else{//ʱ�䷶Χ��ʾ
			pc_datas = pc_dataService.selectDatabynameandtimerange(userName, starttime, endtime);
		}
		int a = 0;
		if (pc_datas != null) {
			a = pc_datas.size();// �ܹ����ݵĸ���
		}

		String[] data1 = new String[a];
		double[] y2Values = new double[a];
		double[] y11Values = new double[a];
		double[] y12Values = new double[a];
		
		for (int i = 0; i < a; i++) {
			data1[i] = pc_datas.get(i).getUploadTime();//�õ��ϴ�ʱ��
			y2Values[i] = pc_datas.get(i).getDiastolicPressure();//�õ�����ѹ
			y11Values[i] = 90;
			y12Values[i] = 140;
		}
		
		List<double[]> yValues2 = new ArrayList<double[]>();//������yֵ		
		yValues2.add(y2Values);
		yValues2.add(y11Values);
		yValues2.add(y12Values);

		List<Date[]> xValues = new ArrayList<Date[]>();//������xֵ
		Date[] datearr1 = new Date[a];
		for (int k = 0; k < a; k++) {
			String str = data1[k];
			Format f = new SimpleDateFormat("yy-MM-dd HH:mm:ss");//�õ�
			try {
				Date d = (Date) f.parseObject(str);

				datearr1[k] = d;

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		xValues.add(datearr1);

		//����TimeChar��ͼ
		mChartView12= ChartFactory.getTimeChartView(getActivity(),
				buildDateDatasetbytime(titles12, xValues, yValues2),
				buildRendererbytime(colors12, pss12), "MM-dd HH:mm");//x�����갴�θ�ʽ��ʾ
		linearLayout1.removeAllViews(); // ��remove��add����ʵ��ͳ��ͼ����
		linearLayout1.addView(mChartView12, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
	}
		
	
	

	private XYMultipleSeriesRenderer buildRendererbytime(int[] colors12,
			PointStyle[] pss12) {

		XYMultipleSeriesRenderer renderer1 = new XYMultipleSeriesRenderer();

		renderer1.setApplyBackgroundColor(true);
		renderer1.setBackgroundColor(Color.TRANSPARENT);
		renderer1.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));

		renderer1.setAxisTitleTextSize(20); // ��������������С
		renderer1.setChartTitleTextSize(30);// ͼ����������С
//		renderer1.setChartTitle("Ѫѹ��������");
		renderer1.setLabelsTextSize(30);// ���ǩ�����С
		renderer1.setLegendTextSize(40);// ͼ�������С
		renderer1.setPointSize(7f);//
		renderer1.setAxesColor(Color.BLACK);
		renderer1.setYLabels(6);// Sets the approximate number of labels for
								// the Y axis.
		renderer1.setXLabels(3);// Sets the approximate number of labels for the
		renderer1.setAxesColor(Color.BLACK); // X axis.
		renderer1.setLabelsColor(Color.rgb(60, 60, 60));
		renderer1.setShowGrid(true);
		renderer1.setYLabelsAlign(Align.RIGHT);// ���ÿ̶�����Y��֮������λ�ù�ϵ
		renderer1.setGridColor(Color.LTGRAY);// �������ɫ
		renderer1.setZoomEnabled(true, false);
		renderer1.setPanEnabled(true, false);
		// renderer1.setPanLimits(new double[] {0,100,0,0});//�����϶�ʱX��Y����������ֵ��Сֵ
		// �˴������С��ʾ���һ�죬���ź��������װɣ������date�Ĳ���ֵ�йأ��������Լ���ĥ���Գ����ģ��ٷ���Ȩ����û���ҵ���Ŀǰ����
		// ���Զ��޸ļ������ԣ�ͬʱע������˵�Ĳ�����һ����Լ������Ծ�ok��
		renderer1.setXAxisMin(new Date(year - 1900, month, day - 7, hour,
				minute).getTime());
		renderer1.setXAxisMax(new Date(year - 1900, month, day + 1, hour,
				minute).getTime());

		renderer1.setYAxisMax(150);
		renderer1.setYAxisMin(50);
		renderer1.setMargins(new int[] { 40, 50, 40, 0 }); // ����ͼ�����ܵ�����
		int length1 = colors12.length;
		for (int i = 0; i < length1; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors12[i]);
			r.setPointStyle(pss12[i]);
			r.setFillPoints(true);
			r.setLineWidth(3.0f);
//			r.setDisplayChartValues(true);//��������ʾ����
//			r.setChartValuesSpacing(10);
//			r.setChartValuesTextSize(20);
			renderer1.addSeriesRenderer(r);
		}
//		renderer1.getSeriesRendererAt(0).setDisplayChartValues(true);
		return renderer1;
	};

	// ��ͼ
		private XYMultipleSeriesDataset buildDateDatasetbytime(String[] titles12,
				List<Date[]> xValues, List<double[]> yValues2) {
			XYMultipleSeriesDataset dataset1 = new XYMultipleSeriesDataset();
			int length = titles12.length;
			for (int i = 0; i < length; i++) {
				TimeSeries series1 = new TimeSeries(titles12[i]);
				Date[] xV = xValues.get(0); // ��TimeChart��������ҪΪDate��������
				double[] yV1 = yValues2.get(i);
				int seriesLength = xV.length;
				for (int k = 0; k < seriesLength; k++) {
					series1.add(xV[k], yV1[k]);

				}
				dataset1.addSeries(series1);
			}
			return dataset1;

		}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.activitymaindiya, null);  
		linearLayout1 = (LinearLayout) view.findViewById(R.id.container12);
		return view;
	}

}
