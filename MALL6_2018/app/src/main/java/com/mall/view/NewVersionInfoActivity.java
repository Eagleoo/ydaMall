package com.mall.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mall.util.IAsynTask;
import com.mall.util.Util;

import java.io.Serializable;

public class NewVersionInfoActivity extends AppCompatActivity {
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_version_info);

        text = (TextView) findViewById(R.id.text);
        ImageView imageView = (ImageView) findViewById(R.id.topback);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        check();

    }

    private void check() {
        Util.asynTask(new IAsynTask() {
            @Override
            public Serializable run() {
                return Util.update1();
            }

            @Override
            public void updateUI(Serializable runData) {
                final String[] info = (String[]) runData;
                for (String string : info) {
                    text.setText(text.getText() + "\n" + string);
                }
            }
        });
    }
}
