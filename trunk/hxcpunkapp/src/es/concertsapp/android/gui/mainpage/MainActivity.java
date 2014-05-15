package es.concertsapp.android.gui.mainpage;


//import android.app.ActionBar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import es.concertsapp.android.component.LetterSpacingTextView;
import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.band.list.BandMainActivity;
import es.concertsapp.android.gui.event.list.EventListActivity;
import es.concertsapp.android.gui.legal.LegalConditionsActivity;
import es.concertsapp.android.gui.legal.MoshTownConditionsActivity;
import es.concertsapp.android.gui.menu.MenuActivity;
import es.concertsapp.android.utils.LastFmApiConnectorFactory;
import es.concertsapp.android.utils.MyApplication;
import es.concertsapp.android.utils.font.FontUtils;
import es.lastfm.api.connector.tags.PunkTags;


public class MainActivity extends MenuActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setContentView(R.layout.main_layout);
		
        LetterSpacingTextView sloganTextView = (LetterSpacingTextView)findViewById(R.id.text_slogan);
        FontUtils.setRobotoFont(this,sloganTextView, FontUtils.FontType.ROBOTOCONDENSED_BOLD);
        sloganTextView.setLetterSpacing(6);
        sloganTextView.setText(R.string.slogan_text);

        Button buttonConcerts = (Button)findViewById(R.id.buttonConciertos);
        buttonConcerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonConciertos(v);
            }
        });
        FontUtils.setRobotoFont(this,buttonConcerts, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);

        Button buttonBands = (Button)findViewById(R.id.buttonGrupos);
        buttonBands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonBands(v);
            }
        });
        FontUtils.setRobotoFont(this,buttonBands, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);

        ImageButton imageButton = (ImageButton)findViewById(R.id.button_logolastfm);
        imageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent myIntent = new Intent(MainActivity.this, LegalConditionsActivity.class);
                startActivity(myIntent);
            }
        });

        ImageButton imageButton2 = (ImageButton)findViewById(R.id.button_moshtown);
        imageButton2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent myIntent = new Intent(MainActivity.this, MoshTownConditionsActivity.class);
                startActivity(myIntent);
            }
        });



        //Actualizo el locale cada vez que entramos al main de la aplicación
        MyApplication.lookUpLocate();
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
