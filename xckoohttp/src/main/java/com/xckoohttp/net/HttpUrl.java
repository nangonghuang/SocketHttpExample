package com.xckoohttp.net;


import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


/**
 * url format:
 * <schema>://<user>:<password>@<host>[:port]/<path>[;params][?query]#<frag>
 * <p>
 * <frag>：片段，比如B站的p2 ： www.bilibili.com/av100000#p2
 * <p>
 * get请求时，包含<query>#<frag>
 * post请求时，不包含<query>#<frag>
 */
public class HttpUrl {
    String schema;
    String host;
    int port;
    String path;
    List<String> queryNamesAndValues;
    String url;

    public HttpUrl(Builder builder) {
        this.schema = builder.schema;
        this.host = builder.host;
        this.port = builder.port;
        this.path = builder.path;
        this.queryNamesAndValues = builder.queryNamesAndValues;
        if (hasQuery()) {
            url = getUrlStringNoQuery() + "?" + getQuerys();
        } else {
            url = getUrlStringNoQuery();
        }

    }

    private static final String TAG = "HttpUrl";

    public URL getFullUrl() {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUrlStringNoQuery() {
        StringBuilder builder = new StringBuilder(schema);
        builder.append("://").append(host);
        if (port != 0) {
            builder.append(":").append(port);
        }
        if (path != null) {
            builder.append("/").append(path);
        }
        return builder.toString();
    }


    public static HttpUrl parse(String url) {
        Builder builder = new Builder();
        return builder.parse(url).build();
    }

    public static int defaultPort(String scheme) {
        if (scheme.equals("http")) {
            return 80;
        } else if (scheme.equals("https")) {
            return 443;
        } else {
            return -1;
        }
    }

    public boolean hasQuery() {
        return queryNamesAndValues != null && queryNamesAndValues.size() != 0;
    }

    static List<String> queryStringToNamesAndValues(String query) {
        List<String> result = new ArrayList<>();
        for (int pos = 0; pos <= query.length(); ) {
            int ampersandOffset = query.indexOf('&', pos);
            if (ampersandOffset == -1) ampersandOffset = query.length();
            int equalsOffset = query.indexOf('=', pos);
            if (equalsOffset == -1 || equalsOffset > ampersandOffset) {
                result.add(query.substring(pos, ampersandOffset) + "="); // No value for this name.
            } else {
                try {
                    result.add(query.substring(pos, equalsOffset) + "=" + URLEncoder.encode(query.substring(equalsOffset + 1, ampersandOffset), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            pos = ampersandOffset + 1;
        }
        return result;
    }

    public String getQuerys() {
        StringBuilder builder = new StringBuilder();
        if (queryNamesAndValues != null) {
            for (int i = 0; i < queryNamesAndValues.size(); i++) {
                builder.append(queryNamesAndValues.get(i));
                if (i != queryNamesAndValues.size() - 1) {
                    builder.append("&");
                }
            }
        }
        return builder.toString();
    }

    public void addQueryParameter(String name, String value) {
        try {
            queryNamesAndValues.add(name + "=" + URLEncoder.encode(value, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void setQueryParameter(String queryParameter) {
        removeAllQueryParameter();
        queryNamesAndValues = queryStringToNamesAndValues(queryParameter);
    }

    public void removeAllQueryParameter() {
        queryNamesAndValues.clear();
    }


    public static class Builder {
        String schema;
        String host;
        int port;
        String path;
        List<String> queryNamesAndValues = new ArrayList<>();

        public Builder setSchema(String schema) {
            this.schema = schema;
            return this;
        }

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public HttpUrl build() {
            return new HttpUrl(this);
        }

        public Builder parse(String url) {
            String temp = url;
            if (url.startsWith("http:")) {
                schema = "http";
                temp = url.substring(schema.length() + 3);
            } else if (url.startsWith("https:")) {
                schema = "https";
                temp = url.substring(schema.length() + 3);
            } else {
                schema = "https";
            }
            int index = temp.indexOf('/');
            if (index == -1) {
                host = temp;
                port = defaultPort(schema);
            } else {
                String hostAport = temp.substring(0, index);
                if (hostAport.contains(":")) {
                    host = hostAport.split(":")[0];
                    port = Integer.valueOf(hostAport.split(":")[1]);
                } else {
                    host = hostAport;
                    port = defaultPort(schema);
                }
                temp = temp.substring(index + 1);
                index = temp.indexOf('?');
                if (index == -1) {
                    path = temp;
                } else {
                    path = temp.substring(0, index);
                    temp = temp.substring(index + 1);
                    queryNamesAndValues = queryStringToNamesAndValues(temp);
                }
            }
            return this;
        }

        public Builder addQueryParameter(String name, String value) {
            try {
                queryNamesAndValues.add(name + "=" + URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return this;
        }
    }
}
