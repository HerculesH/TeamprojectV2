package com.example.herchja.teamprojectv2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.herchja.teamprojectv2.MainActivity.user;

public class FirstFragment extends Fragment {

    public AlertDialog.Builder alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_first, container, false);

        final TextView numberOfContacts = (TextView) v.findViewById(R.id.textView4);
        numberOfContacts.setText(user.messages.size() + " Messages");

        TextView tv = (TextView) v.findViewById(R.id.tvFragFirst);
        tv.setText(getArguments().getString("msg"));

        if(user.messages.isEmpty() != true)
        {
            Toast.makeText(getActivity(), "New message(s)!", Toast.LENGTH_SHORT).show();
        }

        final ListView messageList = (ListView) v.findViewById(R.id.msgList);
        final ArrayList<String> subjects = new ArrayList<String>();
        for(Message m : user.getMessages()){
            subjects.add(m.getFrom());
        }
        final ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(
                getActivity(),android.R.layout.simple_list_item_1,subjects);
        messageList.setAdapter(listViewAdapter);

        messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    final int position, final long id) {

                if (position != 99) {

                    alertDialog = new AlertDialog.Builder(v.getContext());
                    LayoutInflater factory = LayoutInflater.from(v.getContext());
                    final View view = factory.inflate(R.layout.fragment_msg_viewer, null);

                    final TextView cd = (TextView) view.findViewById(R.id.cdTimer);
                    //text of message
                    final TextView txt = (TextView) view.findViewById(R.id.MsgText);
                    txt.setText(user.getMessages().get(position).getText());

                    new CountDownTimer(10000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            cd.setText("seconds remaining: " + millisUntilFinished / 1000);
                        }

                        public void onFinish() {

                                cd.setText("Message erased");
                                txt.setText("");
                                listViewAdapter.notifyDataSetChanged();

                        }
                    }.start();


                    alertDialog.setView(view);
                    alertDialog.setCancelable(true);
                    alertDialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            user.remMessage(position);
                            subjects.remove(position);
                            listViewAdapter.notifyDataSetChanged();
                            numberOfContacts.setText(user.getMessages().size() + " Messages");

                        }
                    });

                    alertDialog.setNegativeButton("Reply", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }


            }
        });

        return v;
    }

    public static FirstFragment newInstance(String text) {

        FirstFragment f = new FirstFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
}

