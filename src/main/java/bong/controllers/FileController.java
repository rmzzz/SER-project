package bong.controllers;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileController {

    public static Object loadBinary(InputStream is) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(is));
        Object temp = ois.readObject();
        ois.close();
        return temp;
    }

    public static void saveBinary(File file, Serializable toBeSaved) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
            oos.writeObject(toBeSaved);
        }
    }

    public static File loadZip(File file) throws Exception {
        String fileName = "";
        byte[] buffer = new byte[1024];
        File destDir = getDataDir();

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file.getAbsolutePath()))) {
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {
                File newFile = new File(destDir, zipEntry.getName());
                fileName = newFile.getName();

                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
                zipEntry = zis.getNextEntry();
            }
        }
        return new File(destDir, fileName);
    }

    public static File getDataDir() {
        String userHome = System.getProperty("user.home");
        String dirName = System.getProperty("bong.dir", userHome + File.separator + "Documents");
        return new File(dirName).getAbsoluteFile();
    }

    public static File getDataFile(String fileName) {
        return new File(getDataDir(), fileName);
    }

}
