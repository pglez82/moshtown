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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null) return false;

        PlaceInterface place = (PlaceInterface) o;

        if (placeName != null ? !placeName.equals(place.getPlaceName()) : place.getPlaceName() != null)
            return false;

        return true;
    }
}
