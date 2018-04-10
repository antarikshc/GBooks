package com.antarikshc.gbooks;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class BookActivity extends AppCompatActivity {

    private TextView txtTitle;
    private TextView txtAuthor;
    private TextView txtPublisher;
    private TextView txtDesc;
    private ImageView coverImage;
    private Button btnBuy;

    Intent webIntent;
    ArrayList<String> data;
    String buyString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        txtTitle = findViewById(R.id.txtTitle);
        txtAuthor = findViewById(R.id.txtAuthor);
        txtPublisher = findViewById(R.id.txtPublisher);

        txtDesc = findViewById(R.id.txtDesc);
        txtDesc.setMovementMethod(new ScrollingMovementMethod());

        coverImage = findViewById(R.id.coverImage);
        btnBuy = findViewById(R.id.btnBuy);


        Intent intent = getIntent();

        /** sequence of ArrayList
         0 Title(),
         1 Author(),
         2 Publisher(),
         3 Desc(),
         4 BuyString(),
         5 PreviewUrl(),
         6 BuyUrl() **/
        data = intent.getStringArrayListExtra("data");

        txtTitle.setText(data.get(0));
        txtAuthor.setText(data.get(1));
        txtPublisher.setText(data.get(2));
        txtDesc.setText(data.get(3));

        buyString = data.get(4);
        if (buyString.equals("NOT FOR SALE")) {
            btnBuy.setText(buyString);
        } else {
            btnBuy.setText("Buy " + buyString);
        }


        Bitmap bitmap;
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        coverImage.setImageBitmap(bitmap);

        webIntent = new Intent(getApplicationContext(), WebviewActivity.class);

    }

    public void previewClick(View view) {
        webIntent.putExtra("url", data.get(5));
        startActivity(webIntent);
    }

    public void buyClick(View view) {
        if (buyString.equals("NOT FOR SALE")) {
            Toast.makeText(this, "No purchase link.", Toast.LENGTH_SHORT).show();
        } else {
            webIntent.putExtra("url", data.get(6));
            startActivity(webIntent);
        }

    }
}
