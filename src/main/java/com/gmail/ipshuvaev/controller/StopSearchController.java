package com.gmail.ipshuvaev.controller;

import com.gmail.ipshuvaev.ActiveSearchMap;
import com.gmail.ipshuvaev.server.RequestHandler;
import com.gmail.ipshuvaev.server.Response;
import io.netty.handler.codec.http.HttpRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**

 */
public class StopSearchController extends RequestHandler {

    public StopSearchController() {
        super("/search/stop/.+");
    }

    @Override
    public void handle(HttpRequest request, final Response response) {
        response.setContentType("application/json; charset=UTF-8");


        String uri;
        try {
            uri = URLDecoder.decode(request.getUri(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            response.send("{\"error\":\"Uri decoding error\"");
            return;
        }

        final Matcher matcher = Pattern.compile("/search/stop/(.+)").matcher(uri);
        if (matcher.matches()) {
            String sessionId = matcher.group(1);
            ActiveSearchMap.stop(sessionId);
            response.send("{}");
        } else {
            response.send("{\"error\":\"Invalid sessionId parameter\"");
        }
    }
}
