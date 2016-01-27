package unic.props.demo;

import android.app.Activity;
import android.os.Bundle;

import unic.props.BubbleImageView;

public class MainActivity extends Activity {
    private BubbleImageView img = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = (BubbleImageView) findViewById(R.id.bubble_img);
        img.setImageResource(R.mipmap.test);


    }

    @Override
    protected void onDestroy() {
        img.release();
        super.onDestroy();

    }
}
