/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.lastfm.api.connector.tags;

/**
 *
 * @author pablo
 */
public class PunkTags implements LastFmTags
{
    
    
    String[] arr = {"punk","hardcore","punk rock","metalcore","emo",
        "post-hardcore","screamo","grindcore","pop punk","hardcore punk",
        "melodic hardcore","crust","ska punk"};

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
