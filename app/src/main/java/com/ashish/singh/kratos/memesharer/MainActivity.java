package com.ashish.singh.kratos.memesharer;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ashish.singh.kratos.memesharer.MySingleton.MySingleton;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBar;
    ImageView imageView;
    Button share_button;
    Button next_button;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        progressBar = findViewById(R.id.spinner);
        imageView = findViewById(R.id.meme_image);
        next_button = findViewById(R.id.next_button);
        share_button = findViewById(R.id.share_button);
        next_button.setOnClickListener(view -> loadMemeImage());
        share_button.setOnClickListener(view -> shareMeme());

        loadMemeImage();
    }

    public void loadMemeImage(){

        progressBar.setVisibility(View.VISIBLE);
        next_button.setEnabled(false);
        share_button.setEnabled(false);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "https://meme-api.herokuapp.com/gimme",
                null, response -> {
                    try {
                        url = response.getString("url");
                        Glide.with(MainActivity.this).load(url).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                imageView.setImageResource(R.drawable.failed);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                share_button.setEnabled(true);
                                next_button.setEnabled(true);
                                return false;
                            }
                        }).into(imageView);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            progressBar.setVisibility(View.GONE);
            imageView.setImageResource(R.drawable.failed);
        });

// Add a request (in this example, called stringRequest) to your RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    public void shareMeme(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, url);
        startActivity(Intent.createChooser(intent,"Share this funny meme"));
    }
}