package com.example.nataliatrybulova.naobrazovku;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by nataliatrybulova on 23.04.16.
 */
public class Algoritmus extends AppCompatActivity {

    int retrieve_value_customer = 0;
    int requiered_value_movie = 0;
    private static final String TAG = "Algoritmus____SERVICE";
    LayoutInflater inflater = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        // getCustomer
        //Ak nema dostatok
        if(requiered_value_movie > retrieve_value_customer)
        {
            Toast.makeText(Algoritmus.this, "NEMAS DOSTATOK BODOV", Toast.LENGTH_LONG).show();

        }
    }


    class RetrieveFeedTask extends AsyncTask<String, Void, JSONObject> {

        private Exception exception;
        private String email;
        private String password;
        private String action;


        RetrieveFeedTask(String action, String email, String pass) {
            this.action = action;
            this.email = email;
            this.password = pass;
        }
        RetrieveFeedTask(String action) {
            this.action = action;
            this.email = null;
            this.password = null;
        }
        RetrieveFeedTask(String action, String email) {
            this.action = action;
            this.email = email;
        }

        protected JSONObject doInBackground(String... urls) {
            JSONObject json = null;
            try {

                //Ak get = 0, ak post = 1
                //HttpURLConnection urlConnection = sendPost("https://10.0.0.1/customers/");
                //HttpURLConnection urlConnection = sendPost("http://192.168.9.101/customers/login");
                HttpURLConnection urlConnection = sendPost("http://192.168.9.101/other/proxy_torta.php", action, email, password);


                //InputStream in = urlConnection.getInputStream();

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;

                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                //print result
                Log.d(TAG, "--response: " + response.toString());
                json = new JSONObject(response.toString());


                //sendPost();

            } catch (Exception e) {
                this.exception = e;
            }


            return json;
        }

        protected void onPostExecute(JSONObject feed) {




        }
    }
    private HttpURLConnection sendPost(String url, String action, String email, String pass) throws Exception {

        Log.d(TAG, "---- sendPost: " + url + "\n   " + action + " " + email + " " + pass);
        // String url = "https://192.168.10.10";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "My header");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-type", "application/json");
        con.setRequestProperty("Accept", "application/json");

        con.setDoInput(true);
        con.setDoOutput(true);


        /**************** CUSTOMER ******************/
        JSONObject json = new JSONObject();

        // Action is used by proxy to select REST API url to forward
        // values: login, add, index, list, get_movie_list
        json.put("action", action);

        if (email != null && !email.isEmpty())
            json.put("email", email);

        if (pass != null && !pass.isEmpty())
            json.put("password", pass);

        // Action is used by proxy to select REST API url to forward
        // values: login, add, index, list, get_movie_list
        //json.put("action", "login");


        /**************** CUSTOMER ******************/

        // Send post request
        //con.setDoOutput(true);
        // Send post request
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.write(json.toString().getBytes("UTF-8"));
        wr.flush();
        wr.close();

        // Teraz vytvorime skustocne spojenie cez siet
        // - az na zaver -  ked sme vsetko nastavili zapisali co chceme odoslat
        con.connect();

        return con;
    }
}
