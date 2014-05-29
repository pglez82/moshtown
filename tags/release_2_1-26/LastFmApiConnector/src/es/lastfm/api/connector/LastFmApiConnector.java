/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.lastfm.api.connector;

import com.google.code.jspot.Results;
import com.google.code.jspot.Spotify;
import de.umass.lastfm.Artist;
import de.umass.lastfm.Caller;
import de.umass.lastfm.Channel;
import de.umass.lastfm.Event;
import de.umass.lastfm.Geo;
import de.umass.lastfm.PaginatedResult;
import de.umass.lastfm.Tag;
import es.lastfm.api.connector.configuration.LastFmApiConfiguration;
import es.lastfm.api.connector.dto.ArtistDTO;
import es.lastfm.api.connector.dto.ArtistEventDTO;
import es.lastfm.api.connector.dto.DetailedEventDTO;
import es.lastfm.api.connector.dto.EventDTO;
import es.lastfm.api.connector.exception.LastFmException;
import es.lastfm.api.connector.tags.LastFmTags;
import es.lastfm.api.connector.utils.TagUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author pablo
 */
public class LastFmApiConnector
{
    //Clave para acceder al api de last.fm
    private static final String key = "5f7bd763af200a040f813ea280bef4b4";
    
    //Tags para esta instancia
    private final LastFmTags lastFmTags;
    
    //Configuraci�n del servicio
    private final LastFmApiConfiguration lastFmApiConfiguration;
    
    private String location;
    private double lat;
    private double lon;
    private boolean coordinates;
    private int distance;
    private int maxDistance;
    
    //Tamaño de página de resultados
    private final static int PAGE_SIZE = 10;
    
    private int page;
    private int totalPages;
    //N�mero total de eventos encontrado en esta b�squeda
    private int totalEventsFound;
    
    //Listener que se invoca cuando se acaba de procesar una página
    private PagedProcesedListener pagedProcesedListener;
    //Listener que se invoca cuando se calcula un elemento nuevo en una página
    private NewEventAvaibleListener newElementAvaibleListener;
    
    private final Locale locale;
    
    public LastFmApiConnector(LastFmTags lastFmTags, LastFmApiConfiguration lastFmApiConfiguration, Locale locale)
    {
        Caller.getInstance().setCache(null);
        this.lastFmTags = lastFmTags;
        this.lastFmApiConfiguration = lastFmApiConfiguration;
        page = 1;
        totalPages = 1;
        totalEventsFound = 0;
        coordinates = false;
        this.locale = locale;
    }

    /**
     * Este método hay que llamarlo obligatoriamente antes de buscar eventos
     * @param pagedProcesedListener listener a ejecutar cuando se acabe de procesar
     * @param newEventAvaibleListener listener a ejecutar cuando se encuentre un evento
     * una página entera de resultados
     */
    public void setEventSearchListeners(PagedProcesedListener pagedProcesedListener,NewEventAvaibleListener newEventAvaibleListener)
    {
        this.pagedProcesedListener = pagedProcesedListener;
        this.newElementAvaibleListener = newEventAvaibleListener;
    }
    
    /**
     * Establece los parámetro sde la búsqueda
     * @param location string con la ciudad
     * @param distance distancia a la que se quiere buscar
     * @param maxDistance distancia m�xima a buscar (el algoritmo si no encuentra
     * nada a una distancia la va duplicando hasta que encuentre algo o se pase esta
     * distancia
     */
    public void setLocation(String location, int distance, int maxDistance)
    {
        this.location = location;
        this.distance=distance;
        this.maxDistance = maxDistance;
        coordinates = false;
    }

    /**
     * Establece los parámetros de la bśuqueda
     * @param lat latitud
     * @param lon longitud
     * @param distance distancia a la que se quiere buscar
     * @param maxDistance distancia m�xima a buscar (el algoritmo si no encuentra
     * nada a una distancia la va duplicando hasta que encuentre algo o se pase esta
     * distancia
     */
    public void setLatLon(double lat,double lon, int distance,int maxDistance)
    {
        this.lat = lat;
        this.lon = lon;
        this.distance=distance;
        this.maxDistance = maxDistance;
        coordinates = true;
    }
    
