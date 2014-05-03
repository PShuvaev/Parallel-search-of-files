package com.gmail.ipshuvaev.util;

/**
 * Исключение, бросаемое при запросе несуществующего ресурса
 */
public class AssetNotFound extends Exception{
    public AssetNotFound(String asset){
        super("Asset [" + asset + "] not found.");
    }
}
