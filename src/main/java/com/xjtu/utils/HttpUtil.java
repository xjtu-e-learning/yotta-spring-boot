package com.xjtu.utils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 关于HTTP传输的信息的解析
 *
 * @author yangkuan
 */
public class HttpUtil {
    public static String getOrigin(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        return origin;
    }

    public static String getUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent;
    }

    public static String getReferer(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        return referer;
    }

    public static String getHeaders(HttpServletRequest request) {
        String headers = "Referer: " + getReferer(request)
                + "; User-Agent: " + getUserAgent(request);
        return headers;
    }

    public static String getIp() {
        String ip = "";
        try {
            InetAddress address = InetAddress.getLocalHost();
            //获取本机ip
            ip = address.getHostAddress().toString();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ip;
    }
}
