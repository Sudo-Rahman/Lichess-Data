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

import choix.InitChoix;
import maps.CreeMap;
import maps.MapsObjet;
import partie.PartiesFile;
import utils.Colors;
import utils.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;

public abstract class Recherche
{
    protected final MapsObjet mapObjet;
    protected final Log log = new Log();
    protected final ObjectInputStream clientReader;
    protected final BufferedWriter clientWriter;
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



    /**
     * affiche les differents choix donner au client.
     */
    public String afficheChoix()
    {
        return Colors.cyan + "1 / Consulter une partie spécifique et la visualiser pas à pas." + Colors.reset + "\n" +
                Colors.green + "2 / Trouver toutes les parties d’un joueur." + Colors.reset + "\n" +
                Colors.cyan + "3 / Consulter les 5 ouvertures les plus jouées" + Colors.reset + "\n" +
                Colors.green + "4 / Consulter les parties terminé avec n coups." + Colors.reset + "\n" +
                Colors.cyan + "5 / Lister les joueurs les plus actifs, les plus actifs sur une semaine, etc." + Colors.reset + "\n" +
                Colors.green + "6 / Calculer le joueur le plus fort au sens du PageRank" + Colors.reset + "\n" +
                Colors.cyan + "7 / Consulter le plus grand nombre de coups consécutifs cc qui soient communs à p parties\n" + Colors.reset +
                Colors.RED_BOLD_BRIGHT + "-1 / Pour quitter le serveur" + Colors.reset;
        //        Colors.green + "" + Colors.reset;
        //        Colors.cyan + "" + Colors.reset;
    }
}
