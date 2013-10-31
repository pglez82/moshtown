package es.concertsapp.android.utils;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.util.Locale;

import es.concertsapp.android.gui.R;

/**
 * Created by pablo on 27/08/13.
 *
 * Clase usada para resolver el contexto desde cualquier sitio.
 */
@ReportsCrashes(
        formKey = "",
        formUri = "https://shdgrao.cloudant.com/acra-hx/_design/acra-storage/_update/report",
        reportType = org.acra.sender.HttpSender.Type.JSON,
        httpMethod = org.acra.sender.HttpSender.Method.PUT,
        formUriBasicAuthLogin="oughtlyedgentremakeencel",
        formUriBasicAuthPassword="Cs0fAFqEPDAWW7vyq3EFc5Ro",
        mode = ReportingInteractionMode.DIALOG,
        customReportContent = { ReportField.REPORT_ID,ReportField.INSTALLATION_ID,ReportField.PACKAGE_NAME,ReportField.BUILD,ReportField.APP_VERSION_CODE,ReportField.AVAILABLE_MEM_SIZE,ReportField.CRASH_CONFIGURATION,ReportField.DISPLAY,ReportField.USER_APP_START_DATE,ReportField.USER_CRASH_DATE,ReportField.SETTINGS_GLOBAL,ReportField.SETTINGS_SYSTEM, ReportField.USER_COMMENT, ReportField.ANDROID_VERSION, ReportField.APP_VERSION_NAME, ReportField.PHONE_MODEL, ReportField.BRAND, ReportField.STACK_TRACE, ReportField.LOGCAT},
        logcatArguments = { "-t", "200", "-v", "time", "es.concertsapp.android.gui:D"},
        resToastText = R.string.acratoasttext,
        resDialogText = R.string.acradialogtext,
        resDialogCommentPrompt = R.string.acracomment, // optional. when defined, adds a user text field input with this text resource as a label
        resDialogOkToast = R.string.acradialogok // optional. displays a Toast message when the user accepts to send a report.
)
public class MyApplication extends Application
{
    private static Context context;
    private static Locale usedLocale;


    public void onCreate(){
        super.onCreate();
        /**
         * Datos en cloudant para almacenar los reports de fallos
         * Web de acceso: cloudant.com user shdgrao pass la de siempre
         * Base de datos donde se almacenan los reports acra-hx
         * Usuario/pass para escriibr datos oughtlyedgentremakeencel/Cs0fAFqEPDAWW7vyq3EFc5Ro
         * Acceso a la herramienta para visualizar los fallos: https://shdgrao.cloudant.com/acralyzer/_design/acralyzer/index.html
         */
        ACRA.init(this);
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

    public static Resources getAppResources()
    {
        return context.getResources();
    }


    public static void lookUpLocate()
    {
        usedLocale = new Locale(context.getResources().getString(R.string.locale));
    }

    public static Locale getLocale()
    {
        if (usedLocale==null)
            usedLocale = new Locale(context.getResources().getString(R.string.locale));

        return usedLocale;
    }
}
