package com.gmail.ipshuvaev.util;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;

/**
 * Загрузка ресурса из текущего jar-файла
 */
public final class AssetUtil {
    private static final Logger logger = Logger.getLogger(AssetUtil.class);
    
    private static final String ASSETS_ROOT_FOLDER = "/index/";


    public static String asset(String assetName) throws AssetNotFound {
        try {
            return loadContent(ASSETS_ROOT_FOLDER + assetName);
        } catch (Exception e) {
            throw new AssetNotFound(assetName);
        }
    }


    private static String loadContent(String path) throws IOException {
        InputStream fis = AssetUtil.class.getResourceAsStream(path);
        return slurp(fis);
    }

    public static String slurp (InputStream in) throws IOException {
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1;) {
            out.append(new String(b, 0, n));
        }
        return out.toString();
    }

}