package com.example.nataliatrybulova.naobrazovku;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    ListView listview;
    private static ItemsAdapter adapter;
    LayoutInflater inflater = null;
    private static List<DataStruct> items = new ArrayList<DataStruct>();
    final Context context = this;
    JSONObject customer;
    EditText user;
    EditText pass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        listview = (ListView) findViewById(R.id.listView);
        adapter = new ItemsAdapter();
        listview.setAdapter(adapter);
    }

    public void getData(String table, SQLiteDatabase db){
        String query = "";
        FileOutputStream outputStream;
        ArrayList<ArrayList<String>> items = null;

        query = "Select * from customers";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    int x = cursor.getColumnCount();
                    ArrayList<String> item = new ArrayList<String>();
                    for (int i = 0; i < x; i++) {
                        item.add(cursor.getString(i));
                    }
                   // this.session.add(item);
                } while (cursor.moveToNext());
            }
    }
    private static class ViewHolder {
        TextView txt1;

        public ViewHolder(TextView txt1) {
            this.txt1 = txt1;
        }

    }

    class ItemsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if(items == null)
                return 0;
            else
                return items.size();
        }

        @Override
        public Object getItem(int pos) {
            return items.get(pos);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            View vi = convertView;
            TextView text;

            if (vi == null) {
                vi = inflater.inflate(R.layout.row, null);
                text = (TextView) vi.findViewById(R.id.nazovFilmu);
                //text.setText(items.get(position).getArg(0));
                convertView.setTag(new ViewHolder(text));
            }else{
                ViewHolder viewHolder = (ViewHolder) convertView.getTag();
                text = viewHolder.txt1;
            }

            text.setText(items.get(position).getArg(0));

            return vi;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_action_scan:
                showLoginDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    private void showLoginDialog()
    {
        LayoutInflater li = LayoutInflater.from(context);
        View prompt = li.inflate(R.layout.login_dialog, null);
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
        alertDialogBuilder.setView(prompt);
        user = (EditText) prompt.findViewById(R.id.login_name);
        pass = (EditText) prompt.findViewById(R.id.login_password);
        //user.setText(Login_USER); //login_USER and PASS are loaded from previous session (optional)
        //pass.setText(Login_PASS);
        alertDialogBuilder.setTitle("Login to premium program");
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();

            }
        });

        alertDialogBuilder.show();
       // if (Login_USER.length()>1) //if we have the username saved then focus on password field, be user friendly :-)
            //pass.requestFocus();
    }

    // HTTP POST request
    private HttpURLConnection sendPost(String url) throws Exception {

       // String url = "https://192.168.10.10";

        String email = user.getText().toString();
        String password = pass.getText().toString();

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "My header");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        con.setDoInput(true);
        con.setDoOutput(true);


        /**************** CUSTOMER ******************/
        JSONObject customer = new JSONObject();

        customer.put("email", email);
        customer.put("password", password);

        /**************** CUSTOMER ******************/

        // Send post request
        con.setDoOutput(true);
        // Send post request
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.write(customer.toString().getBytes("UTF-8"));
        wr.flush();
        wr.close();

        // Teraz vytvorime skustocne spojenie cez siet
        // - az na zaver -  ked sme vsetko nastavili zapisali co chceme odoslat
        con.connect();

        return con;
    }

    class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {
            try {

                //Ak get = 0, ak post = 1
                HttpURLConnection urlConnection = sendPost("https://10.0.0.1/customers/");
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
                Log.d(TAG, response.toString());
                //sendPost();

            } catch (Exception e) {
                this.exception = e;
            }

            return "Executed";
        }

        protected void onPostExecute(String feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }

    public boolean validate() {
        boolean valid = true;

        String email = user.getText().toString();
        String password = pass.getText().toString();;

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            user.setError("enter a valid email address");
            valid = false;
        } else {
            user.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            pass.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            pass.setError(null);
        }

        return valid;
    }

    public boolean validate2()
    {
        String password = pass.getText().toString();
        String username = user.getText().toString();
        try
        {
            if ( username.length()<2 || password.length()<2)
            {
                Toast.makeText(MainActivity.this,"Invalid username or password", Toast.LENGTH_LONG).show();
                showLoginDialog();
            }
            else
            {
                // password=MCrypt3DES.computeSHA1Hash(password); //password is hashed SHA1
                //TODO here any local checks if password or user is valid
                new RetrieveFeedTask().execute();

                //this will do the actual check with my back-end server for valid user/pass and callback with the response
                //new CheckLoginAsync(MainActivity.this,username,password).execute("","");
            }
        }catch(Exception e)
        {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return true;
    }

    @SuppressLint("SdCardPath")
    public static HttpsURLConnection httpsPost(String urlString, KeyStore keyStore, JSONObject customer)
    {
        try
        {
            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Vytvori spojenie ale este sa nepripoji (aj ked tam je openConnection)
            URL url = new URL(urlString);
            HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();  // does not establish the actual connection

            // Create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);

            //HttpsURLConnection.setDefaultSSLSocketFactory(NoSSLv3Factory);

            // Tu to nastavujeme len pre toto spojenie - to co je zakomentovane vyssie to nastavuje globalne (asi pre vsetky buduce spojenia)
            //   t.j. nastavuje sa triede HttpsURLConnection a nie na premennej urlConnection ako nizsie
            // Toto mohol byt problem - mozno to funguje tak ze to globalne nastavenie to zmeni len pre buduce spojenia
            urlConnection.setSSLSocketFactory(context.getSocketFactory());

            // Ignoruje hostname v certifikate
            urlConnection.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            urlConnection.setDoInput(true);
            // Send post request Specifies whether this URLConnection allows sending data.
            urlConnection.setDoOutput(true);
            // Send post request
            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
            wr.write(customer.toString().getBytes("UTF-8"));
            wr.flush();
            wr.close();

            // Teraz vytvorime skustocne spojenie cez siet
            // - az na zaver -  ked sme vsetko nastavili zapisali co chceme odoslat
            urlConnection.connect();

            return urlConnection;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            //Log.e(TAG, "Failed to establish SSL connection to server: " + ex.toString());
            return null;
        }


    }

}



