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
import com.incwo.facilescan.scan.ScanCategory;
import com.incwo.facilescan.scan.BusinessFilesList;

import java.util.ArrayList;

public class ObjectScanFragment extends BaseListFragment {

	private BusinessFilesList xml = null;
	private BusinessFile selectedItem;
	private View mRoot;
	
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    	xml = SingleApp.getBusinessFilesList();
    	selectedItem = SingleApp.getSelectedBusinessScanItem();
    	
        ObjectScanAdapter objectScanAdapter = new ObjectScanAdapter(this.getActivity(), selectedItem.objectsName);
        setListAdapter(objectScanAdapter);
		mRoot = inflater.inflate(R.layout.videos_fragment, null);
		return mRoot;
	}
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ScanCategory item = selectedItem.objects.get(position);
        SingleApp.setSelectedObjectScanItem(item.className);
        
        getTabActivity().pushFragment(BaseTabActivity.TAB_SCAN, new ObjectScanDetailsFragment());
    }
    
    private class ObjectScanAdapter extends ArrayAdapter<String> {  
		private LayoutInflater mInflater;

		ObjectScanAdapter(Activity context, ArrayList<String> arrayList) {
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
