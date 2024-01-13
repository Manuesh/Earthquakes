package it.serverbooster.app.earthquakes.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

@Entity(tableName = "earthquakes")
public class Earthquake implements Serializable, Comparator<Earthquake> {

    public static Earthquake parseJson(JSONObject feature) {

        if(feature == null) return null;

        JSONObject properties = feature.optJSONObject("properties");
        JSONObject geometry = feature.optJSONObject("geometry");
        JSONArray coordinates = geometry.optJSONArray("coordinates");

        Earthquake earthquake = new Earthquake();
        earthquake.setMagnitude(properties.optDouble("mag"));
        earthquake.setPlace(properties.optString("place"));

        Date date = new Date(Long.valueOf(properties.optString("time")));
        earthquake.setTime(date.toString());

        earthquake.setUrl(properties.optString("url"));
        earthquake.setTitle(properties.optString("title"));
        earthquake.setLatitude(Double.valueOf(coordinates.optString(1)));
        earthquake.setLongitude(Double.valueOf(coordinates.optString(0)));
        earthquake.setId(feature.optString("id"));

        return earthquake;
    }


    private Double magnitude;

    private String place;

    private String time;

    private String Url;

    private String title;

    private Double latitude;

    private Double longitude;

    @PrimaryKey @NonNull
    private String id;


    public Double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(Double magnitude) {
        this.magnitude = magnitude;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int compare(Earthquake e1, Earthquake e2) {
        return e1.getTime().compareTo(e2.getTime());
    }

}
