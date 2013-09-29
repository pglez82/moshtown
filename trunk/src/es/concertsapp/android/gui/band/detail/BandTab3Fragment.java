package es.concertsapp.android.gui.band.detail;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.umass.lastfm.Channel;
import de.umass.lastfm.Item;
import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.player.SongPlayer;
import es.concertsapp.android.utils.DialogUtils;
import es.concertsapp.android.utils.LastFmApiConnectorFactory;
import es.concertsapp.android.utils.MyAppParameters;
import es.concertsapp.android.utils.UnexpectedErrorHandler;
import es.lastfm.api.connector.LastFmApiConnector;


public class BandTab3Fragment extends ListFragment implements SongPlayer.PlayerStatusChangedListener
{
    private static final String LOG_TAG = "BANDTAB3FRAGMENT";
    private String spotifyUri;
    private String artistName;

    private Throwable backgroundError=null;

    //Barras de progreso
    private ProgressBar lastfmProgressBar;
    private ProgressBar spotifyProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        SongPlayer.getInstance().setListener(this);
    }

    private void retrieveInformation(String artistName)
    {
        new DonwloadPodcastsTask(getListView()).execute(artistName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.activity_band_info_t3,container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        artistName = args.getString(MyAppParameters.BANDID);
        lastfmProgressBar=(ProgressBar)view.findViewById(R.id.progressbarlastfm);
        spotifyProgressBar=(ProgressBar)view.findViewById(R.id.progressbarspotify);
        retrieveInformation(artistName);
    }

    private void showPlayButton(final ImageButton imageButton,final Item song)
    {
        imageButton.setBackgroundResource(R.drawable.play);
        imageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                playSong(song);
                showStopButton(imageButton,song);
            }
        });
    }

    private void showStopButton(final ImageButton imageButton,final Item song)
    {
        imageButton.setBackgroundResource(R.drawable.stop);
        imageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                stopSong();
                showPlayButton(imageButton,song);
            }
        });
    }

    @Override
    public void playerStatusChanged()
    {
        ((BaseAdapter)getListView().getAdapter()).notifyDataSetChanged();
    }

    static class PodcastHolder
    {
        TextView song;
        ImageButton playstopButton;
    }

    private class DonwloadPodcastsTask extends AsyncTask<String, Void, Channel>
    {
        //Donde se van a cargar los datos
        private ListView listView;

        //Adapter para mostrar los datos cargados por este hilo
        private class PodcastsAdapter extends BaseAdapter
        {
            private List<Item> songs;

            public PodcastsAdapter(Channel podcasts)
            {
                if (podcasts!=null && podcasts.getItems()!=null && podcasts.getItems().size()>0)
                    songs = new ArrayList<Item>(podcasts.getItems());
                else
                    songs = new ArrayList<Item>();
            }

            @Override
            public int getCount()
            {
                synchronized (songs)
                {
                    return songs.size();
                }
            }

            @Override
            public Object getItem(int position)
            {
                synchronized (songs)
                {
                    return songs.get(position);
                }
            }

            @Override
            public long getItemId(int position)
            {
                return position;
            }

            public List<Item> getSongs()
            {
                return songs;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                View row = convertView;
                PodcastHolder holder = null;

                if (row == null) {
                    LayoutInflater inflater = (getActivity()).getLayoutInflater();
                    row = inflater.inflate(R.layout.song_item, parent, false);

                    holder = new PodcastHolder();
                    holder.song = (TextView)row.findViewById(R.id.songtitle);
                    holder.playstopButton = (ImageButton)row.findViewById(R.id.playstop);
                    row.setTag(holder);
                } else {
                    holder = (PodcastHolder) row.getTag();
                }

                Item songLastfm = (Item)this.getItem(position);
                holder.song.setText(songLastfm.getTitle());
                //Miramos que canción se está reproduciendo
                Item songPlaying = SongPlayer.getInstance().getSongPlaying();
                if (songPlaying!=null && songPlaying.getTitle().equals(songLastfm.getTitle()))
                    showStopButton(holder.playstopButton,songLastfm);
                else
                    showPlayButton(holder.playstopButton,songLastfm);

                return row;
            }
        }

        public DonwloadPodcastsTask(ListView listView)
        {
            this.listView = listView;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            lastfmProgressBar.setVisibility(ProgressBar.VISIBLE);
            spotifyProgressBar.setVisibility(ProgressBar.VISIBLE);
            try
            {
                getListView().getEmptyView().setVisibility(View.INVISIBLE);
            }
            catch (Throwable e)
            {
                //Todo: a la espera de una solución mejor. A veces casca porque la vista no está creada. No entiendo porqué puede ser esto, siempre debería de estar
                /*Si la lista no existe nos comemos la excepcion*/
            }
            Log.d(LOG_TAG,"Arrancamos el hilo de buscar canciones del grupo");
        }

        @Override
        protected Channel doInBackground(String... params)
        {
            LastFmApiConnector lastFmApiConnector = LastFmApiConnectorFactory.getInstance();
            Channel podcasts = null;
            try
            {
                Log.d(LOG_TAG,"Lanzamos la petición a last.fm para las canciones y el spotify");
                podcasts =lastFmApiConnector.getArtistPodcast(params[0]);
                spotifyUri=lastFmApiConnector.getSpotifyUri(params[0]);
                Log.d(LOG_TAG,"Finalizada la petición, devolviendo resultados");
            }
            catch (Throwable e)
            {
                Log.e(LOG_TAG,"Se ha producido un error obteniendo los eventos de un artista",e);
                backgroundError = e;
            }
            return podcasts;
        }

        @Override
        protected void onPostExecute(final Channel result)
        {
            try
            {
                getListView().getEmptyView().setVisibility(View.VISIBLE);
            }
            catch (Throwable e){/*Si la lista no existe nos comemos la excepcion*/}
            if (backgroundError!=null)
                UnexpectedErrorHandler.handleUnexpectedError(getActivity(),backgroundError);
            else
            {
                PodcastsAdapter podcastsAdapter = new PodcastsAdapter(result);
                listView.setAdapter(podcastsAdapter);
                if (spotifyUri!=null && !"".equals(spotifyUri))
                {
                    showSpotify(spotifyUri);
                }
                lastfmProgressBar.setVisibility(ProgressBar.INVISIBLE);
                spotifyProgressBar.setVisibility(ProgressBar.INVISIBLE);
            }
        }
    }

    private void playSong(Item song)
    {
        SongPlayer.getInstance().setListSongs(((DonwloadPodcastsTask.PodcastsAdapter)getListView().getAdapter()).getSongs());
        SongPlayer.getInstance().setBandPlaying(((BandInfoActivity)this.getActivity()).getArtistName());
        SongPlayer.getInstance().playSong(song,getActivity());
    }

    private void stopSong()
    {
        SongPlayer.getInstance().stopSong(getActivity());
    }

    private void showSpotify(String uri)
    {
        WebView webView = (WebView)getActivity().findViewById(R.id.spotifyWebView);
        if (webView!=null)
        {
            webView.getSettings().setJavaScriptEnabled(true);
            StringBuilder embed = new StringBuilder();
            embed.append("<html><body style=\"text-align=\"center\"><iframe src=\"");
            embed.append(uri);
            embed.append("\" width=\"300\" height=\"380\" frameborder=\"0\" allowtransparency=\"true\"></iframe></body></html>");
            webView.loadData(embed.toString() , "text/html" , "utf-8");
        }
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        SongPlayer.getInstance().setListener(null);
    }

    /**
     * Cuando el usuario sale de la pantalla le avisamos de que puede controlar el reproductor desde el menu
     */
    @Override
    public void onStop()
    {
        super.onStop();
        if (SongPlayer.getInstance().isPlaying() && artistName.equals(SongPlayer.getInstance().getBandPlaying()))
        {
            DialogUtils.showToast(this.getActivity(), Toast.LENGTH_LONG,R.string.toast_player);
        }
    }
}
