package es.concertsapp.android.conf;

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

    //Máximo número de ciudades almacenadas como favoritos en la búsqueda de eventos (se resaltan en
    //las sugerencias
    public static final int MAX_CITIES_STORED = 3;

    //Distancia para considerar un concierto cercano (de momento solo para marcar que artistas favoritos
    //tienen conciertos cerca
    public static final double NEAR_EVENT_DISTANCE=50;

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
}
