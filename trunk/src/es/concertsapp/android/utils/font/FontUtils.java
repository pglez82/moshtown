package es.concertsapp.android.utils.font;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pablo on 28/09/13.
 */
public class FontUtils
{
    public enum FontType
    {
        ROBOTO_CONDENSED("fonts/Roboto-Condensed.ttf"),
        ROBOTO("PRUEBA");

        private String fontPath;

        FontType(String fontPath)
        {
            this.fontPath=fontPath;
        }

        public String getFontPath()
        {
            return fontPath;
        }
    }

    private static Map<FontType,Typeface> fontInstances = new HashMap<FontType,Typeface>();

    public static void setRobotoFont (Context context, View view, FontType fontType)
    {
        Typeface fontInstance = fontInstances.get(fontType);
        if (fontInstance == null)
        {
            fontInstance = Typeface.createFromAsset(context.getAssets(), fontType.getFontPath());
            fontInstances.put(fontType,fontInstance);
        }
        setFont(view, fontInstance);
    }

    private static void setFont (View view, Typeface robotoTypeFace)
    {
        if (view instanceof ViewGroup)
        {
            for (int i = 0; i < ((ViewGroup)view).getChildCount(); i++)
            {
                setFont(((ViewGroup)view).getChildAt(i), robotoTypeFace);
            }
        }
        else if (view instanceof TextView)
        {
            ((TextView) view).setTypeface(robotoTypeFace);
        }
        else if (view instanceof Button)
        {
            ((Button) view).setTypeface(robotoTypeFace);
        }
    }
}
