package es.concertsapp.android.gui.menu;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.band.list.BandMainActivity;
import es.concertsapp.android.gui.event.list.EventListActivity;

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

    protected static boolean onOptionsItemSelected(MenuItem item, Activity activity)
    {
        Intent myIntent;
        switch (item.getItemId()) {
            case R.id.menu_searchevents:
                myIntent = new Intent(activity, EventListActivity.class);
                activity.startActivity(myIntent);
                return true;
            case R.id.menu_searchbands:
                myIntent = new Intent(activity, BandMainActivity.class);
                activity.startActivity(myIntent);
                return true;
            case R.id.menu_close:
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                return true;
            default:
                return false;
        }

    }
}
