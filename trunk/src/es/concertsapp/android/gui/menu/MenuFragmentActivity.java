package es.concertsapp.android.gui.menu;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.band.list.BandMainActivity;
import es.concertsapp.android.gui.event.list.EventListActivity;

/**
 * Created by pablo on 11/09/13.
 */
public class MenuFragmentActivity extends FragmentActivity
{
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return MenuActions.onCreateOptionsMenu(menu,this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return MenuActions.onOptionsItemSelected(item,this);
    }
}
