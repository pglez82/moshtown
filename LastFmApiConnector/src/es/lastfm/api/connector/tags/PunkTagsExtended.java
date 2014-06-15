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
    
    
    String[] arr = {"rock","alternative","indie","metal","alternative rock","indie rock","punk","black metal","death metal","heavy metal","hardcore","progressive rock",
        "punk rock","thrash metal","metalcore","emo","grindcore","stoner rock","melodic hardcore","ska punk"};

    @Override
    public String[] getWorkingTags()
    {
        return arr;
    }

    @Override
    public String[] getNotDefaultTags()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
