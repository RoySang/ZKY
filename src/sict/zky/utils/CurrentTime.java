package sict.zky.utils;

import java.util.Calendar;

public class CurrentTime {
	private int year, month, day, hour, minute, second;
	private Integer premonth,premonthofyear,preday,predayofmonth,predayofyear;
	private String yearS, monthS, dayS, hourS, minuteS, secondS;
	private String premonthS,premonthofyearS,predayS,predayofmonthS,predayofyearS;
	private String yearH, monthH, dayH, hourH, minuteH;
	private ExChange ex;

	public CurrentTime() {
		super();
	}

	// 得到当前系统时间
	public void getCurrentTime() {
		Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR) % 100;
		month = c.get(Calendar.MONTH) + 1;
		day = c.get(Calendar.DAY_OF_MONTH);
		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);
		second = c.get(Calendar.SECOND);
		
		yearS=String.valueOf(year);
		monthS=String.valueOf(month);
		dayS=String.valueOf(day);
		hourS=String.valueOf(hour);
		minuteS=String.valueOf(minute);
		secondS=String.valueOf(second);
		
	}

	public String TimeToString() {
		getCurrentTime();		
		return this.getYearS()+"-"+this.getMonthS()+"-"+this.getDayS()+" "+this.getHourS()+":"+this.getMinuteS()+":"+this.getSecondS();
	}

	
	
	public void getOnepreweekTime(){
		getCurrentTime();
		premonth=month-1;
		if(day>6){
			preday=day-6;
			predayofmonth=month;
			predayofyear=year;
		}else{
			if(premonth==2||premonth==4||premonth==6||premonth==7||premonth==9||premonth==11){
				preday=31+day-6;
				predayofmonth=month-1;
				predayofyear=year;
			}else if(premonth==0){
				preday=31+day-6;
				predayofmonth=month-1;
				predayofyear=year-1;
			}else if(premonth==3||premonth==5||premonth==8||premonth==10){
				preday=30+day-6;
				predayofmonth=month-1;
				predayofyear=year;
			}else if(premonth==1){
				preday=28+day-6;
				predayofmonth=month-1;
				predayofyear=year;
			}
			
		}
		predayS=String.valueOf(preday);
		predayofmonthS=String.valueOf(predayofmonth);
		predayofyearS=String.valueOf(predayofyear);
		premonthS=String.valueOf(premonth);
	}
	
	// 把时间传化成字符串	
	public String OnepreweekTimeToString() {
		getOnepreweekTime();
		return this.getPredayofyearS()+"-"+this.getPredayofmonthS()+"-"+this.getPredayS()+
				" "+this.getHourS()+":"+this.getMinuteS()
				+":"+this.getSecondS();
	}
	
	
	public void getpremonthTime(){
		getCurrentTime();
		premonth=month-1;
		if(premonth==0){
			premonthofyear=year-1;
			premonth=12;
		}else{
			premonthofyear=year;
		}		
		premonthS=String.valueOf(premonth);
		premonthofyearS=String.valueOf(premonthofyear);
	}
	
	public String OnepremonthTimetoString(){
		getpremonthTime();
		return this.getPremonthofyearS()+"-"+this.getPremonthS()+"-"+this.getDayS()+
				" "+this.getHourS()+":"+this.getMinuteS()
				+":"+this.getSecondS();
	}
	
	
	// 时间转换成十六进制字符串
	public String TimeChangeToHex() {
		String time = this.getYearH() + this.getMonthH() + this.getDayH()
				+ this.getHourH() + this.getMinuteH();
		// ex.exChange(time);
		return time;
	}

	// 时间转化十六进制字符串的函数
	public void TimeToHex() {
		if (year < 16) {
			yearH = "0" + Integer.toHexString(year);
		} else {
			yearH = Integer.toHexString(year);
		}

		monthH = "0" + Integer.toHexString(month);

		if (day < 16) {
			dayH = "0" + Integer.toHexString(day);
		} else {
			dayH = Integer.toHexString(day);
		}

		if (hour < 16) {
			hourH = "0" + Integer.toHexString(hour);
		} else {
			hourH = Integer.toHexString(hour);
		}
		if (minute < 16) {
			minuteH = "0" + Integer.toHexString(minute);
		} else {
			minuteH = Integer.toHexString(minute);
		}

	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getSecond() {
		return second;
	}

	public void setSeccond(int second) {
		this.second = second;
	}


	public Integer getPremonth() {
		return premonth;
	}

	public void setPremonth(Integer premonth) {
		this.premonth = premonth;
	}

	public Integer getPreday() {
		return preday;
	}

	public void setPreday(Integer preday) {
		this.preday = preday;
	}

	public Integer getPredayofmonth() {
		return predayofmonth;
	}

	public void setPredayofmonth(Integer predayofmonth) {
		this.predayofmonth = predayofmonth;
	}

	public Integer getPredayofyear() {
		return predayofyear;
	}

	public void setPredayofyear(Integer predayofyear) {
		this.predayofyear = predayofyear;
	}

	public Integer getPremonthofyear() {
		return premonthofyear;
	}

	public void setPremonthofyear(Integer premonthofyear) {
		this.premonthofyear = premonthofyear;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	public String getYearH() {
		return yearH;
	}

	public void setYearH(String yearH) {
		this.yearH = yearH;
	}

	public String getMonthH() {
		return monthH;
	}

	public void setMonthH(String monthH) {
		this.monthH = monthH;
	}

	public String getDayH() {
		return dayH;
	}

	public void setDayH(String dayH) {
		this.dayH = dayH;
	}

	public String getHourH() {
		return hourH;
	}

	public void setHourH(String hourH) {
		this.hourH = hourH;
	}

	public String getMinuteH() {
		return minuteH;
	}

	public void setMinuteH(String minuteH) {
		this.minuteH = minuteH;
	}

	public String getYearS() {
		if(yearS.length()==1){
			return "0"+yearS;
		}else{
			return yearS;
		}
		
	}

	public void setYearS(String yearS) {
		this.yearS = yearS;
	}

	public String getMonthS() {
		if(monthS.length()==1){
			return "0"+monthS;
		}else{
			return monthS;
		}
	}

	public void setMonthS(String monthS) {
		this.monthS = monthS;
	}

	public String getDayS() {
		if(dayS.length()==1){
			return "0"+dayS;
		}else{
			return dayS;
		}
	}

	public void setDayS(String dayS) {
		this.dayS = dayS;
	}

	public String getHourS() {
		if(hourS.length()==1){
			return "0"+hourS;
		}else{
			return hourS;
		}
	}

	public void setHourS(String hourS) {
		this.hourS = hourS;
	}

	public String getMinuteS() {
		if(minuteS.length()==1){
			return "0"+minuteS;
		}else{
			return minuteS;
		}
	}

	public void setMinuteS(String minuteS) {
		this.minuteS = minuteS;
	}

	public String getSecondS() {
		if(secondS.length()==1){
			return "0"+secondS;
		}else{
			return secondS;
		}
	}

	public void setSecondS(String secondS) {
		this.secondS = secondS;
	}

	public String getPremonthS() {
		if(premonthS.length()==1){
			return "0"+premonthS;
		}else{
			return premonthS;
		}
	}

	public void setPremonthS(String premonthS) {
		this.premonthS = premonthS;
	}

	public String getPremonthofyearS() {
		if(premonthofyearS.length()==1){
			return "0"+premonthofyearS;
		}else{
			return premonthofyearS;
		}
	}

	public void setPremonthofyearS(String premonthofyearS) {
		this.premonthofyearS = premonthofyearS;
	}

	public String getPredayS() {
		if(predayS.length()==1){
			return "0"+predayS;
		}else{
			return predayS;
		}
	}

	public void setPredayS(String predayS) {
		this.predayS = predayS;
	}

	public String getPredayofmonthS() {
		if(predayofmonthS.length()==1){
			return "0"+predayofmonthS;
		}else{
			return predayofmonthS;
		}
	}

	public void setPredayofmonthS(String predayofmonthS) {
		this.predayofmonthS = predayofmonthS;
	}

	public String getPredayofyearS() {
		if(predayofyearS.length()==1){
			return "0"+predayofyearS;
		}else{
			return predayofyearS;
		}
	}

	public void setPredayofyearS(String predayofyearS) {
		this.predayofyearS = predayofyearS;
	}
	
}
