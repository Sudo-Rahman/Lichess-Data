package recherche;

import utils.Log;

import java.io.*;

public abstract class Recherche
{
    private BufferedReader reader;
    private final Log log = new Log();

    public Recherche(String pathFile)
    {
        try
        {
            this.reader = new BufferedReader(new FileReader(pathFile));
        } catch (FileNotFoundException e)
        {
            log.error("Impossible de trouver le fichier !!");
        }
    }

    public abstract void cherche();

    public abstract String toString();

}
