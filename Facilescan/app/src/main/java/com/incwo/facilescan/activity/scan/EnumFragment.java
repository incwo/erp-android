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
import com.incwo.facilescan.scan.Form;
import com.incwo.facilescan.scan.BusinessFilesList;
import com.incwo.facilescan.scan.FormField;

import java.util.ArrayList;

public class EnumFragment extends BaseListFragment {
	private static final String ARG_FIELD = "ARG_FIELD";

	private ArrayList<FormField.KeyValue> mKeyValues;

	public static EnumFragment newInstance(FormField enumField) {
		Bundle args = new Bundle();
		args.putSerializable(ARG_FIELD, enumField);

		EnumFragment fragment = new EnumFragment();
		fragment.setArguments(args);
		return fragment;
	}

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		FormField enumField = (FormField) getArguments().getSerializable(ARG_FIELD);
		// Actually, only the keyValues are needed, not the FormField but otherwise we get an "unchecked warning" when trying to deserialize the ArrayList<KeyValue>.
		mKeyValues = enumField.keyValues;

    	EnumAdapter enumAdapter = new EnumAdapter(this.getActivity(), mKeyValues);
        setListAdapter(enumAdapter);
		View root = inflater.inflate(R.layout.videos_fragment, null);
		return root;
	}
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // Save the key, not the value
        SingleApp.setDataForNextFragment(mKeyValues.get(position).key);
        getTabActivity().popFragment();
    }

    private class EnumAdapter extends ArrayAdapter<FormField.KeyValue> {
		private LayoutInflater mInflater;

		EnumAdapter(Activity context, ArrayList<FormField.KeyValue> arrayList) {
			super(context, R.layout.object_scan_row, arrayList);
			mInflater = LayoutInflater.from(context);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			FormField.KeyValue item = getItem(position);
			
			Row rowView = new Row();
			convertView = mInflater.inflate(R.layout.object_scan_row, null);	
		
			rowView.mTitleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
			convertView.setTag(item);
			rowView.mTitleTextView.setText(item.value);
			return(convertView);
		}

		// Object container for view fields
		class Row {
			TextView mTitleTextView;
		}  
    }
	
}
