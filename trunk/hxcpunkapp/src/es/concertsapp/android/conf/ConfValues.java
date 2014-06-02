package es.concertsapp.android.conf;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import es.lastfm.api.connector.configuration.LastFmApiConfiguration;

/**
 * Created by pablo on 25/08/13.
 */
public class ConfValues implements LastFmApiConfiguration
{
    //-----------------Archivos de almacenamiento----------------------
    //Nombre del archivo donde se almacenan los favoritos
    public static final String FILENAME_FAVORITES = "favouritebands.hxa";
    //Nombre del archivo donde se almacenan las búsquedas más recientes en ciudades
    public static final String FILENAME_CITIES = "cities.hxa";
    //Archivo de configuración
    public static final String FILENAME_CONF = "configuration.hxa";
    //Archivo con las tags seleccionadas
    public static final String FILENAME_TAGS = "tags.hxa";
    //Archivo con los eventos ya notificados al usuario como enventos cercanos al usuario (servicio externo a la app @es.concertsapp.android.background.FavouritesService)
    public static final String FILENAME_EVENTSNOTIFIED = "eventsnotified.hxa";

    //Máximo número de ciudades almacenadas como favoritos en la búsqueda de eventos (se resaltan en
    //las sugerencias
    public static final int MAX_CITIES_STORED = 5;

    //Distancia para considerar un concierto cercano (de momento solo para marcar que artistas favoritos
    //tienen conciertos cerca
    //public static final double NEAR_EVENT_DISTANCE_FAVOURITES=50;

    //Tamaño máximo de la lista de tags del artista a analizar.
    public static final int MAX_TOP_TAGS_ARTISTS = 5;


    //Tamaño máximo de artistas a analizar en los eventos en el caso de que el evento
    //no tenga tags
    public static final int MAX_ARTISTS_EVENT_TAGS = 1;

    //Número de minutos que tienen que pasar antes de coger una ubicación nueva si pedimos la cacheada
    public static final int TIME_POSITION_EXPIRES = 10;

    //Número de minutos que tienen que pasar para que la información sobre los conciertos de favoritos esté
    //obsoleta
    public static final int TIME_FAVOURITE_CONCERTS_EXPIRES = 1;

    @Override
    public int getMaxTopTagsArtist()
    {
        return MAX_TOP_TAGS_ARTISTS;
    }

    @Override
    public int getMaxArtistsEventTags()
    {
        return MAX_ARTISTS_EVENT_TAGS;
    }


    public enum ConfigurableValue
    {
        EVENT_RATIO_DISTANCE(50),
        //Flag que indica si el servicio de avisar por cocneritos favoritos aun si la app esta cerrada
        //está activo o no
        SERVICE_CHECK_FAVOURITE_EVENTS(1);

        private Object defaultValue;
        private Object actualValue;

        ConfigurableValue(Object defaultValue) {
            this.defaultValue = defaultValue;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }

        public Object getActualValue() {
            return actualValue;
        }

        public void setActualValue(Object actualValue) {
            this.actualValue = actualValue;
        }
    }

    //Los valores configurables se devuelven siempre a través de métodos
    public static int getIntConfigurableValue(Context context, ConfigurableValue configurableValue)
    {
        if (configurableValue.getActualValue()==null) {
            try {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                configurableValue.setActualValue(preferences.getInt(configurableValue.name(), (Integer) configurableValue.getDefaultValue()));
            }
            catch (Throwable e)
            {
                configurableValue.setActualValue(configurableValue.getDefaultValue());
            }
        }
        return (Integer)configurableValue.getActualValue();
    }

    public static int restoreIntConfigurationValue(Context context, ConfigurableValue configurableValue)
    {
        setIntConfigurationValue(context,configurableValue,(Integer)configurableValue.getDefaultValue());
        return (Integer)configurableValue.getActualValue();
    }

    public static void setIntConfigurationValue(Context context, ConfigurableValue configurableValue, int newValue)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(configurableValue.name(), newValue); // value to store
        editor.commit();
        configurableValue.setActualValue(newValue);
    }



}
