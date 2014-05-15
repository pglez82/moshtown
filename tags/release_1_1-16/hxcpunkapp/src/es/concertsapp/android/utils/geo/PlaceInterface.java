package es.concertsapp.android.utils.geo;

/**
 * Created by pablo on 24/08/13.
 */
public interface PlaceInterface
{
    public String getPlaceName();
    public LatitudeLongitude getLatLon();
    @Override
    public String toString();
    @Override
    public boolean equals(Object obj);

}
