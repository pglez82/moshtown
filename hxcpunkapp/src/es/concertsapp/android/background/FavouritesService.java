package es.concertsapp.android.background;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.google.code.jspot.Artist;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.concertsapp.android.conf.ConfValues;
import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.band.list.BandMainActivity;
import es.concertsapp.android.gui.band.list.favourites.BandFavoritesFragment;
import es.concertsapp.android.gui.band.list.favourites.FavouriteBandsStore;
import es.concertsapp.android.gui.mainpage.MainActivity;
import es.concertsapp.android.utils.LastFmApiConnectorFactory;
import es.concertsapp.android.utils.MyAppParameters;
import es.concertsapp.android.utils.MyApplication;
import es.concertsapp.android.utils.geo.DistanceCalculator;
import es.concertsapp.android.utils.geo.MyLocation;
import es.lastfm.api.connector.dto.ArtistDTO;
import es.lastfm.api.connector.dto.ArtistEventDTO;

/**
 * Created by pablo on 31/05/14.
 */
public class FavouritesService extends Service
{
    private static final String LOG_TAG = "FAUVORITESSERVICE";
    private static final long INTERVAL = 120*1000;
    private int notificationId=2;

    //Lista de eventos ya notificados por el sistema al usuario. Si encontramos solo de estos, no notificamos.
    private Set<Integer> eventsAlreadyNotified;


    /**
     * Registra la alarma para que se ejecute cada cierto tiempo
     */
    public static void startSchedule()
    {
        Context context = MyApplication.getAppContext();
        if (MyLocation.checkLocationProviders(context)) {
            Intent intent = new Intent(context, FavouritesService.class);
            PendingIntent pintent = PendingIntent.getService(context, 0, intent, 0);

            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            alarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, INTERVAL, INTERVAL, pintent);
        }
        else
            Log.d(LOG_TAG, "No hemos arrancado el servicio de notificaciones porque el usuario no tiene la localización activada");
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //Para la version 4
    @Override
    public void onStart(Intent intent, int startId) {
        handleCommand(intent);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleCommand(intent);
        return START_STICKY;
    }

    /**
     * Carga el conjunto de eventos ya notificados al usuario de disco
     * @param context contexto
     */
    private void loadEventsAlreadyNotified(Context context)
    {
        try
        {
            FileInputStream fis = context.openFileInput(ConfValues.FILENAME_EVENTSNOTIFIED);
            ObjectInputStream is = new ObjectInputStream(fis);
            eventsAlreadyNotified = (Set<Integer>) is.readObject();
            is.close();
            fis.close();
        }
        catch (Throwable e)
        {
            e.printStackTrace();

        }
        finally
        {
            if (eventsAlreadyNotified==null)
            {
                eventsAlreadyNotified = new HashSet<Integer>();
            }
        }
    }

    /**
     * Salva el conjunto de eventos ya notificados a disco
     * @param context contexto
     */
    private void saveEventsAlreadyNotified(Context context)
    {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(ConfValues.FILENAME_EVENTSNOTIFIED, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(eventsAlreadyNotified);
            oos.close();
            fos.close();
        } catch (Throwable e)
        {
            e.printStackTrace();
        }

    }

    private void handleCommand(Intent intent)
    {
                final Context context = MyApplication.getAppContext();
                loadEventsAlreadyNotified(context);
                //Aquí necesitamos tener acceso a la localización. Qué pasa si no tiene habilitado el tema de la localización??
                //Calculo que no deberíamos ni de arrancar este servicio, ni poner la alarma.
                final List<ArtistDTO> listaArtistas = FavouriteBandsStore.loadFavouriteBandsStatic(context);
                final Set<ArtistDTO> listaArtistasEventosCerca = new HashSet<ArtistDTO>();

                if (!listaArtistas.isEmpty()) {
                    MyLocation.getLocation(context, new MyLocation.LocationResult() {
                        @Override
                        public void locationFound(Location location, String name) {
                            try {
                                for (ArtistDTO artist : listaArtistas) {
                                    List<ArtistEventDTO> listArtistEventDTO = LastFmApiConnectorFactory.getInstance().getArtistEvents(artist.getArtistName());
                                    for (ArtistEventDTO artistEventDTO : listArtistEventDTO) {
                                        //Solo tenemos en cuenta el evento si no lo hemos notificado ya al usuario
                                        if (!eventsAlreadyNotified.contains(artistEventDTO.getEventId()))
                                        {
                                            double distance = DistanceCalculator.distance(location.getLatitude(), location.getLongitude(), artistEventDTO.getLatEventPlace(), artistEventDTO.getLonEventPlace());
                                            if (distance < ConfValues.getIntConfigurableValue(context, ConfValues.ConfigurableValue.EVENT_RATIO_DISTANCE)) {
                                                listaArtistasEventosCerca.add(artist);
                                                eventsAlreadyNotified.add(artistEventDTO.getEventId());
                                            }
                                        }
                                    }
                                }
                                if (!listaArtistasEventosCerca.isEmpty()) {
                                    showNotification(context, listaArtistasEventosCerca);
                                }
                                saveEventsAlreadyNotified(context);
                            } catch (Throwable ignored) {
                                //Si se produce una expcion aqui no podemos hacer nada de nada
                            }
                        }
                    });
                }


    }

    private void showNotification(Context context, Set<ArtistDTO> listaArtistas)
    {
        NotificationManager myNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        StringBuilder nombres = new StringBuilder();
        int i=0;
        for (ArtistDTO artist : listaArtistas)
        {
            nombres.append(artist.getArtistName());
            if (i<listaArtistas.size()-1)
                nombres.append(", ");
            i++;
        }

        RemoteViews mNotificationView = new RemoteViews(context.getPackageName(), R.layout.favourites_notification_view);
        mNotificationView.setTextViewText(R.id.notFavInfo,getString(R.string.new_events_notification));
        mNotificationView.setTextViewText(R.id.notFavBands, nombres.toString());

        Intent notificationIntent = new Intent(context, BandMainActivity.class);
        notificationIntent.putExtra(MyAppParameters.FRAGMENTID, 1);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
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
                        .setOngoing(false).build();
        not.contentView=mNotificationView;
        myNotificationManager.notify(notificationId, not);
    }
}
