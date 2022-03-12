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
    private ObjectInputStream clientReader;
    private BufferedWriter clientWriter;
    public final Log log = new Log();

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

    public abstract String toString();

    public BufferedWriter getBufferedWriter()
    {
        return clientWriter;
    }

    public ObjectInputStream getClientReader()
    {
        return clientReader;
    }

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
            System.exit(-1);
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
            System.exit(-1);
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
            System.exit(-1);
        }
        return nb;
    }

    public void lock(){
        try
        {
            System.out.println("tread lock");
            this.wait();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public void closeReader(){
        try
        {
            this.clientReader.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void closeWriter(){
        try
        {
            this.clientWriter.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
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
