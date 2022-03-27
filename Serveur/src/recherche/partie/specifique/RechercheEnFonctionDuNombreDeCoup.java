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

import maps.MapsObjet;
import recherche.RecherchePartieSpecifique;

import java.io.BufferedWriter;
import java.io.ObjectInputStream;

public class RechercheEnFonctionDuNombreDeCoup extends RecherchePartieSpecifique
{
    private int nbCoups;

    public RechercheEnFonctionDuNombreDeCoup(ObjectInputStream clientReader, BufferedWriter clientWriter, MapsObjet mapObjet)
    {
        super(clientReader, clientWriter, mapObjet);
        this.nbCoups = 1;
    }

    @Override
    public void initDemande()
    {
        envoieMessage("Donner le nombre de coups");
        this.nbCoups = litInt();
        envoieMessage("Combien de partie voulez vous rechercher ? (0) pour toutes les parties.");
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
        if (mapObjet.getNbCoupsMap().containsKey(this.nbCoups))
        {
            this.lstLigneParties = mapObjet.getNbCoupsMap().get(this.nbCoups);
            // 0 correspond au maximum de partie, qui est limité par la variable maxNbParties
            if (nbParties == 0)
            {
                nbParties = Math.min(mapObjet.getNbCoupsMap().get(this.nbCoups).size(), this.maxNbParties);
            }
            Thread t = new Thread(this::calcule);
            t.setPriority(Thread.MAX_PRIORITY);
            t.start();
        } else envoieMessage(toString());
    }

    @Override
    public void calcule()
    {
        trieMapList(mapObjet.getNbCoupsMap(), this.nbCoups);

        this.tempsRecherche = System.currentTimeMillis();

        this.lstPartie = this.partiesFile.getAllParties(this.lstLigneParties, this.nbParties);

        this.tempsRecherche = System.currentTimeMillis() - this.tempsRecherche;
        if (this.afficheParties) envoieMessage(toString());
        this.partiesFile.closeReader();

        //Libere la liste de la memoire
        this.lstPartie = null;
        System.gc();
    }

}
