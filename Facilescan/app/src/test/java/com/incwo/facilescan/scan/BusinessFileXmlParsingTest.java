package com.incwo.facilescan.scan;

import org.junit.jupiter.api.*;

import java.util.ArrayList;

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

        assertEquals(7, businessFile.getChildren().size());
    }

    @Test
    void parsesContactForm() throws Exception {
        BusinessFile businessFile = getFirstBusinessFile();
        Form contactForm = (Form)(businessFile.getChildren().get(0));
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
        Form contactForm = (Form)(businessFile.getChildren().get(3));
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
        FormField.KeyValue keyValue0 = whenField.keyValues.get(0);
        assertEquals("1", keyValue0.key);
        assertEquals("Demain", keyValue0.value);
        FormField.KeyValue keyValue4 = whenField.keyValues.get(4);
        assertEquals("30", keyValue4.key);
        assertEquals("D'ici 1 mois", keyValue4.value);

        FormField whoField = contactForm.fields.get(2);
        assertEquals("Qui", whoField.name);
        assertEquals("assigned_user_id", whoField.key);
        assertEquals("enum", whoField.type);
        FormField.KeyValue keyValue15 = whoField.keyValues.get(15);
        assertEquals("537348", keyValue15.key);
        assertEquals("Renaud Pradenc", keyValue15.value);
    }

    @Test
    void parsesProposalSheet() throws Exception {
        BusinessFile businessFile = getFirstBusinessFile();
        ArrayList<Object> formsOrFolders = businessFile.getChildren();
        Form lastForm = (Form)(formsOrFolders.get(formsOrFolders.size()-1));
        assertEquals("Signer le BL BL1612-00224", lastForm.className);
        assertEquals("proposal_sheets+3003726", lastForm.type);

        FormField field = lastForm.fields.get(0);
        assertEquals("Signature", field.name);
        assertEquals("my_signature", field.key);
        assertEquals("signature", field.type);
        assertEquals("Je valide la livraison BL1612-00224", field.description);
    }

    @Test
    void parsesFormFolder() throws Exception {
        BusinessFile businessFile = getFirstBusinessFile();
        FormFolder folder = (FormFolder)(businessFile.getChildren().get(5));
        assertEquals("Bons de livraison à signer", folder.getTitle());
        assertEquals(11, folder.getForms().size());

        Form form = folder.getForms().get(0);
        assertEquals("Signer le BL BL1906-00242", form.className);
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