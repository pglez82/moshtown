package es.concertsapp.android.gui.band.detail;

import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import es.concertsapp.android.component.LastFmImageView;
import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.band.list.favourites.FavouriteBandsStore;
import es.concertsapp.android.utils.LastFmApiConnectorFactory;
import es.concertsapp.android.utils.MyAppParameters;
import es.concertsapp.android.utils.UnexpectedErrorHandler;
import es.lastfm.api.connector.LastFmApiConnector;
import es.lastfm.api.connector.dto.ArtistDTO;
import es.lastfm.api.connector.exception.LastFmException;


public class BandTab1Fragment extends Fragment
{
    private static final String LOG_TAG="BANDTAB1FRAGMENT";
    private FavouriteBandsStore favouriteBandsStore;
    private ArtistDTO artistDTO;

    private Throwable backgroundError=null;
    private ProgressBar similarArtistsProgressBar;
    //Hilo para bajar los artistas similares
    private DownloadSimilarArtists downloadSimilarArtists;

    @SuppressWarnings("unused")
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
            if (rootView!=null)
            {
                ((TextView)rootView.findViewById(R.id.detailedbandname)).setText(artistDTO.getArtistName());

                //Cargamos la imagen
                LastFmImageView artistImageView = (LastFmImageView)rootView.findViewById(R.id.detailedbandimage);
                artistImageView.setLastFmImageSource(artistDTO);

                //Tags del artista
                TextView tagsTextView = ((TextView)rootView.findViewById(R.id.detailedbandtags));
                StringBuilder sb = new StringBuilder();
                Iterator<String> tags = artistDTO.getArtistTags().iterator();
                while (tags.hasNext())
                {
                    sb.append(tags.next());
                    if (tags.hasNext()) sb.append(", ");
                }
                tagsTextView.setText(sb.toString());

                similarArtistsProgressBar = (ProgressBar)rootView.findViewById(R.id.progressbarsimilarband);

                //Cargamos los artistas similares
                ListView listviewSimilarBands = (ListView) rootView.findViewById(R.id.similarartists);
                if (downloadSimilarArtists==null)
                {
                    downloadSimilarArtists = new DownloadSimilarArtists(listviewSimilarBands);
                    downloadSimilarArtists.execute();
                }
                else
                    downloadSimilarArtists.updateListView(listviewSimilarBands);

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

        }
        catch (Throwable e)
        {
            Log.e(LOG_TAG,"Se ha producido une error obteniendo la info del artista",e);
            UnexpectedErrorHandler.handleUnexpectedError(this.getActivity(),e);
        }

        return rootView;
    }

    private void setAddFavouriteButton(final Button button, final ArtistDTO artist)
    {
        button.setBackgroundResource(R.drawable.ic_estrella_on);
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
        button.setBackgroundResource(R.drawable.ic_estrella_off);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favouriteBandsStore.removeFavouriteBand(artist);
                setAddFavouriteButton(button,artist);
            }
        });
    }


    static class ArtistHolder
    {
        TextView bandName;
    }

    private class DownloadSimilarArtists extends AsyncTask<String, Void, List<ArtistDTO>>
    {
        //Donde se van a cargar los datos
        private ListView listView;
        private SimilarArtistsAdapter similarBandsAdapter;

        public synchronized void updateListView(ListView listView)
        {
            this.listView = listView;
            this.listView.setAdapter(similarBandsAdapter);
            this.listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long arg3) {
                    Intent i = new Intent(getActivity(),BandInfoActivity.class);

                    ArtistDTO similarArtist = (ArtistDTO)similarBandsAdapter.getItem(position);
                    i.putExtra(MyAppParameters.BANDID, similarArtist.getArtistName());
                    startActivity(i);
                }
            });
        }

        //Adapter para mostrar los datos cargados por este hilo
        private class SimilarArtistsAdapter extends BaseAdapter
        {
            private List<ArtistDTO> listSimilarArtists;

            public SimilarArtistsAdapter(List<ArtistDTO> listEvents)
            {
                this.listSimilarArtists = listEvents;
            }

            @Override
            public int getCount()
            {
                synchronized (listSimilarArtists)
                {
                    return listSimilarArtists.size();
                }
            }

            @Override
            public Object getItem(int position)
            {
                synchronized (listSimilarArtists)
                {
                    return listSimilarArtists.get(position);
                }
            }

            @Override
            public long getItemId(int position)
            {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                View row = convertView;
                ArtistHolder holder;

                if (row == null) {
                    LayoutInflater inflater = (getActivity()).getLayoutInflater();
                    row = inflater.inflate(R.layout.item_similarartist_row, parent, false);

                    holder = new ArtistHolder();
                    holder.bandName = (TextView)row.findViewById(R.id.similarartistname);
                    row.setTag(holder);
                } else {
                    holder = (ArtistHolder) row.getTag();
                }

                ArtistDTO artistDTO = (ArtistDTO)this.getItem(position);
                holder.bandName.setText(artistDTO.getArtistName());

                return row;
            }
        }

        public DownloadSimilarArtists(ListView listView)
        {
            this.listView = listView;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            similarArtistsProgressBar.setVisibility(View.VISIBLE);
            backgroundError=null;
        }

        @Override
        protected List<ArtistDTO> doInBackground(String... params)
        {
            LastFmApiConnector lastFmApiConnector = LastFmApiConnectorFactory.getInstance();
            List<ArtistDTO> listArtist = null;
            try
            {
                listArtist=lastFmApiConnector.getSimilarArtistsFiltered(artistDTO);
            }
            catch (LastFmException e)
            {
                Log.e(LOG_TAG,"Se ha producido un error obteniendo los artistas similares",e);
                backgroundError = e;
            }
            return listArtist;
        }

        @Override
        protected void onPostExecute(final List<ArtistDTO> result)
        {
            similarArtistsProgressBar.setVisibility(View.INVISIBLE);
            if (backgroundError!=null)
                UnexpectedErrorHandler.handleUnexpectedError(getActivity(),backgroundError);
            else
            {
                similarBandsAdapter = new SimilarArtistsAdapter(result);
                updateListView(listView);
            }
        }
    }

    @Override
    public void onDestroy()
    {
        Log.d(LOG_TAG,"Destruido el fragmento");
        if (downloadSimilarArtists!=null)
        {
            downloadSimilarArtists.cancel(true);
        }
        super.onDestroy();
    }
}
