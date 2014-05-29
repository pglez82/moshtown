package es.concertsapp.android.gui.band.detail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import es.concertsapp.android.utils.MyAppParameters;
import es.lastfm.api.connector.dto.ArtistDTO;

public class BandSectionsPageAdapter extends FragmentPagerAdapter 
{
	private String bandName;
    private ArtistDTO artistDTO;
	 
	public BandSectionsPageAdapter(FragmentManager fm,String bandName,ArtistDTO artistDTO)
	{
        super(fm);
        this.bandName = bandName;
        this.artistDTO=artistDTO;
    }

    @Override
    public Fragment getItem(int i) {
    	Bundle args = new Bundle();
        args.putString(MyAppParameters.BANDID, this.bandName);
        switch (i) {
            case 0:
            	Fragment fragment = new BandTab1Fragment(artistDTO);
                fragment.setArguments(args);
                return fragment;
            case 1:
            	fragment = new BandTab2Fragment(artistDTO);
                fragment.setArguments(args);
            	return fragment;
            case 2:
            	fragment = new BandTab3Fragment();
            	fragment.setArguments(args);
            	return fragment;
            default:
            	return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "TabBanda " + (position + 1);
    }

}
