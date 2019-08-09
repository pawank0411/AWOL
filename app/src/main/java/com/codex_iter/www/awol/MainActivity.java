package com.codex_iter.www.awol;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    EditText user, pass;
    Button btn;
    ProgressDialog pd;
    SharedPreferences userm,logout;
    SharedPreferences.Editor edit;
    LinearLayout ll;
    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";
    private Switch aSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        SharedPreferences theme = getSharedPreferences("theme",0);
        boolean dark = theme.getBoolean("dark_theme", false);
        if (useDarkTheme) {
            if (dark)
                setTheme(R.style.AppTheme_Dark_NoActionBar);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        SharedPreferences device_time = getSharedPreferences("Device_time", 0);
//        SharedPreferences.Editor editor = device_time.edit();
//
//        Toast.makeText(this, String.valueOf(hour), Toast.LENGTH_SHORT).show();
//        editor.putInt("Present_time", hour);
//        editor.apply();

ll=findViewById(R.id.ll);
ll.setOnTouchListener(new View.OnTouchListener() {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    return false;
    }
});

        user = findViewById(R.id.user);
        pass = findViewById(R.id.pass);
        btn = findViewById(R.id.btn);
        userm = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        logout= getSharedPreferences("sub",
                Context.MODE_PRIVATE);

        if (!dark){
            user.setTextColor(Color.parseColor("#141831"));
            pass.setTextColor(Color.parseColor("#141831"));
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String u = user.getText().toString().trim();
                String p = pass.getText().toString().trim();

                 if(u.equals("") & p.equals(""))
                    Toast.makeText(MainActivity.this, "Enter your Details", Toast.LENGTH_SHORT).show();

                else {


                    if (haveNetworkConnection()) {
                        String web = getString(R.string.link);
                        getData(web,u,p);
                        edit= userm.edit();
                        edit.putString("user",u);
                        edit.putString(u+"pass",p);
                        edit.putString("pass",p);
                        edit.apply();
                        edit=logout.edit();
                        edit.putBoolean("logout",false);
                        edit.commit();

                    } else{
                    showData(u,p);
                    }  }  }
        });

        if (userm.contains("user")&&userm.contains("pass")&&logout.contains("logout")&&!logout.getBoolean("logout",false)) {
            user.setText(userm.getString("user", ""));
            pass.setText(userm.getString("pass", ""));
            btn.performClick();
        }



    }


    private void showData(String u, String p) {
        Toast.makeText(getApplicationContext(), "no network connection", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, home.class);
        if(userm.contains(u)) {
            if(p.equals(userm.getString(u+"pass", ""))){
                edit=logout.edit();
                edit.putBoolean("logout",false);
                edit.apply();
                String s = userm.getString(u, "");
                intent.putExtra("result", s);
                Toast.makeText(getApplicationContext(), "showing offline value for this user", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
            else{
                Toast.makeText(getApplicationContext(), "invalid credentials", Toast.LENGTH_SHORT).show();
            }}
        else
            Toast.makeText(getApplicationContext(), "no offline info for this user", Toast.LENGTH_SHORT).show();




    }


    private void getData(final String... param)  {

            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            StringRequest postRequest = new StringRequest(Request.Method.POST, param[0]+"/attendance",
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            if (pd.isShowing())
                                pd.dismiss();
                            user.setText("");
                            pass.setText("");
                            if(response.equals("404"))
                                Toast.makeText(getApplicationContext(), "Wrong Credentials!", Toast.LENGTH_SHORT).show();
                           else{

                            Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, home.class);
                            //getname(param);
                            response += "kkk" + param[1];
                            intent.putExtra("result", response);
                            edit.putString(param[1], response);
                            edit.commit();
                            startActivity(intent);

                        }}
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            if (pd.isShowing())
                                pd.dismiss();
                            user.setText("");
                            pass.setText("");
                            showData(param[1],param[2]);
                          if(error instanceof AuthFailureError)
                              Toast.makeText(getApplicationContext(), "Wrong Credentials!", Toast.LENGTH_SHORT).show();
                          else if(error instanceof ServerError)
                              Toast.makeText(getApplicationContext(), "Cannot connect to servers right now", Toast.LENGTH_SHORT).show();
                          else if(error instanceof NetworkError)
                              Toast.makeText(getApplicationContext(), "cannot establish connection", Toast.LENGTH_SHORT).show();
                          else if(error instanceof TimeoutError)
                              Toast.makeText(getApplicationContext(), "Cannot connect to servers right now", Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String>  params = new HashMap<>();
                    params.put("user", param[1]);
                    params.put("pass", param[2]);

                    return params;
                }
            };
            queue.add(postRequest);




        }
//    private void getname(final String... param){
//        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
//        StringRequest postRequest = new StringRequest(Request.Method.POST, param[0]+"/studentinfo",
//                new Response.Listener<String>()
//                {
//                    @Override
//                    public void onResponse(String response)  {
//
//                     try {
//                           JSONObject jobj  = new JSONObject(response);
//                           JSONArray jarr   = jobj.getJSONArray("detail");
//                           JSONObject jobj1 = jarr.getJSONObject(0);
//                           name = jobj1.getString("name");
//                        } catch (JSONException e) {
//                            Toast.makeText(getApplicationContext(), "cannot fetch name!!", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {}}
//
//        ) {
//            @Override
//            protected Map<String, String> getParams()
//            {
//                Map<String, String>  params = new HashMap<String, String>();
//                params.put("user", param[1]);
//                params.put("pass", param[2]);
//
//                return params;
//            }
//        };
//        queue.add(postRequest);
//
//    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
