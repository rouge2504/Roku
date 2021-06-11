package com.labs206.rokuremote.Utils;

import android.os.AsyncTask;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class RokuControl {

    private static volatile RokuControl instance;

    public static RokuControl getInstance() {
        RokuControl localInstance = instance;
        if (localInstance == null) {
            synchronized (RokuControl.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new RokuControl();
                }
            }
        }
        return localInstance;
    }

    public void sendCommandHTTP(String ip, String command) {
        new CallAPI().execute("http://" + ip + ":8060/keypress/" + command);
    }

    public void sendCommandLitHTTP(String ip, String command) {
        try {
            command = URLEncoder.encode(command, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        new CallAPI().execute("http://" + ip + ":8060/keypress/Lit_" + command);
    }

    public void sendCommandPressHTTP(String ip, String command) {
        new CallAPI().execute("http://" + ip + ":8060/keydown/" + command);
    }

    public void sendCommandReleaseHTTP(String ip, String command) {
        new CallAPI().execute("http://" + ip + ":8060/keyup/" + command);
    }

    public void launchApp(String ip, String appId) {
        new CallAPI().execute("http://" + ip + ":8060/launch/" + appId);
    }

    public class CallAPI extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            String urlString = params[0];
            OutputStream out = null;
            try {
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.getInputStream().close();
                urlConnection.connect();
            } catch (Exception e) {

            } finally {
                try {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                } catch (Exception ex) {
                }
            }
            return "";
        }
    }

}
