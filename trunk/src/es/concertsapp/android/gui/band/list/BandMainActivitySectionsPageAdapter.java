package es.concertsapp.android.gui.band.list;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import es.concertsapp.android.gui.band.list.favourites.BandFavoritesFragment;
import es.concertsapp.android.gui.band.list.favourites.FavouriteBandsStore;

/**
 * Created by pablo on 6/07/13.
 */
public class BandMainActivitySectionsPageAdapter extends FragmentPagerAdapter
{
    private BandListFragment bandListFragment;
    private BandFavoritesFragment bandFavoritesFragment;

    public BandMainActivitySectionsPageAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                bandListFragment = new BandListFragment();
                return bandListFragment;
            case 1:
                bandFavoritesFragment = new BandFavoritesFragment();
                return bandFavoritesFragment;
            case 2:

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }



    @Override
    public CharSequence getPageTitle(int position) {
        return "Principal " + (position + 1);
    }
}
