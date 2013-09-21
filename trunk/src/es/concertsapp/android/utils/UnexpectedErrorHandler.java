package es.concertsapp.android.utils;

import org.acra.ACRA;

/**
 * Created by pablo on 10/09/13.
 */
public class UnexpectedErrorHandler
{
    public static void handleUnexpectedError(Throwable e)
    {
        ACRA.getErrorReporter().handleException(e);
    }
}
