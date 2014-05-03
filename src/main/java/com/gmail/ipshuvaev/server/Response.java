package com.gmail.ipshuvaev.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;

/**
 * Примитивная реализация выходного потока для записи результата в обработчике http-запросов.
 * Использование предполагает единственный вызов метода send(String), либо последовательность вызовов writeChunk.., close.
 * Реализован с использованием writeChunk является неблокирующим и потокобезопасным.
 */
public class Response {
    private boolean headersIsSent = false;
    private String contentType;

    private ChannelHandlerContext context;

    public Response(ChannelHandlerContext context){
        this.context = context;
    }

    /**
     * Запись очередной порции данных
     */
    public void writeChunk(String data){
        if(context == null) return;

        if(!headersIsSent){
            context.write( mkBuffer(transferEncodingHeaders()) );
            headersIsSent = true;
        }

        context.write( mkBuffer(asChunk(data))  );
        context.flush();
    }

    /**
     * Передача единственного куска текста и закрытие потока
     */
    public void send(String data){
        if(context == null) return;

        writeChunk(data);
        close();
    }

    /**
     * Закрытие соединения
     */
    public void close(){
        context.write( mkBuffer(asChunk("")) ).addListener(ChannelFutureListener.CLOSE);
        context.flush();
        context.close();
        context = null;
    }

    /**
     * Стандартные http-заголовки
     */
    private String transferEncodingHeaders(){
        String ctype = contentType == null ? "text/plain; charset=UTF-8" : contentType;
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: " + ctype + "\r\n"
                + "Transfer-Encoding: chunked\r\n"
                + "Vary: Accept-Encoding\r\n"
                + "Connection: keep-alive" + "\r\n\r\n";
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Формирование http-chunk'a при Transfer-Encoding: chunked
     */
    private static String asChunk(String data){
        String len = Integer.toHexString(data.getBytes(Charset.defaultCharset()).length);
        return len + "\r\n" + data + "\r\n";
    }

    private static ByteBuf mkBuffer(String str){
        return Unpooled.copiedBuffer(str.getBytes(Charset.defaultCharset()));
    }


}
