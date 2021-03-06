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
import java.util.ArrayList;
import java.util.List;

/**
 * Classe qui va chercher dans le fichier de donné la partie indiqué par sa position,et crée des Parties.
 *
 * @author Yilmaz Rahman
 * @version 1.0
 * @date 22/03/2022
 */
public class PartiesFile
{
    private static final Log log = new Log();
    private final File file;

    /**
     * @param file fichier de données.
     */
    public PartiesFile(File file)
    {
        this.file = file;
    }

    /**
     * @param pos position de la partie dans le fichier.
     * @return nouvelle Partie.
     * @throws IOException si le fichier n'existe pas.
     */
    private Partie getPartie(Long pos) throws IOException
    {
        //création d'un nouveau bufferedReader car impossible de vider le buffer autrement
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));

        fileInputStream.getChannel().position(pos);
        int comptLigneVide = 0;

        String str;
        List<String> lstStr = new ArrayList<>();
        while (comptLigneVide < 2)
        {
            str = reader.readLine();
            if (str.equals("")) comptLigneVide++;
            else lstStr.add(str);
        }
        fileInputStream.close();
        reader.close();
        return new Partie(lstStr);
    }

    /**
     * @param pos position de la partie dans le fichier
     * @return retourne la partie
     */
    public Partie getPartieInFile(Long pos)
    {
        try
        {
            return getPartie(pos);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param lstPos       list des positions des parties dans le fichier
     * @param maxNbParties definie le maximum de partie à retourner
     * @return retourne la liste des parties
     */
    public List<Partie> getAllParties(List<Long> lstPos, int maxNbParties)
    {
        int compteur = 0;
        List<Partie> lstParties = new ArrayList<>();
        for (Long pos : lstPos)
        {
            if (compteur < maxNbParties)
            {
                lstParties.add(getPartieInFile(pos));
                compteur++;
            } else break;
        }
        return lstParties;
    }

    /**
     * @param lstPos list des positions des parties dans le fichier
     * @return retourne la liste des parties
     */
    public List<Partie> getAllParties(List<Long> lstPos)
    {
        List<Partie> lstParties = new ArrayList<>();
        for (Long pos : lstPos)
            lstParties.add(getPartieInFile(pos));
        return lstParties;
    }

}
