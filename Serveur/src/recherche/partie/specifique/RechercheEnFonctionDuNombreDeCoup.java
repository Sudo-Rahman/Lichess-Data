/*
 * Nom de classe : PartieLesPlusCourt
 *
 * Description   : classe qui cherche les parties les plus court.
 *
 * Version       : 1.0
 *
 * Date          : 20/03/2022
 *
 * Copyright     : Yilmaz Rahman, Colliat Maxime
 *
 */
package recherche.partie.specifique;

import maps.MapsObjets;
import recherche.RecherchePartieSpecifique;

import java.io.BufferedWriter;
import java.io.ObjectInputStream;

public class RechercheEnFonctionDuNombreDeCoup extends RecherchePartieSpecifique
{
    private int nbCoups;

    public RechercheEnFonctionDuNombreDeCoup(ObjectInputStream clientReader, BufferedWriter clientWriter, MapsObjets mapObjets)
    {
        super(clientReader, clientWriter, mapObjets);
        this.nbCoups = 1;
    }

    @Override
    public void initDemande()
    {
        envoieMessage("Donner le nombre de coups");
        this.nbCoups = litInt();
        envoieMessage("Combien de partie voulez vous rechercher ? (-1) pour toutes les parties.");
        this.nbParties = litInt();
        envoieMessage("Voulez vous afficher les parties ? (no/yes)");
        if (litMess().equals("no"))
        {
            this.afficheParties = false;
        }
    }

    @Override
    public void cherche()
    {
        initDemande();
        if (getNbCoupsMap().containsKey(this.nbCoups))
        {
            this.lstLigneParties = getNbCoupsMap().get(this.nbCoups);
            // -1 correspond au maximum de partie, qui est limité par la variable maxNbParties
            if (nbParties == -1)
            {
                nbParties = Math.min(getNbCoupsMap().get(this.nbCoups).size(), this.maxNbParties);
            }
            Thread t = new Thread(this::calcule);
            t.setPriority(Thread.MAX_PRIORITY);
            t.start();
        } else envoieMessage(toString());
    }

    @Override
    public void calcule()
    {
        trieMapList(getNbCoupsMap(), this.nbCoups);

        this.tempsRecherche = System.currentTimeMillis();

        this.lstPartie = this.parsePartie.getAllParties(this.lstLigneParties, this.nbParties);

        this.tempsRecherche = System.currentTimeMillis() - this.tempsRecherche;
        if (this.afficheParties) envoieMessage(toString());
        this.parsePartie.closeReader();

        //Libere la liste de la memoire
        this.lstPartie = null;
        System.gc();
    }

}
