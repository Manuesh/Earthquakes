package it.serverbooster.app.earthquakes;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;

import java.util.List;

import it.serverbooster.app.earthquakes.databinding.FragmentListBinding;
import it.serverbooster.app.earthquakes.model.Earthquake;
import it.serverbooster.app.earthquakes.service.MainViewModel;

public class ListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private FragmentListBinding binding;

    private SearchView searchView;

    private SearchBar searchBar;

    private OnBackPressedCallback onBackPressedCallback;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);
        searchBar = binding.searchBar;
        searchView = binding.searchView;
        searchView.setupWithSearchBar(searchBar);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainViewModel mainViewModel = new ViewModelProvider(requireActivity())
                .get(MainViewModel.class);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        binding.swipeRefreshLayout.setOnRefreshListener(this::onRefresh);

        mainViewModel.getEarthquakes().observe(getViewLifecycleOwner(), new Observer<List<Earthquake>>() {
            @Override
            public void onChanged(List<Earthquake> earthquakes) {
                binding.recyclerView.setAdapter(new EarthquakeAdapter(earthquakes));
            }
        });

        binding.searchRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));


        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressedCallback.setEnabled(true);
                searchView.show();
            }
        });

        searchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mainViewModel.searchEarthquakes(s.toString()).observe(getViewLifecycleOwner(), new Observer<List<Earthquake>>() {
                    @Override
                    public void onChanged(List<Earthquake> earthquakes) {
                        binding.searchRecyclerView.setAdapter(new EarthquakeAdapter(earthquakes, false));
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (searchView.isShowing()) {
                    searchView.hide();
                } else {
                    setEnabled(false);
                    requireActivity().onBackPressed();
                }
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), onBackPressedCallback);
    }

    @Override
    public void onRefresh() {

        MainViewModel mainViewModel = new ViewModelProvider(requireActivity())
                .get(MainViewModel.class);
        mainViewModel.getRefreshedEarthquakes().observe(getViewLifecycleOwner(), new Observer<List<Earthquake>>() {
            @Override
            public void onChanged(List<Earthquake> earthquakes) {
                binding.recyclerView.setAdapter(new EarthquakeAdapter(earthquakes));
            }
        });

        binding.swipeRefreshLayout.setRefreshing(false);
    }
}
