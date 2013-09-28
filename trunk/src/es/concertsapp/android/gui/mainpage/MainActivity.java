package es.concertsapp.android.gui.mainpage;


//import android.app.ActionBar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.band.list.BandMainActivity;
import es.concertsapp.android.gui.event.list.EventListActivity;
import es.concertsapp.android.gui.menu.MenuActivity;
import es.concertsapp.android.utils.LastFmApiConnectorFactory;
import es.concertsapp.android.utils.font.FontUtils;
import es.lastfm.api.connector.tags.PunkTags;


public class MainActivity extends MenuActivity
{
    private String version;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        //Inicializamos el api de last.fm
        LastFmApiConnectorFactory.initilize(new PunkTags());

        setContentView(R.layout.main_layout);
		
		try
		{
            TextView textViewVersion = (TextView)findViewById(R.id.version);
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            textViewVersion.setText(version);
		}
		catch (Throwable e){};

        Button buttonConcerts = (Button)findViewById(R.id.buttonConciertos);
        buttonConcerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonConciertos(v);
            }
        });
        FontUtils.setRobotoFont(this,buttonConcerts, FontUtils.FontType.ROBOTO_CONDENSED);

        Button buttonBands = (Button)findViewById(R.id.buttonGrupos);
        buttonBands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonBands(v);
            }
        });
        FontUtils.setRobotoFont(this,buttonBands, FontUtils.FontType.ROBOTO_CONDENSED);
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
