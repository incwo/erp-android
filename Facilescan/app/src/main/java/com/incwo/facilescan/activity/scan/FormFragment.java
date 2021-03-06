package com.incwo.facilescan.activity.scan;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.incwo.facilescan.R;
import com.incwo.facilescan.helpers.fragments.TabFragment;
import com.incwo.facilescan.activity.application.BaseTabActivity;
import com.incwo.facilescan.managers.SingleApp;
import com.incwo.facilescan.managers.WebService;
import com.incwo.facilescan.scan.FormField;
import com.incwo.facilescan.scan.Form;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/** A fragment to present the content of a Form. */
public class FormFragment extends TabFragment {
    private static final String ARG_BUSINESS_FILE_ID = "ARG_BUSINESS_FILE_ID";
    private static final String ARG_FORM = "ARG_FORM";
    private static int PICK_PHOTO_REQUEST = 11;

    private static final String FILE_PROVIDER_AUTHORITY = "com.incwo.facilescan.fileprovider";

    private View mRoot;
    private String mBusinessFileId;
    private Form mForm;
    private HashMap<String, TextView> mTextViewByFieldKey;
    private FormField lastClickedField = null;
    private AsyncTask<?, ?, ?> SendTask = null;
    private boolean mIsPhotoTaken = false;
    private Bitmap mBitmap = null;
    private File mPhotoFile;
    private Boolean mHasSignature = false;

    private LinearLayout mSubmitLayout = null;
    private LinearLayout mLoadingLayout = null;
    private ImageView mPhotoImageView = null;

    public static FormFragment newInstance(String businessFileId, Form form) {
        Bundle args = new Bundle();
        args.putString(ARG_BUSINESS_FILE_ID, businessFileId);
        args.putSerializable(ARG_FORM, form);

        FormFragment fragment = new FormFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBusinessFileId = getArguments().getString(ARG_BUSINESS_FILE_ID);
        mForm = (Form) getArguments().getSerializable(ARG_FORM);
        mTextViewByFieldKey = new HashMap<>();

        handleReturnOfPreviousFragment();
    }

    private void handleReturnOfPreviousFragment()  {
        if (SingleApp.getDataSendByPreviousFragment() != null && lastClickedField != null) {
            lastClickedField.savedValue = SingleApp.getDataSendByPreviousFragment();
            SingleApp.setDataForNextFragment(null);
        }

    }


