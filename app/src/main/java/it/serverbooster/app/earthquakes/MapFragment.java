package it.serverbooster.app.earthquakes;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import it.serverbooster.app.earthquakes.databinding.FragmentMapBinding;
import it.serverbooster.app.earthquakes.model.Earthquake;
import it.serverbooster.app.earthquakes.service.LocationHelper;
import it.serverbooster.app.earthquakes.service.MainViewModel;

public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private GoogleMap map;

    private Marker marker;

    private List<Marker> earthquakeMarkers = new ArrayList<>();

    private List<Earthquake> earthquakes = new ArrayList<>();

    private MainViewModel mainViewModel;

    private FragmentMapBinding binding;

    private ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean granted) {
                    if (granted) {
                        LocationHelper.start(requireContext(), MapFragment.this);
                    } else {
                        Toast.makeText(requireContext(), R.string.location_required, Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragmentMap);
        mapFragment.getMapAsync(this);

        int fineLocation = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (fineLocation == PackageManager.PERMISSION_DENIED) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

    }


    @Override
    public void onResume() {
        super.onResume();

        int fineLocation = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (fineLocation == PackageManager.PERMISSION_GRANTED) {
            LocationHelper.start(requireContext(), this);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        LocationHelper.stop(requireContext(), this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                Earthquake earthquake = (Earthquake) marker.getTag();
                if (earthquake != null) {

                    Bundle bundle = new Bundle();
                    bundle.putSerializable(DetailActivity.EXTRA_EARTHQUAKE, earthquake);

                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_menu_map_to_detailActivity, bundle);
                }
            }
        });

        fillEarthquakesList();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        bounds.include(currentPosition);

        if (marker == null) {
            MarkerOptions opt = new MarkerOptions();
            opt.title("You");
            opt.position(currentPosition);
            marker = map.addMarker(opt);
        } else {
            marker.setPosition(currentPosition);
        }

        if(!earthquakeMarkers.isEmpty()){
            for (Marker marker : earthquakeMarkers) {
                marker.remove();
            }
        }

        earthquakeMarkers.clear();

        new Thread(() -> {
            if (!earthquakes.isEmpty()) {
                for (Earthquake earthquake : earthquakes) {
                    Location earthquakeLocation = new Location("Earthquake");
                    earthquakeLocation.setLatitude(earthquake.getLatitude());
                    earthquakeLocation.setLongitude(earthquake.getLongitude());

                    if (earthquakeLocation.distanceTo(location) <= 100000) {
                        bounds.include(new LatLng(earthquake.getLatitude(), earthquake.getLongitude()));
                        createEarthquake(earthquake);
                    }
                }
            }

            binding.getRoot().post(() -> {
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 16));
            });

        }).start();

    }

    private void fillEarthquakesList() {
        mainViewModel.getEarthquakes().observe(getViewLifecycleOwner(), earthquakes -> {
            MapFragment.this.earthquakes = earthquakes;
        });
    }

    private void createEarthquake(Earthquake earthquake) {
        MarkerOptions options = new MarkerOptions();
        options.title(earthquake.getTitle());
        options.position(new LatLng(earthquake.getLatitude(), earthquake.getLongitude()));
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        requireActivity().runOnUiThread(() -> {
            Marker marker = map.addMarker(options);
            marker.setTag(earthquake);
            earthquakeMarkers.add(marker);
        });
    }

}
