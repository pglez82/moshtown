package es.concertsapp.android.utils.geo.impl;

import java.io.Serializable;

import es.concertsapp.android.utils.geo.LatitudeLongitude;
import es.concertsapp.android.utils.geo.PlaceInterface;

/**
 * Created by pablo on 28/11/13.
 */
public class GpsObtainedPlace implements PlaceInterface, Serializable
{
    private String placeName;
    private LatitudeLongitude latlon;

    public GpsObtainedPlace(LatitudeLongitude latlon, String placeName)
    {
        this.latlon = latlon;
        this.placeName = placeName;
    }

    @Override
    public String getPlaceName()
    {
        return placeName;
    }

    @Override
    public LatitudeLongitude getLatLon()
    {
        return latlon;
    }

    public String toString()
    {
        return placeName;
    }
}
