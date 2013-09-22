package es.concertsapp.android.gui.band.detail;


//import android.app.ActionBar;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;

import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.menu.MenuFragmentActivity;
import es.concertsapp.android.utils.LastFmApiConnectorFactory;
import es.concertsapp.android.utils.MyAppParameters;
import es.concertsapp.android.utils.UnexpectedErrorHandler;
import es.lastfm.api.connector.LastFmApiConnector;
import es.lastfm.api.connector.dto.ArtistDTO;
import es.lastfm.api.connector.exception.LastFmException;


public class BandInfoActivity extends MenuFragmentActivity
{
    private static final String LOG_TAG = "BANDINFOACTIVITY";
	private BandSectionsPageAdapter bandSectionsPageAdapter;
    private ViewPager mViewPager;
    private String artistName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_band_info);

		//Recibimos el id de la banda
		Bundle extras = getIntent().getExtras();
		artistName=extras.getString(MyAppParameters.BANDID);
        Integer fragment=extras.getInt(MyAppParameters.FRAGMENTID);
        ArtistDTO artistDTO = null;
        try
        {
            LastFmApiConnector lastFmApiConnector = LastFmApiConnectorFactory.getInstance();
            artistDTO=lastFmApiConnector.getArtistInfo(artistName);
        }
        catch (Throwable e)
        {
            Log.e(LOG_TAG,"Error obteniendo la info del artista",e);
            UnexpectedErrorHandler.handleUnexpectedError(this,e);
        }
		bandSectionsPageAdapter = new BandSectionsPageAdapter(getSupportFragmentManager(),artistName,artistDTO);
		
		//Añadimos el adaptador para cambiar entre páginas de desplazamiento horizontal
		mViewPager = (ViewPager) findViewById(R.id.bandpager);
        mViewPager.setAdapter(bandSectionsPageAdapter);
        //IMPORTANTE: Aquí indicamos que guarde tres páginas en memoria (las que tenemos). Si no hacemos
        //esto, cada vez que cambiemos de página está llamando al oncreateview de cada página. Si son
        //muchas no podemos hacer esto.

        mViewPager.setOffscreenPageLimit(3);
        if (fragment!=null)
            mViewPager.setCurrentItem(fragment);
	}

    public String getArtistName()
    {
        return artistName;
    }
}

