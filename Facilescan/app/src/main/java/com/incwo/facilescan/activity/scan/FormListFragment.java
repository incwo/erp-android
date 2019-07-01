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
import com.incwo.facilescan.scan.Form;
import com.incwo.facilescan.scan.FormFolder;

import java.util.ArrayList;

public class FormListFragment extends BaseListFragment {
	private static final String ARG_BUSINESS_FILE_ID = "ARG_BUSINESS_FILE_ID";
	private static final String ARG_FORMS = "ARG_FORMS";

	private String mBusinessFileId;
	private ArrayList<Object> mFormsOrFolders; // Form or FormFolder

	public static FormListFragment newInstance(String businessFileId, ArrayList<Object> forms) {
		Bundle args = new Bundle();
		args.putString(ARG_BUSINESS_FILE_ID, businessFileId);
		args.putSerializable(ARG_FORMS, forms);

		FormListFragment fragment = new FormListFragment();
		fragment.setArguments(args);
		return fragment;
	}

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	mBusinessFileId = getArguments().getString(ARG_BUSINESS_FILE_ID);

		@SuppressWarnings("unchecked")
		ArrayList<Object> forms = (ArrayList<Object>) getArguments().getSerializable(ARG_FORMS);
    	mFormsOrFolders = forms;

        BusinessFileAdapter businessFileAdapter = new BusinessFileAdapter(this.getActivity(), mFormsOrFolders);
        setListAdapter(businessFileAdapter);
		View root = inflater.inflate(R.layout.videos_fragment, null);
		return root;
	}
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Object child = mFormsOrFolders.get(position);
        if(child instanceof Form) {
        	Form form = (Form) child;
			FormFragment formFragment = FormFragment.newInstance(mBusinessFileId, form);
			getTabActivity().pushFragment(BaseTabActivity.TAB_SCAN, formFragment);
		} else if (child instanceof FormFolder) {
        	FormFolder folder = (FormFolder) child;
			ArrayList<Form> forms =  folder.getForms();
			@SuppressWarnings("unchecked")
			ArrayList<Object> children = (ArrayList) forms;
        	FormListFragment formListFragment = FormListFragment.newInstance(mBusinessFileId, children);
			getTabActivity().pushFragment(BaseTabActivity.TAB_SCAN, formListFragment);
		}
    }
    
    private class BusinessFileAdapter extends ArrayAdapter<Object> {
		private LayoutInflater mInflater;

		BusinessFileAdapter(Activity context, ArrayList<Object> arrayList) {
			super(context, R.layout.object_scan_row, arrayList);
			mInflater = LayoutInflater.from(context);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			Object item = getItem(position);
			String title = "";
			if(item instanceof Form) {
				title = ((Form) item).getClassName();
			} else if(item instanceof FormFolder) {
				title = ((FormFolder) item).getTitle();
			}
			
			Row rowView = new Row();
			convertView = mInflater.inflate(R.layout.object_scan_row, null);	
		
			rowView.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
			convertView.setTag(item);
			rowView.titleTextView.setText(title);
			return(convertView);
		}

		// Object container for view fields
		class Row {
			TextView titleTextView;
		}  
    }

	
}
