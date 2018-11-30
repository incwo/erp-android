package com.incwo.facilescan.activity.scan;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.incwo.facilescan.R;
import com.incwo.facilescan.app.FacilescanApp;
import com.incwo.facilescan.helpers.fragments.TabFragment;
import com.incwo.facilescan.managers.SingleApp;
import com.incwo.facilescan.scan.BusinessFile;
import com.incwo.facilescan.scan.ScanCategory;
import com.incwo.facilescan.scan.ScanXml;

import java.util.Calendar;

public class SignatureCanvasFragment extends TabFragment {

	
	 private View mRoot;
	 private DrawView drawView;
	 private ScanCategory selectedItem;
	 private ScanXml xml = null;
	 private View screenshot = null;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    	mRoot = inflater.inflate(R.layout.signature_canvas, null);
    	
    	// this prevent previous fragment to catch onTouch event
    	mRoot.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    	
    	
    	
    	drawView = (DrawView) mRoot.findViewById(R.id.sign_view);
    	drawView.requestFocus();
    	TextView your_signature = (TextView) mRoot.findViewById(R.id.your_signature);
    	your_signature.setText(R.string.your_signature);
    	
    	xml = SingleApp.getScanXml();
    	BusinessFile businessFile = SingleApp.getSelectedBusinessScanItem();
    	selectedItem = businessFile.getObjectByName(SingleApp.getSelectedObjectScanItem());
    	
    	TextView title = (TextView) mRoot.findViewById(R.id.signature_title);
    	if(selectedItem.fields.get(0).getDescription() != null)
    		title.setText(selectedItem.fields.get(0).getDescription());
    	
    	TextView clear = (TextView) mRoot.findViewById(R.id.CLEAR);
    	TextView save = (TextView) mRoot.findViewById(R.id.SAVE);
    	TextView date = (TextView) mRoot.findViewById(R.id.date_label);
    	
    	Calendar c = Calendar.getInstance(); 
    	String dateTemp = DateUtils.formatDateTime(FacilescanApp.getInstance().getApplicationContext(), c.getTimeInMillis(), DateUtils.FORMAT_SHOW_YEAR).toString();
    	String hourTemp = DateUtils.formatDateTime(FacilescanApp.getInstance().getApplicationContext(), c.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME).toString();
    	date.setText(dateTemp + "  " + hourTemp);
    	
    	clear.setText(R.string.to_clear);
    	save.setText(R.string.save);
    	
    	clear.setOnClickListener(mClearListener);
    	save.setOnClickListener(mSaveSignatureListener);
    	
    	screenshot = mRoot.findViewById(R.id.screenshot);
    	screenshot.setDrawingCacheEnabled(true);
    	
    	return mRoot;
	}
	
	 private View.OnClickListener mClearListener = new View.OnClickListener() {
			public void onClick(View view) {
				drawView.clearView();
			}
		};
		
		private View.OnClickListener mSaveSignatureListener = new View.OnClickListener() {
				public void onClick(View view) {
					 Bitmap bitmap = Bitmap.createBitmap(screenshot.getDrawingCache());
						SingleApp.setSignature(bitmap);
						getTabActivity().popFragment();
				}
			};
	
}
