package recherche;

import maps.MapsObjet;
import partie.PartiesFile;
import utils.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Class abstraite qui sert de socle pour les classes de recherches.
 *
 * @author Yilmaz Rahman
 * @version 1.0
 * @date 12/03/2022
 */
public abstract class Recherche
{
    protected final MapsObjet mapObjet;
    protected final Log log = new Log();
    protected final ObjectInputStream clientReader;
    protected final BufferedWriter clientWriter;
    protected int maxNbParties = 100000;
    protected boolean afficheParties = true;
    protected PartiesFile partiesFile;


    /**
     * @param clientReader L'ObjectInputStream du client.
     * @param clientWriter Le BufferedWriter du client.
     * @param mapObjet     L'instance de la classe MapsObjet.
     */
    public Recherche(ObjectInputStream clientReader, BufferedWriter clientWriter, MapsObjet mapObjet)
    {
        this.clientReader = clientReader;
        this.clientWriter = clientWriter;
        this.mapObjet = mapObjet;
        this.partiesFile = new PartiesFile(mapObjet.getFile());

    }

    public abstract void cherche();


    /**
     * Envoie le Message au client à travers le flux de sortie.
     *
     * @param message Le message à envoyer.
     */
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

    /**
     * @return Le texte reçu par le client dans le flux d'entré.
     */
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

    /**
     * @return Le nombre reçu par le client dans le flux d'entré.
     */
    public int litInt()
    {
        int nb = 5;
        try
        {
            nb = Integer.parseInt(litMess());
        } catch (Exception e)
        {
            log.error("Impossible de lire l'entier");
        }
        return nb;
    }
}