    /**
     * Filtra un evento para saber si debería devolverse como resultado o no
     * @param eventDTO evento a filtrar
     * @param analyzeArtistTags si es true, se analiza los tags del artista en el
     * caso de que no existan los tags del concierto.
     * @return true si debería de estar en la lista y false en caso contrario
     */
    private boolean filterEventByTags(EventDTO eventDTO,boolean analyzeArtistTags)
    {
        boolean result = false;
        Collection<String> tags = eventDTO.getTags();
        //Si el evento viene con tags
        if (tags!=null && tags.size()>0)
        {
            if (TagUtils.hasMatchingTag(tags, lastFmTags))
            {
                result = true;
            }
        }
        //En el caso de que el evento no venga con tags, pero queramos analizar las
        //tags de los primeros artistas...
        else if (analyzeArtistTags)
        {
            Collection<String> listArtists = eventDTO.getEventArtists();
            int numAnalized=listArtists.size()<lastFmApiConfiguration.getMaxArtistsEventTags()?listArtists.size():lastFmApiConfiguration.getMaxArtistsEventTags();
            Iterator<String> listArtistsIterator = listArtists.iterator();
            for (int i=0;i<numAnalized;i++)
            {
                if (filterArtistByTags(listArtistsIterator.next()))
                {
                    result = true;
                    break;
                }
            }
        }
        
        return result;
    }
    
    /**
     * Indica si un artista debe estar o no en los resultados devueltos por sus tags
     * @param artistName nombre del artista
     * @return true si el artista tiene alg�n tag coincidente. falso en caso contrario
     */
    private boolean filterArtistByTags(String artistName)
    {
        Collection<Tag> tags=Artist.getTopTags(artistName, key);
        //Cogemos solo las N primeras
        return TagUtils.hasMatchingTag(tags,lastFmTags,lastFmApiConfiguration.getMaxTopTagsArtist());
    }
    
    /**
     * @param analyzeArtistTags si es true, se analiza los tags del artista en el
     * caso de que no existan los tags del concierto.
     */
    public void listPagedEventsFilteredByTags(boolean analyzeArtistTags) throws LastFmException
    {
        try
        {
            int nuevos = 0;
            while ((page<=totalPages) && (nuevos <= PAGE_SIZE))
            {
                PaginatedResult<Event> temporalEvents;
                if (coordinates)
                {
                    temporalEvents = Geo.getEvents(lat,lon,Integer.toString(distance),page,-1,key);
                }            
                else
                {
                    temporalEvents = Geo.getEvents(location,Integer.toString(distance),page,key);
                }
                for (Event tempEvent : temporalEvents)
                {
                    EventDTO eventDTO = new EventDTO(tempEvent,lastFmTags);
                    if (filterEventByTags(eventDTO, true))
                    {
                        newElementAvaibleListener.newElementAvailable(eventDTO);
                        nuevos++;
                        totalEventsFound++;
                    }

                }

                page++;
                totalPages = temporalEvents.getTotalPages();  
                if (pagedProcesedListener != null)
                {
                    pagedProcesedListener.pagedProcessed(page, totalPages, nuevos, totalEventsFound,distance);
                }
            }
            
            //La idea es comprobar aqu� si no hay ning�n resultado y se producieron 0 p�ginas. Solo entonces, lanzamos otra consulta igual
            //pero con un radio mayor
            if (totalEventsFound==0 && distance<=maxDistance)
            {
                distance=distance*2;
                page=1; 
                listPagedEventsFilteredByTags(analyzeArtistTags);
            }
        }
        catch (Throwable e)
        {
            throw new LastFmException("Se ha producido un error buscando conciertos en last.fm", e);
        }
    }
    
    
    /**
     * Nos indica si hay más resultados todavía cuando ha acabado la llamada
     * al método de devolver conciertos
     * @return true si hay más resultados y false en caso contrario
     */
    public boolean hasMoreResults()
    {
        return page<=totalPages;
    }
    
    /*
     * Devuelve información más detallada del evento en cuestión
     */
    public DetailedEventDTO getDetailedInfoEvent(int idEvent) throws LastFmException
    {
        try
        {
            Event event = Event.getInfo(Integer.toString(idEvent), key);
            return new DetailedEventDTO(event);
        }
        catch (Throwable e)
        {
            throw new LastFmException("Se ha producido un error obteniendo la info detallada del evento", e);
        }
    }
    
