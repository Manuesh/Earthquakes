package it.serverbooster.app.earthquakes.service;

import android.content.Context;

import org.chromium.net.CronetEngine;
import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.time.LocalDate;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Request {

    private static volatile Request instance = null;

    public static synchronized Request getInstance(Context context) {
        if(instance == null) {
            synchronized (Request.class) {
                if(instance == null) {
                    instance = new Request(context);
                }
            }
        }
        return instance;
    }

    private Request(Context context) {
        cronetEngine = new CronetEngine.Builder(context)
                .enableHttp2(true)
                .enableQuic(true)
                .enableBrotli(true)
                .setStoragePath(context.getCacheDir().getAbsolutePath())
                .enableHttpCache(CronetEngine.Builder.HTTP_CACHE_DISK, 100 * 1024 * 1024)
                .build();
    }


    private CronetEngine cronetEngine;

    private final Executor executor = Executors.newSingleThreadExecutor();

    public void requestDownload(RequestCallback callback){
        String url = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&latitude=41.8719&longitude=12.5674&maxradius=5";
        url += this.getMonthlyResults();
        cronetEngine.newUrlRequestBuilder(url, callback, executor)
                .build()
                .start();
    }

    public String getMonthlyResults(){
        String queryPiece = "&starttime=";
        LocalDate now = LocalDate.now().minusMonths(2);
        queryPiece += now.toString();
        return queryPiece;
    }


    public abstract static class RequestCallback extends UrlRequest.Callback {

        private final int BUFFER_SIZE = 1024 * 1024; // 1MB

        private final ByteArrayOutputStream received = new ByteArrayOutputStream();

        private final WritableByteChannel channel = Channels.newChannel(received);


        @Override
        public void onRedirectReceived(UrlRequest request, UrlResponseInfo info, String newLocationUrl) throws Exception {
            request.followRedirect();
        }


        @Override
        public void onResponseStarted(UrlRequest request, UrlResponseInfo info) throws Exception {
            if(info.getHttpStatusCode() == 200) {
                request.read(ByteBuffer.allocateDirect(BUFFER_SIZE));
            }
        }

        @Override
        public void onReadCompleted(UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) throws Exception {
            byteBuffer.flip();
            try{
                channel.write(byteBuffer);
            } catch (Exception e) {
                e.printStackTrace();
            }

            byteBuffer.clear();
            request.read(byteBuffer);
        }

        @Override
        public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
            byte[] data = received.toByteArray();
            onCompleted(request, info, data, null);
        }

        @Override
        public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
            onCompleted(request, info, null, error);
        }

        public abstract void onCompleted(UrlRequest request, UrlResponseInfo info, byte[] data, CronetException error);

    }
}
