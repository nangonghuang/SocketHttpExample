package com.xckoohttp.net;

import java.util.HashMap;
import java.util.Map;

public class Request {
    final HttpUrl httpUrl;
    final String method;
    String body;
    Map<String, String> headers;
    String startLine;
    static final String GET = "GET";
    static final String POST = "POST";

    Request(Builder builder) {
        method = builder.method;
        httpUrl = builder.httpUrl;
        body = builder.body;
        startLine = getStartLine();
        headers = new HashMap<>();

        addHeader("Accept", "*/*");
        addHeader("Charset", "UTF-8");
        addHeader("Accept-Language", "zh-cn");
        addHeader("Host", httpUrl.host);
    }

    public boolean hasBody() {
        return method.equals(POST) && body != null && body.length() != 0;
    }

    String getStartLine() {
        StringBuilder builder = new StringBuilder();
        if (method.equalsIgnoreCase(Request.GET)) {
            builder.append("GET ");
            builder.append("/");
            if (httpUrl.path == null || httpUrl.path.length() == 0) {

            } else {
                builder.append(httpUrl.path);
            }

            if (httpUrl.hasQuery()) {
                builder.append("?");
                builder.append(httpUrl.getQuerys());
            }
        } else if (method.equalsIgnoreCase(Request.POST)) {
            builder.append("POST ");
            builder.append("/");
            if (httpUrl.path == null || httpUrl.path.length() == 0) {

            } else {
                builder.append(httpUrl.path);
            }
        }
        builder.append(" HTTP/1.1\r\n");
        return builder.toString();
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public String getHttpHeader() {
        StringBuilder sb = new StringBuilder(getStartLine());
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey());
            sb.append(":");
            sb.append(entry.getValue());
            sb.append("\r\n");
        }
        sb.append("\r\n");
        if (method.equalsIgnoreCase(Request.POST)) {
            if (httpUrl.hasQuery()) {
                sb.append(httpUrl.getQuerys());
            }
        }
        System.out.println(sb.toString());
        return sb.toString();
    }

    public static class Builder {
        String method;
        HttpUrl httpUrl;
        String body;

        public Builder post() {
            this.method = POST;
            return this;
        }

        public Builder get() {
            this.method = GET;
            return this;
        }

        public Builder url(HttpUrl url) {
            this.httpUrl = url;
            return this;
        }

        public Builder url(String url) {
            this.httpUrl = HttpUrl.parse(url);
            return this;
        }

        public Request build() {
            return new Request(this);
        }
    }
}
