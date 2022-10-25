package es.upm.miw.bantumi.util;

import android.content.Context;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileManager {
    private Context context;
    private String filePath;
    private String fileName;
    private StorageSystem storageSystem;

    private FileManager() {}

    public static FileManagerBuilders.ApplicationContext builder() {
        return new Builder();
    }

    public enum StorageSystem {
        FILE_SYSTEM,
        SD_CARD
    }

    public void save(String data) {
        try(FileOutputStream fos = context.openFileOutput(this.fileName, Context.MODE_PRIVATE)) {
            fos.write(data.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Builder implements FileManagerBuilders.ApplicationContext, FileManagerBuilders.FileName,
            FileManagerBuilders.Optionals {

        private FileManager fileManager;

        public Builder() {
            this.fileManager = new FileManager();
            fileManager.storageSystem = StorageSystem.FILE_SYSTEM;
        }

        @Override
        public FileManagerBuilders.FileName applicationContext(Context context) {
            this.fileManager.context = context;
            return this;
        }

        @Override
        public FileManagerBuilders.Optionals fileName(String fileName) {
            this.fileManager.fileName = fileName;
            return this;
        };

        @Override
        public FileManagerBuilders.Optionals filePath(String filePath) {
            this.fileManager.filePath = filePath;
            return this;
        }

        @Override
        public FileManagerBuilders.Optionals storageSystem(StorageSystem storageSystem) {
            this.fileManager.storageSystem = storageSystem;
            return this;
        }

        @Override
        public FileManager build() {
            return this.fileManager;
        }
    }
}
