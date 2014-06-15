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
    
    
    private final String[] workingTags = {"metal","alternative rock","indie rock","punk","black metal","death metal","heavy metal","hardcore","progressive rock",
        "punk rock","thrash metal","metalcore","emo","grindcore","stoner rock","melodic hardcore","ska punk"};

    private final String[] noDefaultTags = {"rock","alternative","indie"};
    
    @Override
    public String[] getWorkingTags()
    {
        return workingTags;
    }

    @Override
    public String[] getNotDefaultTags()
    {
        return noDefaultTags;
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
        for (String s : workingTags)
        {
            stringBuilder.append(s).append(", ");
        }
        return stringBuilder.toString();
    }
    
    
    

}
