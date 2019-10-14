package ru.semenovmy.learning.speedometerproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private SpeedometerView speedometerView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.speed_txt);
        textView.setText(R.string.slowing_down);
        speedometerView = (SpeedometerView) findViewById(R.id.speedometer_view);
    }
}
