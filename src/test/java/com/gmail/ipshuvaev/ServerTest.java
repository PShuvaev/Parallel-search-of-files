package com.gmail.ipshuvaev;

import com.gmail.ipshuvaev.server.HttpParser;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ServerTest {
    String HeaderFirstLine = "GET /wiki/HTTP HTTP/1.0\n";

    @Test
    public void parserTest(){
        assertEquals(HttpParser.parse(""), null);
    }

    @Test
    public void parserTest2(){
        assertEquals(HttpParser.parse(HeaderFirstLine).getMethod(), HttpMethod.GET);
        assertEquals(HttpParser.parse(HeaderFirstLine).getUri(), "/wiki/HTTP");
        assertEquals(HttpParser.parse(HeaderFirstLine).getProtocolVersion(), HttpVersion.HTTP_1_0);
    }
}