    @Override
    public void onStop() {
        super.onStop();

        // Save the contents of the text views into the fields
        // Note that this method is also called when picking the value for an enum.
        // The values of "string" field is lost if we don't read them.
        for(FormField field: mForm.fields) {
            TextView textView = getTextViewForField(field);
            if(textView != null // e.g.: null for Signature fields
                    && !field.type.equals("enum")) { // The savedValue of an enum is its key. Don't replace it with its value.
                    field.savedValue = textView.getText().toString();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.object_scan_details, container, false);

        mSubmitLayout = (LinearLayout) mRoot.findViewById(R.id.submit);
        mLoadingLayout = (LinearLayout) mRoot.findViewById(R.id.LOADING_BOTTOM);
        LinearLayout containerLayout = (LinearLayout) mRoot.findViewById(R.id.field_scan_unit_holder);

        buildForm(inflater, containerLayout);

        mRoot.findViewById(R.id.photo_button).setOnClickListener(mTakePhotoListener);
        mRoot.findViewById(R.id.submit_button).setOnClickListener(mSubmitListener);

        mPhotoImageView = mRoot.findViewById(R.id.photo_imageView);
        if(mBitmap != null) { // Test if not null so the placeholder is shown the first time
            mPhotoImageView.setImageBitmap(mBitmap);
        }

        return mRoot;
    }

    private void buildForm(LayoutInflater inflater, LinearLayout containerLayout) {
        int position = 0;
        for (FormField field : mForm.fields) {
            View view;
            if (field.type.equals("signature")) {
               view = createSignatureFieldView(inflater, containerLayout, field);
               mHasSignature = true;
            } else if (field.type.equals("enum")) {
                view = createEnumFieldView(inflater, containerLayout, field);
            } else {
                view = createStringFieldView(inflater, containerLayout, field);
            }
            containerLayout.addView(view, position);
            ++position;
        }
    }

    private View createSignatureFieldView(LayoutInflater inflater, ViewGroup container, FormField field) {
        View v = inflater.inflate(R.layout.signing_field_scan_values_displayer, container, false);

        TextView nameTextView = (TextView) v.findViewById(R.id.fieldName);
        nameTextView.setId(makeRandomId());
        nameTextView.setText(field.getName());

        TextView titleTextView = (TextView) v.findViewById(R.id.valueHolder);
        titleTextView.setId(makeRandomId());
        titleTextView.setText(field.getDescription());
        titleTextView.setOnClickListener(mSignClickListener);

        TextView statusTextView = (TextView) v.findViewById(R.id.sign);
        statusTextView.setId(makeRandomId());
        statusTextView.setText(R.string.to_sign);
        statusTextView.setOnClickListener(mSignClickListener);

        if (SingleApp.getSignature() == null) {
            statusTextView.setText(R.string.to_sign);
            statusTextView.setOnClickListener(mSignClickListener);
            titleTextView.setOnClickListener(mSignClickListener);
        } else {
            statusTextView.setText(R.string.signed);
            statusTextView.setTextColor(this.getActivity().getResources().getColor(R.color.green_color));
        }

        return v;
    }

    private View createEnumFieldView(LayoutInflater inflater, ViewGroup container, FormField field) {
        View v = inflater.inflate(R.layout.field_scan_values_displayer, container, false);

        TextView nameTextView = (TextView) v.findViewById(R.id.fieldName);
        nameTextView.setId(makeRandomId());
        nameTextView.setText(field.name);

        TextView valueTextView = (TextView) v.findViewById(R.id.valueHolder);
        valueTextView.setId(makeRandomId());
        valueTextView.setOnClickListener(enumOnClickListener);
        associateTextViewWithField(valueTextView, field);
        String enumKey = field.savedValue;
        String enumText = field.getValueForKey(enumKey);
        valueTextView.setText(enumText);

        return v;
    }

    private View createStringFieldView(LayoutInflater inflater, ViewGroup container, FormField field) {
        View v = inflater.inflate(R.layout.field_scan_edit_text, container, false);

        TextView nameTextView = (TextView) v.findViewById(R.id.fieldName);
        nameTextView.setId(makeRandomId());
        nameTextView.setText(field.name);

        TextView valueTextView = (TextView) v.findViewById(R.id.valueHolder);
        valueTextView.setId(makeRandomId());
        associateTextViewWithField(valueTextView, field);
        valueTextView.setText(field.savedValue);

        return v;
    }

    // Android considers that two views with the same id are the same.
    // This causes a problem here because the same views are inflated several times.
    // I took the easy path: use a random id. The chances of collisions are very low and it'll alright.
    // But the right approach would be to use a ListView.
    private int makeRandomId() {
        return new Random().nextInt();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) {
            return;
        }

        if(requestCode == PICK_PHOTO_REQUEST) {
            Uri uri;
            if (data == null) { // Photo taken by the camera
                uri = FileProvider.getUriForFile(getActivity(), FILE_PROVIDER_AUTHORITY, mPhotoFile);
                getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            } else { // Photo picked from the gallery
                uri = data.getData();
            }

            Bitmap imageBitmap = getScaledBitmap(uri, 800, 800);
            if (imageBitmap != null) {
                ImageView iv = (ImageView) mRoot.findViewById(R.id.photo_imageView);
                iv.setImageBitmap(imageBitmap);
                mIsPhotoTaken = true;
                mBitmap = imageBitmap;
            }
            mPhotoFile = null;
        }
    }

