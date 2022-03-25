/*
 * Nom de classe : RechereEnFonctionDuPremierCoup
 *
 * Description   : classe qui cherche les parties en fonction du premier coup demander.
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
import recherche.RecherchePartieSpecifique;

import java.io.BufferedWriter;
import java.io.ObjectInputStream;

public class RechereEnFonctionDuPremierCoup extends RecherchePartieSpecifique
{

    private String coup;

    public RechereEnFonctionDuPremierCoup(ObjectInputStream clientReader, BufferedWriter clientWriter, MapsObjets mapObjets)
    {
        super(clientReader, clientWriter, mapObjets);
    }

    @Override
    public void initDemande()
    {
        envoieMessage("Donner le premier coup, ex : \"d4\"");
        this.coup = litMess();
        envoieMessage("Combien de partie voulez vous rechercher ? (0) pour toutes les parties.");
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
        if (getOpenningMap().containsKey(this.coup))
        {
            this.lstLigneParties = getOpenningMap().get(this.coup);
            if (nbParties == 0)// 0 correspond au maximum de partie, qui est limité par la variable maxNbParties
            {
                nbParties = Math.min(getOpenningMap().get(this.coup).size(), this.maxNbParties);
            }
            Thread t = new Thread(this::calcule);
            t.setPriority(Thread.MAX_PRIORITY);
            t.start();
        } else envoieMessage(toString());
    }


    /*
    lit le fichier tant que le nombre de partie n'a pas été atteint,
    cree une Partie si les lignes du fichier correspondent avec les lignes dans lstLigneParties
     */
    @Override
    public void calcule()
    {

        trieMapList(getOpenningMap(), this.coup);

        tempsRecherche = System.currentTimeMillis();

        this.lstPartie = this.partiesFile.getAllParties(this.lstLigneParties, nbParties);

        this.tempsRecherche = System.currentTimeMillis() - this.tempsRecherche;
        if (this.afficheParties) envoieMessage(toString());

        //Libere la liste de la memoire
        this.lstPartie = null;
        System.gc();
    }
}