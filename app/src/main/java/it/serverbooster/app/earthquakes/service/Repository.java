package it.serverbooster.app.earthquakes.service;

import android.content.Context;

import it.serverbooster.app.earthquakes.service.Request;

public class Repository {

    public static void downloadData(Context context, Request.RequestCallback callback){
        Request.getInstance(context).requestDownload(callback);
    }


}
