package com.gmail.ipshuvaev.filesearch;

import java.io.File;

/**
 * Резултат поиска, генерируемый TextSearch'ом
 */
public class SearchResult {
    private final File file;
    private final String fragment;

    public SearchResult(File file, String fragment) {
        this.file = file;
        this.fragment = fragment;
    }

    public File getFile() {
        return file;
    }

    public String getFragment() {
        return fragment;
    }
}
