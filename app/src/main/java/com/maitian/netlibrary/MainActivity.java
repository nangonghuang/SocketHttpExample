package com.maitian.netlibrary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private HttpManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);

        manager = new HttpManager();

    }


    String url = "https://github.com/square/okhttp/blob/master/okhttp-logging-interceptor/README.md";
    String url2 = "http://www.hnu.edu.cn";
    String url3 = "http://www.baidu.com";
    String url4 = "https://www.baidu.com";
    String url5 = "https://www.github.com";
    String url6 = "https://github.com/";
    String url7 = "https://tieba.baidu.com/index.html";
    String url8 = "https://tieba.baidu.com/f?kw=阿森纳&fr=index";
    //    https://tieba.baidu.com:443/f?kw=%25E9%2598%25BF%25E6%25A3%25AE%25E7%25BA%25B3&fr=index
    String url9 = "https://tieba.baidu.com/p/5616078497?pn=2";
    String url10 = "https://github.com/nangonghuang/test/blob/master/Readme.md";
    String url11 = "https://avatars1.githubusercontent.com/u/13362002?s=400&u=f334344ba16774d46b7774557cbfd8f23914aa32&v=4";
    String URL = url10;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                manager.connectGithub(URL);
                break;
            case R.id.button2:
                okhttpconnect();
                break;
            case R.id.button3:
                break;
        }
    }


    public void okhttpconnect() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        Request request = new Request.Builder()
                .url(URL)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                System.out.println(response.body().string());
            }
        });
    }
}
