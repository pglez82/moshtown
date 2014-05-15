/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.lastfm.api.connector.dto;

import de.umass.lastfm.Event;
import de.umass.lastfm.ImageSize;
import es.lastfm.api.connector.tags.LastFmTags;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author pablo
 */
public class EventDTO implements LastFmImageSourceI
{
    private Event event;
    //Tags coincidentes con lo que nosotros buscamos
    private Set<String> matchingTags = new HashSet<String>();
    private LastFmTags lastFmTags;

    public EventDTO(Event event,LastFmTags lastFmTags)
    {
        this.event = event;
        this.lastFmTags = lastFmTags;
    }

    public void addMatchingTags(Set<String> tags)
    {
        matchingTags.addAll(tags);
    }

    public Set<String> getMatchingTags()
    {
        return matchingTags;
    }

    public boolean hasMatchingTags()
    {
        return !matchingTags.isEmpty();
    }

    public Collection<String> getTags() {
        return event.getTags();
    }
    
    
    
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Evento: ").append(event.getTitle()).append("\n");
        stringBuilder.append("Descripcion: ").append(event.getDescription()).append("\n");
        stringBuilder.append("Bandas: ");
        for (String artist : event.getArtists())
        {
            stringBuilder.append(artist).append(",");
        }
        stringBuilder.append("\n");
        stringBuilder.append("Tags: ");
        for (String tag : matchingTags)
        {
            stringBuilder.append(tag).append(",");
        }
        float porcentaje = (float)matchingTags.size()/lastFmTags.getWorkingTags().length*100;
        stringBuilder.append("(").append(Float.toString(porcentaje)).append(")").append("\n");
        stringBuilder.append("--------------------------------------------\n");
        return stringBuilder.toString();
    }
    
    public int getEventId()
    {
        return event.getId();
    }
    
    public Date getEventDate()
    {
        return event.getStartDate();
    }
    
    public String getEventTitle()
    {
        return event.getTitle();
    }
    
    public Collection<String> getEventArtists()
    {
        return event.getArtists();
    }
    
    public float getMatchingTagPorcentage()
    {
        return (float)matchingTags.size()/lastFmTags.getWorkingTags().length*100;
    }
    
    public String getEventPlace()
    {
        return event.getVenue()!=null ? event.getVenue().getName()+" ("+event.getVenue().getCity()+')' : "";
    }
    
    /*
     * Devuelve una URL a la imagen del evento. Si le pasamos un ImageSize.SMALL
     * devuelve aproximadamente 34x34 pixeles
     */
    public String getImageURL(ImageSize imageSize)
    {
        return event.getImageURL(imageSize);
    }
}
