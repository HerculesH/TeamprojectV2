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

import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

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
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;

import javax.crypto.Cipher;

import static com.example.herchja.teamprojectv2.MainActivity.user;


public class ThirdFragment extends Fragment{

    private Button sendmsg;
    private ToggleButton ToggleTimer;
    private EditText Timerbox;
    private EditText msg;
    private EditText timer;
    public static EditText sendto;
    public String idTo, idFrom, message, key, timerm;
    public static View v;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_third, container, false);


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

        sendto = (EditText) v.findViewById(R.id.editText6);
        timer = (EditText) v.findViewById(R.id.editText5);
        msg = (EditText) v.findViewById(R.id.MsgBox);
        Timerbox = (EditText) v.findViewById(R.id.editText5);


        sendmsg = (Button) v.findViewById(R.id.button);
        sendmsg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //send message implementation
                timerm = timer.getText().toString();
                idTo = sendto.getText().toString();
                idFrom = user.getUsername();
                message = msg.getText().toString();

                key = null;
                new myAsyncTask().execute();

                Toast.makeText(getActivity(), "Message has been sent!", Toast.LENGTH_SHORT).show();
                sendto.setText("");
                msg.setText("");

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


    class myAsyncTask extends AsyncTask<Void, Void, Void>  {

        protected Void doInBackground(Void... params){

            ArrayList<Message> msg = new ArrayList<Message>();
            ArrayList<NameValuePair> nvp = new ArrayList<NameValuePair>();
            byte[] encoded = null;
            byte[] encrypted = null;
            try {
                // get the private key from the server
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://54.148.185.237/readPriv.php");
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
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

            nvp.add(new BasicNameValuePair("toid", idTo));
            nvp.add(new BasicNameValuePair("fromid", idFrom));
            nvp.add(new BasicNameValuePair("text", new String(encoded)));
            nvp.add(new BasicNameValuePair("salt", key));
            nvp.add(new BasicNameValuePair("timer", timerm));
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

    public static void setMsg(String name)
    {
        sendto.setText(name);
    }

    public static ThirdFragment newInstance(String text) {


        ThirdFragment f = new ThirdFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }

}