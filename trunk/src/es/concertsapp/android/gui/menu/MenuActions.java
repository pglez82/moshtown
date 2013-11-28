package es.concertsapp.android.gui.menu;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.band.detail.BandInfoActivity;
import es.concertsapp.android.gui.band.list.BandMainActivity;
import es.concertsapp.android.gui.event.list.EventListActivity;
import es.concertsapp.android.gui.mainpage.MainActivity;
import es.concertsapp.android.gui.player.SongPlayer;
import es.concertsapp.android.utils.MyAppParameters;

/**
 * Created by pablo on 11/09/13.
 */
public class MenuActions
{
    protected static boolean onCreateOptionsMenu(Menu menu, Activity activity)
    {
        MenuInflater inflater = activity.getMenuInflater();
        inflater.inflate(R.menu.general_menu, menu);
        return true;
    }

    protected static boolean onPrepareOptionsMenu(Menu menu, Activity activity)
    {
        SongPlayer songPlayer = SongPlayer.getInstance();
        menu.findItem(R.id.menu_player).setVisible(songPlayer.isPlaying());
        return true;
    }



    protected static boolean onOptionsItemSelected(MenuItem item, Activity activity)
    {
        Intent myIntent;
        switch (item.getItemId()) {
            case R.id.menu_inicio:
                myIntent = new Intent(activity, MainActivity.class);
                activity.startActivity(myIntent);
                return true;
            case R.id.menu_searchevents:
                myIntent = new Intent(activity, EventListActivity.class);
                activity.startActivity(myIntent);
                return true;
            case R.id.menu_searchbands:
                myIntent = new Intent(activity, BandMainActivity.class);
                activity.startActivity(myIntent);
                return true;
            case R.id.menu_favouritebands:
                myIntent = new Intent(activity, BandMainActivity.class);
                myIntent.putExtra(MyAppParameters.FRAGMENTID, 1);
                activity.startActivity(myIntent);
                return true;
            case R.id.menu_close:
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (SongPlayer.getInstance().isPlaying())
                    SongPlayer.getInstance().stopSong(activity);
                activity.startActivity(intent);
                return true;
            case R.id.menu_stop:
                SongPlayer.getInstance().stopSong(activity);
                return true;
            case R.id.menu_show:
                myIntent = new Intent(activity, BandInfoActivity.class);
                myIntent.putExtra(MyAppParameters.BANDID, SongPlayer.getInstance().getBandPlaying());
                myIntent.putExtra(MyAppParameters.FRAGMENTID, 2);
                activity.startActivity(myIntent);
            default:
                return false;
        }

    }
}
