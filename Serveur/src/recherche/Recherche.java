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

import maps.MapsObjet;
import partie.PartiesFile;
import utils.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;

public abstract class Recherche
{
    protected final MapsObjet mapObjet;
    protected final Log log = new Log();
    private final ObjectInputStream clientReader;
    private final BufferedWriter clientWriter;
    protected int maxNbParties = 100000;
    protected boolean afficheParties = true;
    protected PartiesFile partiesFile;


    public Recherche(ObjectInputStream clientReader, BufferedWriter clientWriter, MapsObjet mapObjet)
    {

        this.clientReader = clientReader;
        this.clientWriter = clientWriter;
        this.mapObjet = mapObjet;
        this.partiesFile = new PartiesFile(mapObjet.getFile());
    }

    public abstract void cherche();


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
}
