package es.concertsapp.android.utils.date;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormater 
{
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat month;
    private SimpleDateFormat year;
    private SimpleDateFormat daynumber;
    private SimpleDateFormat daytext;
    private SimpleDateFormat dateEvent;
    private Locale currentLocale;

    private static DateFormater instance;

    private DateFormater()
    {

    }

    public static DateFormater getInstance(Locale locale)
    {
        if (instance == null || instance.currentLocale == null || !instance.currentLocale.equals(locale))
        {
            instance = new DateFormater();
            instance.currentLocale = locale;
            instance.month = new SimpleDateFormat("MMM", locale);
            instance.year = new SimpleDateFormat("yy",locale);
            instance.daytext = new SimpleDateFormat("EEE",locale);
            instance.daynumber = new SimpleDateFormat("dd",locale);
            instance.dateEvent = new SimpleDateFormat("EEE dd MMM yy kk:mm aa");
        }

        return instance;
    }

	public String formatDate(Date date)
	{
		return sdf.format(date);
	}

    public String[] formatMonth(Date date)
    {
        String[] result = new String[2];
        result[0]=month.format(date).toUpperCase();
        result[1]=year.format(date);
        return result;
    }

    public String[] formatDay(Date date)
    {
        String[] result = new String[2];
        result[0]=daytext.format(date).toUpperCase();
        result[1]=daynumber.format(date);
        return result;
    }

    public String formatDateEvent(Date date)
    {
        return dateEvent.format(date).toUpperCase();
    }
}
