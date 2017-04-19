package com.example.herchja.teamprojectv2;

import android.net.Uri;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import java.util.Collection;
import java.util.Collections;

import static android.widget.AdapterView.*;

public class SecondFragment extends Fragment {

    int numContacts = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_second, container, false);

        final TextView numberOfContacts = (TextView) v.findViewById(R.id.ContactsCounter);
        numberOfContacts.setText(numContacts + " Contacts");

        TextView tv = (TextView) v.findViewById(R.id.tvFragSecond);
        tv.setText(getArguments().getString("msg"));

        ListView listView = (ListView) v.findViewById(R.id.mainMenu);
        final ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(
                getActivity(),android.R.layout.simple_list_item_1,MainActivity.listItems);
        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, final long id) {

                if (position == 0) {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                    LayoutInflater factory = LayoutInflater.from(v.getContext());
                    final View view = factory.inflate(R.layout.fragment_create_contact, null);

                    alertDialog.setView(view);
                    alertDialog.setCancelable(true);
                    alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            numContacts++;
                            numberOfContacts.setText(numContacts + " Contacts");
                            final EditText name = (EditText) view.findViewById(R.id.eName);
                            String id = name.getText().toString();
                            listViewAdapter.add(id);
                        }
                    });

                    alertDialog.setNegativeButton("Done", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
                else if(position == 1)
                {
                    MainActivity.pager.setCurrentItem(0,true);
                    Toast.makeText(getActivity(), "New message!", Toast.LENGTH_SHORT).show();
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

    public static SecondFragment newInstance(String text) {

        SecondFragment f = new SecondFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
}