package es.concertsapp.android.utils.geo.impl;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import es.concertsapp.android.utils.geo.LatitudeLongitude;
import es.concertsapp.android.utils.geo.PlaceAutocompleterInterface;
import es.concertsapp.android.utils.geo.PlaceInterface;

/**
 * Created by pablo on 9/08/13.
 */
public class GooglePlacesApi implements PlaceAutocompleterInterface
{
    private static final String LOG_TAG = "GooglePlacesApi";

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_DETAILS = "/details";
    private static final String OUT_JSON = "/json";

    private static final String API_KEY = "AIzaSyDwUxo-csfddL__Q3r7thavAdVO-OqIGqU";

    public List<PlaceInterface> autocomplete(String text,String language) {
        List<PlaceInterface> resultList = null;
        try
        {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?sensor=false&key=" + API_KEY);
            //sb.append("&components=country:es");
            sb.append("&types=(cities)");
            sb.append("&input=" + URLEncoder.encode(text, "utf8"));
            URL url = new URL(sb.toString());
            Log.d(LOG_TAG, "Lanzando "+sb.toString());
            JSonParser jSonParser = new JSonParser();
            JSONObject jsonObj = jSonParser.parseJSon(url);
            if (jsonObj != null)
            {
                JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

                // Extract the GooglePlacesPlace descriptions from the results
                resultList = new ArrayList<PlaceInterface>(predsJsonArray.length());
                for (int i = 0; i < predsJsonArray.length(); i++)
                {
                    GooglePlacesPlace googlePlacesPlace = new GooglePlacesPlace(predsJsonArray.getJSONObject(i).getString("description"),predsJsonArray.getJSONObject(i).getString("reference"),this);
                    resultList.add(googlePlacesPlace);
                }
            }
        }
        catch (Throwable e)
        {
            Log.e(LOG_TAG, "No se han podido recoger los resultados de autocompletar", e);
        }

        return resultList;
    }

    protected LatitudeLongitude getLatLonFromRef(String ref)
    {
        LatitudeLongitude latlon = null;
        try
        {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_DETAILS + OUT_JSON);
            sb.append("?sensor=false&key=" + API_KEY);
            sb.append("&reference=" + URLEncoder.encode(ref, "utf8"));
            URL url = new URL(sb.toString());

            JSonParser jSonParser = new JSonParser();
            JSONObject jsonObj = jSonParser.parseJSon(url);
            if (jsonObj != null)
            {
                JSONObject location = jsonObj.getJSONObject("result").getJSONObject("geometry").getJSONObject("location");
                latlon = new LatitudeLongitude(location.getDouble("lat"),location.getDouble("lng"));
            }
        }
        catch (Throwable e)
        {
            Log.e(LOG_TAG, "No se han podido recoger los resultados de autocompletar", e);
        }

        return latlon;
    }
}
