package com.gmail.ipshuvaev.server;


import io.netty.handler.codec.http.*;

/**
 * Примитивный парсер http-запросов. Производит разбор лишь первой строки запроса.
 * Предполагается дальнейшее расширение.
 */
public class HttpParser {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static DefaultHttpRequest parse(String input){
        DefaultHttpHeaders headers = new DefaultHttpHeaders(true);

        DefaultHttpRequest request;

        int i = input.indexOf('\n');
        if(i > 0){
            String firstLine = input.substring(0, i);
            String[] words = firstLine.split(" ");
            if(words.length != 3)
                return null;

            request = new DefaultHttpRequest(
                    HttpVersion.valueOf(words[2]),
                    HttpMethod.valueOf(words[0]),
                    words[1]);
        } else {
            return null;
        }
        parseHeaders(input, i, request.headers());

        return request;
    }

    /**
     * Осуществляет разбор заголовков, начиная с позиции endOfFirstLine строки input,
     * и помещает их в объект headers
     */
    private static void parseHeaders(String input, int endOfFirstLine, HttpHeaders headers){
        int endH = input.indexOf(LINE_SEPARATOR+LINE_SEPARATOR);
        int startLine = endOfFirstLine+1;
        int endLine;
        while ((endLine = input.indexOf(LINE_SEPARATOR, startLine)) <= endH && endLine != -1){
            String[] header = input.substring(startLine, endLine).split(": ", 2);
            headers.add(header[0], header[1]);
            startLine = endLine + LINE_SEPARATOR.length();
        }
    }
}
