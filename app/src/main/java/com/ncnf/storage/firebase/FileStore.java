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

/**
 * Handle the upload/download of files via the Firebase Storage module
 */
public class FileStore {

    // maximum file size
    private final long MAX_SIZE = 10 * 1024 * 1024;

    private final FirebaseStorage storage;
    private StorageReference fileRef;

    /**
     * Create a file storage with the given adapter
     * @param storage a Firebase Storage adapter
     */
    @Inject
    public FileStore(FirebaseStorage storage) {
        this.storage = storage;
    }

    /**
     * Create a file storage with the default Firebase Storage adapter
     */
    public FileStore() {
        this.storage = FirebaseStorage.getInstance();
    }

    /**
     * Set the path where the file will be saved
     * Call this method before any other in the store
     * @param directory directory of the file
     * @param filename name of the file
     */
    public void setPath(String directory, String filename) {
        this.fileRef = storage.getReference().child(directory).child(filename);
    }

    /**
     * Upload an image
     * @param bitmap a bitmap of the image
     * @return a future with whether the upload was successful
     */
    public CompletableFuture<Boolean> uploadImage(Bitmap bitmap) {
        requiresPath();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        return upload(data);
    }

    /**
     * Upload a file
     * @param data an array of bytes
     * @return a future with whether the upload was successful
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
     * Download an image and update an ImageView
     * @param view the view which will contain the image
     * @param defaultImage an image if the download fails
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
     * Download a file
     * @return a future with the content of the file as an array of bytes
     */
    public CompletableFuture<byte[]> download() {
        requiresPath();

        CompletableFuture<byte[]> future = new CompletableFuture<>();

        Task<byte[]> download = fileRef.getBytes(MAX_SIZE);
        download.addOnSuccessListener(future::complete);
        download.addOnFailureListener(future::completeExceptionally);

        return future;
    }

    // raise an exception if the path is not set
    protected void requiresPath() throws IllegalStateException {
        if (this.fileRef == null)
            throw new IllegalStateException("Please set the path before using any methods.");
    }

}
