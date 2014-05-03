package com.gmail.ipshuvaev.controller;

import com.gmail.ipshuvaev.server.RequestHandler;
import com.gmail.ipshuvaev.server.Response;
import com.gmail.ipshuvaev.util.AssetNotFound;
import com.gmail.ipshuvaev.util.AssetUtil;
import com.gmail.ipshuvaev.util.ContentType;
import io.netty.handler.codec.http.HttpRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AssetController extends RequestHandler {

    public AssetController() {
        super("/asset/.+");
    }

    @Override
    public void handle(HttpRequest request, Response response) {
        String uri = request.getUri();
        Matcher matcher = Pattern.compile("/asset/(.+)").matcher(uri);
        String assetName = "none";
        if (matcher.matches()) {
            assetName = matcher.group(1);
        }
        String asset = null;
        try {
            asset = AssetUtil.asset(assetName);
        } catch (AssetNotFound e) {
            asset = "Error";
        }
        response.setContentType(ContentType.recognize(uri));
        response.send(asset);
    }
}
