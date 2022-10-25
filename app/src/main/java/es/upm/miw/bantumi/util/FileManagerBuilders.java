package es.upm.miw.bantumi.util;

import android.content.Context;

public interface FileManagerBuilders {

    interface ApplicationContext {
        public FileName applicationContext(Context context);
    }

    interface FileName {
        public Optionals fileName(String fileName);
    }

    interface Optionals  {
        public Optionals filePath(String filePath);
        public Optionals storageSystem(FileManager.StorageSystem storageSystem);
        public FileManager build();
    }
}
