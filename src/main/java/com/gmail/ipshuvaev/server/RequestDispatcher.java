package com.gmail.ipshuvaev.server;

import io.netty.handler.codec.http.HttpRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Центральный компонент, к которому приходят все разобранные http-запросы.
 * При очередном запросе просматривает все прикрепленные хэндлеры и вызывает первый,
 * регулярное выражение которого сопоставляется с uri запроса.
 */
public class RequestDispatcher {
    private List<RequestHandler> handlers = new ArrayList<RequestHandler>();

    /**
     * Прикрепление обработчика запросов.
     */
    public RequestDispatcher addHandler(RequestHandler handler){
        handlers.add(handler);
        return this;
    }

    public void dispatch(HttpRequest request, Response response){
        for (RequestHandler handler : handlers){
            if(request.getUri().matches(handler.getUriTemplate())){
                handler.handle(request, response);
                return;
            }
        }
        DEFAULT_HANDLER.handle(request, response);
    }

    /**
     * Дефолтный обработчик запроса.
     * Вызывается в случае, если не нашлось подходящего обработчика из прикрепленных
     */
    private static RequestHandler DEFAULT_HANDLER = new RequestHandler(null) {
        @Override
        public void handle(HttpRequest request, Response response) {
            response.send("<html><head></head><body>Page not found</body>");
        }
    };
}
