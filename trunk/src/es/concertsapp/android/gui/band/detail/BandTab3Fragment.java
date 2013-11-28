package es.concertsapp.android.gui.band.detail;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.umass.lastfm.Channel;
import de.umass.lastfm.Item;
import es.concertsapp.android.component.ExpandablePanel;
import es.concertsapp.android.component.ExpandablePanelGroup;
import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.legal.LegalConditionsActivity;
import es.concertsapp.android.gui.legal.MoshTownConditionsActivity;
import es.concertsapp.android.gui.player.SongPlayer;
import es.concertsapp.android.utils.DialogUtils;
import es.concertsapp.android.utils.LastFmApiConnectorFactory;
import es.concertsapp.android.utils.MyAppParameters;
import es.concertsapp.android.utils.UnexpectedErrorHandler;
import es.concertsapp.android.utils.font.FontUtils;
import es.concertsapp.android.utils.spotify.SpotifyUtils;
import es.lastfm.api.connector.LastFmApiConnector;


public class BandTab3Fragment extends ListFragment implements SongPlayer.PlayerStatusChangedListener
{
    private static final String LOG_TAG = "BANDTAB3FRAGMENT";

    private String artistName;

    private Throwable backgroundError=null;

    private DonwloadPodcastsTask downloadPodcastTask;

    //Barras de progreso
    private ProgressBar progressBarStreaming;
    private int progressBarStreamingStatus=View.INVISIBLE;

    private ExpandablePanel expandablePanelSpotify;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        SongPlayer.getInstance().setListener(this);
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
        progressBarStreaming=(ProgressBar)view.findViewById(R.id.progressbarstreaming);
        expandablePanelSpotify = (ExpandablePanel)view.findViewById(R.id.expandablepanelspotify);
        progressBarStreaming.setVisibility(progressBarStreamingStatus);

