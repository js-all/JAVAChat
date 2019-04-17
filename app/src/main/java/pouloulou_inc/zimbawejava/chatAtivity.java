package pouloulou_inc.zimbawejava;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class chatAtivity extends Activity {
    OkHttpClient client = new OkHttpClient();
    final Gson gson = new GsonBuilder().serializeNulls().create();
    private Activity activity = chatAtivity.this;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Context applicationContext = getApplicationContext();
        final Boolean theme = getIntent().getBooleanExtra("theme",true);
        final String pseudo = getIntent().getStringExtra("pseudo");
        final ArrayList<_message> MSGs = new ArrayList<>();
        final Socket socket = ((GlobalClass)getApplicationContext()).getSocket();
        _message blank = new _message();
        blank.setAuthor("Bienvenue a toi !");
        blank.set_content("Bienvenue a " + pseudo + " !");
        MSGs.add(blank);
        updateMsg(MSGs);

        Button send = findViewById(R.id.sendM);
        send.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                TextView msgg = findViewById(R.id.msgV);
                if (msgg.length() > 0) {

                    CharSequence msgV = msgg.getText();
                    _message msg = new _message();
                    msg.setAuthor(pseudo);
                    msg.set_content(msgV.toString());
                    MSGs.add(msg);
                    updateMsg(MSGs);
                    msgg.setText("");
                    ListView vue = findViewById(R.id.msgContainer);
                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("author", msg.getAuthor());
                        obj.put("content", msg.get_content());
                        socket.emit("newMSG", obj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        ListView vue = findViewById(R.id.msgContainer);
        vue.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                TextView msgg = findViewById(R.id.msgV);
                msgg.setText(MSGs.get(position)._content);
                toast("message copy");
            }
        });
        if (!theme) {
            findViewById(R.id.chatL).setBackgroundResource(R.drawable.theme_dark);
            findViewById(R.id.msgContainer).setBackgroundResource(R.drawable.theme_dark_msg_container);
        }
        else {
            findViewById(R.id.chatL).setBackgroundResource(R.drawable.theme_light);
            findViewById(R.id.msgContainer).setBackgroundResource(R.drawable.theme_light_msg_container);
        }
        socket.on("newMSG", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject jsonObject = (JSONObject)args[0];
                    if (jsonObject.getInt("updaterID") == ((GlobalClass)getApplicationContext()).getId()) {
                        return;
                    }
                    MSGs.add(new _message(jsonObject.getString("author"), jsonObject.getString("content")));
                } catch (org.json.JSONException e) {}
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url(getString(R.string.URLrequest) + getString(R.string.URLgetExistingMessages))
                        .build();
                try (Response response = client.newCall((request)).execute()) {
                    String stringRes = response.body().string();
                    ArrayList<_message> res = gson.fromJson(stringRes, new TypeToken<ArrayList<_message>>(){}.getType());
                    MSGs.addAll(res);
                    updateMsg(MSGs);

                } catch (IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toast("unable to retrieve existing messages.");
                        }
                    });
                }
            }
        }).start();
    }
    public void updateMsg(ArrayList<_message> msgs) {
        ArrayList<HashMap<String,String>> liste = new ArrayList<HashMap<String,String>>();
        HashMap<String,String> elements;
        for (int i = 0 ; i < msgs.size() ; i++) {
            elements = new HashMap<String, String>();
            _message mmsg = msgs.get(i);
            elements.put("author",mmsg.getAuthor());
            elements.put("_content",mmsg._content);
            liste.add(elements);
        }
        ListView vue = findViewById(R.id.msgContainer);
        final ListAdapter adapter = new SimpleAdapter(getApplicationContext(),liste,android.R.layout.simple_list_item_2,new String[] {"author","_content"},new int[] {android.R.id.text1, android.R.id.text2});
        vue.setAdapter(adapter);
        vue.post(new Runnable() {
            @Override
            public void run() {
                ListView vue = findViewById(R.id.msgContainer);
                vue.setSelection(vue.getCount() - 1);
            }
        });
    }
    public void toast(CharSequence s) {
        Toast toast = Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT);
        toast.show();
    }
}
