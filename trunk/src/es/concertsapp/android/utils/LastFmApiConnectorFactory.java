package es.concertsapp.android.utils;

import es.concertsapp.android.conf.ConfValues;
import es.lastfm.api.connector.LastFmApiConnector;
import es.lastfm.api.connector.tags.LastFmTags;

/**
 * Factoria de la clase de acceso al webservice. Implementa el patrón singleton
 */
public class LastFmApiConnectorFactory 
{
	private static LastFmTags lastFmTags;
	private static LastFmApiConnector lastFmApiConnector = null;
	
	public static void initilize(LastFmTags lastFmTagsTemp)
	{
		lastFmTags = lastFmTagsTemp;
	}
	
	public static LastFmApiConnector getInstance()
	{
		if (lastFmApiConnector == null)
			lastFmApiConnector = new LastFmApiConnector(lastFmTags, new ConfValues(), MyApplication.getLocale());
		
		return lastFmApiConnector;
	}
	
	public static LastFmApiConnector getNewInstance()
	{
		lastFmApiConnector = new LastFmApiConnector(lastFmTags, new ConfValues(), MyApplication.getLocale());
		return lastFmApiConnector;
	}

}
