package com.udacity.gradle.builditbigger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.furkansalihege.android.jokeandroidlib.JokeActivity;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.udacity.gradle.builditbigger.backend.myApi.MyApi;

import java.io.IOException;



public class EndpointsAsyncTask extends AsyncTask<Context, Void, EndpointsAsyncTask.Result> {

    private static MyApi myApiService = null;
    @SuppressLint("StaticFieldLeak")
    private Context context;

    class Result {
        String data;
        boolean error;

        Result(String data, boolean error) {
            this.data = data;
            this.error = error;
        }
    }

    @Override
    protected final EndpointsAsyncTask.Result doInBackground(Context... params) {
        if (myApiService == null) {
            String ROOT_URL = "http://10.0.2.2:8080/_ah/api/";
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    .setRootUrl(ROOT_URL)
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            myApiService = builder.build();
        }
        this.context = params[0];
        try {
            return new Result(myApiService.getJoke().execute().getData(), false);
        } catch (IOException e) {
            return new Result(e.getMessage(), true);
        }
    }

    @Override
    protected void onPostExecute(EndpointsAsyncTask.Result result) {
        Intent intent = new Intent(context, JokeActivity.class);
        intent.putExtra(JokeActivity.INTENT_JOKE, result.data);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
