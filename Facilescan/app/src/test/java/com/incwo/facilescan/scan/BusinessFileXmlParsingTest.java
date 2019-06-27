package com.incwo.facilescan.scan;

import org.junit.jupiter.api.*;

import java.io.InputStream;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

class BusinessFileXmlParsingTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void parsesStructure() throws Exception {
        String xml = TestUtils.textFromAsset("FormsBusinessFiles.xml");
        assertThat(xml, notNullValue());

        BusinessFileXmlParsing parsing = new BusinessFileXmlParsing();
        BusinessFilesList businessFilesList = parsing.readFromXmlContent(xml);
        assertEquals(1, businessFilesList.businessFiles.size());

        BusinessFile businessFile = businessFilesList.businessFiles.get(0);
        assertEquals("30", businessFile.id);
        assertEquals("incwo", businessFile.name);
        assertEquals("Bureau Virtuel", businessFile.kind);

        assertEquals(17, businessFile.getForms().size());
    }


}