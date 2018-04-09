package com.antarikshc.gbooks;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<BookData>> {

    public static final String LOG_TAG = MainActivity.class.getName();

    /** URL to fetch data **/
    private static String BOOKS_API_URL =
            "https://www.googleapis.com/books/v1/volumes?q=android&maxResults=10";

    //Books loaded ID, default = 1 currently using single Loader
    private static int BOOKS_LOADER_ID = 1;

    /** global declarations **/
    ListView booksListView;
    private CustomAdapter customAdapter;
    private SearchView searchBar;
    LoaderManager loaderManager = getLoaderManager();
    private TextView EmptyStateTextView;
    ProgressBar loadSpin;
    RelativeLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootLayout = findViewById(R.id.rootLayout);

        booksListView = findViewById(R.id.booksListView);
        searchBar = findViewById(R.id.searchBar);

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(LOG_TAG, query);
                BOOKS_API_URL = "https://www.googleapis.com/books/v1/volumes?q=" + query + "&maxResults=10";
                //destroy previous loader and increment the loader id
                destroyLoader(BOOKS_LOADER_ID);
                BOOKS_LOADER_ID += 1;
                executeLoader();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

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
                    Toast.makeText(MainActivity.this, "No URL found for requested books!", Toast.LENGTH_SHORT).show();
                }

                startActivity(intent);
            }
        });

        //set empty text view for a proper msg to user
        EmptyStateTextView = findViewById(R.id.emptyView);
        booksListView.setEmptyView(EmptyStateTextView);

        loadSpin = findViewById(R.id.loadSpin);

        boolean netConnection = checkNet();
        if (netConnection) {
            executeLoader();
        } else {
            EmptyStateTextView.setText(R.string.no_network);
            loadSpin.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchBar.setQuery("", false);
        rootLayout.requestFocus();
    }

    private void executeLoader(){
        loaderManager.initLoader(BOOKS_LOADER_ID, null, this).forceLoad();
    }
    private void destroyLoader(int id) {
        loaderManager.destroyLoader(id);
    }

    @Override
    public Loader<ArrayList<BookData>> onCreateLoader(int id, Bundle args) {
        return new BooksLoader(this, BOOKS_API_URL);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<BookData>> loader, ArrayList<BookData> books) {
        EmptyStateTextView.setText(R.string.no_books);

        // Clear the adapter of previous books data
        customAdapter.clear();


        if (books != null && !books.isEmpty()) {
            loadSpin.setVisibility(View.GONE);
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
