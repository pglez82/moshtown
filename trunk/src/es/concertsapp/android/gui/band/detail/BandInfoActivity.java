package es.concertsapp.android.gui.band.detail;


//import android.app.ActionBar;

import android.app.AlertDialog;
import android.content.DialogInterface;
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


public class BandInfoActivity extends MenuFragmentActivity
{
    private static final String LOG_TAG = "BANDINFOACTIVITY";
    private String artistName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_band_info);

		//Recibimos el id de la banda
		Bundle extras = getIntent().getExtras();
		artistName= extras != null ? extras.getString(MyAppParameters.BANDID) : null;
        Integer fragment= extras != null ? extras.getInt(MyAppParameters.FRAGMENTID) : 0;
        ArtistDTO artistDTO = null;
        try
        {
            LastFmApiConnector lastFmApiConnector = LastFmApiConnectorFactory.getInstance();
            artistDTO=lastFmApiConnector.getArtistInfo(artistName);
        }
        catch (Throwable e)
        {
            Log.e(LOG_TAG,"Error obteniendo la info del artista",e);
            UnexpectedErrorHandler.handleUnexpectedError(this,e, new AlertDialog.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialogInterface)
                {
                    finish();
                }
            });
            return;
        }
        BandSectionsPageAdapter bandSectionsPageAdapter = new BandSectionsPageAdapter(getSupportFragmentManager(), artistName, artistDTO);
		
		//Añadimos el adaptador para cambiar entre páginas de desplazamiento horizontal
        ViewPager mViewPager = (ViewPager) findViewById(R.id.bandpager);
        mViewPager.setAdapter(bandSectionsPageAdapter);
        //IMPORTANTE: Aquí indicamos que guarde tres páginas en memoria (las que tenemos). Si no hacemos
        //esto, cada vez que cambiemos de página está llamando al oncreateview de cada página. Si son
        //muchas no podemos hacer esto.
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setCurrentItem(fragment);
	}

    public String getArtistName()
    {
        return artistName;
    }
}

