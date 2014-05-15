package es.concertsapp.android.utils.keyboard;

import android.content.Context;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by pablo on 28/11/13.
 */
public class KeyBoardUtils
{
    public static void hideKeyboard(Context context,IBinder windowToken)
    {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(windowToken,0);
    }
}
