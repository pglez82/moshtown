package es.concertsapp.android.gui.band.detail;


import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.umass.lastfm.Channel;
import de.umass.lastfm.Item;
import es.concertsapp.android.gui.R;
import es.concertsapp.android.utils.LastFmApiConnectorFactory;
import es.concertsapp.android.utils.MyAppParameters;
import es.concertsapp.android.utils.UnexpectedErrorHandler;
import es.lastfm.api.connector.LastFmApiConnector;
import es.lastfm.api.connector.exception.LastFmException;


public class BandTab3Fragment extends ListFragment implements MediaController.MediaPlayerControl, MediaPlayer.OnPreparedListener
{
    private static final String LOG_TAG = "BANDTAB3FRAGMENT";
    private Channel podcasts;
    private String spotifyUri;
    private boolean alreadySearched = false;
    private MediaPlayer mediaPlayer;
    private MediaController mediaController;
    private Handler handler = new Handler();

    //Barras de progreso
    private ProgressBar lastfmProgressBar;
    private ProgressBar spotifyProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        alreadySearched=false;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaController = new MediaController(this.getActivity());
    }

    private void retrieveInformation(String artistName)
    {
        if (!alreadySearched)
        {
            new DonwloadPodcastsTask(getListView()).execute(artistName);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.activity_band_info_t3,container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        String artistName = args.getString(MyAppParameters.BANDID);
        lastfmProgressBar=(ProgressBar)view.findViewById(R.id.progressbarlastfm);
        spotifyProgressBar=(ProgressBar)view.findViewById(R.id.progressbarspotify);
        View v=view.findViewById(R.id.buttonrep);
        v.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mediaController.show(0);
            }
        });
        mediaController.setPrevNextListeners(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
            }
        },
        new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
            }
        });
        retrieveInformation(artistName);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer)
    {
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(getActivity().findViewById(R.id.contentlastfm));

        handler.post(new Runnable() {
            public void run() {
                mediaController.setEnabled(true);
                mediaController.show();
            }
        });
    }

    static class PodcastHolder
    {
        TextView song;
    }

    private class DonwloadPodcastsTask extends AsyncTask<String, Void, Channel>
    {
        //Donde se van a cargar los datos
        private ListView listView;

        //Adapter para mostrar los datos cargados por este hilo
        private class PodcastsAdapter extends BaseAdapter
        {
            private Channel podcasts;
            private List<Item> songs;

            public PodcastsAdapter(Channel podcasts)
            {
                this.podcasts = podcasts;
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
                    row.setTag(holder);
                } else {
                    holder = (PodcastHolder) row.getTag();
                }

                Item songLastfm = (Item)this.getItem(position);
                holder.song.setText(songLastfm.getTitle());
                return row;
            }
        }

        public DonwloadPodcastsTask(ListView listView)
        {
            this.listView = listView;
            alreadySearched=true;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            lastfmProgressBar.setVisibility(ProgressBar.VISIBLE);
            spotifyProgressBar.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        protected Channel doInBackground(String... params)
        {
            LastFmApiConnector lastFmApiConnector = LastFmApiConnectorFactory.getInstance();
            podcasts=null;
            try
            {
                podcasts=lastFmApiConnector.getArtistPodcast(params[0]);
                spotifyUri=lastFmApiConnector.getSpotifyUri(params[0]);
            }
            catch (LastFmException e)
            {
                Log.e(LOG_TAG,"Se ha producido un error obteniendo los eventos de un artista",e);
                UnexpectedErrorHandler.handleUnexpectedError(e);
            }
            return podcasts;
        }

        @Override
        protected void onPostExecute(final Channel result)
        {
            PodcastsAdapter podcastsAdapter = new PodcastsAdapter(result);
            listView.setAdapter(podcastsAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long arg3) {
                    List<Item> songs = new ArrayList<Item>(result.getItems());
                    Item song = songs.get(position);
                    playSong(song);
                }
            });
            mediaController = new MediaController(getActivity());
            if (spotifyUri!=null && !"".equals(spotifyUri))
            {
                showSpotify(spotifyUri);
            }
            lastfmProgressBar.setVisibility(ProgressBar.INVISIBLE);
            spotifyProgressBar.setVisibility(ProgressBar.INVISIBLE);
        }
    }



    private void playSong(Item song) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(song.getEnclosureUrl());
            mediaPlayer.prepare();
            mediaPlayer.start();

            // Setup listener so next song starts automatically
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
            {

                public void onCompletion(MediaPlayer arg0)
                {
                }

            });

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error arrancando una canción",e);
        }
    }

    private void showSpotify(String uri)
    {
        WebView webView = (WebView)getActivity().findViewById(R.id.spotifyWebView);
        webView.getSettings().setJavaScriptEnabled(true);
        StringBuilder embed = new StringBuilder();
        embed.append("<html><body style=\"text-align=\"center\"><iframe src=\"");
        embed.append(uri);
        embed.append("\" width=\"300\" height=\"380\" frameborder=\"0\" allowtransparency=\"true\"></iframe></body></html>");
        webView.loadData(embed.toString() , "text/html" , "utf-8");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mediaPlayer.isPlaying())
            mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    public void start()
    {
        mediaPlayer.start();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public void seekTo(int i) {
        mediaPlayer.seekTo(i);
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public int getBufferPercentage() {
        return 0;
    }

    public boolean canPause() {
        return true;
    }

    public boolean canSeekBackward() {
        return true;
    }

    public boolean canSeekForward() {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer !=null)
        {
            mediaPlayer.release();
        }

    }

}
