/*
 * Nom de classe : RechereEnFonctionDuPremierCoup
 *
 * Description   : classe qui cherche les parties en fonction d'une date precise.
 *
 * Version       : 1.0 , 1,1
 *
 * Date          : 12/03/2022 , 18/03/2022
 *
 * Copyright     : Yilmaz Rahman, Colliat Maxime
 *
 */

package recherche.partie.specifique;

import maps.MapsObjets;
import partie.Partie;
import recherche.RecherchePartieSpecifique;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RechercheEnFonctionDate extends RecherchePartieSpecifique
{
    private String date;


    public RechercheEnFonctionDate(ObjectInputStream clientReader, BufferedWriter clientWriter, MapsObjets mapObjets)
    {
        super(clientReader, clientWriter, mapObjets);
    }


    @Override
    public void initDemande()
    {
        SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy");

        Date dateDeb = null;
        Date dateFin = null;
        try
        {
            dateDeb = sdformat.parse((String) getUtcDateMap().keySet().toArray()[0]);
            dateFin = sdformat.parse((String) getUtcDateMap().keySet().toArray()[1]);
            for (Object date : getUtcDateMap().keySet())
            {
                if (dateDeb.compareTo(sdformat.parse((String) date)) > 0)
                {
                    dateDeb = sdformat.parse((String) date);
                }
                if (dateFin.compareTo(sdformat.parse((String) date)) < 0)
                {
                    dateFin = sdformat.parse((String) date);
                }
            }
        } catch (ParseException e)
        {
            log.error("Impossible de convertir le String en date !!");
        }
        envoieMessage("Donner la date, (compris entre " + sdformat.format(dateDeb) + " et " + sdformat.format(dateFin) + " sur le fichier" + mapObjets.getFile().getName());
        this.date = litMess();
        envoieMessage("Combien de partie voulez vous rechercher ? (-1) pour toutes les parties.");
        nbParties = litInt();
        envoieMessage("Voulez vous afficher les parties ? (no/yes)");
        if (litMess().equals("no"))
        {
            this.afficheParties = false;
        }
    }

    /*
    lance le calcule pour chercher les parties si l'element demander par le client est bien dans l'hasmap
     */
    @Override
    public void cherche()
    {
        initDemande();
        if (getUtcDateMap().containsKey(this.date))
        {
            this.lstLigneParties = getUtcDateMap().get(this.date);
            if (nbParties == -1)
            {
                nbParties = Math.min(getUtcDateMap().get(this.date).size(), this.maxNbParties);
            }
            Thread t = new Thread(this::calcule);
            t.setPriority(Thread.MAX_PRIORITY);
            t.start();
        }
        else envoieMessage(toString());
    }


    /**
     * lit le fichier tant que le nombre de partie n'a pas été atteint,
     * cree une Partie si les lignes du fichier correspondent avec les lignes dans lstLigneParties
     */
    @Override
    public void calcule()
    {
        trieMapList(getUtcDateMap(), this.date);

        tempsRecherche = System.currentTimeMillis();

        this.lstPartie = this.parsePartie.getAllParties(this.lstLigneParties,nbParties);

        this.tempsRecherche = System.currentTimeMillis() - this.tempsRecherche;
        if (this.afficheParties) envoieMessage(toString());

        //Libere la liste de la memoire
        this.lstPartie = null;
        System.gc();
    }

}
