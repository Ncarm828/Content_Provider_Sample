package appsandmaps.temple.edu.content_provider;


import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

public class UpdateActivityClass extends AsyncTask<Void, String, String> {

    private static final String TAG = "async task";
    private Exception exception;
    public AsyncResponse delegate = null;

    protected String doInBackground(Void... params) {


        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://www.mikekorostelev.com/~bits/self/db/user/mike/update");
        Log.i(TAG, "adding");
        try {
            // Add your data


            ResponseHandler<String> responseHandler=new BasicResponseHandler();
            String json = httpclient.execute(httppost, responseHandler);
            //JSONObject response=new JSONObject(responseBody);
            publishProgress(json);
            return(json);

        } catch (ClientProtocolException e) {
            return e.toString();
            // TODO Auto-generated catch block
        } catch (IOException e) {
            return e.toString();
            // TODO Auto-generated catch block
        }


    }

    protected void onPostExecute(String response) {
        // TODO: check this.exception
        // TODO: do something with the feed
        Log.i(TAG, "the response = " + response);
        // ContractClass.DataBaseInfoHolder = response;
         delegate.ProcessFinish(response);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        //  ContractClass.DataBaseInfoHolder = values[0];
    }



}