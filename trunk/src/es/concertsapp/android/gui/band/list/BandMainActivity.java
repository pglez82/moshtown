package es.concertsapp.android.gui.band.list;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Window;

import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.menu.MenuFragmentActivity;

/**
 * Created by pablo on 6/07/13.
 *
 * Clase principal para las bandas. tiene dos páginas. una para buscar bandas y otra para bandas
 * favoritas
 */
public class BandMainActivity extends MenuFragmentActivity
{
    private BandMainActivitySectionsPageAdapter bandMainActivitySectionsPageAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.band_main_layout);

        //Recibimos el id de la banda
        bandMainActivitySectionsPageAdapter = new BandMainActivitySectionsPageAdapter(getSupportFragmentManager());

        //Añadimos el adaptador para cambiar entre páginas de desplazamiento horizontal
        mViewPager = (ViewPager) findViewById(R.id.bandpager);
        mViewPager.setAdapter(bandMainActivitySectionsPageAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i==0)
                    setTitle("Grupos");
                else if (i==1)
                    setTitle("Favoritos");
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }
}