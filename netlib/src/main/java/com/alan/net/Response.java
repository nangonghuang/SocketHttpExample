package com.alan.net;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Response {
    int code;
    String status;
    List<String> headers;
    InputStream body;


    void resolveStartLine(String startLine) {
        int firstBlank = startLine.indexOf(" ");
        int secondBlank = startLine.indexOf(" ", firstBlank + 1);
        code = Integer.parseInt(startLine.substring(firstBlank + 1, secondBlank));
        status = startLine.substring(secondBlank + 1);
    }

    void addHeader(String line) {
        if (headers == null) {
            headers = new ArrayList<>();
        }
        headers.add(line);
    }

    public String getHeaderLine(String key) {
        for (String header : headers) {
            if (header.contains(key)) {
                return header;
            }
        }
        return "";
    }

    public int getContentLength() {
        int length = -1;
        String lengthLine = getHeaderLine("Content-Length");
        if (lengthLine.length() != 0) {
            String[] header = lengthLine.split(":");
            try {
                length = Integer.parseInt(header[1].trim());
            } catch (Exception e) {

            }
        }
        return length;
    }

    public int getBuflength() {
        int length = getContentLength();
        int buflength = 1024;
        if (length != -1) {
            if (length < buflength || length % buflength != 0) {

            } else {
                buflength = 1023;
            }
        }
        return buflength;
    }

    public InputStream getBody() {
        return body;
    }

    public String toUtf8String() {
        if (body != null) {
            int buflength = getBuflength();
            byte[] buf = new byte[buflength];
            StringBuilder sb = new StringBuilder();
            int len = 0;
            try {
                while ((len = getBody().read(buf)) != -1) {
                    sb.append(new String(buf, 0, len, "utf-8"));
                    if (len == 0 || len != buflength) {  //表示读完了,否则会阻塞几秒的时间
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Response{" +
                    "code=" + code +
                    ", status='" + status + '\'' +
                    ", header='" + headers + '\'' +
                    ", body='" + sb.toString() + '\'' +
                    '}';
        } else {
            return "Response{" +
                    "code=" + code +
                    ", status='" + status + '\'' +
                    ", header='" + headers + '\'' +
                    '}';
        }
    }
}
