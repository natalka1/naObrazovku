package com.example.nataliatrybulova.naobrazovku;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MainActivity";


    ListView listview;
    private static ItemsAdapter adapter;
    LayoutInflater inflater = null;
    private static List<DataStruct> items = new ArrayList<DataStruct>();
    public List<String> movies = new ArrayList<String>();

    final Context context = this;
    JSONObject customer;
    EditText user;
    EditText pass;
    public JSONObject jsonObj = null;

    private boolean method2Executed = false;

    Customer cust;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        listview = (ListView) findViewById(R.id.listView);
        adapter = new ItemsAdapter();
        listview.setAdapter(adapter);
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        String toastMsg;
        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                toastMsg = "Large screen";
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                toastMsg = "Normal screen";
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                toastMsg = "Small screen";
                break;
            default:
                toastMsg = "Screen size is neither large, normal or small";
        }
        Toast.makeText(MainActivity.this, toastMsg, Toast.LENGTH_LONG).show();

        /* 888 888 */cust = new Customer();

        /* ************************************************** */
        if(items.size() == 0 && method2Executed == false)
            new RetrieveFeedTask("get_movie_list").execute();

        /* ************************************************** */


        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView kliknute = (TextView) view.findViewById(R.id.nazovFilmu);
                String nazov_filmu = kliknute.getText().toString();
                int body_za_film = 0;

                //Ak nema dostatok bodov !!!!

                for (int i = 0; i < items.size(); i++) {
                    if(items.get(i).getArg(0).equals(nazov_filmu) ) {
                        body_za_film = items.get(i).getPoints();
                        Toast.makeText(MainActivity.this, "Potrebnych: "+ body_za_film, Toast.LENGTH_LONG).show();


                        if (body_za_film > cust.getPoints()) {
                            Toast.makeText(MainActivity.this, "Nemas dostatok bodov", Toast.LENGTH_LONG).show();
                            return;

                        }
                        else if (items.get(i).getArg(1).equals("true")) {
                            Toast.makeText(MainActivity.this, "Mas dostatok bodov", Toast.LENGTH_LONG).show();
                            Log.d("MAM DOSTATOK BODOV", "cust.getEmail: " + cust.getEmail());
                            if(cust.getEmail().equals("default"))
                            {
                                Toast.makeText(MainActivity.this, "Zle sa nacital email", Toast.LENGTH_LONG).show();
                                return;

                            }
                            new RetrieveFeedTask("update", cust.getEmail(), body_za_film).execute();
                        }



                    }
                }

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

            if(items.get(position).getArg(1).equals("false"))
            {
                text.setEnabled(false);
                Log.d(TAG, "FALSE");


            }
            else{
                text.setEnabled(true);

                Log.d(TAG, "TRUE");

            }

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

                        new RetrieveFeedTask("get_movie_list", username, password).execute();
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
    private HttpURLConnection sendPost(String url, String action, String email, String pass, String points) throws Exception {

        Log.d(TAG, "---- sendPost: " + url + "\n   " + action + " " + email + " " + pass + points);
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

        if (points != null && !points.isEmpty())
            json.put("movie_points", points);

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


    class RetrieveFeedTask extends AsyncTask<String, Void, JSONObject> {

        private Exception exception;
        private String email;
        private String password;
        private String action;
        private int points = 0;


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
        RetrieveFeedTask(String action, String email, Integer points) {
            this.action = action;
            this.email = email;
            this.points = points;
            this.password = null;

        }
        RetrieveFeedTask(String action, String email) {
            this.action = action;
            this.email = email;
            this.password = null;
        }

        protected JSONObject doInBackground(String... urls) {
            JSONObject json = null;
            String points_argument = "0";
            try {

                //Ak get = 0, ak post = 1
                //HttpURLConnection urlConnection = sendPost("https://10.0.0.1/customers/");
                //HttpURLConnection urlConnection = sendPost("http://192.168.9.101/customers/login");
                if(points != 0)
                {
                    points_argument = Integer.toString(points);
                }
                HttpURLConnection urlConnection = sendPost("http://10.0.0.1/proxy_torta.php", action, email, password, points_argument);


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

            if (items != null)
                items.clear();

            JSONArray arr = null;
            int points = 0;
            int level_id = 0;
            String email = "default";
            String updated = "default";
            Log.d(TAG, feed.toString());
            Log.d(TAG, updated);


            try {
                updated = feed.getString("action");
                if (updated == null && updated.isEmpty()) {
                    Log.d(TAG, "Prazdny action  ");
                    updated = "default";

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "Pred ifom:" +updated);

            if (updated.equals("default"))
            {

                try {
                    arr = feed.getJSONArray("movie_list");
                    String level_idd = feed.getString("level_id");
                    JSONObject j = feed.getJSONObject("cust");
                    String p = j.getString("total_points");
                    points = Integer.parseInt(p);
                    level_id = Integer.parseInt(level_idd);
                    email = j.getString("email");
                    cust = new Customer(level_id, points, email);
                    Log.d("Customer je: ", level_id + "///" + cust);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            for (int i = 0; i < arr.length(); i++) {
                try {
                    String movie = arr.getJSONObject(i).getString("name");
                    String enabled = arr.getJSONObject(i).getString("enabled");
                    int points_movie = Integer.parseInt(arr.getJSONObject(i).getString("points"));
                    Log.d(TAG, "Toto je enabled:  " + enabled);

                    String bo = "false";
                    if (enabled.equals("true")) {
                        bo = "true";
                    }
                    Log.d(TAG, "Toto je bo:  " + bo);

                    DataStruct skuska = new DataStruct(movie, bo, points_movie);

                    items.add(skuska);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            method2Executed = true;
            adapter.notifyDataSetChanged();
        }

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
}



