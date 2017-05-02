package com.example.herchja.teamprojectv2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import static android.widget.AdapterView.OnItemClickListener;
import static com.example.herchja.teamprojectv2.MainActivity.sendmsg;
import static com.example.herchja.teamprojectv2.MainActivity.user;

public class SecondFragment extends Fragment {

    private int numContacts = user.getContactList().size()-1;
    private String contactEdit = "";
    private String cmd = "";
    private int index = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_second, container, false);

        Button logout = (Button) v.findViewById(R.id.logout2);
        logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                MainActivity.alertDialog  = new AlertDialog.Builder(v.getContext());
                MainActivity.alertDialog .setTitle("Logout");
                final TextView input = new TextView(getContext());
                input.setTextSize(18);
                input.setGravity(Gravity.CENTER | Gravity.BOTTOM);

                input.setText("Are you sure you want to logout?");

                MainActivity.alertDialog .setView(input);
                MainActivity.alertDialog .setCancelable(true);
                MainActivity.alertDialog .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
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

        final TextView numberOfContacts = (TextView) v.findViewById(R.id.ContactsCounter);
        numberOfContacts.setText(numContacts + " Contacts");

        TextView tv = (TextView) v.findViewById(R.id.tvFragSecond);
        tv.setText(getArguments().getString("msg"));

        ListView listView = (ListView) v.findViewById(R.id.mainMenu);
        final ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(
                getActivity(),android.R.layout.simple_list_item_1,user.getContactList());
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


                            final EditText name = (EditText) view.findViewById(R.id.eName);
                            String nameUser = name.getText().toString();
                            final EditText id = (EditText) view.findViewById(R.id.eID);
                            String idUser = id.getText().toString();
                            listViewAdapter.add(nameUser);
                            listViewAdapter.notifyDataSetChanged();
                            cmd = "new";
                            contactEdit = nameUser;
                            new contactTask().execute();

                            numContacts = user.getContactList().size() -1;
                            numberOfContacts.setText(numContacts + " Contacts");

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
                    final EditText setName = (EditText) view.findViewById(R.id.viewCname);
                    setName.setText(user.getContactList().get(position));
                    final EditText setId = (EditText) view.findViewById(R.id.viewCid);
                    setId.setText("0");
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
                                user.setContact(setName.getText().toString(), position);
                                cmd = "edit";
                                contactEdit = setName.getText().toString();
                                index = position-1;
                                new contactTask().execute();

                                setName.setFocusable(false);
                                setId.setFocusable(false);
                            }
                        }
                    });

                    alertDialog.setView(view);
                    alertDialog.setCancelable(true);
                    alertDialog.setPositiveButton("Send Message", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            sendmsg(user.getContactList().get(position));
                            MainActivity.pager.setCurrentItem(2,true);
                        }
                    });

                    alertDialog.setNeutralButton("Delete Contact", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            user.remContact(position);
                            cmd = "del";
                            index = position-1;
                            new contactTask().execute();
                            numContacts = user.getContactList().size()-1;
                            numberOfContacts.setText(numContacts + " Contacts");
                            listViewAdapter.notifyDataSetChanged();

                        }
                    });

                    alertDialog.setNegativeButton("Done", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            user.setContact(setName.getText().toString(), position);

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

    public static void sendmsg(String name)
    {
        final EditText sendmsg = (EditText) ThirdFragment.v.findViewById(R.id.editText6);
        sendmsg .setText(name);
        MainActivity.pager.setCurrentItem(2,true);
    }

    class contactTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {

            ArrayList<NameValuePair> nvp = new ArrayList<NameValuePair>();
            nvp.add(new BasicNameValuePair("cmd", cmd));
            nvp.add(new BasicNameValuePair("name", user.getUsername()));
            nvp.add(new BasicNameValuePair("string", contactEdit));
            nvp.add(new BasicNameValuePair("ndx", Integer.toString(index)));
            InputStream is = null;

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://54.148.185.237/addcont.php");
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