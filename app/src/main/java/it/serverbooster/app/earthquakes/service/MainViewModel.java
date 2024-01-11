package it.serverbooster.app.earthquakes.service;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.serverbooster.app.earthquakes.Earthquakes;
import it.serverbooster.app.earthquakes.database.DB;
import it.serverbooster.app.earthquakes.model.Earthquake;


public class MainViewModel extends AndroidViewModel {

    private MutableLiveData<List<Earthquake>> earthquakes = new MutableLiveData<>();

    private Repository repository;

    public MainViewModel(@NonNull Application application) {
        super(application);

        this.repository = ((Earthquakes) application).getRepository();

        new Thread(() -> {

            List<Earthquake> list = DB.getInstance(application).getEarthquakesDAO().findAll();

            if (list.isEmpty()) {

                repository.downloadData(application, new Request.RequestCallback() {

                    @Override
                    public void onCompleted(UrlRequest request, UrlResponseInfo info, byte[] data, CronetException error) {

                        List<Earthquake> tempEarthquakes = new ArrayList<>();

                        if (data != null) {
                            String response = new String(data);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray features = jsonObject.optJSONArray("features");
                                for (int i = 0; i < features.length(); i++) {
                                    JSONObject feature = features.optJSONObject(i);
                                    Earthquake earthquake = Earthquake.parseJson(feature);;
                                    tempEarthquakes.add(earthquake);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (error != null) {
                                error.printStackTrace();
                            }
                        }
                        DB.getInstance(getApplication()).getEarthquakesDAO().insert(tempEarthquakes);
                        earthquakes.postValue(tempEarthquakes);
                    }

                });
            } else {
                earthquakes.postValue(list);
            }

        }).start();

    }

    private void refresh() {
        new Thread(() -> {
            repository.downloadData(getApplication(), new Request.RequestCallback() {

                @Override
                public void onCompleted(UrlRequest request, UrlResponseInfo info, byte[] data, CronetException error) {

                    List<Earthquake> tempEarthquakes = new ArrayList<>();

                    if (data != null) {
                        String response = new String(data);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray features = jsonObject.optJSONArray("features");
                            for (int i = 0; i < features.length(); i++) {
                                JSONObject feature = features.optJSONObject(i);
                                Earthquake earthquake = Earthquake.parseJson(feature);;
                                tempEarthquakes.add(earthquake);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (error != null) {
                            error.printStackTrace();
                        }
                    }
                    DB.getInstance(getApplication()).getEarthquakesDAO().insert(tempEarthquakes);
                    earthquakes.postValue(tempEarthquakes);
                }

            });
        }).start();
    }

    public LiveData<List<Earthquake>> getEarthquakes() {
        return earthquakes;
    }

    public LiveData<List<Earthquake>> getRefreshedEarthquakes() {
        refresh();
        return earthquakes;
    }

}