        ImageButton imageButton = (ImageButton)view.findViewById(R.id.button_logolastfm);
        imageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent myIntent = new Intent(getActivity(), LegalConditionsActivity.class);
                startActivity(myIntent);
            }
        });

        ImageButton imageButton2 = (ImageButton)view.findViewById(R.id.button_moshtown);
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
        FontUtils.setRobotoFont(getActivity(),view.findViewById(R.id.lastfmpanelbutton),FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
        FontUtils.setRobotoFont(getActivity(),view.findViewById(R.id.spotifypanelbutton),FontUtils.FontType.ROBOTOCONDENSED_LIGHT);

        ExpandablePanel expandablePanelLast = (ExpandablePanel)view.findViewById(R.id.expandablepanellastfm);
        final ImageView flechaLast = (ImageView)view.findViewById(R.id.flechaLast);
        expandablePanelLast.addOnExpandListener(new ExpandablePanel.OnExpandListener()
        {
            @Override
            public void onExpand(int id, View handle, View content)
            {
                flechaLast.setImageResource(R.drawable.ic_flecha_arriba);
            }

            @Override
            public void onCollapse(int id, View handle, View content)
            {
                flechaLast.setImageResource(R.drawable.ic_flecha_abajo);
            }
        });

        ExpandablePanel expandablePanelSpotify = (ExpandablePanel)view.findViewById(R.id.expandablepanelspotify);
        final ImageView flechaSpotify = (ImageView)view.findViewById(R.id.flechaSpotify);
        expandablePanelSpotify.addOnExpandListener(new ExpandablePanel.OnExpandListener()
        {
            @Override
            public void onExpand(int id, View handle, View content)
            {
                flechaSpotify.setImageResource(R.drawable.ic_flecha_arriba);
            }

            @Override
            public void onCollapse(int id, View handle, View content)
            {
                flechaSpotify.setImageResource(R.drawable.ic_flecha_abajo);
            }
        });

        ListView listView = getListView();
        if (!artistName.contains("&"))
        {
            if (downloadPodcastTask==null)
            {
                downloadPodcastTask = new DonwloadPodcastsTask(listView);
                downloadPodcastTask.execute(artistName);
            }
            else
            {
                downloadPodcastTask.updateListView(listView);
            }
        }
        else
        {

        }


    }

    public void setStreamingProgressBarVisibility(int visibility)
    {
        this.progressBarStreamingStatus = visibility;
        progressBarStreaming.setVisibility(progressBarStreamingStatus);
    }

    @Override
    public void onDestroy()
    {
        Log.d(LOG_TAG,"Destruido el fragmento");
        if (downloadPodcastTask!=null)
        {
            Log.d(LOG_TAG,"Cancelamos el hilo de buscar spotify y canciones de last.fm porque hemos destruido el fragmento");
            downloadPodcastTask.cancel(true);
        }
        super.onDestroy();
    }

    private void showPlayButton(final ImageButton imageButton,final Item song,final DonwloadPodcastsTask.PodcastsAdapter podcastAdapter)
    {
        imageButton.setBackgroundResource(R.drawable.ic_play);
        imageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                playSong(song);
                showStopButton(imageButton,song,podcastAdapter);
                podcastAdapter.notifyDataSetChanged();
            }
        });
    }

    private void showStopButton(final ImageButton imageButton,final Item song,final DonwloadPodcastsTask.PodcastsAdapter podcastAdapter)
    {
        imageButton.setBackgroundResource(R.drawable.ic_stop);
        imageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                stopSong();
                showPlayButton(imageButton,song,podcastAdapter);
                podcastAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void playerStatusChanged()
    {
        ListView listView = getListView();
        if (listView!=null)
        {
            BaseAdapter bd = (BaseAdapter)listView.getAdapter();
            if (bd!=null)
                bd.notifyDataSetChanged();
        }
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
        private PodcastsAdapter podcastsAdapter;
        private String spotifyUri;
        private SpotifyUtils spotifyUtils = SpotifyUtils.getInstance();

        public synchronized void updateListView(ListView listView)
        {
            this.listView = listView;
            this.listView.setAdapter(podcastsAdapter);
            WebView webView = (WebView)getActivity().findViewById(R.id.spotifyWebView);
            TextView textViewNS = (TextView)getActivity().findViewById(R.id.textSpotifyNotInstalled);
            Button buttonNS = (Button)getActivity().findViewById(R.id.buttonSpotifyNotInstalled);
            if (spotifyUtils.isSpotifyInstalled(getActivity()))
            {
                if (webView!=null) webView.setVisibility(View.VISIBLE);
                if (textViewNS!=null)textViewNS.setVisibility(View.GONE);
                if (buttonNS!=null)buttonNS.setVisibility(View.GONE);
                if (spotifyUri!=null && !"".equals(spotifyUri))
                {
                    showSpotify(spotifyUri,webView);
                    if (podcastsAdapter==null || podcastsAdapter.isEmpty())
                    {
                        if (!expandablePanelSpotify.isExpanded())
                            expandablePanelSpotify.tooglePanel();
                    }
                }
            }
            else
            {
                if (webView!=null) webView.setVisibility(View.GONE);
                if (textViewNS!=null)textViewNS.setVisibility(View.VISIBLE);
                if (buttonNS!=null)
                {
                    buttonNS.setVisibility(View.VISIBLE);
                    buttonNS.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            spotifyUtils.lauchSpotifyInstall(getActivity());
                        }
                    });
                }


            }
        }

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
                PodcastHolder holder;

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

                //Establecemos la fuente
                FontUtils.setRobotoFont(getActivity(),holder.song, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);

                //Miramos que canción se está reproduciendo
                Item songPlaying = SongPlayer.getInstance().getSongPlaying();
                if (songPlaying!=null && songPlaying.getTitle().equals(songLastfm.getTitle()))
                    showStopButton(holder.playstopButton,songLastfm,podcastsAdapter);
                else
                    showPlayButton(holder.playstopButton,songLastfm,podcastsAdapter);

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
            setStreamingProgressBarVisibility(ProgressBar.VISIBLE);
            listView.getEmptyView().setVisibility(View.INVISIBLE);
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
            if (!isCancelled())
            {
                if (backgroundError!=null)
                    UnexpectedErrorHandler.handleUnexpectedError(getActivity(),backgroundError);
                else
                {
                    podcastsAdapter = new PodcastsAdapter(result);
                    updateListView(listView);
                    setStreamingProgressBarVisibility(ProgressBar.INVISIBLE);
                }
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

    private synchronized void showSpotify(String uri, WebView webView)
    {
        if (webView!=null)
        {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            StringBuilder embed = new StringBuilder();
            embed.append("<html><head><style>body{margin:0;margin-left:-7px}</style></head><body><iframe src=\"");
            embed.append(uri);
            embed.append("\" width=\"300\" height=\"380\" frameborder=\"0\" allowtransparency=\"true\"></iframe></body></html>");
            webView.loadData(embed.toString() , "text/html" , "utf-8");
        }
    }

    @Override
    public void onDestroyView()
    {
        Log.d(LOG_TAG,"Destruida la vista");
        super.onDestroyView();
        SongPlayer.getInstance().setListener(null);
        if (SongPlayer.getInstance().isPlaying() && artistName.equals(SongPlayer.getInstance().getBandPlaying()))
        {
            DialogUtils.showToast(this.getActivity(), Toast.LENGTH_LONG,R.string.toast_player);
        }
    }
}
