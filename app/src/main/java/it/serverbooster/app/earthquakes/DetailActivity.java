package it.serverbooster.app.earthquakes;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import it.serverbooster.app.earthquakes.databinding.ActivityDetailBinding;
import it.serverbooster.app.earthquakes.model.Earthquake;

public class DetailActivity extends AppCompatActivity {


    public static final String EXTRA_EARTHQUAKE = "extra_earthquake";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailBinding binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Earthquake earthquake = (Earthquake) getIntent().getSerializableExtra(EXTRA_EARTHQUAKE);
        if(earthquake != null) {
            binding.setEarthquake(earthquake);
            binding.shareButton.setOnClickListener(view -> initializeShareIntent(earthquake));
        }
    }

    public void initializeShareIntent(Earthquake earthquake){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, earthquake.getUrl());
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, earthquake.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TITLE, earthquake.getTitle());
        startActivity(Intent.createChooser(shareIntent, null));
    }

}
