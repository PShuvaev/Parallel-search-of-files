package com.gmail.ipshuvaev;

import com.gmail.ipshuvaev.filesearch.TextSearch;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ActiveSearchMap {
    private static final Map<String, TextSearch> searchProcessMap = new ConcurrentHashMap<String, TextSearch>();

    public static void add(String sessionId, TextSearch process){
        searchProcessMap.put(sessionId, process);
    }

    public static void stop(String sessionId){
        TextSearch p = searchProcessMap.get(sessionId);
        if(p != null){
            p.stop();
            searchProcessMap.remove(sessionId);
        }
    }
}
