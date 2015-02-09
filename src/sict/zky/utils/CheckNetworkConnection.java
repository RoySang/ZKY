package sict.zky.utils;

import android.content.Context;
import android.net.ConnectivityManager;

public class CheckNetworkConnection {
Context context;

	public CheckNetworkConnection(Context context) {
	super();
	this.context = context;
}

	public static boolean checkifNetworkConnection(Context context)  
	  {  
	       final ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);  
	 
	      final android.net.NetworkInfo wifi =connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
	       final android.net.NetworkInfo mobile =connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);  
	 
	      if(wifi.isAvailable()||mobile.isAvailable())  
	           return true;  
	       else  
	           return false;  
	  }  

}
