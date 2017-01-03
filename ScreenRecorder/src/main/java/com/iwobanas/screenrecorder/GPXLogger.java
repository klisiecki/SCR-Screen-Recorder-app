package com.iwobanas.screenrecorder;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.location.LocationManager.GPS_PROVIDER;

public class GPXLogger implements LocationListener {

    private static final int GPS_DEFAULT_MIN_TIME = 500;
    private static final int MIN_GPS_METERS = 0;

    public static final String TAG = "scr_GPXLogger";

    private final Context context;
    private final List<Location> locations;
    private final LocationManager locationManager;
    private File outputFile;

    public GPXLogger(Context context) {
        this.context = context;
        this.locations = new ArrayList<>();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Log.d(TAG, this.getClass().getSimpleName() + " created");
    }

    public void init(File outputFile) {
        locationManager.requestLocationUpdates(GPS_PROVIDER, GPS_DEFAULT_MIN_TIME, MIN_GPS_METERS, this);
        locations.clear();
        this.outputFile = outputFile;
        Log.d(TAG, this.getClass().getSimpleName() + " initialized");

    }

    public void saveFile() {
        locationManager.removeUpdates(this);
        writeLocations(outputFile, locations);
        Log.d(TAG, "saved GPX file to " + outputFile.getName());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "new location: " + location.toString());
        locations.add(location);
    }

    private void writeLocations(File file, List<Location> points) {
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"SCR Screen Recorder\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\"><trk>\n";
        String name = "<name>gpx</name><trkseg>\n";

        String segments = "";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
        for (Location l : points) {
            segments += "<trkpt lat=\"" + l.getLatitude() + "\" lon=\"" + l.getLongitude() + "\">" +
                    "<time>" + df.format(new Date(l.getTime())) + "</time>" +
                    "<speed>" + l.getSpeed() + "</speed>" +
                    "</trkpt>\n";
        }

        String footer = "</trkseg></trk></gpx>";

        try {
            FileWriter writer = new FileWriter(file);
            writer.append(header);
            writer.append(name);
            writer.append(segments);
            writer.append(footer);
            writer.flush();
            writer.close();
            Log.i(TAG, "Saved " + points.size() + " points.");
        } catch (IOException e) {
            Toast.makeText(context, "Error while writing GPX", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error Writting Path",e);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged " + provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "onProviderEnabled " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "onProviderDisabled " + provider);
    }
}
