package com.maitian.netlibrary;

import android.util.Log;

import com.alan.net.Call;
import com.alan.net.Callback;
import com.alan.net.Client;
import com.alan.net.Request;
import com.alan.net.Response;

public class HttpManager {
    private final int connectTimeout = 15000;
    private final int readTimeout = 15000;
    private Client client;

    public HttpManager() {
        client = new Client.Builder()
                .setConnectTimeout(connectTimeout)
                .setReadTimeout(readTimeout)
                .setPlatform(new AndroidPlatform())
                .build();
    }

    public Response execRequest(Request request) {
        return client.newCall(request).exexuteOnCurrentThread();
    }

    private static final String TAG = "HttpManager";

    public void connectGithub(String url) {
        Request request = new Request.Builder().url(url).get().build();
        client.newCall(request).exexute(new Callback() {
            @Override
            public void onFailure(Call call, String errorMsg) {
                Log.w(TAG, "onFailure errorMsg = " + errorMsg);
            }

            @Override
            public void onResponse(Call call, Response response) {
                Log.w(TAG, "useHttpURLConnection " + Thread.currentThread().getId() + "\r\n  onResponse response2 = " + response.toString());
            }
        });

    }
}
