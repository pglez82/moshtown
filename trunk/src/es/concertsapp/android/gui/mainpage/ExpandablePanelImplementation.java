package es.concertsapp.android.gui.mainpage;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import es.concertsapp.android.component.ExpandablePanel;
import es.concertsapp.android.component.ExpandablePanelGroup;
import es.concertsapp.android.gui.R;

public class ExpandablePanelImplementation extends Activity
{
    private static final String lorenIpsumText="Lorem ipsum ad his scripta blandit partiendo, eum fastidii accumsan euripidis in, eum liber hendrerit an. Qui ut wisi vocibus suscipiantur, quo dicit ridens inciderint id. Quo mundi lobortis reformidans eu, legimus senserit definiebas an eos. Eu sit tincidunt incorrupte definitionem, vis mutat affert percipit cu, eirmod consectetuer signiferumque eu per. In usu latine equidem dolores. Quo no falli viris intellegam, ut fugit veritus placerat per. Ius id vidit volumus mandamus, vide veritus democritum te nec, ei eos debet libris consulatu. No mei ferri graeco dicunt, ad cum veri accommodare. Sed at malis omnesque delicata, usu et iusto zzril meliore. Dicunt maiorum eloquentiam cum cu, sit summo dolor essent te. Ne quodsi nusquam legendos has, ea dicit voluptua eloquentiam pro, ad sit quas qualisque. Eos vocibus deserunt quaestio ei. Blandit incorrupte quaerendum in quo, nibh impedit id vis, vel no nullam semper audiam. Ei populo graeci consulatu mei, has ea stet modus phaedrum. Inani oblique ne has, duo et veritus detraxit. Tota ludus oratio ea mel, offendit persequeris ei vim. Eos dicat oratio partem ut, id cum ignota senserit intellegat. Sit inani ubique graecis ad, quando graecis liberavisse et cum, dicit option eruditi at duo. Homero salutatus suscipiantur eum id, tamquam voluptaria expetendis ad sed, nobis feugiat similique usu ex. Eum hinc argumentum te, no sit percipit adversarium, ne qui feugiat persecuti. ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_collapsable);
        TextView text;
        text = (TextView)findViewById(R.id.contentpanel1);
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setText(lorenIpsumText);

        text = (TextView)findViewById(R.id.contentpanel2);
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setText(lorenIpsumText);

        text = (TextView)findViewById(R.id.contentpanel3);
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setText(lorenIpsumText);



        ExpandablePanel expandablePanel1 = (ExpandablePanel)findViewById(R.id.expandablePanel1);
        expandablePanel1.addOnExpandListener(new ExpandablePanel.OnExpandListener()
        {
            public void onExpand(int id, View handle, View content)
            {
                Button btn = (Button) handle;
                btn.setText("Menos");
            }

            public void onCollapse(int id, View handle, View content)
            {
                Button btn = (Button) handle;
                btn.setText("Más");
            }
        });

        ExpandablePanel expandablePanel2 = (ExpandablePanel)findViewById(R.id.expandablePanel2);
        expandablePanel2.addOnExpandListener(new ExpandablePanel.OnExpandListener()
        {
            public void onExpand(int id, View handle, View content)
            {
                Button btn = (Button) handle;
                btn.setText("Menos");
            }

            public void onCollapse(int id, View handle, View content)
            {
                Button btn = (Button) handle;
                btn.setText("Más");
            }
        });

        ExpandablePanel expandablePanel3 = (ExpandablePanel)findViewById(R.id.expandablePanel3);
        expandablePanel3.addOnExpandListener(new ExpandablePanel.OnExpandListener()
        {
            public void onExpand(int id, View handle, View content)
            {
                Button btn = (Button) handle;
                btn.setText("Menos");
            }

            public void onCollapse(int id, View handle, View content)
            {
                Button btn = (Button) handle;
                btn.setText("Más");
            }
        });
    }

}
