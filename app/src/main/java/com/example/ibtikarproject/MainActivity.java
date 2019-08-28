package com.example.ibtikarproject;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textView = (TextView) findViewById(R.id.txt_view);
        final Button okHttpButton = findViewById(R.id.okhttp);


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
                    inputFromApi = readStream(in);

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }

                return inputFromApi;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
//                textView.setText(inputFromApi);

            }
        };

        myTask.execute();



        //====================================parcing data using JSONObject=====================================






        okHttpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                @SuppressLint("StaticFieldLeak") final AsyncTask taskTwo=new AsyncTask() {

                    String response = null;
                    @Override
                    protected Object doInBackground(Object[] objects) {

                        OkHttpClass example = new OkHttpClass();

                        try {
                            response = example.run("https://api.themoviedb.org/3/movie/popular?api_key=6b93b25da5cdb9298216703c40a31832");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
//                        TextView textView1=findViewById(R.id.textView);
//                        textView1.setText(response);
                    }
                };
                taskTwo.execute();

//     ==========================simple jsonobj===============

                try {
                    JSONObject jsonObject =new JSONObject(taskTwo.get().toString());
                  JSONArray result = jsonObject.getJSONArray("results");
                    ArrayList<Results> movies=new ArrayList<Results>();

                    for (int i=0; i<result.length(); i++){
                        Results r =new Results();
                        JSONObject obj = result.getJSONObject(i);
                        r.setTitle(obj.getString("title"));
                        r.setRelease_date(obj.getString("release_date"));
                        movies.add(r);
                        textView.setMovementMethod(new ScrollingMovementMethod());
                        textView.append(r.getRelease_date()+"\n");
                        textView.append(r.getTitle()+"\n\n");
                    }

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


       //=============================================


//     ================= Gson==========

                Gson g = new Gson();
                try {
                    PopularMovies pm= g.fromJson(taskTwo.get().toString(),PopularMovies.class);
                    Results[] res= pm.getResults();
                    for(int i=0; i<res.length;i++){
                        TextView textView1=findViewById(R.id.textView);
                        textView1.setMovementMethod(new ScrollingMovementMethod());
                        textView1.append(res[i].getTitle()+"\n");
                        textView1.append(res[i].getOriginal_language()+"\n");
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


//==============================================
            }
        });



    }

    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is), 1000);
        for (String line = r.readLine(); line != null; line = r.readLine()) {
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }



    public class OkHttpClass {
        OkHttpClient client = new OkHttpClient();

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        String run(String url) throws IOException {
            Request request = new Request.Builder()
                    .url(url)
                    .build();


            try (Response response = client.newCall(request).execute()) {
                return response.body().string();
            }
        }
    }
}
