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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.apache.commons.codec.binary.Base64;
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
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;

import javax.crypto.Cipher;
import static com.example.herchja.teamprojectv2.MainActivity.user;

/**
 * This class handles most, if not all, things related to the sending message window.
 */
public class ThirdFragment extends Fragment{

    private Button sendmsg;
    private ToggleButton ToggleTimer;
    private EditText Timerbox;
    private EditText msg;
    private EditText timer;
    public static EditText sendto;
    public String idTo, idFrom, message, key, timerm;
    public static View v;

    // initialize the view on creation
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_third, container, false);

        // handle logout functions
        Button logout = (Button) v.findViewById(R.id.logout3);
        logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                MainActivity.alertDialog = new AlertDialog.Builder(v.getContext());
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

        // get each of the boxes for all of the fields in the send messages screen.
        sendto = (EditText) v.findViewById(R.id.editText6);
        timer = (EditText) v.findViewById(R.id.editText5);
        msg = (EditText) v.findViewById(R.id.MsgBox);
        Timerbox = (EditText) v.findViewById(R.id.editText5);
        sendmsg = (Button) v.findViewById(R.id.button);

        // create listener for the sending messages button
        sendmsg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // get all of the fields from the send messages window.
                timerm = timer.getText().toString();
                idTo = sendto.getText().toString();
                idFrom = user.getUsername();
                message = msg.getText().toString();

                // create a task to send the message over to the database.
                key = null;
                new myAsyncTask().execute();

                // create a successfully message after the execution of the message sending
                Toast.makeText(getActivity(), "Message has been sent!", Toast.LENGTH_SHORT).show();
                sendto.setText("");
                msg.setText("");

            }
        });

        // creating timer box for message timer
        Timerbox.setFocusable(false);
        ToggleTimer = (ToggleButton) v.findViewById(R.id.toggleButton);

        // handle the listener
        ToggleTimer.setOnClickListener(new View.OnClickListener() {

            // change true/false for the timer
            @Override
            public void onClick(View view) {
                if(ToggleTimer.isChecked()) {
                    Timerbox.setFocusableInTouchMode(true);
                } else {
                    ToggleTimer.setFocusable(false);
                }
            }
        });

        TextView tv = (TextView) v.findViewById(R.id.tvFragThird);
        tv.setText(getArguments().getString("msg"));
        return v;
    }

    /**
     * Task for sending a message over to the database
     */
    class myAsyncTask extends AsyncTask<Void, Void, Void>  {

        /**
         * This will be running in the background when the send message button is clicked.
         * @param params
         * @return
         */
        protected Void doInBackground(Void... params){
            ArrayList<NameValuePair> nvp = new ArrayList<NameValuePair>();
            byte[] encoded = null;
            byte[] encrypted = null;

            try { // handle getting the private key from the database
                // get the private key from the server
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://54.148.185.237/readPriv.php");
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();

                // read the inputstream from the phpscript and get the private key
                InputStream is = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null)
                    sb.append(line + "\n");
                is.close();
                String result = sb.toString();
                String privateKey = result.replaceAll("privateKey", "");

                // generate the encoding of the private key with the message.
                // please kill me this is disgustingly hard
                byte[] privateBytes = Base64.decodeBase64(privateKey.getBytes());
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PrivateKey privKey = keyFactory.generatePrivate(keySpec);
                encrypted = encrypt(privKey, message);
                encoded = Base64.encodeBase64(encrypted);
            } catch (Exception e) {
               System.out.println("Error encoding message: " + e.getMessage());
            }

            // store the values into a arraylist hashmap.
            nvp.add(new BasicNameValuePair("toid", idTo));
            nvp.add(new BasicNameValuePair("fromid", idFrom));
            nvp.add(new BasicNameValuePair("text", new String(encoded)));
            nvp.add(new BasicNameValuePair("salt", key));
            nvp.add(new BasicNameValuePair("timer", timerm));

            try {
                // create a connection with the server and send the message
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://54.148.185.237/sendMessages.php");
                httppost.setEntity(new UrlEncodedFormEntity(nvp));
                HttpResponse response = httpclient.execute(httppost);
            } catch (Exception e) {
                System.out.println("Error in getting messages: " + e.getMessage());

            }
            return null;
        }

        /**
         * Encrypt the messaage
         * @param privateKey
         * @param message
         * @return
         * @throws Exception
         */
        public byte[] encrypt(PrivateKey privateKey, String message) throws Exception {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return cipher.doFinal(message.getBytes());
        }
    }

    /**
     * Sets the message name? Not too sure tbh.
     * @param name
     */
    public static void setMsg(String name)
    {
        sendto.setText(name);
    }

    /**
     * Kind of like an initializer, creates a new instance of itself and returns it.
     * @param text
     * @return
     */
    public static ThirdFragment newInstance(String text) {
        ThirdFragment f = new ThirdFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);
        return f;
    }
}