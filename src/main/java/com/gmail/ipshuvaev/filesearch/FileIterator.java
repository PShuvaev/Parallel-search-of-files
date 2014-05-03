package com.gmail.ipshuvaev.filesearch;

import com.gmail.ipshuvaev.util.Callback;

import java.io.File;
import java.io.FilenameFilter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Реализует многопоточный обход файлов внутри заданной директории.
 */
public class FileIterator {
    private transient boolean stopped = false;
    private File rootDirectory;
    private AtomicLong taskCounter = new AtomicLong(0);

    public FileIterator(File rootDirectory){
        this.rootDirectory = rootDirectory;
        if(!rootDirectory.isDirectory()){
            throw new RuntimeException("File ["+rootDirectory+"] must be a directory");
        }
    }

    /**
     * Прерывает поиск
     */
    public void stop(){
        stopped = true;
    }

    /**
     * Реализует рекурсивный обход директорий, начиная с rootDirectory.
     * Асинхронно вызывает на каждом файле функцию onFileCallback.onFile.
     * При завершении обхода вызывается doneCallback.run
     */
    public synchronized void traverse(final Callback<File> onFileCallback){
        if(onFileCallback == null)
            throw new IllegalArgumentException("Callback is null");

        ExecutorService executor = getExecutor();
        traverse(onFileCallback, executor, rootDirectory);

        waitForCompletion();
        executor.shutdown();
    }

    /**
     * Рекурсивный обход файлов в директории file с накоплением вычислений в executor
     */
    private void traverse(final Callback<File> onFileCallback, final ExecutorService executor, final File file){
        if(stopped) return;

        if(!file.isDirectory()){
            onFileCallback.call(file);
        } else {
            File[] subfiles = file.listFiles(FILENAME_FILTER);
            if(subfiles == null) return;

            for (final File subfile : subfiles){
                // внимание! инкрементирование счетчика задач должно происходить в текущем потоке,
                // ибо неизвестно, когда задача реально выполнится
                taskCounter.incrementAndGet();

                try {
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            traverse(onFileCallback, executor, subfile);
                            taskCounter.decrementAndGet();
                        }
                    });
                } catch (RejectedExecutionException s){
                    System.out.println("rejected");
                }
            }
        }
    }

    /**
     * Дожидается завершения всех задач в executor'e
     */
    private void waitForCompletion(){
        while (taskCounter.get() != 0 && !stopped) {
            Thread.yield();
        }
    }


    /**
     * Возвращает количество потоков, которые будут выполнять обход директорий
     */
    private static ExecutorService getExecutor(){
        int threadCount = Runtime.getRuntime().availableProcessors();
        return Executors.newFixedThreadPool( threadCount );
    }


    /**
     * Фильтр, исключающий ссылки на директории "текущая" и "уровнем выше"
     */
    private static final FilenameFilter FILENAME_FILTER = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return !(name.equals(".") ||name.equals(".."));
        }
    };
}
