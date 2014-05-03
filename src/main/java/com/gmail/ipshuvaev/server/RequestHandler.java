package com.gmail.ipshuvaev.server;

import io.netty.handler.codec.http.HttpRequest;

/**
 * Обработчик http-запроса.
 */
public abstract class RequestHandler {
    // регулярное выражение, определяющее вызов данного обработчика назаданном uri
    private String uriTemplate;

    public RequestHandler(String uriTemplate){
        this.uriTemplate = uriTemplate;
    }

    /**
     * Основной метод, в котором предполагается обработка запроса и запись ответа
     */
    public abstract void handle(HttpRequest request, Response response);

    public String getUriTemplate(){
        return uriTemplate;
    }
}
