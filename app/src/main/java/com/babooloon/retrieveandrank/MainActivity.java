package com.babooloon.retrieveandrank;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private EditText editText;
    private TextView textView;

    private static final String USERNAME = "3a92a1a2-6d59-4ae0-9a38-cc2dc0785d80";
    private static final String PASSWORD = "Je8iOi18gSGY";
    private static final String SOLR_CLUSTER_ID = "sc1afc2fba_ebc2_4630_a1df_d0ec76078c26";
    private static final String RANKDER_ID = "1eec74x28-rank-965";
    private static final String COLLECTION_NAME = "V2";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.editText);
        textView = (TextView) findViewById(R.id.textView);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RetrieveAndRankTask().execute(editText.getText().toString());
            }
        };
        button.setOnClickListener(onClickListener);

    }

    private class RetrieveAndRankTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // textView = (TextView) findViewById(R.id.textView);
        }
        @Override
        protected String doInBackground(String... params) {
            String query = params[0];

            String json_response = "";
            HttpURLConnection connection;
            try{
                // Set up post connection
                query = query.replace(" ", "%20");
                String url_str = "https://gateway.watsonplatform.net/retrieve-and-rank/api/v1/solr_clusters/"
                        + SOLR_CLUSTER_ID + "/solr/" + COLLECTION_NAME + "/fcselect?ranker_id=" + RANKDER_ID + "&q=" + query;
                System.out.println(url_str);
                URL url = new URL(url_str);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");

                // Login credentials
                String creds = USERNAME + ":" + PASSWORD;
                String header = "Basic " + new String(android.util.Base64.encode(creds.getBytes(), android.util.Base64.NO_WRAP));
                connection.addRequestProperty("Authorization", header);

                // Connect
                connection.connect();

                // Get response if success
                //TODO json format in java
                if(connection.getResponseCode()==201 || connection.getResponseCode()==200){
                    // d_root in Document
                    // SAXReader reader = new SAXReader();
                    // Document d_root = reader.read(connection.getInputStream());
                    // json_response in string
                    InputStreamReader in = new InputStreamReader(connection.getInputStream());
                    BufferedReader br = new BufferedReader(in);
                    String text = "";
                    while ((text = br.readLine()) != null) {
                        json_response += text;
                    }
                }
            }catch (MalformedURLException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Executed");
            return json_response;
        }

        @Override
        protected void onPostExecute(String strings) {
            super.onPostExecute(strings);
            textView.setText(strings);
        }

    }
}
