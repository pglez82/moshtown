package es.concertsapp.android.gui.event.add;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.menu.MenuActivity;
import es.concertsapp.android.gui.settings.SettingsActivity;
import es.concertsapp.android.utils.font.FontUtils;

/**
 * Created by pablo on 12/05/14.
 */
public class EventAddActivity extends MenuActivity
{
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actitivy_add_event);
        TextView textViewAddEventTitle = (TextView)findViewById(R.id.add_event_title_textview);
        TextView textViewAddEventInfo = (TextView)findViewById(R.id.add_event_info_textview);
        FontUtils.setRobotoFont(this, textViewAddEventTitle, FontUtils.FontType.ROBOTOCONDENSED_BOLD);
        FontUtils.setRobotoFont(this, textViewAddEventInfo, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
        Button buttonAddEventLink = (Button)findViewById(R.id.add_event_link_button);
        FontUtils.setRobotoFont(this, buttonAddEventLink, FontUtils.FontType.ROBOTOCONDENSED_BOLD);
        textViewAddEventInfo.setMovementMethod(new ScrollingMovementMethod());
        buttonAddEventLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://www.last.fm/events/add")));
            }
        });
        ImageButton imageButton3 = (ImageButton)findViewById(R.id.button_configuration);
        imageButton3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent myIntent = new Intent(EventAddActivity.this, SettingsActivity.class);
                startActivity(myIntent);
            }
        });

    }
}
