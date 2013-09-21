package es.concertsapp.android.gui.player;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import de.umass.lastfm.Item;

/**
 * Created by pablo on 21/09/13.
 */
public class SongPlayer implements MediaPlayer.OnPreparedListener
{




    public interface PlayerStatusChangedListener
    {
        public void playerStatusChanged();
    }

    private static final String LOG_TAG = "SONGPLAYER";
    private static SongPlayer INSTANCE = new SongPlayer();

    private MediaPlayer mediaPlayer;
    private Item songPlaying=null;
    private List<Item> listSongs;
    private String bandPlaying;
    private PlayerStatusChangedListener listener;

    private SongPlayer()
    {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
    }

    public static SongPlayer getInstance()
    {
        if (INSTANCE==null)
            INSTANCE=new SongPlayer();
        return INSTANCE;
    }

    public void setListener(PlayerStatusChangedListener listener)
    {
        this.listener = listener;
    }

    public void setListSongs(List<Item> listSongs)
    {
        this.listSongs = listSongs;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer)
    {
        if (songPlaying!=null)
        {
            mediaPlayer.start();
            Log.d(LOG_TAG,"Hemos empezado a reproducir una canción");
        }
        else
        {
            mediaPlayer.reset();
            Log.d(LOG_TAG,"Han pulsado la parada antes de que acabasemos de preparar");
        }
    }

    public Item getSongPlaying()
    {
        return songPlaying;
    }

    public void setBandPlaying(String bandPlaying)
    {
        this.bandPlaying = bandPlaying;
    }

    public void playSong(Item song)
    {
        try
        {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(song.getEnclosureUrl());
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.prepareAsync();
            Log.d(LOG_TAG,"Empezamos a preparar la canción");
            // Setup listener so next song starts automatically
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
            {

                public void onCompletion(MediaPlayer arg0)
                {
                    //Empezamos la siguiente canción si la hay
                    int index=listSongs.indexOf(songPlaying);
                    if (index>=0 && index<listSongs.size()-1)
                        playSong(listSongs.get(index+1));
                    else
                    {
                        songPlaying=null;
                        if (listener!=null)
                            listener.playerStatusChanged();
                    }
                }

            });
            songPlaying=song;
            if (listener!=null)
                listener.playerStatusChanged();

        } catch (Throwable e) {
            Log.e(LOG_TAG, "Error arrancando una canción", e);
            if (listener!=null)
                listener.playerStatusChanged();
        }
    }

    public void stopSong()
    {
        Log.d(LOG_TAG,"Paramos la canción");
        mediaPlayer.stop();
        songPlaying=null;
        if (listener!=null)
            listener.playerStatusChanged();
    }

    public boolean isPlaying()
    {
        return songPlaying!=null;
    }

    public String getBandPlaying()
    {
        return bandPlaying;
    }
}
