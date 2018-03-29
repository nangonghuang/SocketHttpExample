package com.xckoohttp.net;

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

    @Override
    public String toString() {
        if (body != null) {
//            return "Response{" +
//                    "code=" + code +
//                    ", status='" + status + '\'' +
//                    ", header='" + headers + '\'' +
//                    ", body=" + Tools.bytes2Utf8String(Tools.readBAFromInputStream(body)) +
//                    '}';
            return ", body=" + Tools.bytes2Utf8String(Tools.readBAFromInputStream(body));
        } else {
            return "Response{" +
                    "code=" + code +
                    ", status='" + status + '\'' +
                    ", header='" + headers + '\'' +
                    '}';
        }
    }
}
