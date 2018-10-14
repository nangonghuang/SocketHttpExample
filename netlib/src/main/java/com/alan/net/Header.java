package com.alan.net;

/**
 * 起始行 : http报文第一行
 * 首部字段 ： 冒号分隔，空行结束
 *
 *
 * 请求报文：
 * GET /tools/html SCHEMA_HTTP/1.0     : <method> <request-url> <version> CRLF
 * Host        接受请求的服务器地址，可以是IP:端口号，也可以是域名
 * User-Agent  发送请求的应用程序名称
 * Connection  指定与连接相关的属性，如Connection:Keep-Alive
 * Accept-Charset  通知服务端可以发送的编码格式
 * Accept-Encoding  通知服务端可以发送的数据压缩格式
 * Accept-Language  通知服务端可以发送的语言
 *
 * <请求正文(可选)>  get请求时，无正文，request-url为path+query
 *                  post请求时，query为正文，request-url为path
 *
 *
 *
 * 响应报文:
 * SCHEMA_HTTP/1.0 200 OK       :<version><status><reason-phrase>  CRLF
 * Date:Sun xxxx
 * Server：Apach/1.3 xxxx
 * Last-modified:Tue,xxxxx                   ---------------------
 * Content-length:403                        实体首部
 * Content-type:text/html;charset:utf-8
 * Content-encoding:gzip
 *
 * xxxx                                      实体主体
 */
public class Header {
    public static final String CHARSET = "Charset";
    public static final String CHARSET_UTF8 = "UTF-8";
    String startLine;
}
