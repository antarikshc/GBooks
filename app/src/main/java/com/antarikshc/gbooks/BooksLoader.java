package com.antarikshc.gbooks;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;

public class BooksLoader extends AsyncTaskLoader<ArrayList<BookData>> {

    private String mUrl;

    BooksLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<BookData> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        ArrayList<BookData> earthquakes = ConnectAPI.fetchBookData(mUrl);
        return earthquakes;

    }
}
