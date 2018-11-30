package com.incwo.facilescan.toasts;

import android.content.Context;
import android.widget.Toast;

import com.incwo.facilescan.app.FacilescanApp;

/*
 * "ToastNoQueue" is a toast manager that provide two features:
 *   - currently displayed toast is hidden and replaced by the following ("NoQueue")
 *   - you can manually hide the displayed toast with "cancelToast()" without saving a reference on it
 */  
 
public class ToastNoQueue{

	/*
	 * According to Android Toast documentation :
	 *   - static method "makeText" should be used to show a Toast that you won't customize with a view.
	 *   - constructor call "new Toast(Context context)" should be used to show a Toast that you will customize with a view.
	 *   
	 *   Thus, "customizableToast" is created with the Toast constructor whereas "toast" is created with the "makeText" static method
	 */
	
	private static ImprovedToast customisableToast = null;
	private static ImprovedToast toast = null;	
	
	/*
	 * Shared part
	 */
	
	/**
	 * Used to hide displayed Toast
	 * That call the "cancel()" Toast method
	 */
	public static void cancelToast()
	{
		if (customisableToast != null && customisableToast.isDisplaying())
			customisableToast.cancel();
		if (toast != null && toast.isDisplaying())
			toast.cancel();	
	}
		
	/*
	 * Customizable Toast methods
	 */
	
	/**
	 * Return a Toast which you can set a customized view
	 */
	public static Toast getCustomizableToast(Context context)
	{
		if (customisableToast == null)
			customisableToast = new  ImprovedToast(context);
		return(customisableToast);
	}
	
	/**
	 * Call "getCustomizableToast(Context context)" with application context
	 */
	public static Toast getCustomizableToast()
	{
		Context context = FacilescanApp.getInstance().getApplicationContext();
		return (getCustomizableToast(context));
	}
	
	/*
	 * No Customizable Toast methods
	 */
	
	private static Toast getToast()
	{
		if (toast == null)
		{
			Context context = FacilescanApp.getInstance().getApplicationContext();
			toast = ImprovedToast.makeText(context, "", Toast.LENGTH_SHORT);
		}
		return (toast);
	}
	
	/**
	 * Return a Toast which you are suppose to show without any view customization. 
	 */
	public static Toast makeText(Context context, int id, int duration)
	{
		getToast().setText(id);
		getToast().setDuration(duration);
		return (toast);
	}
	
	/**
	 * Return a Toast which you are suppose to show without any view customization. 
	 */
	public static Toast makeText(Context context, CharSequence text, int duration)
	{
		getToast().setText(text);
		getToast().setDuration(duration);
		return (toast);
	}
	
	/**
	 * Call "Toast makeText(Context context, int id, int duration)" with application Context
	 */
	public static Toast makeText(int id, int duration)
	{
		Context context = FacilescanApp.getInstance().getApplicationContext();
		return (makeText(context, id, duration));
	}
	
	/**
	 * Call "Toast makeText(Context context, CharSequence text, int duration)" with application Context
	 */
	public static Toast makeText(CharSequence text, int duration)
	{
		Context context = FacilescanApp.getInstance().getApplicationContext();
		return (makeText(context, text, duration));
	}

}
