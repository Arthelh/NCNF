package com.ncnf.storage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.CompletableFuture;

public class FileStore {

    private final long MAX_SIZE = 10 * 1024 * 1024;

    private final FirebaseStorage storage;
    private final StorageReference fileRef;

    public FileStore(FirebaseStorage storage, String directory, String filename) {
        this.storage = storage;
        this.fileRef = storage.getReference().child(directory).child(filename);
    }

    public FileStore(String directory, String filename) {
        this.storage = FirebaseStorage.getInstance();
        this.fileRef = storage.getReference().child(directory).child(filename);
    }

    public CompletableFuture<Boolean> uploadImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        return upload(data);
    }

    public CompletableFuture<Boolean> upload(byte[] data) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        UploadTask uploadTask = fileRef.putBytes(data);
        uploadTask.addOnSuccessListener(task -> future.complete(true));
        uploadTask.addOnFailureListener(future::completeExceptionally);

        return future;
    }

    public void downloadImage(ImageView view) {
        download().thenAccept(data -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            view.setImageBitmap(bitmap);
        });

    }

    public CompletableFuture<byte[]> download() {
        CompletableFuture<byte[]> future = new CompletableFuture<>();

        Task<byte[]> download = fileRef.getBytes(MAX_SIZE);
        download.addOnSuccessListener(future::complete);
        download.addOnFailureListener(exception -> {

        });

        return future;
    }

}
