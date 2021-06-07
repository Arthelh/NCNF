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

/**
 * A file store that caches the file when they are downloaded
 */
public class FirebaseCacheFileStore extends FirebaseFileStore {
    private static final String LOG_TAG = "CACHING";

    private File file;
    private Context context;

    /**
     * Create a file storage with the given adapter
     * @param storage a Firebase Storage adapter
     */
    @Inject
    public FirebaseCacheFileStore(FirebaseStorage storage) {
        super(storage);
    }

    /**
     * Create a file storage with the default Firebase Storage adapter
     */
    public FirebaseCacheFileStore() {
        super();
    }

    /**
     * See parent.
     * Also create a pointer to a file in the cache
     */
    @Override
    public void setPath(String directory, String filename) {
        requiresContext();

        super.setPath(directory, filename);
        this.file = createFile(directory, filename);
    }

    /**
     * Set the context (required to access the filesystem)
     * @param context the activity context
     */
    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * Try to find the file in the cache and download it if the file is not present.
     */
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
     * Invalidate the cached file if any and upload the new one
     * @param data an array of bytes
     * @return a future with whether the upload was successful
     */
    @Override
    public CompletableFuture<Boolean> upload(byte[] data) {
        // invalidate cache
        if (file.exists())
            file.delete();

        return super.upload(data);
    }

    // Write the given bytes to the file in the cache
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

    // Create a reference to a file in the cache
    private File createFile(String directory, String filename) {
        File root = context.getCacheDir();
        File dir = new File(root, directory);
        dir.mkdirs();
        return new File(dir, filename);
    }

    // raise an exception if the context is not set
    private void requiresContext() throws IllegalStateException {
        if (this.context == null)
            throw new IllegalStateException("Please set a context before setting the path.");
    }
}
