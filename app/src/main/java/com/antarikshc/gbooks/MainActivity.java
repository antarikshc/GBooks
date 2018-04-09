package com.antarikshc.gbooks;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<BookData>> {

    public static final String LOG_TAG = MainActivity.class.getName();

    /** URL to fetch data **/
    private static final String BOOKS_API_URL =
            "https://www.googleapis.com/books/v1/volumes?q=android&maxResults=10";

    //Books loaded ID, default = 1 currently using single Loader
    private static final int BOOKS_LOADER_ID = 1;

    /** global declarations **/
    ListView booksListView;
    private CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        booksListView = findViewById(R.id.booksListView);

        customAdapter = new CustomAdapter(getApplicationContext(), new ArrayList<BookData>());

        //setting customAdapter for book list view
        booksListView.setAdapter(customAdapter);

        booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BookData currentData = customAdapter.getItem(i);

                Intent intent = new Intent(getApplicationContext(), WebviewActivity.class);
                if (currentData != null) {
                    intent.putExtra("url", currentData.getInfoUrl());
                } else {
                    Toast.makeText(MainActivity.this, "No URL found for requested earthquake!", Toast.LENGTH_SHORT).show();
                }

                startActivity(intent);
            }
        });

        boolean netConnection = checkNet();
        if (netConnection) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(BOOKS_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<ArrayList<BookData>> onCreateLoader(int id, Bundle args) {
        return new BooksLoader(this, BOOKS_API_URL);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<BookData>> loader, ArrayList<BookData> books) {

        // Clear the adapter of previous earthquake data
        customAdapter.clear();


        if (books != null && !books.isEmpty()) {
            customAdapter.addAll(books);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<BookData>> loader) {
        // Loader reset, so we can clear out our existing data.
        customAdapter.clear();
    }

    public boolean checkNet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }

}