    private Bitmap getScaledBitmap(Uri imageUri, int maxWidth, int maxHeight) {
        if (imageUri == null)
            return null;

        // Read the image
        InputStream imageStream = null;
        Bitmap original = null;
        try {
            imageStream = getActivity().getContentResolver().openInputStream(imageUri);
            BitmapFactory.Options opt = new BitmapFactory.Options();

            opt.inSampleSize = 2; // Skip every odd pixel
            original = BitmapFactory.decodeStream(imageStream, null, opt);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Determine the reduced size
        int width;
        int height;
        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();
        if(originalWidth == 0 || originalHeight == 0) {
            width = 300;
            height = 200;
        } else {
            float widthScale = (float)maxWidth/originalWidth;
            float heightScale = (float)maxHeight/originalHeight;
            float scale = Math.min(widthScale, heightScale);
            width = (int)(scale * originalWidth);
            height = (int)(scale * originalHeight);
        }

        Bitmap finalBitmap = Bitmap.createScaledBitmap(original, width, height, true);

        // createScaledBitmap can return a copy or use the source instance
        if (finalBitmap != original) {
            original.recycle();
        }
        return finalBitmap;
    }

    private View.OnClickListener mTakePhotoListener = new View.OnClickListener() {
        public void onClick(View view) {
            launchPhotoIntent();
        }
    };

    private void launchPhotoIntent() {
        // cameraIntents
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mPhotoFile = new File(getActivity().getFilesDir(), "Scan.jpg");
        Uri uri = FileProvider.getUriForFile(getActivity(), FILE_PROVIDER_AUTHORITY, mPhotoFile);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> listCams = packageManager.queryIntentActivities(captureIntent, PackageManager.MATCH_DEFAULT_ONLY);
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        for (ResolveInfo res : listCams) {
            getActivity().grantUriPermission(res.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            Intent camIntent = new Intent(captureIntent);
            camIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            camIntent.setPackage(res.activityInfo.packageName);
            cameraIntents.add(camIntent);
        }

        // galleryIntent
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // chooserIntent to choose between galleryIntent and the cameraIntents
        final Intent chooserIntent = Intent.createChooser(galleryIntent, getString(R.string.photo_source_prompt));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

        getActivity().startActivityForResult(chooserIntent, PICK_PHOTO_REQUEST);
    }

    private View.OnClickListener enumOnClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            lastClickedField = findFieldForValueTextView(view);
            if(lastClickedField == null) {
                return;
            }

            EnumFragment frag = EnumFragment.newInstance(lastClickedField);
            getTabActivity().pushFragment(BaseTabActivity.TAB_SCAN, frag);
        }
    };

    private TextView getTextViewForField(FormField field) {
        return mTextViewByFieldKey.get(field.key);
    }

    private void associateTextViewWithField(TextView textView, FormField field) {
        mTextViewByFieldKey.put(field.key, textView);
    }


    @Nullable
    private FormField findFieldForValueTextView(View textView) {
        for(FormField field: mForm.fields) {
            TextView fieldTextView = getTextViewForField(field);
            //if(fieldTextView.getId() == textView.getId()) {
            if(fieldTextView == textView) {
                return field;
            }
        }
        return null; // Not found
    }

    private View.OnClickListener mSignClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            Fragment fragment = SignatureCanvasFragment.newInstance(mForm);
            getTabActivity().pushFragment(BaseTabActivity.TAB_SCAN, fragment);
        }
    };

    private View.OnClickListener mSubmitListener = new View.OnClickListener() {
        public void onClick(View view) {

            if (mHasSignature) {
                if (!mIsPhotoTaken && SingleApp.getSignature() == null) // if photo field and signature field are empty, force signature
                    mSignClickListener.onClick(view);
                else
                    SendTask = new AsyncTaskSendScan().execute();
            } else {
                if (!mIsPhotoTaken)  // if photo field is empty, force signature
                    mTakePhotoListener.onClick(view);
                else
                    SendTask = new AsyncTaskSendScan().execute();
            }
        }
    };


    private class AsyncTaskSendScan extends AsyncTask<String, Integer, Long> {

        protected void onPreExecute() {
            mSubmitLayout.setVisibility(View.GONE);
            mLoadingLayout.setVisibility(View.VISIBLE);
        }

        protected Long doInBackground(String... tasks) {

            WebService ws = new WebService();

            if (mIsPhotoTaken)
                ws.uploadForm(mBusinessFileId, mForm, mBitmap, SingleApp.getAccount());
            else
                ws.uploadForm(mBusinessFileId, mForm, null, SingleApp.getAccount());

            return (long) ws.responseCode;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Long result) {

            if (result >= 200 && result < 300) {

            } else {
                WebService.showError(result);
            }


            mSubmitLayout.setVisibility(View.VISIBLE);
            mLoadingLayout.setVisibility(View.GONE);
            getTabActivity().popFragment();
            SendTask = null;
        }
    }

    public void onDestroy() {
        super.onDestroy();

        if (SendTask != null && SendTask.getStatus() == AsyncTask.Status.RUNNING) {
            SendTask.cancel(true);
        }
        SendTask = null;
        SingleApp.clearSignature();
    }
}
