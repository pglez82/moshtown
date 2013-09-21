package es.concertsapp.android.utils.geo;

import java.util.List;

/**
 * Created by pablo on 24/08/13.
 *
 * Este interfaz devuelve los sitis
 */
public interface PlaceAutocompleterInterface
{
    public List<PlaceInterface> autocomplete(String text,String language);
}
