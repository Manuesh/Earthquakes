package it.serverbooster.app.earthquakes;

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
        }

    }
}
