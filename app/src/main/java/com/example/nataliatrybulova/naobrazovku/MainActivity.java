package com.example.nataliatrybulova.naobrazovku;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
        DataStruct skuska = new DataStruct("Avatar", "1");
        DataStruct skuska2 = new DataStruct("Avatar1_nove", "2");
        DataStruct skuska3 = new DataStruct("Titanic2_nove", "3");
        DataStruct skuska4 = new DataStruct("Titanic3_nove", "4");
        items.add(skuska);
        items.add(skuska2);
        items.add(skuska3);
        items.add(skuska4);

        Toast.makeText(MainActivity.this, "SKUSKA !!!!!!!" , Toast.LENGTH_LONG).show();

        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView kliknute = (TextView) view.findViewById(R.id.nazovFilmu);


                String nazov_filmu = kliknute.getText().toString();
                Toast.makeText(MainActivity.this, nazov_filmu , Toast.LENGTH_LONG).show();


                //Spustit aplikaciu, ktora spusti film
                //spusta sa nazov package + nazov aktivity
                try {
                    //Intent intent = new Intent("com.example.nataliatrybulova.skuska.MainActivity");
                                    //startActivity(intent);

                    /*TOTO IDE, ALE OTVORI MI TO LEN PREHLIADAC NA FILMY*/
                    openApp(context,  "com.msi.android.mediabrowser");

                    /* SNAHA O TO, ABY MI OTVORILO AJ POZADOVANY FILMY*/
                    openApp_2(context,  "com.msi.android.mediabrowser");
                    //Intent intent = new Intent();
                    //intent.setComponent(new ComponentName("com.example.nataliatrybulova.skuska", "MainActivity"));
                    //startActivity(intent);

                }
                catch(Exception e)
                {
                    String vypis = e.getMessage() + "************" +e.getStackTrace().toString();
                    Toast.makeText(MainActivity.this, vypis , Toast.LENGTH_LONG).show();

                }

                //Intent intent = new Intent("com.msi.android.mediabrowser.AlbumGallery");
                //intent.putExtra("LAST_GOOD_DATA", "AlbumGallery:::/sdcard/videos/movies/Avatar.mp4:::0:::movies");
                //intent.putExtra("nazov_filmu", nazov_filmu);
            }
        });
    }

    public boolean openApp_2(Context context, String packageName){

        int position = 0;
        String fileName = "/opt/data/videos/movies/zamena.mp4";
        String RELOAD_VIDEOAD_PARAM = "com.msi.intent.action.reloadvideoadparam";
        sendBroadcast(new Intent(RELOAD_VIDEOAD_PARAM));

        PackageManager manager = context.getPackageManager();
        Intent i = manager.getLaunchIntentForPackage(packageName);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        i.setComponent(new ComponentName("com.msi.android.mediabrowser", "com.msi.android.mediabrowser.IntroductionView"));


        i.addFlags(268435456);
        i.addFlags(4);

        if (i == null)  {
            Toast.makeText(context, "Vyrucilo sa to" , Toast.LENGTH_LONG).show();

            return false;
        }
        i.putExtra("LAST_GOOD_DATA", "PlayerView:::" + fileName + ":::" + "1:::" + position + ":::0:::0:::0");
        context.startActivity(i);

        return true;


    }
    public static boolean openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        Intent i = manager.getLaunchIntentForPackage(packageName);

        if (i == null)  {
            return false;
            //throw new PackageManager.NameNotFoundException();
        }

        //i.setAction("android.intent.action.MAIN")  ;
        i.addCategory(Intent.CATEGORY_LAUNCHER);
                //i.putExtra("i","nasrac") ;
        //i.putExtra("LAST_GOOD_DATA", "AlbumGallery:::/sdcard/videos/movies/Avatar.mp4:::0:::movies");
        i.putExtra("LAST_GOOD_DATA", "AlbumGallery:::/opt/data/videos/:::0:::movies");


        context.startActivity(i);
        return true;

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
                text.setText(items.get(position).getArg(0));
                vi.setTag(new ViewHolder(text));
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
        Log.d(TAG, "---- show dialog");
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

                        // Daco skusi spravic
                        String password = pass.getText().toString();
                        String username = user.getText().toString();
                        //new RetrieveFeedTask("login", username, password).execute();

                        new RetrieveFeedTask("getLevel", username, password).execute();
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                Log.d(TAG, "---- CANCEL");
                dialog.cancel();

            }
        });

        alertDialogBuilder.show();
       // if (Login_USER.length()>1) //if we have the username saved then focus on password field, be user friendly :-)
            //pass.requestFocus();
    }

    // HTTP POST request
    // action values: login, add, index, list, get_movie_list
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

    class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        private Exception exception;
        private String email;
        private String password;
        private String action;

        RetrieveFeedTask(String action, String email, String pass) {
            this.action = action;
            this.email = email;
            this.password = pass;
        }
        protected String doInBackground(String... urls) {
            try {

                //Ak get = 0, ak post = 1
                //HttpURLConnection urlConnection = sendPost("https://10.0.0.1/customers/");
                //HttpURLConnection urlConnection = sendPost("http://192.168.9.101/customers/login");
                HttpURLConnection urlConnection = sendPost("http://192.168.9.104/other/proxy_torta.php", action, email, password);


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

                //new RetrieveFeedTask("login", username, password).execute();

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
            urlConnection.setRequestProperty("Accept", "application/json; charset=UTF-8");
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



