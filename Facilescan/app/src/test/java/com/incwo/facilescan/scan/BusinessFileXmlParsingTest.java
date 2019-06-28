package com.incwo.facilescan.scan;

import org.junit.jupiter.api.*;

import java.io.InputStream;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

class BusinessFileXmlParsingTest {

    @Test
    void parsesStructure() throws Exception {
        BusinessFile businessFile = getFirstBusinessFile();
        assertEquals("30", businessFile.id);
        assertEquals("incwo", businessFile.name);
        assertEquals("Bureau Virtuel", businessFile.kind);

        assertEquals(6, businessFile.getForms().size()); // 7 children if counting the folder
    }

    @Test
    void parsesContactForm() throws Exception {
        BusinessFile businessFile = getFirstBusinessFile();
        Form contactForm = businessFile.getForms().get(0);
        assertEquals("Contact", contactForm.className);
        assertEquals("contacts", contactForm.type);

        FormField firstNameField = contactForm.fields.get(0);
        assertEquals("Prénom", firstNameField.name);
        assertEquals("first_name", firstNameField.key);
        assertEquals("string", firstNameField.type);

        FormField nameField = contactForm.fields.get(1);
        assertEquals("Nom", nameField.name);
        assertEquals("last_name", nameField.key);
        assertEquals("string", nameField.type);

        FormField firmField = contactForm.fields.get(2);
        assertEquals("Société", firmField.name);
        assertEquals("firm", firmField.key);
        assertEquals("string", firmField.type);
    }

    @Test
    void parsesToDoForm() throws Exception {
        BusinessFile businessFile = getFirstBusinessFile();
        Form contactForm = businessFile.getForms().get(3);
        assertEquals("A faire", contactForm.className);
        assertEquals("tasks", contactForm.type);

        FormField whatField = contactForm.fields.get(0);
        assertEquals("Faire quoi", whatField.name);
        assertEquals("title", whatField.key);
        assertEquals("string", whatField.type);

        FormField whenField = contactForm.fields.get(1);
        assertEquals("Quand", whenField.name);
        assertEquals("days", whenField.key);
        assertEquals("enum", whenField.type);
        assertEquals("1", whenField.values.get(0));
        assertEquals("Demain", whenField.valueTitles.get(0));
        assertEquals("30", whenField.values.get(4));
        assertEquals("D'ici 1 mois", whenField.valueTitles.get(4));

        FormField whoField = contactForm.fields.get(2);
        assertEquals("Qui", whoField.name);
        assertEquals("assigned_user_id", whoField.key);
        assertEquals("enum", whoField.type);
        assertEquals("537348", whoField.values.get(15));
        assertEquals("Renaud Pradenc", whoField.valueTitles.get(15));
    }

    @Test
    void parsesProposalSheet() throws Exception {
        BusinessFile businessFile = getFirstBusinessFile();
        Form form = businessFile.getForms().get(5);
        assertEquals("Signer le BL BL1612-00224", form.className);
        assertEquals("proposal_sheets+3003726", form.type);

        FormField field = form.fields.get(0);
        assertEquals("Signature", field.name);
        assertEquals("my_signature", field.key);
        assertEquals("signature", field.type);
        assertEquals("Je valide la livraison BL1612-00224", field.description);
    }


    private BusinessFile getFirstBusinessFile() throws Exception {
        BusinessFilesList businessFilesList = getBusinessFilesList();
        assertEquals(1, businessFilesList.businessFiles.size());

        return businessFilesList.businessFiles.get(0);
    }

    private BusinessFilesList getBusinessFilesList() throws Exception {
        String xml = TestUtils.textFromAsset("FormsBusinessFiles.xml");
        assertThat(xml, notNullValue());

        BusinessFileXmlParsing parsing = new BusinessFileXmlParsing();
        return parsing.readFromXmlContent(xml);
    }

}