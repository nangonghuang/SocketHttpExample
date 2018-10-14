package com.alan.net;

public class Client {
    int connectTimeout;
    int readTimeout;
    PlatForm platForm;
    ThreadPool processer;
    Connections connections;

    public Client(Builder builder) {
        this.connectTimeout = builder.connectTimeout;
        this.readTimeout = builder.readTimeout;
        this.platForm = builder.platForm;
        processer = new ThreadPool();
        connections = new Connections();
    }

    public Call newCall(Request request) {
        return new Call(this, request);
    }

    public static class Builder {
        int connectTimeout;
        int readTimeout;
        PlatForm platForm;

        public Builder setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder setPlatform(PlatForm platForm) {
            this.platForm = platForm;
            return this;
        }

        public Client build() {
            return new Client(this);
        }
    }
}
