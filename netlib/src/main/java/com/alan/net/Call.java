package com.xckoohttp.net;


import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class Call {
    Client client;
    Request request;

    public Call(Client client, Request request) {
        this.client = client;
        this.request = request;
    }

    private static final String TAG = "Call ";

    /**
     * 当前线程执行，可以直接拿到Response
     *
     * @return
     */
    public Response exexuteOnCurrentThread() {
        Response response2 = useHttpURLConnection();
        return response2;
    }

    /**
     * 做了一层包装，在线程池执行，通过callback拿到结果
     *
     * @return
     */
    public void exexute(Callback responseCallback) {
        client.processer.runOnChildThread(() -> {
            Response response = useSocketConnection();
            if (responseCallback != null) {
                if (response.code == -1) {
                    responseCallback.onFailure(Call.this, response.status);
                } else {
                    responseCallback.onResponse(Call.this, response);
                }
                client.connections.closeConnection(request.httpUrl.url);
            }
        });
    }

    private Response useHttpURLConnection() {
        Response response = new Response();
        try {
//            if (request.httpUrl.schema.equalsIgnoreCase(Constant.SCHEMA_HTTP)) {
            HttpURLConnection connection = (HttpURLConnection) request.httpUrl.getFullUrl().openConnection();
            connection.setConnectTimeout(client.connectTimeout);
            connection.setReadTimeout(client.readTimeout);
            connection.setRequestMethod(request.method);
            connection.setRequestProperty(Header.CHARSET, Header.CHARSET_UTF8);

            if (request.method.equalsIgnoreCase(Request.GET)) {
                connection.connect();
            } else if (request.method.equalsIgnoreCase(Request.POST)) {
                connection.setDoOutput(true);
                if (request.hasBody()) {
                    connection.getOutputStream().write(request.body.getBytes());
                }
            }

            int responseCode = connection.getResponseCode();
            System.out.println("useHttpURLConnection responseCode =  " + responseCode + "," + connection.getResponseMessage());

            if (responseCode == HttpURLConnection.HTTP_OK) {
//                String str = Tools.bytes2Utf8String(Tools.readBAFromInputStream(connection.getInputStream()));
                response.code = responseCode;
                response.status = connection.getResponseMessage();
//                response.headers = connection.getHeaderFields().toString();
                response.body = connection.getInputStream();
            } else if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                String location = connection.getHeaderField("Location");
                HttpURLConnection connection2 = (HttpURLConnection) new URL(location).openConnection();
                connection2.setConnectTimeout(client.connectTimeout);
                connection2.setReadTimeout(client.readTimeout);
                connection2.setRequestProperty(Header.CHARSET, Header.CHARSET_UTF8);
                connection2.setRequestMethod(request.method);
                if (request.method.equalsIgnoreCase(Request.GET)) {
                    connection2.connect();
                } else if (request.method.equalsIgnoreCase(Request.POST)) {
                    connection2.setDoOutput(true);
                    connection2.getOutputStream().write(request.body.getBytes());
                }
                if (connection2.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                        String str2 = Tools.bytes2Utf8String(Tools.readBAFromInputStream(connection.getInputStream()));
                    response.code = connection2.getResponseCode();
                    response.status = connection2.getResponseMessage();
//                    response.headers = connection2.getHeaderFields().toString();
                    response.body = connection2.getInputStream();
                }
            } else {
                response.code = responseCode;
                response.status = connection.getResponseMessage();
            }
//            }
        } catch (IOException e) {
            response.code = -1;
            response.status = e.getMessage();
        }
        return response;
    }

    /**
     * * GET /tools/html SCHEMA_HTTP/1.0     : <method> <request-url> <version> CRLF
     *
     * @return
     */
    private Response useSocketConnection() {
        Response response = new Response();
        Socket socket = null;
        try {
            System.out.println("url : " + request.httpUrl.url);
            if (request.httpUrl.schema.equals(Constant.SCHEMA_HTTP)) {
                socket = new Socket(request.httpUrl.host, request.httpUrl.port);
            } else if (request.httpUrl.schema.equals(Constant.SCHEMA_HTTPS)) {
                SSLContext sslContext = getSSLContext();
                sslContext.init(null, new TrustManager[]{systemDefaultTrustManager()}, null);
                socket = (SSLSocket) (sslContext.getSocketFactory()).createSocket(request.httpUrl.host, request.httpUrl.port);
            } else {
                throw new Exception("not supported schema");
            }

            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(request.getHttpHeader().getBytes());
            outputStream.flush();

            client.connections.addConnection(request.httpUrl.url, socket);


            String line = null;
//            InputStreamReader reader = new InputStreamReader(socket.getInputStream(), "utf-8");
//            BufferedReader bufferedReader = new BufferedReader(reader);
//            boolean chunked = false;
//            response.resolveStartLine(bufferedReader.readLine());
//            while ((line = bufferedReader.readLine()) != null) {
//                System.out.println("line : " + line);
//                if (line.length() != 0) {
//                    response.addHeader(line);
//                    if (line.contains("chunked") || line.contains("Chunked")) {
//                        chunked = true;
//                    }
//                } else {
////                    break;
//                }
//            }
//            bufferedReader.close();

            String startLine = readLine2(socket.getInputStream());
            System.out.println("startLine : " + startLine);
            response.resolveStartLine(startLine);
            while ((next = socket.getInputStream().read()) != -1) {
                line = readLine2(socket.getInputStream());
                System.out.println("line : " + line);
                if (line.length() != 0) {
                    response.addHeader(line);
                } else {
                    break;
                }
            }

            Log.i(TAG, "useSocketConnection: ************************************************");
            response.body = socket.getInputStream(); // 如果遇到头部带有chunked，还需要去掉字数行
        } catch (Exception e) {
            e.printStackTrace();
            response.code = -1;
            response.status = e.getMessage();
        }
        return response;
    }

    int next;

    String readLine(InputStream stream) {
        StringBuilder builder = new StringBuilder();
        ByteBuffer byteBuffer = ByteBuffer.allocate(8 * 1024);
        int c = 0;
        if (next != 0) {
            byteBuffer.put((byte) next);
            next = 0;
        }
        try {
            while (true) {
                c = stream.read();
                if (c == 13) {
                    next = stream.read();
                    if (next == 10) {
                        next = 0;
                    }
                    byteBuffer.flip();
                    byte[] buffer2 = new byte[byteBuffer.limit()];
                    byteBuffer.get(buffer2);
                    return builder.toString() + new String(buffer2, "utf-8");
                } else if (c == -1) {
                    byteBuffer.flip();
                    byte[] buffer2 = new byte[byteBuffer.limit()];
                    byteBuffer.get(buffer2);
                    return builder.toString() + new String(buffer2, "utf-8");
                }
                if (!byteBuffer.hasRemaining()) {
                    builder.append(new String(byteBuffer.array().clone(), "utf-8"));
                    byteBuffer.clear();
                } else {
                    byteBuffer.put((byte) c);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    ByteBuffer byteBuffer = ByteBuffer.allocate(8 * 1024);

    String readLine2(InputStream stream) {
        StringBuilder builder = new StringBuilder();
        int c = 0;
        if (next != 0 && next != 10 && next != 13) {
            byteBuffer.put((byte) next);
            next = 0;
        }
        try {
            while (true) {
                c = stream.read();
                if (c == 10 || c == -1) {
                    return builder.toString() + getStringFromBuffer();
                } else if (c == 13) {
                    next = stream.read();
                    if (next == 10) {
                        next = 0;
                    }
                    return builder.toString() + getStringFromBuffer();
                }
                if (!byteBuffer.hasRemaining()) {
                    String s = new String(byteBuffer.array().clone(), "utf-8");
                    builder.append(s);
                    byteBuffer.clear();
                } else {
                    byteBuffer.put((byte) c);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getStringFromBuffer() throws UnsupportedEncodingException {
        byteBuffer.flip();
        byte[] buffer2 = new byte[byteBuffer.limit()];
        byteBuffer.get(buffer2);
        byteBuffer.clear();
        return new String(buffer2, "utf-8");
    }


    private X509TrustManager systemDefaultTrustManager() {
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:"
                        + Arrays.toString(trustManagers));
            }
            return (X509TrustManager) trustManagers[0];
        } catch (GeneralSecurityException e) {
            throw assertionError("No System TLS", e); // The system has no TLS. Just give up.
        }
    }

    public static AssertionError assertionError(String message, Exception e) {
        AssertionError assertionError = new AssertionError(message);
        try {
            assertionError.initCause(e);
        } catch (IllegalStateException ise) {
            // ignored, shouldn't happen
        }
        return assertionError;
    }

    private SSLContext getSSLContext() {
        try {
            return SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No TLS provider", e);
        }
    }
}
