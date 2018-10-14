package com.alan.net;


import java.io.IOException;

public interface HttpCallback {

    void onFailure(Call call, IOException e);


    void onResponse(Call call, Response response) throws IOException;
}
