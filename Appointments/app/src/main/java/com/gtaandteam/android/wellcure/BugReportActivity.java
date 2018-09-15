package com.gtaandteam.android.wellcure;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class BugReportActivity extends AppCompatActivity {

    EditText title, desc;
    Button Report;

    android.support.v7.widget.Toolbar MyToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_bug_report);

        MyToolbar = findViewById(R.id.MyToolbar);
        setSupportActionBar(MyToolbar);


            title = findViewById(R.id.Title);
            desc = findViewById(R.id.Desc);
            Report = findViewById(R.id.ReportButton);

            Report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO: Stuff
                }
            });




        }
}
