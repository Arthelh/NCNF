package com.ncnf.utilities;

import android.graphics.Bitmap;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ncnf.database.DatabaseResponse;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.CompletableFuture;

public class FileUpload {

    private final FirebaseStorage storage;
    private final StorageReference fileRef;

    public FileUpload(String directory, String filename) {
        this.storage = FirebaseStorage.getInstance();
        this.fileRef = storage.getReference().child(directory).child(filename);
    }

    public CompletableFuture<DatabaseResponse> upload(Bitmap bitmap) {
        CompletableFuture<DatabaseResponse> future = new CompletableFuture<>();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = fileRef.putBytes(data);
        uploadTask.addOnSuccessListener(task -> {
            future.complete(new DatabaseResponse(true, task.getMetadata(), null));
        });
        uploadTask.addOnFailureListener(exception -> {
            future.complete(new DatabaseResponse(false, null, exception));
        });
        return future;
    }

    public StorageReference getStorageRef() {
        return fileRef;
    }

}
