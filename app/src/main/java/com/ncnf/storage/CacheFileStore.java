package com.ncnf.storage;

import android.content.Context;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.ncnf.database.DatabaseResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;

public class CacheFileStore extends FileStore {

    private static final String LOG_TAG = "CACHING";

    private final File file;
    private final Context context;

    public CacheFileStore(FirebaseStorage storage, Context context, String directory, String filename) {
        super(storage, directory, filename);
        this.context = context;
        this.file = createFile(directory, filename);
    }

    public CacheFileStore(Context context, String directory, String filename) {
        super(directory, filename);
        this.context = context;
        this.file = createFile(directory, filename);
    }

    @Override
    public CompletableFuture<DatabaseResponse> download() {
        if (file.exists()) {
            try {
                byte[] data = Files.readAllBytes(file.toPath());
                return CompletableFuture.completedFuture(new DatabaseResponse(true, data, null));
            } catch (IOException e) {
                // Will download the file
                Log.w(LOG_TAG, "Could not read the file: " + file.getPath());
            }
        }
        CompletableFuture<DatabaseResponse> f = super.download();
        f.thenApply(res -> {
            storeFile((byte[]) res.getResult());
            return res;
        });
        return f;
    }

    private void storeFile(byte[] data) {
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();
        } catch (IOException e) {
            Log.w(LOG_TAG, "Could not store the file: " + file.getPath() + " because of: " + e);
        }
    }

    private File createFile(String directory, String filename) {
        File root = context.getCacheDir();
        File dir = new File(root, directory);
        dir.mkdirs();
        return new File(dir, filename);
    }
}
