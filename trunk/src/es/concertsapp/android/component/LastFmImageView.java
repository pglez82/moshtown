package es.concertsapp.android.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import de.umass.lastfm.ImageSize;
import es.concertsapp.android.gui.R;
import es.concertsapp.android.utils.images.Dimension;
import es.concertsapp.android.utils.images.ImageDownloader;
import es.lastfm.api.connector.dto.ArtistDTO;
import es.lastfm.api.connector.dto.DetailedEventDTO;
import es.lastfm.api.connector.dto.LastFmImageSourceI;
import es.lastfm.api.utils.LastFmImageSizeCalc;

/**
 * Created by pablo on 12/10/13.
 * Este componente está creado especificamente para imagenes de last.fm. Su funcionalidad es, descargar
 * la imagen de last.fm más adecuada según el tamaño que se especifique del imageview en tiempo de diseño.
 * La idea es que también se pueda definir una altura máxima y que corte la imagen para cuadrarla a esa altura
 * ... le pondríamos un ancho y una altura máxima y el la ajustaría directamente cortando la imagen
 */
public class LastFmImageView extends ImageView
{
    //Interfaz de donde podemos sacar la url de la imagen
    private LastFmImageSourceI lastFmImageSourceI=null;
    //Anchura calculada. Después de que ya se sabe que anchura va a tener el imageview es cuando ponemos
    //a descargar la versión más adecuada de la imagen
    private int width=0;
    //Altura máxima. sino está definida debería de ser cero. Sino se define, no cortamos nada,
    //Si se define y la altura máxima es mayor que esta, hay que cortar la imagen
    private Dimension maxDim;

    //Si se debería mostrar el progreso al cargar la foto o no. Si se muestra TIENE que estar en un relativeLayout
    private boolean showprogress;
    private ProgressBar progressBar;

    private static final String LOG_TAG="LASTFMIMAGEVIEW";

    public LastFmImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initializeComponent(context,attrs);
    }

    public LastFmImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initializeComponent(context,attrs);
    }

    private void initializeComponent(Context context,AttributeSet attrs)
    {
        super.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
        {
            @Override
            public boolean onPreDraw()
            {
                setWidth(getMeasuredWidth());
                return true;
            }
        });

        //Leemos el atributo con el alto máximo
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LastFmImageView, 0, 0);
        //int maxWidth = a.getInteger(R.styleable.LastFmImageView_maxLastFmImageWidth,0);
        int maxHeight = a.getInteger(R.styleable.LastFmImageView_maxLastFmImageHeight,0);
        showprogress = a.getBoolean(R.styleable.LastFmImageView_showprogress,false);
        if (maxHeight!=0)
            maxDim = new Dimension(0,maxHeight);

    }

    public void setLastFmImageSource(LastFmImageSourceI lastFmImageSourceI)
    {
        this.lastFmImageSourceI = lastFmImageSourceI;
        //TODO: HAY QEU REVISAR A FONDO ESTA PARTE, PORQUE TEMO QUE SE ESTÉN DESCARGANDO DEMASIADAS IMAGENES
        //downloadImage();
    }

    private void setWidth(int newWidth)
    {
        //Comprobamos si ha habido un cambio real
        if (newWidth!=0 && width!=newWidth)
        {
            width=newWidth;
            if (maxDim!=null)
                maxDim.setFixedWidth(width);
            downloadImage();
        }
    }

    private void downloadImage()
    {
        if (lastFmImageSourceI!=null)
        {

            //Aquí se sustituye a si mismo por un progrees bar?
            if (showprogress)
            {
                progressBar = new ProgressBar(getContext());
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(this.getHeight()/3,this.getHeight()/3);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL,1);
                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL,1);
                RelativeLayout parentLayout = (RelativeLayout)this.getParent();
                this.setVisibility(INVISIBLE);
                parentLayout.addView(progressBar,layoutParams);
            }

            ImageDownloader imageDownloader = ImageDownloader.getInstance();
            ImageSize imageSize=LastFmImageSizeCalc.getOptimunImageSize(width);
            String imageUrl = lastFmImageSourceI.getImageURL(imageSize);
            if (imageUrl != null)
                imageDownloader.download(imageUrl, this,maxDim);
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm)
    {
        super.setImageBitmap(bm);
        if (showprogress)
        {
            RelativeLayout layout = (RelativeLayout)this.getParent();
            layout.removeView(progressBar);
            this.setVisibility(VISIBLE);
        }

    }
}
