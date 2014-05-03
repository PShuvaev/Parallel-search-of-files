package com.gmail.ipshuvaev.filesearch;

import com.gmail.ipshuvaev.util.Callback;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Реализация поиска текстовой строки внутри текстовых файлов заданной директории/поддиректорий.
 * Регистр не учитывается.
 */
public class TextSearch {
    private FileIterator fileIterator;

    public TextSearch(File rootDirectory){
        fileIterator = new FileIterator(rootDirectory);
    }

    /**
     * Запускает поиск строки text в файлах директории rootDirectory и ее поддиректориях
     */
    public void search(final String text, final Callback<SearchResult> callback){
        fileIterator.traverse(new Callback<File>() {
            @Override
            public void call(File file) {
                SearchResult result = search(file, text);
                if( result != null ){
                    callback.call(result);
                }
            }
        });
    }

    /**
     * Построчно сканирует содержимое файла file на предмет нахождения в нем строки str.
     * Если строка не найдена, возвращает null
     */
    private static SearchResult search(File file, String str) {
        try {
            if(!file.canRead()) return null;

            Scanner scanner = new Scanner(file);
            try {
                while(scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    int pos = line.toLowerCase().indexOf(str.toLowerCase());
                    if(pos >= 0){
                        return new SearchResult(file, fragment(line, pos));
                    }
                }
            } finally {
                scanner.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String fragment(String s, int pos){
        int startI = Math.max(pos - 20, 0);
        int endI = Math.min(pos+20, s.length());
        return s.substring(startI, endI);
    }


    /**
     * Останавливает дальнейший поиск файлов
     */
    public void stop(){
        if(fileIterator != null){
            fileIterator.stop();
        }
    }
}
