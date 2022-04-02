package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Cette classe utilise la classe Colors pour traiter les exceptions ou afficher des messages ou encore pour déboguer.
 *
 * @author Yilmaz Rahman
 * @version 1.0
 * @date 22/02/2022
 */
public class Log
{
    public Log() {}

    /**
     * @param s : message a afficher en mode debug.
     */
    public void debug(String s)
    {
        System.out.println(Colors.grey + getDate() + " | DEBUG | " + s + Colors.reset);
    }

    /**
     * @param s : message a afficher en mode info.
     */
    public void info(String s)
    {
        System.out.println(Colors.green + getDate() + " | INFO | " + s + Colors.reset);
    }

    /**
     * @param s : message a afficher en mode warning.
     */
    public void warning(String s)
    {
        System.out.println(Colors.blue + getDate() + " | WARNING | " + s + Colors.reset);
    }

    /**
     * @param s : message a afficher en mode error.
     */
    public void error(String s)
    {
        System.out.println(Colors.red + getDate() + " | ERROR | " + s + Colors.reset);
    }

    /**
     * @param s : message a afficher en mode fatal.
     */
    public void fatal(String s)
    {
        System.out.println(Colors.redBold + getDate() + " | FATAL | " + s + Colors.reset);
    }

    /**
     * @return : la date courante.
     */
    private String getDate()
    {
        return new SimpleDateFormat("d/MM/yyyy H:m:s").format(new Date());
    }
}
