package es.concertsapp.android.utils.geo.impl;

import android.util.Log;

import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by pablo on 24/08/13.
 */
public class JSonParser
{
    private static final String LOG_TAG = "JSONPARSER";

    /**
     * Devuelve el objeto parseado de JSon o null si ha dado algun error
     * @param url url para descargar y parsear
     * @return objeto de json o null si ha habido algún error
     */
    public static JSONObject parseJSon(URL url)
    {
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
            return new JSONObject(jsonResults.toString());

        } catch (Throwable e)
        {
            Log.e(LOG_TAG, "Error processing JSON response", e);
            return null;
        }
        finally
        {
            if (conn != null)
            {
                conn.disconnect();
            }
        }
    }
}
