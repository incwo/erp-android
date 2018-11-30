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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
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
import com.incwo.facilescan.scan.BusinessFile;
import com.incwo.facilescan.scan.ScanField;
import com.incwo.facilescan.scan.ScanCategory;
import com.incwo.facilescan.scan.BusinessFilesList;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ObjectScanDetailsFragment extends TabFragment {
    private static int PICK_PHOTO_REQUEST = 11;

    private static final String FILE_PROVIDER_AUTHORITY = "com.incwo.facilescan.fileprovider";

    private View mRoot;
    private BusinessFilesList xml = null;
    private ScanCategory mSelectedItem;
    private ScanField lastClickedField = null;
    private AsyncTask<?, ?, ?> SendTask = null;
    private boolean mIsPhotoTaken = false;
    private Bitmap mBitmap = null;
    private File mPhotoFile;
    private Boolean mHasSignature = false;

    private LinearLayout mSubmitLayout = null;
    private LinearLayout mLoadingLayout = null;
    private ImageView mPhotoImageView = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        xml = SingleApp.getBusinessFilesList();
        BusinessFile selectedBusiness = SingleApp.getSelectedBusinessScanItem();
        mSelectedItem = selectedBusiness.getObjectByName(SingleApp.getSelectedObjectScanItem());

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
        for(ScanField field: mSelectedItem.fields) {
            TextView valueHolder = field.valueHolder;
            if(valueHolder != null) { // e.g.: null for Signature fields
                field.savedValue = field.valueHolder.getText().toString();
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
        for (ScanField field : mSelectedItem.fields) {
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

    private View createSignatureFieldView(LayoutInflater inflater, ViewGroup container, ScanField field) {
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

    private View createEnumFieldView(LayoutInflater inflater, ViewGroup container, ScanField field) {
        View v = inflater.inflate(R.layout.field_scan_values_displayer, container, false);

        TextView nameTextView = (TextView) v.findViewById(R.id.fieldName);
        nameTextView.setId(makeRandomId());
        nameTextView.setText(field.name);

        TextView valueTextView = (TextView) v.findViewById(R.id.valueHolder);
        valueTextView.setId(makeRandomId());
        valueTextView.setOnClickListener(enumOnClickListener);
        field.valueHolder = valueTextView;
        valueTextView.setText(field.savedValue);

        return v;
    }

    private View createStringFieldView(LayoutInflater inflater, ViewGroup container, ScanField field) {
        View v = inflater.inflate(R.layout.field_scan_edit_text, container, false);

        TextView nameTextView = (TextView) v.findViewById(R.id.fieldName);
        nameTextView.setId(makeRandomId());
        nameTextView.setText(field.name);

        TextView valueTextView = (TextView) v.findViewById(R.id.valueHolder);
        valueTextView.setId(makeRandomId());
        field.valueHolder = valueTextView;
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
            lastClickedField = findFieldForValueHolder(view);
            if(lastClickedField == null) {
                return;
            }

            // and send it to the next fragment
            Fragment frag = new EnumFragment();
            Bundle args = new Bundle();
            args.putString("fieldName", lastClickedField.name);
            frag.setArguments(args);

            getTabActivity().pushFragment(BaseTabActivity.TAB_SCAN, frag);
        }
    };

    @Nullable
    private ScanField findFieldForValueHolder(View valueHolder) {
        for(ScanField field: mSelectedItem.fields) {
            if(field.valueHolder.getId() == valueHolder.getId()) {
                return field;
            }
        }
        return null; // Not found
    }

    private View.OnClickListener mSignClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            Fragment frag = new SignatureCanvasFragment();

            getTabActivity().pushFragment(BaseTabActivity.TAB_SCAN, frag);
        }
    };

    private View.OnClickListener mSubmitListener = new View.OnClickListener() {
        public void onClick(View view) {

            if (mHasSignature) {
                if (mIsPhotoTaken == false && SingleApp.getSignature() == null) // if photo field and signature field are empty, force signature
                    mSignClickListener.onClick(view);
                else
                    SendTask = new AsyncTaskSendScan().execute();
            } else {
                if (mIsPhotoTaken == false)  // if photo field is empty, force signature
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

            BusinessFile businessFile = SingleApp.getSelectedBusinessScanItem();
            if (mIsPhotoTaken)
                ws.submitScanInformations(businessFile.id, mSelectedItem, mBitmap);
            else
                ws.submitScanInformations(businessFile.id, mSelectedItem, null);

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
