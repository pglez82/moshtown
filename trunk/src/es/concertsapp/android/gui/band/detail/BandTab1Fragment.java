package es.concertsapp.android.gui.band.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collection;

import de.umass.lastfm.ImageSize;
import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.band.list.favourites.FavouriteBandsStore;
import es.concertsapp.android.utils.DialogUtils;
import es.concertsapp.android.utils.LastFmApiConnectorFactory;
import es.concertsapp.android.utils.MyAppParameters;
import es.concertsapp.android.utils.UnexpectedErrorHandler;
import es.concertsapp.android.utils.images.ImageDownloader;
import es.lastfm.api.connector.LastFmApiConnector;
import es.lastfm.api.connector.dto.ArtistDTO;
import es.lastfm.api.connector.exception.LastFmException;


public class BandTab1Fragment extends Fragment
{
    private static final String LOG_TAG="BANDTAB1FRAGMENT";
    private FavouriteBandsStore favouriteBandsStore;
	private String[] similarBands;
    private ArtistDTO artistDTO;

    public BandTab1Fragment()
    {
        super();
    }

    public BandTab1Fragment(ArtistDTO artistDTO)
    {
        super();
        this.artistDTO = artistDTO;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        favouriteBandsStore=FavouriteBandsStore.getInstance(null);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.activity_band_info_t1, container, false);
        try
        {
            if (artistDTO==null)
            {
                Bundle args = getArguments();
                String artistName=args.getString(MyAppParameters.BANDID);
                LastFmApiConnector lastFmApiConnector = LastFmApiConnectorFactory.getInstance();
                artistDTO = lastFmApiConnector.getArtistInfo(artistName);
            }
            ((TextView)rootView.findViewById(R.id.detailedbandname)).setText(artistDTO.getArtistName());

            //Cargamos la imagen
            ImageView artistImageView = (ImageView)rootView.findViewById(R.id.detailedbandimage);
            ImageDownloader imageDownloader = ImageDownloader.getInstance();
            imageDownloader.download(artistDTO.getImageURL(ImageSize.MEDIUM), artistImageView);

            //Tags del artista
            TextView tagsTextView = ((TextView)rootView.findViewById(R.id.detailedbandtags));
            StringBuilder stringBuilder = new StringBuilder("Tags:");
            for (String tag : artistDTO.getArtistTags())
                stringBuilder.append(tag).append(',');
            tagsTextView.setText(stringBuilder.toString());


            //Cargamos los artistas similares
            ListView listviewSimilarBands = (ListView) rootView.findViewById(R.id.similarartists);
            View header = inflater.inflate(R.layout.list_similarbands_header, null);
            listviewSimilarBands.addHeaderView(header,null,false);
            Collection<ArtistDTO> listSimilarBands = artistDTO.getSimilarArtists();
            similarBands = new String[listSimilarBands.size()];
            int i=0;
            for (ArtistDTO artist : listSimilarBands)
                similarBands[i++]=artist.getArtistName();
            ArrayAdapter<String> arrayAdapter =  new ArrayAdapter<String>(getActivity(),R.layout.list_similarbands_row, similarBands);
            listviewSimilarBands.setAdapter(arrayAdapter);

            listviewSimilarBands.setOnItemClickListener(new OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
                {
                    if (position>0)
                    {
                        Intent i = new Intent(getActivity(), BandInfoActivity.class);
                        //TODO: Probablemente aqué haya que cambiar cosas para pasar un id
                        i.putExtra(MyAppParameters.BANDID, similarBands[position-1]);
                        startActivity(i);
                    }
                    else if (position==0)
                    {
                        //nos pulsaron en la cabera
                    }

                }
            });

            //Handler del botón de añadir favorito
            Button buttonFavorite = (Button)rootView.findViewById(R.id.addFavouriteBandDetail);
            if (favouriteBandsStore.getFavouriteBands().contains(artistDTO))
            {
                setRemoveFavouriteButton(buttonFavorite,artistDTO);
            }
            else
            {
                setAddFavouriteButton(buttonFavorite,artistDTO);
            }


            //Cargamos la descripción
            TextView descTextView = ((TextView)rootView.findViewById(R.id.detailedbanddescription));
            descTextView.setText(Html.fromHtml(artistDTO.getSummary()));
            descTextView.setMovementMethod(new ScrollingMovementMethod());
            descTextView.setMovementMethod(LinkMovementMethod.getInstance());


        }
        catch (Throwable e)
        {
            Log.e(LOG_TAG,"Se ha producido une error obteniendo la info del artista",e);
            UnexpectedErrorHandler.handleUnexpectedError(this.getActivity(),e);
            DialogUtils.showErrorDialog(this.getActivity(),R.string.lastfm_error);
        }

        return rootView;
    }

    private void setAddFavouriteButton(final Button button, final ArtistDTO artist)
    {
        button.setBackgroundResource(R.drawable.favourite);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favouriteBandsStore.addFavouriteBand(artist);
                setRemoveFavouriteButton(button,artist);
            }
        });

    }

    private void setRemoveFavouriteButton(final Button button, final ArtistDTO artist)
    {
        button.setBackgroundResource(R.drawable.nofavourite);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favouriteBandsStore.removeFavouriteBand(artist);
                setAddFavouriteButton(button,artist);
            }
        });
    }
}
