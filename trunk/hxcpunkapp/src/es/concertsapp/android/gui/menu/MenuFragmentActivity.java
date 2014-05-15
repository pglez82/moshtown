package es.concertsapp.android.gui.menu;

import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        return MenuActions.onPrepareOptionsMenu(menu,this);
    }
}
