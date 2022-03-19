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

import maps.MapsObjets;
import utils.Log;

import java.io.*;
import java.util.List;
import java.util.Map;

public abstract class Recherche
{
    private BufferedReader fileReader;
    private final ObjectInputStream clientReader;
    private final BufferedWriter clientWriter;
    protected final MapsObjets mapObjets;
    protected final Log log = new Log();


    public Recherche(ObjectInputStream clientReader, BufferedWriter clientWriter, MapsObjets mapObjets)
    {

        this.clientReader = clientReader;
        this.clientWriter = clientWriter;
        try
        {
            this.fileReader = new BufferedReader(new FileReader(mapObjets.getFile()));
        } catch (FileNotFoundException e)
        {
            log.error("Impossible de trouver le fichier !!");
        }
        this.mapObjets = mapObjets;
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


    public Map<String, List<long[]>> getNameMap()
    {
        return mapObjets.getNameMap();
    }

    public Map<Integer, List<long[]>> getEloMap()
    {
        return mapObjets.getEloMap();
    }

    public Map<String, List<long[]>> getUtcDateMap()
    {
        return mapObjets.getUtcDateMap();
    }

    public Map<String, List<long[]>> getUtcTimeMap()
    {
        return mapObjets.getUtcTimeMap();
    }

    public Map<String, List<long[]>> getOpenningMap()
    {
        return mapObjets.getOpenningMap();
    }
}
