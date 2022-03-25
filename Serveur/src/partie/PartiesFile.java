/*
 * Nom de classe : ParsePartie
 *
 * Description   : Classe qui va chercher dans le fichier de donné la partie indiqué par sa position, et crée des Parties.
 *
 * Version       : 1.0
 *
 * Date          : 22/03/2022
 *
 * @author : Yilmaz Rahman, Colliat Maxime
 *
 */


package partie;

import utils.Log;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class PartiesFile
{
    private static final Log log = new Log();
    private FileInputStream fileInputStream;

    public PartiesFile(File file)
    {
        try
        {
            this.fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e)
        {
            log.error("Impossible de trouver le fichier !!");
        }
    }

    /**
     * @param pos position de la partie dans le fichier
     * @return nouvelle Partie
     * @throws IOException
     */
    public Partie getPartieInFile(long pos) throws IOException
    {
        //crée un nouveau bufferedReader car impossible de vider le buffer autrement
        BufferedReader reader = new BufferedReader(new InputStreamReader(this.fileInputStream));

        this.fileInputStream.getChannel().position(pos);
        int comptLigneVide = 0;
        int lignes = 0;

        String str;
        List<String> lstStr = new ArrayList<>();
        while (comptLigneVide < 2)
        {
            str = reader.readLine();
            if (lignes < 15 && str.equals(""))
            {
                comptLigneVide = 0;
            } else
            {
                if (str.equals("")) comptLigneVide++;
                else lstStr.add(str);
            }
            lignes++;
        }
        return new Partie(lstStr);
    }

    /**
     * @param lstPos
     * @param maxNbParties
     * @return
     */
    public List<Partie> getAllParties(List<Long> lstPos, int maxNbParties)
    {
        int compteur = 0;
        List<Partie> lstParties = new ArrayList<>();
        for (Long pos : lstPos)
        {
            if (compteur < maxNbParties)
            {
                try
                {
                    lstParties.add(getPartieInFile(pos));
                    compteur++;
                } catch (IOException e) {e.printStackTrace();}
            } else break;
        }
        return lstParties;
    }

    public void closeReader()
    {
        try
        {
            this.fileInputStream.close();
        } catch (IOException e)
        {
            log.error("Impossible de fermer randomeAccessFile et bufferedReader !!");
        }
    }
}
