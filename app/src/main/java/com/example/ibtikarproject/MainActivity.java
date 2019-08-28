package com.example.ibtikarproject;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textView=(TextView)findViewById(R.id.txt_view);

        @SuppressLint("StaticFieldLeak") final AsyncTask myTask = new AsyncTask() {
            String inputFromApi;
            @Override
            protected Object doInBackground(Object[] objects) {
                URL url = null;
                try {
                    url = new URL("https://api.themoviedb.org/3/movie/popular?api_key=6b93b25da5cdb9298216703c40a31832");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpsURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setConnectTimeout(10000);
                    urlConnection.setReadTimeout(10000);


                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    InputStream in = urlConnection.getInputStream();
                    inputFromApi= readStream(in);

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }

//                publishProgress(inputFromApi);
                return inputFromApi;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                textView.setText(inputFromApi);

            }
        };

        myTask.execute();
        try {
            Log.i("gabelmsg",myTask.get().toString());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is),1000);
        for (String line = r.readLine(); line != null; line =r.readLine()){
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }
}
