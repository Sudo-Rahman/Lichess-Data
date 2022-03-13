/*
 * Nom de classe : Recherche
 *
 * Description   : Class abstraite qui sert de socle pour les classes de recherches.
 *
 * Version       : 1.0
 *
 * Date          : 12/03/2022
 *
 * Copyright     : Yilmaz Rahman, Colliat Maxime
 *
 */

package recherche;

import utils.Log;

import java.io.*;

public abstract class Recherche
{
    private BufferedReader fileReader;
    private final ObjectInputStream clientReader;
    private final BufferedWriter clientWriter;
    protected final Log log = new Log();


    public Recherche(String pathFile, ObjectInputStream clientReader, BufferedWriter clientWriter)
    {

        this.clientReader = clientReader;
        this.clientWriter = clientWriter;
        try
        {
            this.fileReader = new BufferedReader(new FileReader(pathFile));
        } catch (FileNotFoundException e)
        {
            log.error("Impossible de trouver le fichier !!");
        }
    }

    public abstract void cherche();

    public BufferedReader getFileReader()
    {
        return fileReader;
    }

    public void envoieMessage(String message)
    {
        try
        {
            this.clientWriter.write(message);
            this.clientWriter.newLine();
            this.clientWriter.flush();
        } catch (IOException e)
        {
            log.error("Impossible d'envoyer le message");
        }
    }

    public String litMess()
    {
        String mess = null;
        try
        {
            mess = (String) this.clientReader.readObject();
        } catch (Exception e)
        {
            log.error("Impossible de lire le message");
        }
        return mess;
    }

    public int litInt()
    {
        int nb = 5;
        try
        {
            nb = Integer.parseInt((String) this.clientReader.readObject());
        } catch (Exception e)
        {
            log.error("Impossible de lire l'entier");
        }
        return nb;
    }


    public void closeFileReader(){
        try
        {
            this.fileReader.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
