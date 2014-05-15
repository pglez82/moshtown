package es.concertsapp.android.utils.geo;

import java.io.Serializable;

/**
 * Created by pablo on 24/08/13.
 */
public class LatitudeLongitude implements Serializable
{
    public final double lat;
    public final double lon;

    public LatitudeLongitude(double lat, double lon)
    {
        this.lat = lat;
        this.lon = lon;
    }
}
