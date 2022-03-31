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
        envoieMessage("Voulez vous réiterez sur les parties (yes) ou afficher les parties (no) :");
        if (litMess().equals("yes"))
        {
            this.afficheParties = false;
        } else
        {
            envoieMessage("Combien de partie voulez vous rechercher ? (0) pour toutes les parties.");
            nbParties = litInt();
        }
    }

    @Override
    public void cherche()
    {
        initDemande();
        if (mapObjet.getNbCoupsMap().containsKey(this.nbCoups))
        {
            this.lstLigneParties = mapObjet.getNbCoupsMap().get(this.nbCoups);
            if (this.afficheParties)
            {
                // 0 correspond au maximum de partie, qui est limité par la variable maxNbParties
                if (nbParties == 0)
                {
                    nbParties = Math.min(mapObjet.getNbCoupsMap().get(this.nbCoups).size(), this.maxNbParties);
                }
                Thread t = new Thread(this::calcule);
                t.setPriority(Thread.MAX_PRIORITY);
                t.start();
            } else reiterationSurParties();
        } else envoieMessage(toString());
    }

    @Override
    public void calcule()
    {
        this.tempsRecherche = System.currentTimeMillis();

        this.lstPartie = this.partiesFile.getAllParties(this.lstLigneParties, this.nbParties);

        this.tempsRecherche = System.currentTimeMillis() - this.tempsRecherche;
        if (this.afficheParties) envoieMessage(toString());
        //Libere la liste de la memoire
        this.lstPartie = null;
        System.gc();
    }

}
