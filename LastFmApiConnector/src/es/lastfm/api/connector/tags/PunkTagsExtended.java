/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.lastfm.api.connector.tags;

/**
 *
 * @author pablo
 */
public class PunkTagsExtended implements LastFmTags
{
    
    
    String[] arr = {"metal","alternative rock","indie rock","punk","death metal",
        "heavy metal","hardcore","punk rock","thrash metal","metalcore","emo",
        "grindcore","stoner rock", "ska punk"};

    @Override
    public String[] getWorkingTags()
    {
        return arr;
    }

    @Override
    public String toString()
    {
        return "Punk";
    }

    @Override
    public String getLongDescription()
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : arr)
        {
            stringBuilder.append(s).append(", ");
        }
        return stringBuilder.toString();
    }
    
    
    

}
