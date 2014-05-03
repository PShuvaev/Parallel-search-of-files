package com.gmail.ipshuvaev.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.Charset;

public class JsonUtil {

    /**
     * Возвращает строковое представление json-объекта,
     * полученного из пар ключ-attrs[2*i], значение-attrs[2*i+1]
     */
    public static String jsonObject(String ...attrs){
        if(attrs.length % 2 != 0){
            throw new IllegalArgumentException("Array must contain an even number of arguments");
        }
        StringBuilder sb = new StringBuilder("{");
        int attrCount = attrs.length / 2;
        for(int i = 0; i < attrCount; i++){
            String key = attrs[2*i];
            String value = attrs[2*i+1];
            sb.append("\"").append(key).append("\"");
            sb.append(':');
            sb.append(value);

            if(i < attrCount-1) sb.append(',');
        }
        return sb.append('}').toString();
    }

    public static String jsonArray(String ...array){
        StringBuilder sb = new StringBuilder("[");
        for (String obj : array) {
            sb.append(obj);
        }
        return sb.append(']').toString();
    }

    /**
     * Кодировка строки в вид, удобный для передачи в строке json-объекта.
     */
    public static String encodeStr(String string) {
        if (string == null || string.length() == 0) {
            return "\"\"";
        }
        byte[] encodedStr = encodeURIComponent(string).getBytes(Charset.defaultCharset());
        return '"' + DatatypeConverter.printBase64Binary(encodedStr) + '"';
    }

    /**
     * Аналог js функции encodeURIComponent.
     * java.net.URLEncoder.encode не используется в силу некоторых отличий в алгоритме кодирования
     */
    private static String encodeURIComponent(String str){
        // экранируем символы, имеющие особое значение в js строке
        String newStr = str.replace("\\", "\\\\").replace("'", "\\'");

        try {
            return (String)jsEngine.eval("encodeURIComponent('"+newStr+"')");
        } catch (ScriptException e) {
            return "Unexpected string";
        }
    }

    private static final ScriptEngine jsEngine = new ScriptEngineManager().getEngineByName("JavaScript");
}
