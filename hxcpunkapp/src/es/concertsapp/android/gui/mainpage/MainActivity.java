package es.concertsapp.android.gui.mainpage;


//import android.app.ActionBar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.List;

import es.concertsapp.android.component.LetterSpacingTextView;
import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.band.list.BandMainActivity;
import es.concertsapp.android.gui.event.detail.EventInfoActivity;
import es.concertsapp.android.gui.event.list.EventListActivity;
import es.concertsapp.android.gui.footer.FooterLayoutUtils;
import es.concertsapp.android.gui.legal.LegalConditionsActivity;
import es.concertsapp.android.gui.legal.MoshTownConditionsActivity;
import es.concertsapp.android.gui.menu.MenuActivity;
import es.concertsapp.android.gui.settings.SettingsActivity;
import es.concertsapp.android.utils.MyAppParameters;
import es.concertsapp.android.utils.MyApplication;
import es.concertsapp.android.utils.font.FontUtils;


public class MainActivity extends MenuActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setContentView(R.layout.main_layout);
		
        LetterSpacingTextView sloganTextView = (LetterSpacingTextView)findViewById(R.id.text_slogan);
        FontUtils.setRobotoFont(this,sloganTextView, FontUtils.FontType.ROBOTOCONDENSED_BOLD);
        sloganTextView.setCustomLetterSpacing(6);
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

        FooterLayoutUtils.initializeButtonsFunctions(this);


        checkIfComingFromLink();



        //Actualizo el locale cada vez que entramos al main de la aplicación
        MyApplication.lookUpLocate();
	}

    private void checkIfComingFromLink()
    {
        final Intent intent = getIntent();
        final String action = intent.getAction();


        if (intent !=null && intent.getData()!=null && Intent.ACTION_VIEW.equals(action))
        {
            int eventId = -1;
            if ("moshtown".equals(intent.getData().getScheme()))
            {
                final List<String> segments = intent.getData().getPathSegments();
                if (segments != null && segments.size() ==1)
                    try{eventId = Integer.parseInt(segments.get(0));}catch (Throwable e){}
            }
            else
            {
                final List<String> segments = intent.getData().getPathSegments();
                if (segments != null && segments.size() == 2) {
                    if ("events".equals(segments.get(0)))
                        try{eventId = Integer.parseInt(segments.get(1));} catch (Throwable e){}
                }
            }
            try {
                if (eventId!=-1) {
                    Intent i = new Intent(MainActivity.this, EventInfoActivity.class);
                    i.putExtra(MyAppParameters.EVENTID, eventId);
                    startActivity(i);
                }
            } catch (Throwable e) {
                //No hacemos nada, dejamos la app en la pagina principal
            }
        }
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
