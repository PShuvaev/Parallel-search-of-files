package com.gmail.ipshuvaev;

import com.gmail.ipshuvaev.filesearch.FileIterator;
import com.gmail.ipshuvaev.filesearch.SearchResult;
import com.gmail.ipshuvaev.filesearch.TextSearch;
import com.gmail.ipshuvaev.util.Callback;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileSearchTest {
    File rootFolder = new File("./src/test/testdir");


    AtomicInteger testFilesCount = new AtomicInteger(0);
    @Test
    public void test(){
        new FileIterator(rootFolder).traverse(new Callback<File>() {
            @Override
            public void call(File file) {
                testFilesCount.incrementAndGet();
            }
        });
        assertEquals(testFilesCount.get(), 5);
    }


    public transient boolean testKey;
    @Test
    public void test2(){
        testKey = false;

        new TextSearch(rootFolder).search("bar", new Callback<SearchResult>() {
            @Override
            public void call(SearchResult result) {
                if(result.getFile().getName().equals("bar")){
                    testKey = true;
                    assertEquals(result.getFragment(), "This is bar file");
                }
            }
        });
        assertTrue(testKey);
    }

    @Test
    public void test2_1(){
        testKey = false;

        new TextSearch(rootFolder).search("bar", new Callback<SearchResult>() {
            @Override
            public void call(SearchResult result) {
                if(result.getFile().getName().equals("bar2")){
                    testKey = true;
                    if(result.getFile().getName().equals("bar2")){
                        assertEquals(result.getFragment(), "bbbbbbbbbbbbbbbbbaz bar file!!!");
                    }
                }
            }
        });
        assertTrue(testKey);
    }


    public transient File testFile;
    @Test
    public void test3(){
        testFile = null;

        new TextSearch(rootFolder).search("thIs is jAva", new Callback<SearchResult>() {
            @Override
            public void call(SearchResult result) {
                testFile = result.getFile();
            }
        });
        assertTrue(testFile != null && testFile.getName().equals("java"));
    }


    AtomicInteger test4counter = new AtomicInteger(0);
    @Test
    public void test4(){
        final TextSearch textSearch = new TextSearch(rootFolder);
        textSearch.search("file", new Callback<SearchResult>() {
            @Override
            public void call(SearchResult result) {
                test4counter.incrementAndGet();
            }
        });

        assertEquals(test4counter.get(), 4);
    }

}
