package es.concertsapp.android.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import java.util.List;

import es.concertsapp.android.gui.R;

/**
 * Clase de utilidad para mostrar dialogos
 */
public class DialogUtils 
{
    /**
     * Muestra un diálogo con un título y un texto y botón de aceptar
     * @param context contexto
     * @param title título
     * @param text texto
     * @param onDismissListener Listener para ejecutar una vez que se da a aceptar en el botón del díalogo.
     *                          Normalmente utilizaremos esto para volver a la pantalla anterior si estabamos intentando
     *                          abrir una pantalla nueva y se ha producido un error.
     */
	public static void showMessageDialog(Context context, int title,int text,AlertDialog.OnDismissListener onDismissListener)
	{
        //Crequeamos primero que la actividad no esté en las últimas
        boolean finishing = false;
        if (context instanceof Activity)
        {
            finishing = ((Activity)context).isFinishing();
        }
        if (context!=null && !finishing )
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(text).setTitle(title);
            AlertDialog dialog = builder.create();
            if (onDismissListener!=null)
                dialog.setOnDismissListener(onDismissListener);
            dialog.show();
        }
	}

    public static void showMessageDialog(Context context, int title,int text)
    {
        showMessageDialog(context,title,text,null);
    }

    public static void showErrorDialog(Context context, int text,AlertDialog.OnDismissListener onDismissListener)
    {
        showMessageDialog(context, R.string.error_dialog_title, text,onDismissListener);
    }

    public static void showErrorDialog(Context context, int text)
    {
        showMessageDialog(context, R.string.error_dialog_title, text,null);
    }

    /**
     * Muestra un diálogo con una lista de opciones para elegir.
     * @param context
     * @param items
     * @param onClickListener que se debe de ejecutar una vez que el usuario pinche en una opción.
     */
	public static void showDialogWithChoices(Context context,List<String> items, DialogInterface.OnClickListener onClickListener)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setSingleChoiceItems(items.toArray(new CharSequence[items.size()]), 0, onClickListener);
        builder.show();
	}

    public static void showToast(Context context, int duration, int message)
    {
        @SuppressWarnings("MagicConstant")
        Toast toast = Toast.makeText(context, message ,duration);
        toast.show();

    }

}
