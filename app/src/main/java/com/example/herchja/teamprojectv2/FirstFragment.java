package com.example.herchja.teamprojectv2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.Timer;
import java.util.TimerTask;

import static com.example.herchja.teamprojectv2.MainActivity.user;

public class FirstFragment extends Fragment {

    private int idMes;
    private Timer autoUpdate;
    public ArrayAdapter<String> listViewAdapter;
    private  ArrayList<String> subjects;

    @Override
    public void onResume() {
        super.onResume();
        autoUpdate = new Timer();
        autoUpdate.schedule(new TimerTask() {

            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        updateLists();
                    }
                });
            }
        }, 0, 15000); // updates each 40 secs
    }

    private void updateLists(){
        subjects.clear();
        for(Message m : user.getMessages()){
            subjects.add(String.format("From  -  %-" + (40 - m.getFrom().length()) +"s %20s", m.getFrom(), m.getTimestamp().substring(0,16)));
        }
        listViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        autoUpdate.cancel();
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View first = inflater.inflate(R.layout.fragment_first, container, false);

        Button logout = (Button) first.findViewById(R.id.logout1);
        logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                MainActivity.alertDialog  = new AlertDialog.Builder(v.getContext());
                MainActivity.alertDialog .setTitle("Logout");
                final TextView input = new TextView(getContext());
                input.setTextSize(18);
                input.setGravity(Gravity.CENTER | Gravity.BOTTOM);

                input.setText("Are you sure you want to logout?");

                MainActivity.alertDialog.setView(input);
                MainActivity.alertDialog.setCancelable(true);
                MainActivity.alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);

                    }
                });

                MainActivity.alertDialog .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
                MainActivity.alertDialog .show();

            }
        });

        final TextView numberOfContacts = (TextView) first.findViewById(R.id.textView4);
        numberOfContacts.setText(user.messages.size() + " Messages");

        TextView tv = (TextView) first.findViewById(R.id.tvFragFirst);
        tv.setText(getArguments().getString("msg"));

        final ListView messageList = (ListView) first.findViewById(R.id.msgList);
        subjects = new ArrayList<String>();
        listViewAdapter = new ArrayAdapter<String>(
                getActivity(),android.R.layout.simple_list_item_1,subjects);
        messageList.setAdapter(listViewAdapter);
        listViewAdapter.notifyDataSetChanged();

        messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    final int position, final long id) {

                if (position != 99) {

                    MainActivity.alertDialog = new AlertDialog.Builder(v.getContext());
                    LayoutInflater factory = LayoutInflater.from(v.getContext());
                    final View view = factory.inflate(R.layout.fragment_msg_viewer, null);

                    final TextView cd = (TextView) view.findViewById(R.id.cdTimer);
                    //text of message
                    final TextView txt = (TextView) view.findViewById(R.id.MsgText);
                    txt.setText(user.getMessages().get(position).getText());
                    int countdown = 10000;
                    if(user.getMessages().get(position).getTimer() != 0){
                        countdown = 1000 * (user.getMessages().get(position).getTimer());
                    }
                    new CountDownTimer(countdown,1000) {

                        public void onTick(long millisUntilFinished) {
                            cd.setText("seconds remaining: " + millisUntilFinished / 1000);
                        }

                        public void onFinish() {

                                cd.setText("Message erased");
                                txt.setText("");
                            listViewAdapter.notifyDataSetChanged();

                        }
                    }.start();


                    MainActivity.alertDialog.setView(view);
                    MainActivity.alertDialog.setCancelable(true);
                    MainActivity.alertDialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            idMes = user.getMessages().get(position).getId();
                            user.remMessage(position);
                            subjects.remove(position);
                            new delTask().execute();
                            listViewAdapter.notifyDataSetChanged();
                            numberOfContacts.setText(user.getMessages().size() + " Messages");

                        }
                    });

                    MainActivity.alertDialog.setNegativeButton("Reply", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            SecondFragment.sendmsg(user.getMessages().get(position).getFrom());
                            ThirdFragment.setMsg(user.getMessages().get(position).getFrom());

                            idMes = user.getMessages().get(position).getId();
                            user.remMessage(position);
                            subjects.remove(position);
                            new delTask().execute();
                            listViewAdapter.notifyDataSetChanged();
                            numberOfContacts.setText(user.getMessages().size() + " Messages");

                        }
                    });
                    MainActivity.alertDialog.show();
                }
            }
        });

        return first;
    }

    public static FirstFragment newInstance(String text) {

        FirstFragment f = new FirstFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
    class delTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {

            ArrayList<NameValuePair> nvp = new ArrayList<NameValuePair>();
            nvp.add(new BasicNameValuePair("id", Integer.toString(idMes)));
            InputStream is = null;

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://54.148.185.237/delmessage.php");
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
}

