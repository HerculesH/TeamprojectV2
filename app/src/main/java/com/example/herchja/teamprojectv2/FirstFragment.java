package com.example.herchja.teamprojectv2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

/**
 * The first fragment which is the recieve message screen of the application
 */
public class FirstFragment extends Fragment {

    private int idMes;
    private Timer autoUpdate;
    public ArrayAdapter<String> listViewAdapter;
    private  ArrayList<String> subjects;

    /**
     * sets a continous update TimerTask which refreshes the messages being sent to the user
     */
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

    /**
     * The method which is being called from the TimerTask
     */
    private void updateLists(){
        subjects.clear();
        for(Message m : user.getMessages()){
            subjects.add(String.format("From  -  %-" + (40 - m.getFrom().length()) +"s %20s", m.getFrom(), m.getTimestamp().substring(0,16)));
        }
        listViewAdapter.notifyDataSetChanged();
    }

    /**
     * Handles the event if the app is being paused
     */
    @Override
    public void onPause() {
        autoUpdate.cancel();
        super.onPause();
    }

    /**
     * Creates the first instance of the fragment
     * @param inflater Makes sure that the view is being displayed correctly
     * @param container Hanles the container which the view is being stored in
     * @param savedInstanceState Is the instance of the fragment
     * @return returns the fragment to the Activity
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View first = inflater.inflate(R.layout.fragment_first, container, false);

        Button logout = (Button) first.findViewById(R.id.logout1);
        logout.setOnClickListener(new View.OnClickListener() {

            /**
             *  Handles the widget when the user clicks on logout
             * @param v returns a alertdialog
             */
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

                        LoginActivity.restart(getContext(),0);

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

        /**
         * Displays the correct number of contacts to the user
         */
        final TextView numberOfContacts = (TextView) first.findViewById(R.id.textView4);
        numberOfContacts.setText(user.messages.size() + " Messages");

        /**
         * Displays the name of the fragment to the user
         */
        TextView tv = (TextView) first.findViewById(R.id.tvFragFirst);
        tv.setText(getArguments().getString("msg"));

        /**
         * Creates a ListView and a ListViewAdapter to store the messages being sent to the user
         */
        final ListView messageList = (ListView) first.findViewById(R.id.msgList);
        subjects = new ArrayList<String>();
        listViewAdapter = new ArrayAdapter<String>(
                getActivity(),android.R.layout.simple_list_item_1,subjects);
        messageList.setAdapter(listViewAdapter);
        listViewAdapter.notifyDataSetChanged();

        messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * Handles the case if someone taps on the listView objects
             * @param parent the parent view
             * @param v the view
             * @param position the position of each object in the view
             * @param id the id of objects
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    final int position, final long id) {

                /**
                 * If a user taps on a position which is not 999999999 in the view the user will have the message displayed to him from the sender
                 */
                if (position != 999999999) {

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
                    /**
                     * A timer which deletes the message after a certain amount of time
                     */
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

                    /**
                     * Handles the Done or reply button event by the user and deletes the message from the ListView and either takes the user to the ThirdFragment to reply or nothing at all
                     */
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

    /**
     * Names the Firstfragment of the user
     * @param text name of fragment
     * @return returns the fragment name
     */
    public static FirstFragment newInstance(String text) {

        FirstFragment f = new FirstFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }

    /**
     * Handles user information in the backend like updating messages
     */
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

