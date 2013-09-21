package es.concertsapp.android.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormater 
{
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	public static String formatDate(Date date)
	{
		return sdf.format(date);
	}

}
