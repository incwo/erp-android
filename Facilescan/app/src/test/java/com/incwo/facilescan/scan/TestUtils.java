package com.incwo.facilescan.scan;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;

public class TestUtils {

    public static String textFromAsset(String assetName) throws Exception {
        InputStream inputStream = TestUtils.class.getClassLoader().getResourceAsStream(assetName);
        return readTextStream(inputStream);
    }

    public static String readTextStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }

    public static void serializeToFile(Object object, String filename) {
        try {
            FileOutputStream file = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(file);

            out.writeObject(object);

            out.close();
            file.close();

            System.out.println("Object has been serialized");
        } catch(IOException ex) {
            System.out.println("IOException is caught");
        }
    }
}
