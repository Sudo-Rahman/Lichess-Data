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
    protected final MapsObjets mapObjets;
    protected final Log log = new Log();
    private final ObjectInputStream clientReader;
    private final BufferedWriter clientWriter;
    protected int maxNbParties = 100000;
    protected boolean afficheParties = true;
    private BufferedReader fileReader;


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


    public void closeFileReader()
    {
        try
        {
            this.fileReader.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    public Map<Object, List<long[]>> getNameMap()
    {
        return mapObjets.getWarm().getNameMap();
    }

    public Map<Object, List<long[]>> getEloMap()
    {
        return mapObjets.getWarm().getEloMap();
    }

    public Map<Object, List<long[]>> getUtcDateMap()
    {
        return mapObjets.getWarm().getUtcDateMap();
    }

    public Map<Object, List<long[]>> getUtcTimeMap()
    {
        return mapObjets.getWarm().getUtcTimeMap();
    }

    public Map<Object, List<long[]>> getOpenningMap()
    {
        return mapObjets.getWarm().getOpenningMap();
    }

    public Map<Object, List<long[]>> getNbCoupsMap()
    {
        return mapObjets.getWarm().getNbCoupsMap();
    }

    public long getNbParties()
    {
        return this.mapObjets.getNbParties();
    }
}
