package com.incwo.facilescan.scan;

import org.junit.jupiter.api.*;

import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

class BusinessFileXmlParsingTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void readFromXmlContent() {
    }

    @Test
    void readsAsset() throws Exception {
        String xml = TestUtils.textFromAsset("FormsBusinessFiles.xml");
        assertThat(xml, notNullValue());
    }
}