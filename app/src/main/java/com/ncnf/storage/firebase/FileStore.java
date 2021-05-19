package com.ncnf.storage.firebase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

public class FileStore {

    private final long MAX_SIZE = 10 * 1024 * 1024;

    private final FirebaseStorage storage;
    private StorageReference fileRef;

    @Inject
    public FileStore(FirebaseStorage storage) {
        this.storage = storage;
    }

    public FileStore() {
        this.storage = FirebaseStorage.getInstance();
    }

    public void setPath(String directory, String filename) {
        this.fileRef = storage.getReference().child(directory).child(filename);
    }

    public CompletableFuture<Boolean> uploadImage(Bitmap bitmap) {
        requiresPath();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        return upload(data);
    }

    public CompletableFuture<Boolean> upload(byte[] data) {
        requiresPath();

        CompletableFuture<Boolean> future = new CompletableFuture<>();

        UploadTask uploadTask = fileRef.putBytes(data);
        uploadTask.addOnSuccessListener(task -> future.complete(true));
        uploadTask.addOnFailureListener(future::completeExceptionally);

        return future;
    }

    public void downloadImage(ImageView view, Bitmap defaultImage) {
        requiresPath();

        download().thenAccept(data -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            view.setImageBitmap(bitmap);
        }).exceptionally(e -> {
            view.setImageBitmap(defaultImage);
            return null;
        });

    }

    public CompletableFuture<byte[]> download() {
        requiresPath();

        CompletableFuture<byte[]> future = new CompletableFuture<>();

        Task<byte[]> download = fileRef.getBytes(MAX_SIZE);
        download.addOnSuccessListener(future::complete);
        download.addOnFailureListener(future::completeExceptionally);

        return future;
    }

    protected void requiresPath() throws IllegalStateException {
        if (this.fileRef == null)
            throw new IllegalStateException("Please set the path before using any methods.");
    }

}
