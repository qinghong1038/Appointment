package com.gtaandteam.android.wellcure;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    TextView About;

    LinearLayout Youtube, Facebook, Wellcure;
    String youtubeURL = "https://www.youtube.com/channel/UChzQns81GkjawXR9zxiHJKA";
    String facebookURL = "https://www.facebook.com/RitusClinic/";
    String wellcureURL = "https://www.Wellcureclinic.com";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        About = findViewById(R.id.about);
        About.setText(R.string.about);
        Youtube = findViewById(R.id.youtube);
        Facebook = findViewById(R.id.facebook);
        Wellcure = findViewById(R.id.wellcure);


        Youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(youtubeURL));
                startActivity(intent);

            }
        });

        Facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(facebookURL));
                startActivity(intent);

            }
        });

        Wellcure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(wellcureURL));
                startActivity(intent);

            }
        });

    }
}
