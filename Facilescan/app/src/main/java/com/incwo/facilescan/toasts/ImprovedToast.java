package com.incwo.facilescan.toasts;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.incwo.facilescan.app.FacilescanApp;

import java.util.Calendar;

/*
 * "ImprovedToast" is not suppose to be directly used by the developer.
 * This class is used by "ToastNoQueue" which is a toast Manager
 */

public class ImprovedToast extends Toast {

	private long lastCallToShowInMillis = 0;
	private static View defaultView;
	
	static{
		 defaultView = Toast.makeText(FacilescanApp.getInstance().getApplicationContext(), "", Toast.LENGTH_SHORT).getView();
	}
	
	/*
	 * according to the Android source code:
	 * "LONG_DELAY" correspond to Toast.LENGTH_LONG.
	 * "SHORT_DELAY" correspond to Toast.LENGTH_SHORT
	 */
	private static final int LONG_DELAY = 3500; // 3.5 seconds
	private static final int SHORT_DELAY = 2000; // 2 seconds
	
	public ImprovedToast(Context context) {
		super(context);
	}

	@Override
	public void show()
	{
		super.show();
		Calendar c = Calendar.getInstance(); 
		lastCallToShowInMillis = c.getTimeInMillis();
	}
	
	public static ImprovedToast makeText(Context context, CharSequence text, int duration)
	{
		   ImprovedToast result = new ImprovedToast(context);
		   result.setView(defaultView);
		   result.setText(text);
		   result.setDuration(duration);
		   return result;
	}
	
	public static ImprovedToast makeText(Context context, int id, int duration)
	{
		 return makeText(context, context.getResources().getText(id), duration);
	}
	
	public boolean isDisplaying()
	{
		if (lastCallToShowInMillis == 0)
			return false;
		Calendar c = Calendar.getInstance();
		long actualTime = c.getTimeInMillis();
		int duration = getRealDuration();
		if (actualTime - lastCallToShowInMillis > duration)
			return (false);
		return (true);
	}
	
	private int getRealDuration()
	{
		int tmp = getDuration();
		if (tmp == Toast.LENGTH_LONG)
			return LONG_DELAY;
		else if (tmp == Toast.LENGTH_SHORT)
			return SHORT_DELAY;
		return tmp;			
	}
	
}
