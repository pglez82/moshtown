package es.concertsapp.android.utils.geo.impl;

import java.io.Serializable;

import es.concertsapp.android.utils.geo.LatitudeLongitude;
import es.concertsapp.android.utils.geo.PlaceInterface;

/**
 * Created by pablo on 24/08/13.
 */
public class GooglePlacesPlace implements PlaceInterface, Serializable
{
    private final String placeName;
    private LatitudeLongitude latlon;

    //Con esta referencia podemos sacar la latitud y longitud cuando nos la pidan
    private transient final String ref;
    //Referencia a la clase que lo creo para poder luego sacar la información detallada
    //Indicamos que es transient para que no lo serialize
    private transient final GooglePlacesApi googlePlacesApi;

    public GooglePlacesPlace(String placeName, String ref,GooglePlacesApi googlePlacesApi)
    {
        this.placeName = placeName;
        this.ref = ref;
        this.googlePlacesApi = googlePlacesApi;
    }

    @Override
    public String getPlaceName()
    {
        return placeName;
    }

    @Override
    public LatitudeLongitude getLatLon()
    {
        //Lanzamos una consulta detallada a google apis autocomplete y cogemos la lat y lon
        if (latlon==null)
            latlon=googlePlacesApi.getLatLonFromRef(ref);

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
        if (o == null || getClass() != o.getClass()) return false;

        GooglePlacesPlace place = (GooglePlacesPlace) o;

        if (placeName != null ? !placeName.equals(place.placeName) : place.placeName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        return placeName != null ? placeName.hashCode() : 0;
    }
}
