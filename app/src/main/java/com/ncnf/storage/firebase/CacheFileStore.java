package com.ncnf.storage.firebase;

import android.content.Context;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

public class CacheFileStore extends FileStore {

    private static final String LOG_TAG = "CACHING";

    private File file;
    private Context context;

    @Inject
    public CacheFileStore(FirebaseStorage storage) {
        super(storage);
    }

    public CacheFileStore() {
        super();
    }

    @Override
    public void setPath(String directory, String filename) {
        requiresContext();

        super.setPath(directory, filename);
        this.file = createFile(directory, filename);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public CompletableFuture<byte[]> download() {
        super.requiresPath();

        if (file.exists()) {
            try {
                byte[] data = Files.readAllBytes(file.toPath());
                return CompletableFuture.completedFuture(data);
            } catch (IOException e) {
                // Will download the file
                Log.w(LOG_TAG, "Could not read the file: " + file.getPath());
            }
        }
        CompletableFuture<byte[]> future = super.download();
        future.thenApply(data -> {
            storeFile(data);
            return data;
        });
        return future;
    }

    /**
     * Store data file on FirebaseStorage
     * @param data
     */
    private void storeFile(byte[] data) {
        super.requiresPath();

        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();
        } catch (IOException e) {
            Log.w(LOG_TAG, "Could not store the file: " + file.getPath() + " because of: " + e);
        }
    }

    /**
     * Create a new directory and a new file from the root on FirebaseStorage
     * @param directory Directory path where the file will be stored (can be nested directories)
     * @param filename Name of the created file
     */
    private File createFile(String directory, String filename) {
        File root = context.getCacheDir();
        File dir = new File(root, directory);
        dir.mkdirs();
        return new File(dir, filename);
    }

    /**
     * Check if the context was set up previously
     * @throws IllegalStateException Exception throw if the context wasn't set properly
     */
    private void requiresContext() throws IllegalStateException {
        if (this.context == null)
            throw new IllegalStateException("Please set a context before setting the path.");
    }
}
