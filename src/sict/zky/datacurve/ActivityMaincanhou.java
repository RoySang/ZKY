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
import sict.zky.service.Pc_bgdataService;
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

@SuppressLint("NewApi")
public class ActivityMaincanhou extends Fragment
{
	private String titles22[] = { "血糖-餐后 mmol/L","",""};
	private int colors22[] = { Color.rgb(0, 173, 239),Color.rgb(151, 208, 91), Color.rgb(243, 64, 70)};
	private PointStyle pss22[] = { PointStyle.CIRCLE,PointStyle.POINT,PointStyle.POINT};
	private Pc_bgdataService pc_bgdataService;
	private GraphicalView mChartView22;
	private String userName;
	private String starttime;
	private String endtime;
	private int timeorci;
	private Integer year = 0, month = 0, day = 0, hour = 0, minute = 0;
	private LinearLayout linearLayout1;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		
		Calendar c = Calendar.getInstance();//得到当前时间
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);	
		
		userName=((DatacurveGLU)getActivity()).getUserName();
		starttime=((DatacurveGLU)getActivity()).getStarttime();
		endtime=((DatacurveGLU)getActivity()).getEndtime();
		timeorci=((DatacurveGLU)getActivity()).getTimeorci();
		
		pc_bgdataService=new Pc_bgdataService(getActivity());
		if(timeorci==0){//按次追踪
			showpc_bgdatabyci();
		}else{//按时间追踪
			showpc_bgdatabytime();
		}
		super.onActivityCreated(savedInstanceState);
	}
