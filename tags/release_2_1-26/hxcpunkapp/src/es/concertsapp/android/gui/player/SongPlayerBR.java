package es.concertsapp.android.gui.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by pablo on 19/11/13.
 */
public class SongPlayerBR extends BroadcastReceiver
{
        @Override
        public void onReceive(Context context, Intent intent) {
            SongPlayer.getInstance().stopSong(context);
        }
}
