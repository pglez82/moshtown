/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.lastfm.api.connector.utils;

import de.umass.lastfm.Tag;
import es.lastfm.api.connector.tags.LastFmTags;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author pablo
 */
public class TagUtils
{
    /**
     * Calcula todo el conjunto de tags que coinciden con una serie de tags
     * @param currentTags tags del artista o evento
     * @param lastFmTags tags contra las que chequeamos
     * @return conjunto de tags coincidentes
     */
    public static Set<String> computeAllMatchingTags(Collection<String> currentTags, LastFmTags lastFmTags)
    {
        String[] workingTags = lastFmTags.getWorkingTags();
        Set<String> tags = new HashSet<String>();
        for (String currentTag : currentTags)
        {
            for (String tag : workingTags)
            {
                if (tag.equals(currentTag))
                {
                    tags.add(tag);
                }
            }
        }
        return tags;
    }
    
    /**
     * No busca todas las tags coincidentes sino que solo busca si hay una.
     * Es más rápido que el método anterior.
     * @param currentTags tags del artista o evento
     * @param lastFmTags tags contra las que chequeamos
     * @param N número de tags a analizar
     * @return true si hay al menos una tag coincidente, y falso en caso contrario.
     */
    public static boolean hasMatchingTag(Collection<Tag> currentTags,LastFmTags lastFmTags,int N)
    {
        int tagListSize = currentTags.size()<=N ? currentTags.size() : N; 
        String[] workingTags = lastFmTags.getWorkingTags();
        Iterator<Tag> tagIterator = currentTags.iterator();
        for (int i=0;i<tagListSize;i++)
        {
            String currentTagName = tagIterator.next().getName();
            for (String tag : workingTags)
            {
                if (tag.equals(currentTagName))
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean hasMatchingTag(Collection<String> currentTags,LastFmTags lastFmTags)
    {
        String[] workingTags = lastFmTags.getWorkingTags();
        for (String tag1 : currentTags)
        {
            for (String tag2 : workingTags)
            {
                if (tag1.equals(tag2))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
