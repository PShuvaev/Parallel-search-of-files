package com.gmail.ipshuvaev.controller;

import com.gmail.ipshuvaev.ActiveSearchMap;
import com.gmail.ipshuvaev.filesearch.SearchResult;
import com.gmail.ipshuvaev.filesearch.TextSearch;
import com.gmail.ipshuvaev.server.RequestHandler;
import com.gmail.ipshuvaev.server.Response;
import com.gmail.ipshuvaev.util.Callback;
import io.netty.handler.codec.http.HttpRequest;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.gmail.ipshuvaev.util.JsonUtil.jsonArray;
import static com.gmail.ipshuvaev.util.JsonUtil.jsonObject;
import static com.gmail.ipshuvaev.util.JsonUtil.encodeStr;

/**
 * Запускает поиск по каталогу, указанному в uri.
 */
public class StartSearchController extends RequestHandler {

    String IDENTIFICATION_ERROR = jsonArray(jsonObject("error", encodeStr("Identification error")));
    String INTERNAL_ERROR = jsonArray(jsonObject("error", encodeStr("Some internal error")));
    String INVALID_PARAMS_ERROR = jsonArray(jsonObject("error", encodeStr("Some parameters are invalid")));
    String DIRECTORY_NOT_EXIST_ERROR = jsonArray(jsonObject("error", encodeStr("Directory not exist")));

    public StartSearchController() {
        super("/search/start/.+");
    }

    @Override
    public void handle(HttpRequest request, final Response response) {
        response.setContentType("application/json; charset=UTF-8");

        String uri;
        try {
            uri = URLDecoder.decode(request.getUri(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            response.send(INTERNAL_ERROR);
            return;
        }

        String sessionId = request.headers().get("session-id");
        if(sessionId == null){
            response.send(IDENTIFICATION_ERROR);
            return;
        }

        final Matcher matcher = Pattern.compile("/search/start/(.+)").matcher(uri);
        if (matcher.matches()) {
            runSearch(sessionId, matcher.group(1), response);
        } else {
            response.send(INVALID_PARAMS_ERROR);
        }
    }

    /**
     * Запуск потока поиска
     */
    private void runSearch(final String sessionId, final String query, final Response response) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = query.lastIndexOf('/');  // строка запроса имеет вид ПутьКДиректории/ИскомаяФраза

                String text = query.substring(i+1, query.length());
                String path = query.substring(0, i);

                if(text.equals("") || path.equals("")){
                    response.send(INVALID_PARAMS_ERROR);
                    return;
                }

                File dir = new File(path);
                if(!dir.exists()){
                    response.send(DIRECTORY_NOT_EXIST_ERROR);
                } else {
                    final TextSearch textSearcher = new TextSearch(new File(path));
                    ActiveSearchMap.add(sessionId, textSearcher);

                    response.writeChunk("[");
                    textSearcher.search(text, new Callback<SearchResult>() {
                        @Override
                        public void call(SearchResult result) {
                            String chunk = jsonObject(
                                    "fileName", encodeStr(result.getFile().getAbsolutePath()),
                                    "fragment", encodeStr(result.getFragment()),
                                    "sessionId", '\"' + sessionId + "\""
                            ).concat(",");
                            response.writeChunk(chunk);
                        }
                    });

                    response.writeChunk("]");
                    response.close();
                }
            }
        }).start();
    }

}
