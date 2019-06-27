package com.incwo.facilescan.scan;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

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
}
