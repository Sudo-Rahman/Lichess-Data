/*
 * Nom de classe : RecherchePartieJoueur
 *
 * Description   : classe qui cherche les parties d'un joueur.
 *
 * Version       : 1.0 , 1,1
 *
 * Date          : 12/03/2022 , 18/03/2022
 *
 * Copyright     : Yilmaz Rahman, Colliat Maxime
 *
 */

package recherche.partie.specifique;

import maps.MapsObjet;
import recherche.RecherchePartieSpecifique;

import java.io.BufferedWriter;
import java.io.ObjectInputStream;

public class RecherchePartieJoueur extends RecherchePartieSpecifique
{
    private String joueur;


    public RecherchePartieJoueur(ObjectInputStream clientReader, BufferedWriter clientWriter, MapsObjet mapObjet)
    {
        super(clientReader, clientWriter, mapObjet);
    }


    @Override
    public void initDemande()
    {
        envoieMessage("Donner l'username du joueur");
        this.joueur = litMess();
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

    /*
    lance le calcule pour chercher les parties si l'element demander par le client est bien dans l'hasmap
     */
    @Override
    public void cherche()
    {
        initDemande();
        if (mapObjet.getNameMap().containsKey(this.joueur))
        {
            this.lstLigneParties = mapObjet.getNameMap().get(this.joueur);
            if (this.afficheParties)
            {
                if (nbParties == 0)
                {
                    nbParties = Math.min(mapObjet.getNameMap().get(this.joueur).size(), this.maxNbParties);
                }
                Thread t = new Thread(this::calcule);
                t.setPriority(Thread.MAX_PRIORITY);
                t.start();
            } else reiterationSurParties();
        } else envoieMessage(toString());
    }


    /*
    lit le fichier tant que le nombre de partie n'a pas été atteint,
    cree une Partie si les lignes du fichier correspondent avec les lignes dans lstLigneParties
     */
    @Override
    public void calcule()
    {
        tempsRecherche = System.currentTimeMillis();

        this.lstPartie = this.partiesFile.getAllParties(this.lstLigneParties, nbParties);

        this.tempsRecherche = System.currentTimeMillis() - this.tempsRecherche;
        if (this.afficheParties) envoieMessage(toString());

        //Libere la liste de la memoire
        this.lstPartie = null;
        System.gc();
    }

}
