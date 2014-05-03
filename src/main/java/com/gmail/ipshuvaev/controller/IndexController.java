package com.gmail.ipshuvaev.controller;

import com.gmail.ipshuvaev.server.RequestHandler;
import com.gmail.ipshuvaev.server.Response;
import io.netty.handler.codec.http.HttpRequest;

public class IndexController extends RequestHandler {
    private static final AssetController ASSET_CONTROLLER = new AssetController();

    public IndexController() {
        super("/");
    }

    @Override
    public void handle(HttpRequest request, Response response) {
        request.setUri("/asset/index.html");
        ASSET_CONTROLLER.handle(request, response);
    }

}
