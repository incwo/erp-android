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
import com.incwo.facilescan.managers.SingleApp;
import com.incwo.facilescan.scan.BusinessFile;
import com.incwo.facilescan.scan.ScanCategory;
import com.incwo.facilescan.scan.ScanXml;

import java.util.ArrayList;

public class EnumFragment extends BaseListFragment {

	private ScanXml xml = null;
	private View mRoot;
	private ArrayList<String> mValues;
	private ArrayList<String> mTitles;
	
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	xml = SingleApp.getScanXml();
    	BusinessFile businessFile = SingleApp.getSelectedBusinessScanItem();
    	ScanCategory objScanItem = businessFile.getObjectByName(SingleApp.getSelectedObjectScanItem());
    	mTitles = objScanItem.getFieldByName(getArguments().getString("fieldName")).valueTitles;
    	mValues = objScanItem.getFieldByName(getArguments().getString("fieldName")).values;

    	EnumAdapter enumAdapter = new EnumAdapter(this.getActivity(), mTitles);
        setListAdapter(enumAdapter);
		mRoot = inflater.inflate(R.layout.videos_fragment, null);
		return mRoot;
	}
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        SingleApp.setDataForNextFragment(mTitles.get(position));
        getTabActivity().popFragment();
    }

    private class EnumAdapter extends ArrayAdapter<String> {
		private LayoutInflater mInflater;

		EnumAdapter(Activity context, ArrayList<String> arrayList) {
			super(context, R.layout.object_scan_row, arrayList);
			mInflater = LayoutInflater.from(context);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			String item = getItem(position);
			
			Row rowView = new Row();
			convertView = mInflater.inflate(R.layout.object_scan_row, null);	
		
			rowView.mTitleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
			convertView.setTag(item);
			rowView.mTitleTextView.setText(item);
			return(convertView);
		}

		// Object container for view fields
		class Row {
			TextView mTitleTextView;
		}  
    }
	
}
