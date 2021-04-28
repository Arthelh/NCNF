package com.ncnf.utilities;

import android.graphics.Bitmap;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FileUploadTests {

    @Test
    public void uploadIsSuccessful() {
        FirebaseStorage storage = Mockito.mock(FirebaseStorage.class, Mockito.RETURNS_DEEP_STUBS);
        StorageReference fileRef = Mockito.mock(StorageReference.class);
        UploadTask task = Mockito.mock(UploadTask.class);
        Bitmap bitmap = Mockito.mock(Bitmap.class);

        when(storage.getReference().child(anyString()).child(anyString())).thenReturn(fileRef);
        when(fileRef.putBytes(any(byte[].class))).thenReturn(task);
        when(task.addOnSuccessListener(any())).thenReturn(task);
        when(task.addOnFailureListener(any())).thenReturn(task);

        FileUpload file = new FileUpload(storage,"/events", "event.jpg");

        file.uploadImage(bitmap);

        verify(fileRef).putBytes(any(byte[].class));
    }

}
