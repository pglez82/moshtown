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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import es.concertsapp.android.component.ExpandablePanel;
import es.concertsapp.android.component.LastFmImageView;
import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.band.list.favourites.FavouriteBandsStore;
import es.concertsapp.android.gui.legal.LegalConditionsActivity;
import es.concertsapp.android.gui.legal.MoshTownConditionsActivity;
import es.concertsapp.android.utils.LastFmApiConnectorFactory;
import es.concertsapp.android.utils.MyAppParameters;
import es.concertsapp.android.utils.UnexpectedErrorHandler;
import es.concertsapp.android.utils.font.FontUtils;
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
    private int similarArtistsProgressBarState=View.INVISIBLE;


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
                TextView detailedBandName = ((TextView)rootView.findViewById(R.id.detailedbandname));
                detailedBandName.setText(artistDTO.getArtistName());

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
                similarArtistsProgressBar.setVisibility(similarArtistsProgressBarState);

                //Cargamos los artistas similares
                ListView listviewSimilarBands = (ListView) rootView.findViewById(R.id.similarartists);
                TextView noResults = (TextView)rootView.findViewById(R.id.listsimilarnoresults);
                listviewSimilarBands.setEmptyView(noResults);
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

                ExpandablePanel expandablePanelBio = (ExpandablePanel)rootView.findViewById(R.id.expandablepanelbio);
                final ImageView flechaBio = (ImageView)rootView.findViewById(R.id.flechaBio);
                expandablePanelBio.addOnExpandListener(new ExpandablePanel.OnExpandListener()
                {
                    @Override
                    public void onExpand(int id, View handle, View content)
                    {
                        flechaBio.setImageResource(R.drawable.ic_flecha_arriba);
                    }

                    @Override
                    public void onCollapse(int id, View handle, View content)
                    {
                        flechaBio.setImageResource(R.drawable.ic_flecha_abajo);
                    }
                });

                ExpandablePanel expandablePanelRelated = (ExpandablePanel)rootView.findViewById(R.id.expandablepanelrelated);
                final ImageView flechaRelated = (ImageView)rootView.findViewById(R.id.flechaRelated);
                expandablePanelRelated.addOnExpandListener(new ExpandablePanel.OnExpandListener()
                {
                    @Override
                    public void onExpand(int id, View handle, View content)
                    {
                        flechaRelated.setImageResource(R.drawable.ic_flecha_arriba);
                    }

                    @Override
                    public void onCollapse(int id, View handle, View content)
                    {
                        flechaRelated.setImageResource(R.drawable.ic_flecha_abajo);
                    }
                });


                //Cargamos la descripción
                TextView descTextView = ((TextView)rootView.findViewById(R.id.detailedbanddescription));
                if (artistDTO.getSummary()!=null && !"".equals(artistDTO.getSummary()))
                    descTextView.setText(Html.fromHtml(artistDTO.getSummary()));
                else
                    descTextView.setText(getString(R.string.no_bio_text));

                descTextView.setMovementMethod(new ScrollingMovementMethod());
                descTextView.setMovementMethod(LinkMovementMethod.getInstance());

                ImageButton imageButton = (ImageButton)rootView.findViewById(R.id.button_logolastfm);
                imageButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Intent myIntent = new Intent(getActivity(), LegalConditionsActivity.class);
                        startActivity(myIntent);
                    }
                });

                ImageButton imageButton2 = (ImageButton)rootView.findViewById(R.id.button_moshtown);
                imageButton2.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Intent myIntent = new Intent(getActivity(), MoshTownConditionsActivity.class);
                        startActivity(myIntent);
                    }
                });

                //Establecemos las fuentes
                FontUtils.setRobotoFont(getActivity(),detailedBandName, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
                FontUtils.setRobotoFont(getActivity(),noResults, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
                FontUtils.setRobotoFont(getActivity(),descTextView, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
                FontUtils.setRobotoFont(getActivity(),tagsTextView, FontUtils.FontType.ROBOTOCONDENSED_BOLD);
                FontUtils.setRobotoFont(getActivity(),rootView.findViewById(R.id.biopanelbutton),FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
                FontUtils.setRobotoFont(getActivity(),rootView.findViewById(R.id.relatedpanelbutton),FontUtils.FontType.ROBOTOCONDENSED_LIGHT);

            }

        }
        catch (Throwable e)
        {
            Log.e(LOG_TAG,"Se ha producido une error obteniendo la info del artista",e);
            UnexpectedErrorHandler.handleUnexpectedError(this.getActivity(),e);
        }

        return rootView;
    }

    public void setProgressBarVisibility(int visibility)
    {
        this.similarArtistsProgressBarState = visibility;
        similarArtistsProgressBar.setVisibility(similarArtistsProgressBarState);
    }



    private void setAddFavouriteButton(final Button button, final ArtistDTO artist)
    {
        button.setBackgroundResource(R.drawable.ic_estrella_grupos_off);
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
        button.setBackgroundResource(R.drawable.ic_estrella_grupos_on);
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
        private int noElementsVisibility=View.INVISIBLE;

        public synchronized void updateListView(ListView listView)
        {
            this.listView = listView;
            this.listView.setAdapter(similarBandsAdapter);
            this.listView.getEmptyView().setVisibility(noElementsVisibility);
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

        public synchronized void setNoElementsVisibility(int visibility)
        {
            noElementsVisibility = visibility;
            listView.getEmptyView().setVisibility(noElementsVisibility);
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
                FontUtils.setRobotoFont(getActivity(),holder.bandName, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
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
            setNoElementsVisibility(View.INVISIBLE);
            setProgressBarVisibility(View.VISIBLE);
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
            setProgressBarVisibility(View.INVISIBLE);
            if (backgroundError!=null)
                UnexpectedErrorHandler.handleUnexpectedError(getActivity(),backgroundError);
            else
            {
                similarBandsAdapter = new SimilarArtistsAdapter(result);
                updateListView(listView);
                if (result==null || result.isEmpty())
                    setNoElementsVisibility(View.VISIBLE);
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
