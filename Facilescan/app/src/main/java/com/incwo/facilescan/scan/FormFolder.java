package com.incwo.facilescan.scan;

import java.util.ArrayList;

public class FormFolder {
    private String mTitle;
    private ArrayList<Form> mForms;

    FormFolder(String title) {
        mTitle = title;
        mForms = new ArrayList<Form>();
    }

    public String getTitle() {
        return mTitle;
    }

    public ArrayList<Form> getForms() {
        return mForms;
    }

    public void addForm(Form form) {
        mForms.add(form);
    }
}
