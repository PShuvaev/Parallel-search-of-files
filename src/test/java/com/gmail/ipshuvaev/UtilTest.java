package com.gmail.ipshuvaev;

import com.gmail.ipshuvaev.util.AssetNotFound;
import com.gmail.ipshuvaev.util.AssetUtil;
import com.gmail.ipshuvaev.util.ContentType;
import com.gmail.ipshuvaev.util.JsonUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class UtilTest {

    @Test
    public void testContentRecognize(){
        assertEquals(ContentType.recognize("/foo/index.html"), "text/html; charset=UTF-8");
        assertEquals(ContentType.recognize("/foo/index.css"), "text/css; charset=UTF-8");
    }

    @Test(expected = AssetNotFound.class)
    public void testAssetUtilEx() throws AssetNotFound {
        AssetUtil.asset("non-existent file");
    }

    @Test(expected = AssetNotFound.class)
    public void testAssetUtilEx2() throws AssetNotFound {
        AssetUtil.asset(null);
    }

    @Test
    public void testAssetUtil() throws AssetNotFound {
        assertEquals(AssetUtil.asset("test"), "test content");
    }

    @Test
    public void testJsonUtil1(){
        assertEquals(JsonUtil.jsonObject("key", String.valueOf(12)), "{\"key\":12}");
    }

}
