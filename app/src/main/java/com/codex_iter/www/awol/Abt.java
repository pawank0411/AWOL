package com.codex_iter.www.awol;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Abt extends AppCompatActivity {
    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";
    private TextView ver, dis, para, hey, abt;
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
        setContentView(R.layout.activity_abt);
        ImageView fb,gi,gmal;
        TextView cdx;
        fb=findViewById(R.id.fb);
        gi=findViewById(R.id.git);
        gmal=findViewById(R.id.gmail);
        ver = findViewById(R.id.ver);
        dis = findViewById(R.id.dis);
        para = findViewById(R.id.para);
        hey = findViewById(R.id.hey);
        abt = findViewById(R.id.abt_me);
        if (!dark){
            ver.setTextColor(Color.parseColor("#141831"));
            dis.setTextColor(Color.parseColor("#141831"));
            para.setTextColor(Color.parseColor("#141831"));
            hey.setTextColor(Color.parseColor("#141831"));
            abt.setTextColor(Color.parseColor("#141831"));
        }


        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/mohitlalitaagarwalnovember")));
            }
        });
        gi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/mojito9542")));
            }
        });
        gmal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "mohitagarwal9542@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for AWOL");
                getApplicationContext().startActivity(Intent.createChooser(emailIntent, null));
            }
        });
        cdx=findViewById(R.id.cdx);
         cdx.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/codex-iter")));
             }
         });
    }
}