    public ArtistDTO getArtistInfo(String artist) throws LastFmException
    {
        try
        {
            Artist temp = Artist.getInfo(artist, locale , null, key);
            //Si el local es diferente del ingl�s tenemos que mirar si la descripci�n est� vac�a o no
            //En el caso de que est� vac�a cargar la por defecto en ingl�s
            if (locale!=Locale.ENGLISH)
            {
                if (temp.getWikiSummary()==null || "".equals(temp.getWikiSummary()))
                {
                    temp = Artist.getInfo(artist, Locale.ENGLISH , null, key);
                }
            }
            ArtistDTO artistDTO = new ArtistDTO(temp);
            return artistDTO;
        }
        catch (Throwable e)
        {
            throw new LastFmException("Se ha producido un error obteniendo la informaci�n del artista", e);
        }
    }
    
    /**
     * Devuelve la lista de artistas similares filtrados por estilo
     * @param artistDTO 
     */
    public List<ArtistDTO> getSimilarArtistsFiltered(ArtistDTO artistDTO) throws LastFmException
    {
        try
        {
            List<ArtistDTO> filtered = new ArrayList<ArtistDTO>();
            for (ArtistDTO similarArtist : artistDTO.getSimilarArtists())
            {
                if (filterArtistByTags(similarArtist.getArtistName()))
                    filtered.add(similarArtist);
            }
            return filtered;
        }
        catch (Throwable e)
        {
            throw new LastFmException("Se ha producido un error obteniendo los artistas similares a otro artista", e);
        }
    }
    
    /*En principio devuelve solo la primera página devuelta por last.fm*/
    public List<ArtistEventDTO> getArtistEvents(String artist) throws LastFmException
    {
        try
        {
            List<ArtistEventDTO> listArtistEventDTO = new ArrayList<ArtistEventDTO>();
            PaginatedResult<Event> result = Artist.getEvents(artist, key);
            Iterator<Event> it = result.iterator();
            while (it.hasNext())
            {
                ArtistEventDTO artistEventDTO = new ArtistEventDTO(it.next());
                listArtistEventDTO.add(artistEventDTO);
            }

            return listArtistEventDTO;
        }
        catch (Throwable e)
        {
            throw new LastFmException("Error obteniendo los eventos de un artista", e);
        }
        
    }
    
    /*
     * Devuelve una lista de bandas por un nombre filtradas por tags
     * 
     * Como es un listado lento lo devolvemos  através de un listener para que
     * el entorno gráfico pueda ir mostrandolo sobre la marcha
     */
    public void getArtists(String partialName, NewArtistAvaibleListener newArtistAvaibleListener) throws LastFmException
    {
        try
        {
            Collection<Artist> artists = Artist.search(partialName, key);
            for (Artist artist : artists)
            {
                if (filterArtistByTags(artist.getName()))
                {
                    newArtistAvaibleListener.newArtistAvailable(new ArtistDTO(artist));
                }
            }
        }
        catch (Throwable e)
        {
            throw new LastFmException("Error obteniendo artistas de Lastfm",e);
        }
    }
    
    public String getSpotifyUri(String artist)
    {
        StringBuilder uri=new StringBuilder();
        try
        {
            Spotify spotify = new Spotify();
            Results<com.google.code.jspot.Artist> artists = spotify.searchArtist(artist);
            if (artists != null && artists.getTotalResults()>0)
            {
                //uri.append("<iframe src=\"https://embed.spotify.com/?uri=");
                //uri.append("https://embed.spotify.com?uri=");
                uri.append(artists.getItems().get(0).getId());
                //uri.append(" width=\"300\" height=\"380\" frameborder=\"0\" allowtransparency=\"true\"></iframe>");
                
            }
        }
        catch (Throwable e)
        {
        }
        
        return uri.toString();
        
    }
    
    public Channel getArtistPodcast(String artist) throws LastFmException
    {
        try
        {
            return Artist.getPodcast(artist, key);
        }
        catch (Throwable e)
        {
            throw new LastFmException("Se ha producido un error obteniendo el podcast del artista", e);
        }
    }
}

