package com.incwo.facilescan.activity.scan;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.incwo.facilescan.R;
import com.incwo.facilescan.helpers.fragments.BaseListFragment;
import com.incwo.facilescan.activity.application.BaseTabActivity;
import com.incwo.facilescan.managers.SingleApp;
import com.incwo.facilescan.scan.BusinessFile;
import com.incwo.facilescan.scan.Form;
import com.incwo.facilescan.scan.BusinessFilesList;

import java.util.ArrayList;

public class BusinessFileFragment extends BaseListFragment {
	private static final String ARG_BUSINESS_FILE = "ARG_BUSINESS_FILE";

	private BusinessFile mBusinessFile;

	public static BusinessFileFragment newInstance(BusinessFile businessFile) {
		Bundle args = new Bundle();
		args.putSerializable(ARG_BUSINESS_FILE, businessFile);

		BusinessFileFragment fragment = new BusinessFileFragment();
		fragment.setArguments(args);
		return fragment;
	}

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	mBusinessFile = (BusinessFile) getArguments().getSerializable(ARG_BUSINESS_FILE);
    	
        BusinessFileAdapter businessFileAdapter = new BusinessFileAdapter(this.getActivity(), mBusinessFile.getFormClassNames());
        setListAdapter(businessFileAdapter);
		View root = inflater.inflate(R.layout.videos_fragment, null);
		return root;
	}
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Form form = mBusinessFile.getForms().get(position);

        FormFragment formFragment = FormFragment.newInstance(mBusinessFile.id, form);
        getTabActivity().pushFragment(BaseTabActivity.TAB_SCAN, formFragment);
    }
    
    private class BusinessFileAdapter extends ArrayAdapter<String> {
		private LayoutInflater mInflater;

		BusinessFileAdapter(Activity context, ArrayList<String> arrayList) {
			super(context, R.layout.object_scan_row, arrayList);
			mInflater = LayoutInflater.from(context);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			String item = getItem(position);
			
			Row rowView = new Row();
			convertView = mInflater.inflate(R.layout.object_scan_row, null);	
		
			rowView.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
			convertView.setTag(item);
			rowView.titleTextView.setText(item);
			return(convertView);
		}

		// Object container for view fields
		class Row {
			TextView titleTextView;
		}  
    }

	
}
