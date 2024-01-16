package it.serverbooster.app.earthquakes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.search.SearchBar;

import java.util.List;

import it.serverbooster.app.earthquakes.databinding.FragmentListBinding;
import it.serverbooster.app.earthquakes.databinding.FragmentSearchBinding;
import it.serverbooster.app.earthquakes.model.Earthquake;
import it.serverbooster.app.earthquakes.service.MainViewModel;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;

    private SearchView searchView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        searchView = binding.searchView;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainViewModel mainViewModel = new ViewModelProvider(requireActivity())
                .get(MainViewModel.class);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                mainViewModel.searchEarthquakes(query).observe(getViewLifecycleOwner(), new Observer<List<Earthquake>>() {
                    @Override
                    public void onChanged(List<Earthquake> earthquakes) {
                        binding.recyclerView.setAdapter(new EarthquakeAdapter(earthquakes));
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                mainViewModel.searchEarthquakes(newText).observe(getViewLifecycleOwner(), new Observer<List<Earthquake>>() {
                    @Override
                    public void onChanged(List<Earthquake> earthquakes) {
                        binding.recyclerView.setAdapter(new EarthquakeAdapter(earthquakes));
                    }
                });
                return true;
            }

        });

    }

}
