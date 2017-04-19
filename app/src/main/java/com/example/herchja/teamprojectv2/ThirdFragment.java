package com.example.herchja.teamprojectv2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.w3c.dom.Text;


public class ThirdFragment extends Fragment {

    private Button sendmsg;
    private ToggleButton encrypt;
    private EditText encryptbox;
    private EditText msg;
    private TextView msgview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_third, container, false);

        msg = (EditText) v.findViewById(R.id.editText3);
        msgview = (TextView) v.findViewById(R.id.textView5);
        encryptbox = (EditText) v.findViewById(R.id.editText5);

        sendmsg = (Button) v.findViewById(R.id.button);
        sendmsg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                msgview.append(msg.getText() + System.getProperty("line.separator"));
            }
        });

        encryptbox.setFocusable(false);

        encrypt = (ToggleButton) v.findViewById(R.id.toggleButton);
        encrypt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(encrypt.isChecked())
                {
                    encryptbox.setFocusableInTouchMode(true);
                }
                else
                {
                    encryptbox.setFocusable(false);
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