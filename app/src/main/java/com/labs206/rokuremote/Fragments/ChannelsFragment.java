package com.labs206.rokuremote.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.labs206.rokuremote.Adapters.ChannelsAdapter;
import com.labs206.rokuremote.Models.ChannelModel;
import com.labs206.rokuremote.PremActivity;
import com.labs206.rokuremote.R;
import com.labs206.rokuremote.Utils.AdsManager;
import com.labs206.rokuremote.Utils.AppPreferences;
import com.labs206.rokuremote.Utils.ItemClickSupport;
import com.labs206.rokuremote.Utils.RokuControl;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.labs206.rokuremote.Utils.ScreenChecker.screenSize;

public class ChannelsFragment extends Fragment {

    private static final String TAG = "ChannelsFragment";

    public ChannelsFragment() {
    }

    public static ChannelsFragment newInstance() {
        return new ChannelsFragment();
    }

    private ChannelsAdapter adapter;
    private RecyclerView recyclerView;
    private GridLayoutManager manager;
    private ArrayList<ChannelModel> channels = new ArrayList<>();
    private Context mContext;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_channels, container, false);
        recyclerView = root.findViewById(R.id.recycler_channels);
        int count = 2;
        if (screenSize(getActivity()) > 6.7) {
            count = 3;
        }
        manager = new GridLayoutManager(getContext(), count);
        recyclerView.setLayoutManager(manager);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                if (AdsManager.getInstance().isPremium(getActivity())) {
                    launchApp(channels.get(position).appId);
                } else {
                    startActivity(new Intent(getActivity(), PremActivity.class));
                }
            }
        });
        loadApps();
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    public void launchApp(String appId) {
        String deviceIp = AppPreferences.getInstance(getContext()).getString("deviceIp");
        if (deviceIp != null && deviceIp.length() > 0) {
            RokuControl.getInstance().launchApp(deviceIp, appId);
        }
    }

    public void loadApps() {
        String deviceIp = AppPreferences.getInstance(getContext()).getString("deviceIp");
        if (deviceIp != null && deviceIp.length() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                OkHttpClient client = new OkHttpClient();
                Request.Builder formBody = new Request.Builder().url("http://" + deviceIp + ":8060/query/apps");
                client.newCall(formBody.build()).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {}
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        ResponseBody responseBody = response.body();
                        if (response.isSuccessful()) {
                            parseXML(responseBody.string());
                        }
                    }
                });
            }else {
                new requestTask().execute("http://" + deviceIp + ":8060/query/apps");
            }
        } else {
            Toast.makeText(this.getContext(), "No Roku Device Connected", Toast.LENGTH_LONG).show();
        }
    }

    class requestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            HttpURLConnection urlConnection = null;
            String urlString = uri[0]; // URL to call
            OutputStream out = null;
            try {
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
            } catch (Exception e) {}
            try {
                BufferedReader br = null;
                if (urlConnection.getResponseCode() == 200) {
                    br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String strCurrentLine;
                    String response = "";
                    while ((strCurrentLine = br.readLine()) != null) {
                        response+=strCurrentLine;
                    }
                    if(urlConnection != null)
                        urlConnection.disconnect();
                    return response;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.length() > 0) {
                parseXML(result);
            }
        }
    }

    public void parseXML(String xml) {
        channels = new ArrayList<>();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xml));
            while (xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xmlPullParser.getEventType()) {
                    case XmlPullParser.START_DOCUMENT: {
                        break;
                    }
                    case XmlPullParser.START_TAG: {
                        if (xmlPullParser.getName().equals("app")) {
                            ChannelModel channelModel = new ChannelModel();
                            String id = xmlPullParser.getAttributeValue(null, "id");
                            String name = xmlPullParser.nextText();
                            channelModel.appId = id;
                            channelModel.appName = name;
                            channels.add(channelModel);
                        }
                        break;
                    }
                    case XmlPullParser.END_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        break;
                    default:
                        break;
                }
                xmlPullParser.next();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if(mContext != null) {
            Activity activity = (Activity) mContext;
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    adapter = new ChannelsAdapter(getActivity(), channels);
                    recyclerView.setAdapter(adapter);
                }
            });
        }
    }
}