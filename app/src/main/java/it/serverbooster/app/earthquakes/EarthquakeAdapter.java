package it.serverbooster.app.earthquakes;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
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

    private boolean flag;

    public EarthquakeAdapter(List<Earthquake> data) {
        this.data = data;
        this.flag = true;
    }

    public EarthquakeAdapter(List<Earthquake> data, boolean flag) {
        this.data = data;
        this.flag = false;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterEarthquakeBinding binding = AdapterEarthquakeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding, flag);
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


        public ViewHolder(AdapterEarthquakeBinding binding, boolean flag) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.layoutLinear.setOnClickListener(this);

            if(flag == false) {
                this.binding.cardView.setCardBackgroundColor(this.binding.cardView.getResources().getColor(R.color.material1));
            } else {
                this.binding.cardView.setCardBackgroundColor(this.binding.cardView.getResources().getColor(R.color.material2));
            }
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
