package it.serverbooster.app.earthquakes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.serverbooster.app.earthquakes.databinding.AdapterEarthquakeBinding;
import it.serverbooster.app.earthquakes.model.Earthquake;

public class EarthquakeAdapter extends RecyclerView.Adapter<EarthquakeAdapter.ViewHolder>{

    private List<Earthquake> data;

    public EarthquakeAdapter(List<Earthquake> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterEarthquakeBinding binding = AdapterEarthquakeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Earthquake earthquake = data.get(position);
        holder.onBind(earthquake);
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private AdapterEarthquakeBinding binding;

        public ViewHolder(AdapterEarthquakeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.layoutLinear.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Earthquake earthquake = data.get(getAdapterPosition());
            Bundle bundle = new Bundle();
            bundle.putSerializable(DetailActivity.EXTRA_EARTHQUAKE, earthquake);

            Navigation.findNavController(view).navigate(R.id.action_menu_list_to_detailActivity, bundle);
        }

        public void onBind(Earthquake earthquake) {
            binding.setEarthquake(earthquake);
        }

    }

}
