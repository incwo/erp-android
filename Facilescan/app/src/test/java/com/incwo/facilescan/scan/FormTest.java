package com.incwo.facilescan.scan;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static com.incwo.facilescan.scan.TestUtils.serializeToFile;
import static org.junit.jupiter.api.Assertions.*;

class FormTest {
    @Test
    void serializeForm() {
        Form form = new Form();
        form.className = "TestClassName";
        form.type = "TesType";
        FormField field = new FormField();
        form.fields.add(field);

        serializeToFile(form, "TestForm.out");
    }
}