public void showpc_bgdatabyci() {//按次显示
		
		List<Pc_bgdata> pc_bgdatas;
		if(starttime.equals("0")){//显示全部
			pc_bgdatas = pc_bgdataService.selectDatabytype(userName,2);
		}else{//按时间范围显示
			pc_bgdatas = pc_bgdataService.selectDatabynameandtimerangeandtype(userName, starttime, endtime,2);
		}
		int a = 0;
	  if (pc_bgdatas != null) {
			a = pc_bgdatas.size();// 总共数据的个数
		}

//		String[] data1 = new String[a];
	  double[] y2Values = new double[a];
	  double[] y11Values = new double[a];
	  double[] y12Values = new double[a];
		
		for (int i = 0; i < a; i++) {
//			data1[i] = pc_datas.get(i).getUploadTime();//得到上传时间
			y2Values[i] = pc_bgdatas.get(i).getBloodGlucose();//得到脉搏值
			y11Values[i] = 4.4;
			y12Values[i] = 8.0;
		}
		
		List<double[]> yValues2 = new ArrayList<double[]>();//保存所有点的y值
		yValues2.add(y2Values);
		yValues2.add(y11Values);
		yValues2.add(y12Values);
		
		List<double[]> xValues = new ArrayList<double[]>();//保存所有点的x值
		double[] datearr1 = new double[a];
		for (int k = 0; k < a; k++) {//按次，0—a
			datearr1[k] = k;
		}
		xValues.add(datearr1);

		//根据LineChar画图
		mChartView22 = ChartFactory.getLineChartView(getActivity(),
				buildDateDatasetbyci(titles22, xValues, yValues2),
				buildRendererbyci(colors22, pss22));
		linearLayout1.removeAllViews(); // 先remove再add可以实现统计图更新
		linearLayout1.addView(mChartView22, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

	}
	private XYMultipleSeriesDataset buildDateDatasetbyci(String[] titles11,
			List<double[]> xValues, List<double[]> yValues1) {
		XYMultipleSeriesDataset dataset1 = new XYMultipleSeriesDataset();
		int length = titles11.length;
		for (int i = 0; i < length; i++) {
			XYSeries series1 = new XYSeries(titles11[i]);
			double[] xV = xValues.get(0); // 画TimeChart横坐标需要为Date数据类型
			double[] yV1 = yValues1.get(i);
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series1.add(xV[k], yV1[k]);

			}
			dataset1.addSeries(series1);
		}
		return dataset1;

	}
	private XYMultipleSeriesRenderer buildRendererbyci(int[] colors13,
			PointStyle[] pss13) {

		XYMultipleSeriesRenderer renderer1 = new XYMultipleSeriesRenderer();

		renderer1.setApplyBackgroundColor(true);
		renderer1.setBackgroundColor(Color.TRANSPARENT);
		renderer1.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
		renderer1.setAxesColor(Color.BLACK);

		renderer1.setAxisTitleTextSize(20); // 坐标轴标题字体大小
		renderer1.setChartTitleTextSize(30);// 图标标题字体大小
//		renderer1.setChartTitle("");

		renderer1.setLabelsTextSize(30);// 轴标签字体大小
		renderer1.setLegendTextSize(40);// 图例字体大小
		renderer1.setPointSize(7f);//
		renderer1.setYLabels(8);// Sets the approximate number of labels for
								// the Y axis.
		renderer1.setXLabels(10);// Sets the approximate number of labels for
									// the
									// X axis.
									// renderer1.setLabelsColor(Color.rgb(0, 0,
									// 0));
		renderer1.setLabelsColor(Color.rgb(60, 60, 60));
//		renderer1.setShowGrid(true);// 是否显示网格
		renderer1.setYLabelsAlign(Align.RIGHT);// 设置刻度线与Y轴之间的相对位置关系
		renderer1.setGridColor(Color.LTGRAY);// 网格的颜色
		renderer1.setPanLimits(new double[] { 0, 10000, 0, 0 });// 设置拖动时X轴Y轴允许的最大值最小值

		// renderer1.setA
		renderer1.setZoomEnabled(true, false);
		renderer1.setPanEnabled(true, false);
		// 此处最大最小表示相隔一天，相信很容易明白吧，具体跟date的参数值有关，这是我自己琢磨测试出来的，官方的权威的没有找到。目前好用
		// 可以多修改几次试试，同时注意上面说的参数，一块测试几个试试就ok了
		// renderer1.setXAxisMin(new Date(year - 1900, month, day-7, hour,
		// minute).getTime());
		// renderer1.setXAxisMax(new Date(year - 1900, month, day+1, hour,
		// minute).getTime());

		renderer1.setXAxisMax(10);
		renderer1.setXAxisMin(0);

		renderer1.setYAxisMax(14);
		renderer1.setYAxisMin(1);
		renderer1.setMargins(new int[] { 40, 50, 40, 0 }); // 设置图形四周的留白
		int length1 = colors13.length;
		for (int i = 0; i < length1; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors13[i]);
			r.setPointStyle(pss13[i]);
			r.setFillPoints(true);
			r.setLineWidth(3.0f);
//			r.setDisplayChartValues(true);//折线上显示数字
			r.setChartValuesSpacing(10);
			r.setChartValuesTextSize(20);
			renderer1.addSeriesRenderer(r);
		}
		renderer1.getSeriesRendererAt(0).setDisplayChartValues(true);
		return renderer1;
	};
	
	
	public void showpc_bgdatabytime() {//按时间追踪
		// if (pc_dataService.ishavepc_data(name)) {
		List<Pc_bgdata> pc_bgdatas;
		if(starttime.equals("0")){//显示全部
			pc_bgdatas = pc_bgdataService.selectDatabytype(userName,2);
		}else{//时间范围显示
			pc_bgdatas = pc_bgdataService.selectDatabynameandtimerangeandtype(userName, starttime, endtime,2);
		}
		int a = 0;
		if (pc_bgdatas != null) {
			a = pc_bgdatas.size();// 总共数据的个数
		}

		String[] data1 = new String[a];
		double[] y2Values = new double[a];
		double[] y11Values = new double[a];
		  double[] y12Values = new double[a];
		
		for (int i = 0; i < a; i++) {
			data1[i] = pc_bgdatas.get(i).getUploadTime();//得到上传时间
			y2Values[i] = pc_bgdatas.get(i).getBloodGlucose();//得到血糖值
			y11Values[i] = 4.4;
			y12Values[i] = 8.0;
		}
		
		List<double[]> yValues2 = new ArrayList<double[]>();//保存点的y值		
		yValues2.add(y2Values);
		yValues2.add(y11Values);
		yValues2.add(y12Values);

		List<Date[]> xValues = new ArrayList<Date[]>();//保存点的x值
		Date[] datearr1 = new Date[a];
		for (int k = 0; k < a; k++) {
			String str = data1[k];
			Format f = new SimpleDateFormat("yy-MM-dd HH:mm:ss");//得到
			try {
				Date d = (Date) f.parseObject(str);

				datearr1[k] = d;

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		xValues.add(datearr1);

		//根据TimeChar画图
		mChartView22 = ChartFactory.getTimeChartView(getActivity(),
				buildDateDatasetbytime(titles22, xValues, yValues2),
				buildRendererbytime(colors22, pss22), "MM-dd HH:mm");//x轴坐标按次格式显示
		linearLayout1.removeAllViews(); // 先remove再add可以实现统计图更新
		linearLayout1.addView(mChartView22, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
	}
		
	
	

	private XYMultipleSeriesRenderer buildRendererbytime(int[] colors1,
			PointStyle[] pss1) {

		XYMultipleSeriesRenderer renderer1 = new XYMultipleSeriesRenderer();

		renderer1.setApplyBackgroundColor(true);
		renderer1.setBackgroundColor(Color.TRANSPARENT);
		renderer1.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));

		renderer1.setAxisTitleTextSize(20); // 坐标轴标题字体大小
		renderer1.setChartTitleTextSize(30);// 图标标题字体大小
//		renderer1.setChartTitle("血压数据曲线");
		renderer1.setLabelsTextSize(30);// 轴标签字体大小
		renderer1.setLegendTextSize(40);// 图例字体大小
		renderer1.setPointSize(7f);//
		renderer1.setAxesColor(Color.BLACK);
		renderer1.setYLabels(6);// Sets the approximate number of labels for
								// the Y axis.
		renderer1.setXLabels(3);// Sets the approximate number of labels for the
		renderer1.setAxesColor(Color.BLACK); // X axis.
		renderer1.setLabelsColor(Color.rgb(60, 60, 60));
		renderer1.setShowGrid(true);
		renderer1.setYLabelsAlign(Align.RIGHT);// 设置刻度线与Y轴之间的相对位置关系
		renderer1.setGridColor(Color.LTGRAY);// 网格的颜色
		renderer1.setZoomEnabled(true, false);
		renderer1.setPanEnabled(true, false);
		// renderer1.setPanLimits(new double[] {0,100,0,0});//设置拖动时X轴Y轴允许的最大值最小值
		// 此处最大最小表示相隔一天，相信很容易明白吧，具体跟date的参数值有关，这是我自己琢磨测试出来的，官方的权威的没有找到。目前好用
		// 可以多修改几次试试，同时注意上面说的参数，一块测试几个试试就ok了
		renderer1.setXAxisMin(new Date(year - 1900, month, day - 7, hour,
				minute).getTime());
		renderer1.setXAxisMax(new Date(year - 1900, month, day + 1, hour,
				minute).getTime());

		renderer1.setYAxisMax(14);
		renderer1.setYAxisMin(1);
		renderer1.setMargins(new int[] { 40, 50, 40, 0 }); // 设置图形四周的留白
		int length1 = colors1.length;
		for (int i = 0; i < length1; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors1[i]);
			r.setPointStyle(pss1[i]);
			r.setFillPoints(true);
			r.setLineWidth(3.0f);
//			r.setDisplayChartValues(true);//折线上显示数字
//			r.setChartValuesSpacing(10);
//			r.setChartValuesTextSize(20);
			renderer1.addSeriesRenderer(r);
		}
//		renderer1.getSeriesRendererAt(0).setDisplayChartValues(true);
		return renderer1;
	};

	// 画图
		private XYMultipleSeriesDataset buildDateDatasetbytime(String[] titles1,
				List<Date[]> xValues, List<double[]> yValues1) {
			XYMultipleSeriesDataset dataset1 = new XYMultipleSeriesDataset();
			int length = titles1.length;
			for (int i = 0; i < length; i++) {
				TimeSeries series1 = new TimeSeries(titles1[i]);
				Date[] xV = xValues.get(0); // 画TimeChart横坐标需要为Date数据类型
				double[] yV1 = yValues1.get(i);
				int seriesLength = xV.length;
				for (int k = 0; k < seriesLength; k++) {
					series1.add(xV[k], yV1[k]);

				}
				dataset1.addSeries(series1);
			}
			return dataset1;

		}


	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.activitymaincanhou, null);  
		linearLayout1 = (LinearLayout) view.findViewById(R.id.container22);
		return view;
	}
	

}
