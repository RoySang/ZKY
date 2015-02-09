package sict.zky.testBPM;

public class AnalyseBPMResult
{
	
	
	// 0error 1 偏低     2 最适当血压 3 正常血压  4正常高血压  5 轻度高血压 6中度高血压 7 重度高血压
	public int analyse_bpm_result(int high,int low)
	{
		if(low<60)
		{
			return 1;			
		}
		if(high<90)
		{
			return 1;			
		}	
		
		if(high>=90 && high <120)
		{
			if(low>=60 && low <80 ) return 2;
			if(low>=80 && low <85 ) return 3;
			if(low>=85 && low <90 ) return 4;
			if(low>=90 && low <100 ) return 5;
			if(low>=100 && low <110 ) return 6;
			if(low>=110)  return 7;
		}	
		
		if(high>=120 && high <130)
		{
			if(low>=60 && low <85 ) return 3;
			
			if(low>=85 && low <90 ) return 4;
			if(low>=90 && low <100 ) return 5;
			if(low>=100 && low <110 ) return 6;
			if(low>=110)  return 7;	
			
		}
		
		if(high>=130 && high <140)
		{
			if(low>=60 && low <90 ) return 4;
			
			if(low>=90 && low <100 ) return 5;
			if(low>=100 && low <110 ) return 6;
			if(low>=110)  return 7;	
			
		}
		
		if(high>=140 && high <160)
		{
			if(low>=60 && low <100 ) return 5;	
			
			if(low>=100 && low <110 ) return 6;
			if(low>=110)  return 7;	
			
		}
		
		if(high>=160 && high <180)
		{
			if(low>=60 && low <110 ) return 6;			
			
			if(low>=110)  return 7;
			
		}
		
		if(high>=180)
		{
			return 7;
		}


		return 0;	
		
	}
	
	
	


}
