package com.ncnf.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ncnf.database.DatabaseResponse;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.CompletableFuture;

public class FileUpload {

    private final long MAX_SIZE = 10 * 1024 * 1024;

    private final FirebaseStorage storage;
    private final StorageReference fileRef;

    public FileUpload(FirebaseStorage storage, String directory, String filename) {
        this.storage = storage;
        this.fileRef = storage.getReference().child(directory).child(filename);
    }

    public FileUpload(String directory, String filename) {
        this.storage = FirebaseStorage.getInstance();
        this.fileRef = storage.getReference().child(directory).child(filename);
    }

    public CompletableFuture<DatabaseResponse> uploadImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        return upload(data);
    }

    public CompletableFuture<DatabaseResponse> upload(byte[] data) {
        CompletableFuture<DatabaseResponse> future = new CompletableFuture<>();

        UploadTask uploadTask = fileRef.putBytes(data);
        uploadTask.addOnSuccessListener(task -> {
            future.complete(new DatabaseResponse(true, task.getMetadata(), null));
        });
        uploadTask.addOnFailureListener(exception -> {
            future.complete(new DatabaseResponse(false, null, exception));
        });
        return future;
    }

    public void downloadImage(ImageView view) {
        download().thenAccept(res -> {
            byte[] data = (byte[]) res.getResult();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            view.setImageBitmap(bitmap);
        });

    }

    public CompletableFuture<DatabaseResponse> download() {
        CompletableFuture<DatabaseResponse> future = new CompletableFuture<>();

        Task<byte[]> download = fileRef.getBytes(MAX_SIZE);
        download.addOnSuccessListener(data -> {
            future.complete(new DatabaseResponse(true, data, null));
        });
        download.addOnFailureListener(exception -> {
            future.complete(new DatabaseResponse(false, null, exception));
        });

        return future;
    }

    public StorageReference getStorageRef() {
        return fileRef;
    }

}
