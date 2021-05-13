package com.ncnf.storage;

import android.content.Context;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ncnf.mocks.MockTask;

import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class CacheFileStoreTests {

    private final byte[] data = new byte[] {42, 24};
    private final String directory = "events";
    private final String filename = "event.jpg";

    @Test
    public void theFileIsNotInTheCache() throws IOException {
        FirebaseStorage storage = Mockito.mock(FirebaseStorage.class, Mockito.RETURNS_DEEP_STUBS);
        StorageReference fileRef = Mockito.mock(StorageReference.class);
        Context context = Mockito.mock(Context.class);
        MockTask<byte[]> task = new MockTask<>(data, new Exception());

        when(fileRef.getBytes(anyLong())).thenReturn(task);
        when(storage.getReference().child(anyString()).child(anyString())).thenReturn(fileRef);

        TemporaryFolder folder = new TemporaryFolder();
        folder.create();
        when(context.getCacheDir()).thenReturn(folder.getRoot());

        CacheFileStore fileStore = new CacheFileStore(storage);
        fileStore.setContext(context);
        fileStore.setPath(directory, filename);

        CompletableFuture<byte[]> future = fileStore.download();

        File file = new File(folder.getRoot() + "/" + directory,filename);
        assertTrue(file.exists());

        try {
            assertEquals(data, future.get());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }

    }

    @Test
    public void fileIsInTheCache() throws IOException {
        FirebaseStorage storage = Mockito.mock(FirebaseStorage.class, Mockito.RETURNS_DEEP_STUBS);
        StorageReference fileRef = Mockito.mock(StorageReference.class);
        Context context = Mockito.mock(Context.class);

        when(storage.getReference().child(anyString()).child(anyString())).thenReturn(fileRef);

        TemporaryFolder folder = new TemporaryFolder();
        folder.create();
        File dir = folder.newFolder(directory);
        File file = new File(dir, filename);
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(data);
        fos.close();

        when(context.getCacheDir()).thenReturn(folder.getRoot());

        CacheFileStore fileStore = new CacheFileStore(storage);
        fileStore.setContext(context);
        fileStore.setPath(directory, filename);
        CompletableFuture<byte[]> future = fileStore.download();

        try {
            assertArrayEquals(data, (byte[]) future.get());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void throwsExceptionIfNoContext() {
        FirebaseStorage storage = Mockito.mock(FirebaseStorage.class, Mockito.RETURNS_DEEP_STUBS);

        CacheFileStore fileStore = new CacheFileStore(storage);

        assertThrows(IllegalStateException.class, () -> {
           fileStore.setPath(directory, filename);
        });
    }
}
