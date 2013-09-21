package es.concertsapp.android.gui.mainpage;


//import android.app.ActionBar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.band.list.BandMainActivity;
import es.concertsapp.android.gui.event.list.EventListActivity;
import es.concertsapp.android.gui.menu.MenuActivity;
import es.concertsapp.android.utils.LastFmApiConnectorFactory;
import es.lastfm.api.connector.tags.PunkTags;


public class MainActivity extends MenuActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        //Inicializamos el api de last.fm
        LastFmApiConnectorFactory.initilize(new PunkTags());

        setContentView(R.layout.main_layout);
		
		try
		{
			String version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			setTitle("Versión:"+version);
		}
		catch (Throwable e){};

        Button buttonConcerts = (Button)findViewById(R.id.buttonConciertos);
        buttonConcerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonConciertos(v);
            }
        });

        Button buttonBands = (Button)findViewById(R.id.buttonGrupos);
        buttonBands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonBands(v);
            }
        });

        Button buttonTest = (Button)findViewById(R.id.buttonTest);
        buttonTest.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent myIntent = new Intent(MainActivity.this, ExpandablePanelImplementation.class);
                startActivity(myIntent);
            }
        });
	}

    private void buttonConciertos(View v)
    {
        Intent myIntent = new Intent(MainActivity.this, EventListActivity.class);
        this.startActivity(myIntent);
    }

    private void buttonBands(View v)
    {
        Intent myIntent = new Intent(MainActivity.this, BandMainActivity.class);
        this.startActivity(myIntent);
    }
}
