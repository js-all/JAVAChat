package pouloulou_inc.zimbawejava;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

public class logActivity extends Activity {
    Boolean theme;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log);
        Button sub = findViewById(R.id.subP);
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView ps = findViewById(R.id.ps);
                String p = ps.getText().toString();
                if (Pattern.matches("(?<! )[a-zA-Z0-9 .\\-\\[\\]+=/#!?]{3,20}",p)) {
                    if (p != "unknown user") {
                        callChat(ps.getText().toString());
                    }
                    else {
                        toast("veuiler choisire un pseudo qui n'est pas \"unknown user\"");
                    }
                }
                else if(p.length() == 0)  {
                    callChat("unknown user");
                }
                else {
                    toast("veuiller entré un pseudo valide, character autoriser : de a à z,de A à Z,de 0 à 9,-,[,],=,+,/,#,?,!. De longueure entre 3 et 20.");
                }
            }
        });
        Switch th = findViewById(R.id.th);
        th.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Switch th = findViewById(R.id.th);
                if (isChecked) {
                    th.setText(R.string.thDark);
                    findViewById(R.id.logL).setBackgroundResource(R.drawable.theme_dark);
                    theme = false;
                }
                else {
                    th.setText(R.string.thLight);
                    findViewById(R.id.logL).setBackgroundResource(R.drawable.theme_light);
                    theme = true;
                }
            }
        });
    }
    public void CBGC(int c) {
        findViewById(R.id.logL).setBackgroundColor(c);
    }
    public void toast(CharSequence s) {
        Toast toast = Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG);
        toast.show();
    }
    public void callChat(String ps) {
        Intent intent = new Intent(getApplicationContext(), chatAtivity.class);
        intent.putExtra("pseudo", ps);
        intent.putExtra("theme",theme);
        startActivity(intent);
    }

}
