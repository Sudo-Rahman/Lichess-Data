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
        String dateDeb = (String) getUtcDateMap().keySet().toArray()[0];
        String dateFin = (String) getUtcDateMap().keySet().toArray()[getUtcDateMap().size() - 1];
        envoieMessage("Donner la date, (compris entre " + dateDeb + " et " + dateFin + " sur le fichier" + mapObjets.getFile().getName());
        this.date = litMess();
        envoieMessage("Combien de partie voulez vous rechercher ? (-1) pour toutes les parties.");
        nbParties = litInt();
        envoieMessage("Voulez vous afficher les parties ? (no/yes)");
        if(litMess().equals("no")){
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
        } else envoieMessage(toString());
    }


    /**
    *lit le fichier tant que le nombre de partie n'a pas été atteint,
    *cree une Partie si les lignes du fichier correspondent avec les lignes dans lstLigneParties
     */
    @Override
    public void calcule()
    {
        String ligne;
        int comptLigneVide = 0;
        long lignes = 0L;


        tempsRecherche = System.currentTimeMillis();

        int partie = 0;
        try
        {
            while ((ligne = getFileReader().readLine()) != null &&  lstPartie.size() < nbParties)
            {
                if (lignes >= lstLigneParties.get(partie)[0] && lignes <= lstLigneParties.get(partie)[1])
                {
                    if (ligne.equals("")) {comptLigneVide++;} else lstStrLigne.add(ligne);
                    if (comptLigneVide == 2)
                    {
                        lstPartie.add(new Partie(lstStrLigne));
                        lstStrLigne.clear();
                        partie++;
                        comptLigneVide = 0;
                    }
                }
                lignes++;
            }
            this.tempsRecherche = System.currentTimeMillis() - this.tempsRecherche;

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        if(this.afficheParties) envoieMessage(toString());
        closeFileReader();

        //Libere la liste de la memoire
        this.lstPartie = null;
        System.gc();
    }

}
