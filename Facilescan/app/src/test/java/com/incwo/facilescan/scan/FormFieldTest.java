package com.incwo.facilescan.scan;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static com.incwo.facilescan.scan.TestUtils.serializeToFile;
import static org.junit.jupiter.api.Assertions.*;

class FormFieldTest {
    @Test
    void serializeField() throws Exception {
        FormField field = new FormField();
        field.name = "TestName";
        field.key = "TestKey";
        field.type = "TestType";
        field.classValue = "TestClassValue";
        ArrayList<FormField.KeyValue> keyValues = new ArrayList<>();
        keyValues.add(new FormField.KeyValue("TestKey", "TestValue"));
        field.keyValues = keyValues;
        field.description = "TestDescription";

        serializeToFile(field, "TestFormField.out");
    }
}