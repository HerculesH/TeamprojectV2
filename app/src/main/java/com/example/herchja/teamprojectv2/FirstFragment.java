package com.example.herchja.teamprojectv2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FirstFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_first, container, false);

        final TextView numberOfContacts = (TextView) v.findViewById(R.id.textView4);
        numberOfContacts.setText(MainActivity.msgItems.size() + " Messages");

        TextView tv = (TextView) v.findViewById(R.id.tvFragFirst);
        tv.setText(getArguments().getString("msg"));

        final ListView messageList = (ListView) v.findViewById(R.id.msgList);
        final ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(
                getActivity(),android.R.layout.simple_list_item_1,MainActivity.msgItems);
        messageList.setAdapter(listViewAdapter);

        messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, final long id) {

                if (position == 0) {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                    LayoutInflater factory = LayoutInflater.from(v.getContext());
                    final View view = factory.inflate(R.layout.fragment_msg_viewer, null);

                    alertDialog.setView(view);
                    alertDialog.setCancelable(true);
                    alertDialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            MainActivity.msgItems.remove(0);
                            listViewAdapter.notifyDataSetChanged();
                            numberOfContacts.setText(MainActivity.msgItems.size() + " Messages");

                        }
                    });

                    alertDialog.setNegativeButton("Reply", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
                else if(position == 1)
                {
                    MainActivity.pager.setCurrentItem(0,true);
                    Toast.makeText(getActivity(), "First item", Toast.LENGTH_SHORT).show();
                }
                else if (position == 2)
                {
                    Toast.makeText(getActivity(), "Second item", Toast.LENGTH_SHORT).show();
                }
                else if (position == 3)
                {


                    Toast.makeText(getActivity(), "Third item", Toast.LENGTH_SHORT).show();

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

