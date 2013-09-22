package es.concertsapp.android.utils;

import android.content.Context;

import org.acra.ACRA;

import java.net.ConnectException;
import java.net.UnknownHostException;

import de.umass.lastfm.CallException;
import es.concertsapp.android.gui.R;
import es.lastfm.api.connector.exception.LastFmException;

/**
 * Created by pablo on 10/09/13.
 */
public class UnexpectedErrorHandler
{
    public static void handleUnexpectedError(Context c,Throwable e)
    {
        if (e instanceof LastFmException)
        {
            if (e.getCause()!=null && (e.getCause() instanceof CallException) && e.getCause().getCause()!=null && (e.getCause().getCause() instanceof UnknownHostException || e.getCause().getCause() instanceof ConnectException))
                DialogUtils.showErrorDialog(c, R.string.connection_error);
            else
                DialogUtils.showErrorDialog(c,R.string.lastfm_error);
        }
        else
        {
            ACRA.getErrorReporter().handleException(e);
        }
    }
}
