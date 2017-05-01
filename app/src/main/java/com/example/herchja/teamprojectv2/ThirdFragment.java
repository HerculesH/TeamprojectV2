package com.example.herchja.teamprojectv2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.example.herchja.teamprojectv2.MainActivity.user;


public class ThirdFragment extends Fragment {

    private Button sendmsg;
    private ToggleButton ToggleTimer;
    private EditText Timerbox;
    private EditText msg;
    private EditText subject;
    private EditText sendto;
    public static View v;
    public String idTo, idFrom, message, key;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_third, container, false);

        subject = (EditText) v.findViewById(R.id.SubBox);
        sendto = (EditText) v.findViewById(R.id.editText6);
        msg = (EditText) v.findViewById(R.id.MsgBox);
        Timerbox = (EditText) v.findViewById(R.id.editText5);

        sendmsg = (Button) v.findViewById(R.id.button);
        sendmsg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //send message implementation
                idTo = sendto.getText().toString();
                idFrom = user.getUsername();
                message = msg.getText().toString();
                key = null;
                new myAsyncTask().execute();

                Toast.makeText(getActivity(), "Message has been sent!", Toast.LENGTH_SHORT).show();
                sendto.setText("");
                msg.setText("");
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


    class myAsyncTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {

            ArrayList<Message> msg = new ArrayList<Message>();
            ArrayList<NameValuePair> nvp = new ArrayList<NameValuePair>();
            nvp.add(new BasicNameValuePair("toid", idTo));
            nvp.add(new BasicNameValuePair("fromid", idFrom));
            nvp.add(new BasicNameValuePair("text", message));
            nvp.add(new BasicNameValuePair("salt", key));
            InputStream is = null;

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://54.148.185.237/sendMessages.php");
                httppost.setEntity(new UrlEncodedFormEntity(nvp));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null)
                    sb.append(line + "\n");

                is.close();
                String result = sb.toString();


            } catch (Exception e) {
                System.out.println("Error in getting messages: " + e.getMessage());

            }
            return null;
        }
    }


    public static ThirdFragment newInstance(String text) {

        ThirdFragment f = new ThirdFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }

}