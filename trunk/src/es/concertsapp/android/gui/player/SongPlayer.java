package es.concertsapp.android.gui.player;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.List;

import de.umass.lastfm.Item;
import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.band.detail.BandInfoActivity;
import es.concertsapp.android.utils.MyAppParameters;
import es.concertsapp.android.utils.UnexpectedErrorHandler;

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

    private boolean paused=false;
    private int position;
    private PhoneStateListener phoneStateListener;

    private MediaPlayer mediaPlayer;
    private Item songPlaying=null;
    private List<Item> listSongs;
    private String bandPlaying;
    private PlayerStatusChangedListener listener;
    private int notificationId=1;

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

    public void playSong(Item song, final Context context)
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
                        playSong(listSongs.get(index+1),context);
                    else
                    {
                        songPlaying=null;
                        if (listener!=null)
                            listener.playerStatusChanged();
                        updateNotification(bandPlaying,songPlaying,context);
                    }
                }

            });
            songPlaying=song;
            if (listener!=null)
                listener.playerStatusChanged();
            updateNotification(bandPlaying,songPlaying,context);
            createCallListener(context);

        } catch (Throwable e)
        {
            Log.e(LOG_TAG, "Error arrancando una canción", e);
            UnexpectedErrorHandler.handleUnexpectedError(context, e);

        }
    }

    public void stopSong(Context context)
    {
        Log.d(LOG_TAG,"Paramos la canción");
        mediaPlayer.stop();
        songPlaying=null;
        if (listener!=null)
            listener.playerStatusChanged();
        updateNotification(bandPlaying,songPlaying,context);
        removeCallListener(context);
    }

    public void pauseSong(Context context)
    {
        if (mediaPlayer !=null)
        {
            mediaPlayer.pause();
            position=mediaPlayer.getCurrentPosition();
            paused=true;
        }
    }

    public void resumeSong(Context context)
    {
        if (mediaPlayer!=null && paused)
        {
            mediaPlayer.seekTo(position);
            mediaPlayer.start();
        }
    }

    public boolean isPlaying()
    {
        return songPlaying!=null;
    }

    public String getBandPlaying()
    {
        return bandPlaying;
    }

    private void updateNotification(String band, Item song, Context context) {


        NotificationManager myNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (songPlaying!=null)
        {
            RemoteViews mNotificationView = new RemoteViews(context.getPackageName(),R.layout.player_notification_view);
            mNotificationView.setTextViewText(R.id.notTextBand,band);
            mNotificationView.setTextViewText(R.id.notTextSong,song.getTitle());
            //Añadimos la posiblidad de cerrar el reproductor desde aquí
            PendingIntent closePendingIntent = PendingIntent.getBroadcast(context,0,new Intent("es.concertsapp.android.gui.player.STOPPLAYER"),0);
            mNotificationView.setOnClickPendingIntent(R.id.notStopButton,closePendingIntent);



            Intent notificationIntent = new Intent(context, BandInfoActivity.class);
            notificationIntent.putExtra(MyAppParameters.BANDID, getBandPlaying());
            notificationIntent.putExtra(MyAppParameters.FRAGMENTID, 2);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(BandInfoActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(notificationIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            Notification not =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_principal)
                            .setContentIntent(resultPendingIntent)
                            .setOngoing(true).build();
            not.contentView=mNotificationView;
            myNotificationManager.notify(notificationId, not);
        }
        else
        {
            myNotificationManager.cancel(notificationId);
        }


    }

    private void createCallListener(final Context context)
    {
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    SongPlayer.getInstance().pauseSong(context);

                } else if(state == TelephonyManager.CALL_STATE_IDLE) {
                    SongPlayer.getInstance().resumeSong(context);
                } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    SongPlayer.getInstance().pauseSong(context);
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        TelephonyManager mgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if(mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    private void removeCallListener(Context context)
    {
        TelephonyManager mgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if(mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }
}
