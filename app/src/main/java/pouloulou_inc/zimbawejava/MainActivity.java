package pouloulou_inc.zimbawejava;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import java.net.URISyntaxException;

import io.socket.emitter.Emitter;


public class MainActivity extends AppCompatActivity {
    private Activity activity = MainActivity.this;
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d("TAG", "onConnectError: " + args);
        }
    };
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if(activity != null) {
                activity.runOnUiThread((new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.d("TAG", "onDisconnect: " + args);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }));
            }
        }
    };
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("TAG", "onConnect: " + args);
        }
    };
    private Emitter.Listener newMessageListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("TAG", "newMessageListener: "+ args);
        }
    };
    private Emitter.Listener onID = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                ((GlobalClass)getApplicationContext()).setId(((JSONObject)args[0]).getInt("id"));
            } catch (JSONException e) {}
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            GlobalClass globalClassvars = (GlobalClass) getApplicationContext();
            globalClassvars.setSocket(IO.socket(getString(R.string.URLrequest)));
            Socket socket = globalClassvars.getSocket();
            socket.on(Socket.EVENT_CONNECT, onConnect);
            socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            socket.on("newMessage", newMessageListener);
            socket.on("ID", onID);
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this,logActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onDestroy() {
        Socket socket = ((GlobalClass)getApplicationContext()).getSocket();
        super.onDestroy();

        if (socket != null) {
            socket.disconnect();
            socket = null;
        }
    }
    public void toast(String s) {
        Toast toast = Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT);
        toast.show();
    }
    public void toast(CharSequence s) {
        Toast toast = Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT);
        toast.show();
    }
}
