package com.example.herchja.teamprojectv2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import static com.example.herchja.teamprojectv2.MainActivity.user;


public class ThirdFragment extends Fragment {

    private Button sendmsg;
    private ToggleButton ToggleTimer;
    private EditText Timerbox;
    private EditText msg;
    private EditText subject;
    public static View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_third, container, false);

        subject = (EditText) v.findViewById(R.id.SubBox);
        msg = (EditText) v.findViewById(R.id.MsgBox);
        Timerbox = (EditText) v.findViewById(R.id.editText5);

        sendmsg = (Button) v.findViewById(R.id.button);
        sendmsg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //send message implementation
                DatabaseHandler.sendMessage("jane", user.getUsername(), "text", null);
                msg = (EditText) v.findViewById(R.id.MsgBox);
                String messa = msg.getText().toString();
                subject.setText("");

            }
        });

        Timerbox.setFocusable(false);

        ToggleTimer = (ToggleButton) v.findViewById(R.id.toggleButton);
        ToggleTimer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(ToggleTimer.isChecked())
                {
                    Timerbox.setFocusableInTouchMode(true);
                }
                else
                {
                    ToggleTimer.setFocusable(false);
                }
            }
        });

        TextView tv = (TextView) v.findViewById(R.id.tvFragThird);
        tv.setText(getArguments().getString("msg"));

        return v;
    }

    public static ThirdFragment newInstance(String text) {

        ThirdFragment f = new ThirdFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }

}