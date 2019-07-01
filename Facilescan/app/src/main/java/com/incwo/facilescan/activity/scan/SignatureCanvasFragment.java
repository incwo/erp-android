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
import com.incwo.facilescan.scan.Form;
import com.incwo.facilescan.scan.BusinessFilesList;

import java.util.Calendar;

public class SignatureCanvasFragment extends TabFragment {
	private static final String ARG_FORM = "ARG_FORM";

	private DrawView drawView;
	private View screenshot = null;
	
	public static SignatureCanvasFragment newInstance(Form form) {
		Bundle args = new Bundle();
		args.putSerializable(ARG_FORM, form);

		SignatureCanvasFragment fragment = new SignatureCanvasFragment();
		fragment.setArguments(args);
		return fragment;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Form form = (Form) getArguments().getSerializable(ARG_FORM);

    	View root = inflater.inflate(R.layout.signature_canvas, null);
    	
    	// this prevent previous fragment to catch onTouch event
    	root.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    	

    	drawView = (DrawView) root.findViewById(R.id.sign_view);
    	drawView.requestFocus();
    	TextView your_signature = (TextView) root.findViewById(R.id.your_signature);
    	your_signature.setText(R.string.your_signature);

    	TextView title = (TextView) root.findViewById(R.id.signature_title);
    	if(form.fields.get(0).getDescription() != null)
    		title.setText(form.fields.get(0).getDescription());
    	
    	TextView clear = (TextView) root.findViewById(R.id.CLEAR);
    	TextView save = (TextView) root.findViewById(R.id.SAVE);
    	TextView date = (TextView) root.findViewById(R.id.date_label);
    	
    	Calendar c = Calendar.getInstance(); 
    	String dateTemp = DateUtils.formatDateTime(FacilescanApp.getInstance().getApplicationContext(), c.getTimeInMillis(), DateUtils.FORMAT_SHOW_YEAR).toString();
    	String hourTemp = DateUtils.formatDateTime(FacilescanApp.getInstance().getApplicationContext(), c.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME).toString();
    	date.setText(dateTemp + "  " + hourTemp);
    	
    	clear.setText(R.string.to_clear);
    	save.setText(R.string.save);
    	
    	clear.setOnClickListener(mClearListener);
    	save.setOnClickListener(mSaveSignatureListener);
    	
    	screenshot = root.findViewById(R.id.screenshot);
    	screenshot.setDrawingCacheEnabled(true);
    	
    	return root;
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
