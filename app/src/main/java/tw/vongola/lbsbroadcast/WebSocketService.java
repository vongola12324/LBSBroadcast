package tw.vongola.lbsbroadcast;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;
import org.json.JSONException;
import org.json.JSONObject;


import java.net.URISyntaxException;
import java.util.Locale;

public class WebSocketService extends Service implements TextToSpeech.OnInitListener{
    private Socket webSocket;
    {
        try {
            webSocket = IO.socket("http://blackteatoast.asuscomm.com:8080");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    private TextToSpeech tts = null;
    private static final String TAG="BroadcastTTS";
    private Handler handler;
    private Emitter.Listener onNewMessage = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            try {
                if (((JSONObject)args[0]).has("Msg")){
                    tts.speak(((JSONObject)args[0]).getString("Msg"), TextToSpeech.QUEUE_ADD, null);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onCreate(){
        super.onCreate();
        webSocket.on("recvMsg", onNewMessage);
        webSocket.connect();
        Log.d("WebSocket", "Connected");
        tts = new TextToSpeech(this, this);
        tts.setSpeechRate((float) 1.2);
    }

    @Override
    public void onDestroy(){
        webSocket.disconnect();
        webSocket.off("recvMsg", onNewMessage);
        if (this.tts != null) {
            this.tts.stop();
            this.tts.shutdown();
        }
        Log.v(TAG, "BroadcastService Service OnDestory");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void sendMessage(double[] location){
        JSONObject json = new JSONObject();
        try {
            json.put("longitude", location[0]);
            json.put("latitude", location[1]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json.length() > 0)
            webSocket.emit("sendLocation", json);
    }

    @Override
    public void onInit(int status) {
        Log.v(TAG, "BroadcastService Service OnInit");
        if (status == TextToSpeech.SUCCESS) {
            int result = this.tts.setLanguage(Locale.TAIWAN);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.v(TAG, "Language is not available.");
            }
        } else {
            Log.v(TAG, "Could not initialize TextToSpeech.");
        }
    }

}
