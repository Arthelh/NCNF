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

    /**
     * Create a new directory and a new file from the root on FirebaseStorage
     * @param directory Directory path where the file will be stored (can be nested directories)
     * @param filename Name of the created file
     */
    public void setPath(String directory, String filename) {
        this.fileRef = storage.getReference().child(directory).child(filename);
    }

    /**
     * Upload an image to FirebaseStorage
     * @param bitmap BitMap representing the image
     * @return CompletableFuture containing FirebaseStorage's response : true if all operations were correctly executed
     */
    public CompletableFuture<Boolean> uploadImage(Bitmap bitmap) {
        requiresPath();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        return upload(data);
    }

    /**
     * Upload data bytes to FirebaseStorage
     * @param data Data (in byte) we want to store
     * @return CompletableFuture containing FirebaseStorage's response : true if all operations were correctly executed
     */
    public CompletableFuture<Boolean> upload(byte[] data) {
        requiresPath();

        CompletableFuture<Boolean> future = new CompletableFuture<>();

        UploadTask uploadTask = fileRef.putBytes(data);
        uploadTask.addOnSuccessListener(task -> future.complete(true));
        uploadTask.addOnFailureListener(future::completeExceptionally);

        return future;
    }

    /**
     * Download an image from FirebaseStorage
     * @param view View where the image will be download and set up
     * @param defaultImage Default image that will be set up if no corresponding image is found
     */
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

    /**
     * Download data bytes from FirebaseStorage
     * @return CompletableFuture containing the fetched data bytes
     */
    public CompletableFuture<byte[]> download() {
        requiresPath();

        CompletableFuture<byte[]> future = new CompletableFuture<>();

        Task<byte[]> download = fileRef.getBytes(MAX_SIZE);
        download.addOnSuccessListener(future::complete);
        download.addOnFailureListener(future::completeExceptionally);

        return future;
    }

    /**
     * Check if the path was set up previously
     * @throws IllegalStateException Exception throw if the path wasn't set properly
     */
    protected void requiresPath() throws IllegalStateException {
        if (this.fileRef == null)
            throw new IllegalStateException("Please set the path before using any methods.");
    }

}
