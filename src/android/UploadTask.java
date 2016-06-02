package studio.wetrack.geolocation;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhonghui on 16/6/2.
 */
public class UploadTask extends AsyncTask<String , Integer , String> {
    private HttpConnectionWithArgs.HttpResultListener mListener;

    public void setHttpResultListener(HttpConnectionWithArgs.HttpResultListener listener) {
        mListener = listener;
    }


    @Override
    protected String doInBackground(String... params) {
        try {
            JSONObject uploadInfo = new JSONObject(params[0]);
            HttpConnectionWithArgs.doPost( uploadInfo.getString("url") , uploadInfo.getString("contentType") , params[1] , mListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

}
