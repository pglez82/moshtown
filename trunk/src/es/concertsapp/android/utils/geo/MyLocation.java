package es.concertsapp.android.utils.geo;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Clase que se encarga de obtener en un hilo la ubicación a través del gps o de la red.
 * Lo que hace esta clase es ver si está disponible el gps o el agps. Se lanza la petición tanto al
 * gps como al agps. El dato que primero se devuelva es el que tomamos como válido.
 *
 * Además de esto, se lanza un task que tardará 20 segundos en empezar, que pregunta por la última
 * localización conocida. Esto lo que hace es que si tanto el gps como el agps no nos devuelve nada,
 * utilizaremos la última posición conocida (después de 20 segs).
 *
 * Por otro lado se cachea la posición obtenida, y tenemos dos llamadas posibles:
 *   - getLocation - Busca siempre la nueva posición.
 *   - getCachedLocation - Si ya se ha buscado la posición, devuelve la cacheada, siempre y cuando no sea
 *   más antigua que la indicada en la constante TIME_POSITION_EXPIRES (expresado en minutos)
 *
 */
public class MyLocation
{
    //geo fix -6.074386 43.387055
    private static final String LOG_TAG = "MYLOCATION";
    //Número de minutos que tienen que pasar antes de coger una ubicación nueva si pedimos la cacheada
    private static final int TIME_POSITION_EXPIRES = 10;
    private Timer timer1;
    private LocationManager lm;
    private LocationResult locationResult;
    private boolean gps_enabled = false;
    private boolean network_enabled = false;
    private Context context;

    //Cacheamos el resultado
    private static Location cachedLoc=null;
    private static String cachedName=null;
    //Hora a la que hemos cacheado la posición
    private static long cachedTime;

    //Singleton
    private static MyLocation myLocation;

    public static boolean getLocation(Context context, LocationResult locationResult)
    {
        Log.d(LOG_TAG,"Lanzando una nueva petición al gps");
        myLocation = new MyLocation();
        return myLocation.getInternalLocation(context,locationResult);
    }

    public static boolean getCachedLocation(Context context, LocationResult locationResult)
    {

        if (cachedLoc!=null && (getTimeInMinutes()-cachedTime<TIME_POSITION_EXPIRES))
        {
            Log.d(LOG_TAG,"Devolviendo la posición cacheada");
            locationResult.locationFound(cachedLoc,cachedName);
            return true;
        }
        else
        {
            Log.d(LOG_TAG,"Lanzando una nueva petición al gps");
            myLocation = new MyLocation();
            return myLocation.getInternalLocation(context,locationResult);
        }

    }

    public static void cancelSearch()
    {
        if (myLocation!=null)
            myLocation.cancelSearchInternal();
    }

    public void cancelSearchInternal()
    {
        if (timer1!=null)
            timer1.cancel();
        if (lm!=null)
        {
            lm.removeUpdates(locationListenerGps);
            lm.removeUpdates(locationListenerNetwork);
        }
    }

    /**
     * Devuelve el número de minutos que han pasado desde 1970
     * @return
     */
    private static int getTimeInMinutes()
    {
        Time time = new Time();
        time.setToNow();
        return (int)time.toMillis(true)/1000/60;
    }

    private synchronized void saveLocationToCache(Location loc, String nam)
    {
        cachedLoc=loc;
        cachedName=nam;
        cachedTime=getTimeInMinutes();
    }

    private boolean getInternalLocation(Context context, LocationResult result) {
        this.context = context;
        //I use LocationResult callback class to pass location value from MyLocation to user code.
        locationResult = result;
        if (lm == null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        //exceptions will be thrown if provider is not permitted.
        try
        {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        catch (Exception ex)
        {
            Log.d(LOG_TAG,"Error obteniendo el gps",ex);
        }
        try
        {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        catch (Exception ex)
        {
            Log.d(LOG_TAG,"Error obteniendo la red",ex);
        }

        //don't start listeners if no provider is enabled
        if (!gps_enabled && !network_enabled)
            return false;

        if (gps_enabled)
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
        if (network_enabled)
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
        timer1 = new Timer();
        timer1.schedule(new GetLastLocation(), 20000);
        return true;
    }

    LocationListener locationListenerGps = new LocationListener()
    {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            String placeName = getPlaceName(location);
            saveLocationToCache(location,placeName);
            locationResult.locationFound(location, placeName);
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerNetwork);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            String placeName = getPlaceName(location);
            saveLocationToCache(location,placeName);
            locationResult.locationFound(location, placeName);
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerGps);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    class GetLastLocation extends TimerTask {
        @Override
        public void run() {
            lm.removeUpdates(locationListenerGps);
            lm.removeUpdates(locationListenerNetwork);

            Location net_loc = null, gps_loc = null;
            if (gps_enabled)
                gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (network_enabled)
                net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            //if there are both values use the latest one
            if (gps_loc != null && net_loc != null) {
                if (gps_loc.getTime() > net_loc.getTime())
                {
                    String placeName = getPlaceName(gps_loc);
                    saveLocationToCache(gps_loc,placeName);
                    locationResult.locationFound(gps_loc, placeName);
                }
                else
                {
                    String placeName = getPlaceName(net_loc);
                    saveLocationToCache(net_loc,placeName);
                    locationResult.locationFound(net_loc, getPlaceName(net_loc));
                }
                return;
            }

            if (gps_loc != null)
            {
                String placeName = getPlaceName(gps_loc);
                saveLocationToCache(gps_loc,placeName);
                locationResult.locationFound(gps_loc, getPlaceName(gps_loc));
                return;
            }
            if (net_loc != null)
            {
                String placeName = getPlaceName(net_loc);
                saveLocationToCache(net_loc,placeName);
                locationResult.locationFound(net_loc, getPlaceName(net_loc));
                return;
            }
            locationResult.locationFound(null, "");
        }
    }

    /**
     * Devuelve el nombre correspondiente a esta localización en forma Ciudad (Pais)
     * @param location localización de la que se quiere obtener el nombre
     * @return String con el nombre d ela localización o "" si no se encuentra
     */
    private String getPlaceName(Location location) {
        Log.d(LOG_TAG, "Intentando obtener el nombre de la loc");
        String retorno = "";
        Geocoder geocoder = new Geocoder(context);
        try {
            if (location != null) {
                List<Address> listAddress = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (listAddress != null && listAddress.size() >= 1)
                {
                    StringBuilder builder = new StringBuilder(listAddress.get(0).getLocality());
                    builder.append(" (");
                    builder.append(listAddress.get(0).getCountryName());
                    builder.append(")");
                    retorno = builder.toString();
                }
            }
        } catch (Throwable e) {
            Log.d(LOG_TAG, "Error obteniendo el nombre de la localizacion", e);
        }

        return retorno;
    }


    public interface LocationResult
    {
        public void locationFound(Location location, String name);
    }
}
