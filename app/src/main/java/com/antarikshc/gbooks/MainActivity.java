package com.antarikshc.gbooks;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<BookData>> {

    public static final String LOG_TAG = MainActivity.class.getName();

    /**
     * URL to fetch data
     **/
    private static String BOOKS_API_URL = null;

    //Books loaded ID, default = 1 currently using single Loader
    private static int BOOKS_LOADER_ID = 1;

    /**
     * global declarations
     **/
    ListView booksListView;
    private CustomAdapter customAdapter;
    private SearchView searchBar;
    LoaderManager loaderManager;
    private TextView EmptyStateTextView;
    ProgressBar loadSpin;
    RelativeLayout rootLayout;
    LinearLayout searchLayout;

    //Boolean value to let us know whether Search Bar is translated or not
    boolean searchBarInCenter;

    //Use this to show "Tap on search!" when user open this app, rest of the time no books
    boolean firstSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bind all views from xml to variables
        rootLayout = findViewById(R.id.rootLayout);

        booksListView = findViewById(R.id.booksListView);

        searchBar = findViewById(R.id.searchBar);
        searchLayout = findViewById(R.id.searchLayout);

        loadSpin = findViewById(R.id.loadSpin);
        EmptyStateTextView = findViewById(R.id.emptyView);

        //on click listener to perform search operation
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Clear focus so that search bar can be translated back up top
                searchBar.setQuery("", false);
                searchBar.clearFocus();

                //remove the Empty State msg to make room for spinner
                EmptyStateTextView.setText("");

                //this will spin till View.GONE is called at onLoadFinished
                loadSpin.setVisibility(View.VISIBLE);

                BOOKS_API_URL = "https://www.googleapis.com/books/v1/volumes?q=" + query + "&maxResults=10&key=AIzaSyC3xxnV71jfPxUp0C9iORZPnTsLInrO1Bg";

                //destroy previous loader and increment the loader id
                booksListView.animate().alpha(0.1f).setDuration(400);
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

        //animate search bar and book list when users tap on search
        searchBar.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //if in center, translate up and vice-versa
                if (searchBarInCenter) {
                    searchLayout.animate().translationYBy(-500f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
                    booksListView.animate().translationYBy(-500f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
                    booksListView.animate().alpha(1f).setDuration(400);
                    EmptyStateTextView.animate().alpha(1f).setDuration(400);

                    searchBarInCenter = false;

                } else if (!searchBarInCenter) {
                    searchLayout.animate().translationYBy(500f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
                    booksListView.animate().translationYBy(500f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
                    booksListView.animate().alpha(0f).setDuration(400);
                    EmptyStateTextView.animate().alpha(0f).setDuration(400);

                    searchBarInCenter = true;
                }
            }
        });

        //we are not searching for books onCreate
        loadSpin.setVisibility(View.GONE);

        customAdapter = new CustomAdapter(getApplicationContext(), new ArrayList<BookData>());
        //setting customAdapter for book list view
        booksListView.setAdapter(customAdapter);

        booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BookData currentData = customAdapter.getItem(i);

                Intent intent = new Intent(getApplicationContext(), BookActivity.class);

                if (currentData != null) {
                    ArrayList<String> data = new ArrayList<>(
                            Arrays.asList(
                                    currentData.getTitle(),
                                    currentData.getAuthor(),
                                    currentData.getPublisher(),
                                    currentData.getDesc(),
                                    currentData.getBuyString(),
                                    currentData.getPreviewUrl(),
                                    currentData.getBuyUrl()));

                    intent.putStringArrayListExtra("data", data);

                    Bitmap bitmap = currentData.getCoverImage();
                    ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
                    byte[] byteArray = bStream.toByteArray();

                    intent.putExtra("image", byteArray);

                    startActivity(intent);

                } else {
                    Toast.makeText(MainActivity.this, "No Data found for selected book!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //set empty text view for a proper msg to user
        booksListView.setEmptyView(EmptyStateTextView);

        loaderManager = getLoaderManager();
        boolean netConnection = checkNet();
        if (netConnection) {
            //we need to call Loader in onCreate
            //else it won't persist through orientations
            executeLoader();
        } else {
            EmptyStateTextView.setText(R.string.no_network);
            loadSpin.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //we dont want search bar in focus when user returns to Main screen
        searchBar.setQuery("", false);
        rootLayout.requestFocus();
        booksListView.setVisibility(View.VISIBLE);
    }

    //Initiate and destroy loader methods to be called after search is submitted
    private void executeLoader() {
        loaderManager.initLoader(BOOKS_LOADER_ID, null, this);
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
        if (!firstSearch) {
            EmptyStateTextView.setText(R.string.tap_on_search);
            firstSearch = true;
        } else {
            EmptyStateTextView.setText(R.string.no_books);
        }

        // Clear the adapter of previous books data
        customAdapter.clear();
        loadSpin.setVisibility(View.GONE);

        if (books != null && !books.isEmpty()) {
            customAdapter.addAll(books);
            booksListView.animate().alpha(1.0f).setDuration(400);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<BookData>> loader) {
        // Loader reset, so we can clear out our existing data.
        customAdapter.clear();
    }

    //Check internet is connected or not, to notify user
    public boolean checkNet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}