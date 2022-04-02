package recherche.partie.specifique;

import maps.MapsObjet;
import recherche.RecherchePartieSpecifique;

import java.io.BufferedWriter;
import java.io.ObjectInputStream;

/**
 * classe qui cherche les parties d'un joueur.
 *
 * @author Yilmaz Rahman
 * @version 1.0
 * @date 12/03/2022
 */
public class RecherchePartieJoueur extends RecherchePartieSpecifique
{
    private String joueur;


    /**
     * @param clientReader L'ObjectInputStream du client.
     * @param clientWriter Le BufferedWriter du client.
     * @param mapObjet     L'instance de la classe MapsObjet.
     */
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
            this.lstPosParties = mapObjet.getNameMap().get(this.joueur);
            if (this.afficheParties)
            {
                if (nbParties == 0)
                {
                    nbParties = Math.min(mapObjet.getNameMap().get(this.joueur).size(), this.maxNbParties);
                }
                Thread t = new Thread(this::calcule);
                t.setPriority(Thread.MAX_PRIORITY);
                t.start();
            } else reiterationSurParties("Joueur : " + this.joueur);
        } else envoieMessage(toString());
    }


    /*
    lit le fichier tant que le nombre de partie n'a pas été atteint,
    cree une Partie si les lignes du fichier correspondent avec les lignes dans lstPosParties
     */
    @Override
    public void calcule()
    {
        tempsRecherche = System.currentTimeMillis();

        this.lstPartie = this.partiesFile.getAllParties(this.lstPosParties, nbParties);

        this.tempsRecherche = System.currentTimeMillis() - this.tempsRecherche;
        if (this.afficheParties) envoieMessage(toString());

        //Libere la liste de la memoire
        this.lstPartie = null;
        System.gc();
    }

}
