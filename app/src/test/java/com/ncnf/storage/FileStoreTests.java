package com.ncnf.storage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ncnf.database.DatabaseResponse;
import com.ncnf.mocks.MockTask;
import com.ncnf.storage.FileStore;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FileStoreTests {

    private final byte[] data = new byte[] {42, 24};
    private final String directory = "events";
    private final String filename = "event.jpg";

    @Test
    public void uploadImageIsSuccessful() {
        FirebaseStorage storage = Mockito.mock(FirebaseStorage.class, Mockito.RETURNS_DEEP_STUBS);
        StorageReference fileRef = Mockito.mock(StorageReference.class);
        UploadTask task = Mockito.mock(UploadTask.class);
        Bitmap bitmap = Mockito.mock(Bitmap.class);

        when(storage.getReference().child(anyString()).child(anyString())).thenReturn(fileRef);
        when(fileRef.putBytes(any(byte[].class))).thenReturn(task);
        when(task.addOnSuccessListener(any())).thenReturn(task);
        when(task.addOnFailureListener(any())).thenReturn(task);

        FileStore file = new FileStore(storage, directory, filename);

        file.uploadImage(bitmap);

        verify(fileRef).putBytes(any(byte[].class));
    }

    @Test
    public void downloadIsSuccessful() {
        FirebaseStorage storage = Mockito.mock(FirebaseStorage.class, Mockito.RETURNS_DEEP_STUBS);
        StorageReference fileRef = Mockito.mock(StorageReference.class);
        MockTask<byte[]> task = new MockTask<>(data, null);
        ImageView view = Mockito.mock(ImageView.class);

        when(storage.getReference().child(anyString()).child(anyString())).thenReturn(fileRef);
        when(fileRef.getBytes(anyLong())).thenReturn(task);

        FileStore file = new FileStore(storage, directory, filename);

        CompletableFuture<DatabaseResponse> future = file.download();

        try {
            assertEquals(data, future.get().getResult());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

}
