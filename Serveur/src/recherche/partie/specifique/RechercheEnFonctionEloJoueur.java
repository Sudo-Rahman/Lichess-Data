package recherche.partie.specifique;

import maps.MapsObjet;
import partie.Partie;
import recherche.RecherchePartieSpecifique;

import java.io.BufferedWriter;
import java.io.ObjectInputStream;

/**
 * Classe qui cherche les parties en fonction de des élos des joueurs.
 *
 * @author Yilmaz Rahman
 * @version 1.0
 * @date 13/03/2022
 */
public class RechercheEnFonctionEloJoueur extends RecherchePartieSpecifique
{

    private int eloSup;
    private int eloInf;

    /**
     * @param clientReader L'ObjectInputStream du client.
     * @param clientWriter Le BufferedWriter du client.
     * @param mapObjet     L'instance de la classe MapsObjet.
     */
    public RechercheEnFonctionEloJoueur(ObjectInputStream clientReader, BufferedWriter clientWriter, MapsObjet mapObjet)
    {
        super(clientReader, clientWriter, mapObjet);
    }


    /*
    lance le calcule pour chercher les parties si l'element demander par le client est bien dans l'hasmap
     */
    @Override
    public void cherche()
    {
        initDemande();
        if (getAllElos() > 0)
        {
            // 0 correspond au maximum de partie, qui est limité par la variable maxNbParties
            if (this.afficheParties)
            {
                if (nbParties == 0)
                {
                    nbParties = Math.min(this.lstPosParties.size(), this.maxNbParties);
                }
                Thread t = new Thread(this::calcule);
                t.setPriority(Thread.MAX_PRIORITY);
                t.start();
            } else
            {
                this.iterative = true;
                setDescription(", elo compris entre : " + this.eloInf + " et " + this.eloSup);
            }
        } else envoieMessage(toString());
    }

    @Override
    public void initDemande()
    {
        envoieMessage("Donner la borne inferieur ex : 1000 -> les joueurs en dessous de 1000 ne serons pas pris");
        this.eloInf = litInt();
        envoieMessage("Donner la borne superieur ex : 2000 -> les joueurs au dessus de 2000 ne serons pas pris");
        this.eloSup = litInt();
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
    lit le fichier tant que le nombre de partie n'a pas été atteint,
    cree une Partie si les lignes du fichier correspondent avec les lignes dans lstPosParties
     */
    @Override
    public void calcule()
    {
        this.tempsRecherche = System.currentTimeMillis();
        for (Long pos : this.lstPosParties)
        {
            if (this.lstPartie.size() < this.nbParties)
            {
                Partie p = this.partiesFile.getPartieInFile(pos);
                if (p.getBlackElo() <= this.eloSup && p.getBlackElo() >= this.eloInf && p.getWhiteElo() <= this.eloSup && p.getWhiteElo() >= this.eloInf)
                    this.lstPartie.add(p);
            } else break;
        }

        this.tempsRecherche = System.currentTimeMillis() - this.tempsRecherche;
        if (this.afficheParties) envoieMessage(toString());
        //Libere la liste de la memoire
        this.lstPartie = null;
        System.gc();
    }

    private int getAllElos()
    {
        int compt = 0;
        for (int i = this.eloInf; i < this.eloSup; i++)
        {
            if (mapObjet.getEloMap().containsKey(i))
            {
                this.lstPosParties.addAll(mapObjet.getEloMap().get(i));
                compt += mapObjet.getEloMap().get(i).size();
            }
        }
        return compt;
    }
}
