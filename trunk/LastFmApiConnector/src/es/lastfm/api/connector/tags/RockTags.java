/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.lastfm.api.connector.tags;

/**
 *
 * @author pablo
 */
public class RockTags implements LastFmTags
{
    String[] arr = {"rock","classic rock", "alternative rock", "hard rock"};

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
        return "Rock";
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
