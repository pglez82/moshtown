package es.concertsapp.android.gui.band.detail;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import de.umass.lastfm.Channel;
import de.umass.lastfm.Item;
import es.concertsapp.android.utils.LastFmApiConnectorFactory;
import es.concertsapp.android.utils.MyAppParameters;
import es.concertsapp.android.utils.UnexpectedErrorHandler;
import es.lastfm.api.connector.LastFmApiConnector;
import es.lastfm.api.connector.exception.LastFmException;


public class BandTab3FragmentConHtml extends Fragment
{
    private static final String LOG_TAG = "BANDTAB3FRAGMENT";
	private WebView mWebview ;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);

        mWebview = new WebView(getActivity());
        
        Bundle args = getArguments();
        String artistName=args.getString(MyAppParameters.BANDID);
        LastFmApiConnector lastFmApiConnector = LastFmApiConnectorFactory.getInstance();
        try
        {
            Channel channel = lastFmApiConnector.getArtistPodcast(artistName);
            String spotify = lastFmApiConnector.getSpotifyUri(artistName);
            //Creamos una página
            //Parte de arriba de la página
            StringBuilder code=new StringBuilder();
            code.append("<html><head><title>").append(artistName).append("</title></head><body>");
            if (channel!=null && channel.getItems()!=null && channel.getItems().size()>0)
            {
                for (Item song : channel.getItems())
                {
                    code.append("<h1>").append(song.getTitle()).append("</h1>");
                    code.append("<audio controls preload=\"none\">	<source src=\"");
                    code.append(song.getEnclosureUrl()).append("type=\"audio/mpeg\"/></audio>");
                }
            }
            else
            {
                code.append("No hay canciones disponibles en Last.fm.");
            }
            if (spotify!=null && !"".equals(spotify))
                code.append(spotify);
            code.append("</body></html>");
            mWebview.loadData(code.toString() , "text/html" , "utf-8");
        }
        catch (LastFmException e)
        {
            Log.e(LOG_TAG,"Se ha producido un error al obtener el podcast",e);
            UnexpectedErrorHandler.handleUnexpectedError(e);
        }
        return mWebview;   
    }

    @Override
	public void onDestroy() {
		super.onDestroy();
        if (mWebview!=null)
		    mWebview.loadUrl("about:blank");
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    //mWebview.onPause();
	}

	@Override
	public void onResume() {
	    super.onResume();
	    //mWebview.onResume();
	}
        
    
        
    
}
