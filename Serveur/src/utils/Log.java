/*
 * Nom de classe : Log
 *
 * Description   : Cette classe utilise la classe Color pour traiter les exeptions ou afficher des messages ou encore pour deboguer,
 *                 tout en ayant une vision plus claire du message puisqu'il sera affiché en couleur, et la coulleur sera differente selon l'importance du log.
 *                 Debug etant la plus faible et Fatal considéré comme une exeption tres importante.
 *
 * Version       : 1.0
 *
 * Date          : 22/02/2022
 *
 * Copyright     : Yilmaz Rahman, Colliat Maxime
 *
 */


package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log
{
    private Date date;

    public Log()
    {
        this.date = new Date();
    }

    public void debug(String s)
    {
        System.out.println(Colors.grey + getDate().format(date) + " | DEBUG | " + s + Colors.reset);
    }

    public void info(String s)
    {
        System.out.println(Colors.green + getDate().format(date) + " | INFO | " + s + Colors.reset);
    }

    public void warning(String s)
    {
        System.out.println(Colors.blue + getDate().format(date) + " | WARNING | " + s + Colors.reset);
    }

    public void error(String s)
    {
        System.out.println(Colors.red + getDate().format(date) + " | ERROR | " + s + Colors.reset);
    }

    public void fatal(String s)
    {
        System.out.println(Colors.redBold + getDate().format(date) + " | FATAL | " + s + Colors.reset);
    }

    private SimpleDateFormat getDate()
    {
        return new SimpleDateFormat("d/MM/yyyy H:m:s");
    }
}
