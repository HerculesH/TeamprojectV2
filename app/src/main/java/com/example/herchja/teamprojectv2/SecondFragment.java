package com.example.herchja.teamprojectv2;

import android.content.SharedPreferences;
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
import android.widget.ToggleButton;

import java.util.Collection;
import java.util.Collections;

import static android.content.Context.MODE_PRIVATE;
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
        final ArrayAdapter<User> listViewAdapter = new ArrayAdapter<User>(
                getActivity(),android.R.layout.simple_list_item_1,MainActivity.listItems);
        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    final int position, final long id) {

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
                            String nameUser = name.getText().toString();
                            final EditText id = (EditText) view.findViewById(R.id.eID);
                            String idUser = id.getText().toString();
                            MainActivity.eUser = new User(nameUser,idUser);
                            listViewAdapter.add(MainActivity.eUser);
                            MainActivity.pref.saveArray(MainActivity.listItems,MainActivity.mPrefs);

                        }
                    });

                    alertDialog.setNegativeButton("Done", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
                else if(position != 0)
                {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                    LayoutInflater factory = LayoutInflater.from(v.getContext());
                    final View view = factory.inflate(R.layout.fragment_detailed_contact, null);

                    MainActivity.userChooser = position;
                    final User grab = MainActivity.listItems.get(MainActivity.userChooser);
                    final EditText setName = (EditText) view.findViewById(R.id.viewCname);
                    setName.setText(grab.getUsername());
                    final EditText setId = (EditText) view.findViewById(R.id.viewCid);
                    setId.setText(grab.getId());
                    setName.setFocusable(false);
                    setId.setFocusable(false);

                    final ToggleButton edit = (ToggleButton) view.findViewById(R.id.toggleButton2);
                    edit.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            if(edit.isChecked())
                            {
                                setName.setFocusableInTouchMode(true);
                                setId.setFocusableInTouchMode(true);
                            }
                            else
                            {
                                setName.setFocusable(false);
                                setId.setFocusable(false);
                            }
                        }
                    });

                    alertDialog.setView(view);
                    alertDialog.setCancelable(true);
                    alertDialog.setPositiveButton("Send Message", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            final EditText transfer = (EditText) ThirdFragment.v.findViewById(R.id.editText6);
                            transfer.setText(grab.getId());
                            MainActivity.pager.setCurrentItem(2,true);
                        }
                    });

                    alertDialog.setNeutralButton("Delete Contact", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.listItems.remove(position);
                            numContacts--;
                            numberOfContacts.setText(numContacts + " Contacts");
                            listViewAdapter.notifyDataSetChanged();

                        }
                    });

                    alertDialog.setNegativeButton("Done", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.listItems.get(MainActivity.userChooser).setUsername(setName.getText().toString());
                            MainActivity.listItems.get(MainActivity.userChooser).setId(setId.getText().toString());
                            listViewAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();

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