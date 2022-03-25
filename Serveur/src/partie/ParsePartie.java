package partie;

import utils.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ParsePartie
{
    private static final Log log = new Log();
    private RandomAccessFile randomAccessFile;

    public ParsePartie(File file)
    {
        try
        {
            this.randomAccessFile = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException e)
        {
            log.error("Impossible de trouver le fichier !!");
        }
    }

    public Partie getPartieInFile(long pos) throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(this.randomAccessFile.getFD()));

        this.randomAccessFile.seek(pos);
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
            this.randomAccessFile.close();
        } catch (IOException e)
        {
            log.error("Impossible de fermer randomeAccessFile et bufferedReader !!");
        }
    }
}
