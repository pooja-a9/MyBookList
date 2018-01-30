package com.example.android.mybooklist;

import java.util.List;

/**
 * Created by chaitanya on 11/15/2016.
 */

public class Book {

    public final String title;
    public final List<String> authors;


    public Book(String t, List<String> a) {
        title = t;
        authors = a;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getAuthors() {
        return authors;
    }
}
