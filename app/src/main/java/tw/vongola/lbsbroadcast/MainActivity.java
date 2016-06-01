package tw.vongola.lbsbroadcast;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends ActionBarActivity {
    private WebSocketService websocket = null;
    private GPSService gps = null;
    private TimerTask task = null;
    private Handler handler = null;
    private static TextView sv = null;
    private static ImageView iv = null;

    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (websocket == null) {
            websocket = new WebSocketService();
            startService(new Intent(this, WebSocketService.class));
        }
        if (gps == null) {
            gps = new GPSService(this);
            startService(new Intent(this, GPSService.class));
        }
        if (handler == null) {
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    websocket.sendMessage(gps.getLocation());
                    Log.d("Send Location", "Sent!");
                    handler.postDelayed(this, 10000);
                }
            }, 1000);
        }
        if (sv == null) {
            sv = (TextView) findViewById(R.id.textView);
            sv.setText(getString(R.string.main_activity_text));
        }
        if (iv == null) {
            iv = (ImageView) findViewById(R.id.imageView);
            iv.setImageResource(R.drawable.speaker);
//            iv.setMaxWidth((int) (iv.getWidth() * 0.75));
        }

//        sendButton = (Button) findViewById(R.id.sendButton);
//        sendButton.setText(getString(R.string.button_send_location));
//        sendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                websocket.sendMessage(gps.getLocation());
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            stopService(new Intent(this, WebSocketService.class));
            stopService(new Intent(this, GPSService.class));
            this.finish();
            System.exit(0);
        }

        return super.onOptionsItemSelected(item);
    }

}
