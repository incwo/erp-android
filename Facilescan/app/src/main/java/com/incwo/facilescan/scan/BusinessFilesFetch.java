package com.incwo.facilescan.scan;


import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.incwo.facilescan.helpers.Account;
import com.incwo.facilescan.managers.WebService;

import java.util.ArrayList;

public class BusinessFilesFetch {
    private Account mAccount;
    private Listener mListener = null;
    private AsyncGetTask mAsyncTask = null;

    public static class Listener {
        public void onSuccess(ArrayList<BusinessFile> businessFiles) {

        }

        public void onFailed(int responseCode) {

        }

        public void onCancelled() {

        }

        public void always() {

        }
    }

    public BusinessFilesFetch(@NonNull Account account) {
        mAccount = account;
    }

    public void fetch(@NonNull Listener listener) {
        mListener = listener;
        mAsyncTask = new AsyncGetTask(mAccount);
        mAsyncTask.execute();
    }

    public void cancel() {
        if(mAsyncTask != null) {
            mAsyncTask.cancel(true);
        }
    }

    private enum ResultType {
        NONE,
        SUCCESS,
        WRONG_RESPONSE_CODE,
        INVALID_XML,
        CANCELLED
    }

    private class TaskResult {
        ResultType type = ResultType.NONE;
        @Nullable ArrayList<BusinessFile> businessFiles = null;
        int responseCode = 0;
    }

    private class AsyncGetTask extends AsyncTask<Void, Integer, TaskResult> {
        Account mAccount;

        AsyncGetTask(@NonNull Account account) {
            mAccount = account;
        }

        protected void onPreExecute() {
        }

        protected TaskResult doInBackground(Void... tasks) {
            WebService webService = new WebService();
            webService.logToScan(mAccount);

            TaskResult result = new TaskResult();
            if (isCancelled()) {
                result.type = ResultType.CANCELLED;
                return result;
            }

            int responseCode = webService.responseCode;
            if (responseCode < 200 || responseCode >= 300) {
                result.type = ResultType.WRONG_RESPONSE_CODE;
                result.responseCode = responseCode;
                return result;
            }

            BusinessFileXmlParsing parser = new BusinessFileXmlParsing();
            BusinessFilesList businessFilesList = parser.readFromXmlContent(webService.body);
            if (businessFilesList == null) {
                result.type = ResultType.INVALID_XML;
                result.responseCode = responseCode;
                return result;
            }

            result.type = ResultType.SUCCESS;
            result.businessFiles = businessFilesList.businessFiles;
            return result;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(TaskResult result) {
            switch (result.type) {
                case NONE:
                    break;
                case SUCCESS:
                    mListener.onSuccess(result.businessFiles);
                    break;
                case WRONG_RESPONSE_CODE:
                case INVALID_XML:
                    mListener.onFailed(result.responseCode);
                    break;
                case CANCELLED:
                    mListener.onCancelled();
                    break;
            }
            mListener.always();

            mListener = null;
            mAsyncTask = null;
        }
    }
}
