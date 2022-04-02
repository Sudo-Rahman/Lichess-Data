package recherche.partie.specifique;


import maps.MapsObjet;
import recherche.RecherchePartieSpecifique;

import java.io.BufferedWriter;
import java.io.ObjectInputStream;

/**
 * classe qui cherche les parties en fonction du premier coup demander.
 *
 * @author Yilmaz Rahman
 * @version 1.0
 * @date 12/03/2022
 */
public class RechereEnFonctionDuPremierCoup extends RecherchePartieSpecifique
{

    private String coup;

    /**
     * @param clientReader L'ObjectInputStream du client.
     * @param clientWriter Le BufferedWriter du client.
     * @param mapObjet     L'instance de la classe MapsObjet.
     */
    public RechereEnFonctionDuPremierCoup(ObjectInputStream clientReader, BufferedWriter clientWriter, MapsObjet mapObjet)
    {
        super(clientReader, clientWriter, mapObjet);
    }

    @Override
    public void initDemande()
    {
        envoieMessage("Donner le premier coup, ex : \"d4\"");
        this.coup = litMess();
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
        if (mapObjet.getOpenningMap().containsKey(this.coup))
        {
            this.lstPosParties = mapObjet.getOpenningMap().get(this.coup);
            if (this.afficheParties)
            {
                if (nbParties == 0)// 0 correspond au maximum de partie, qui est limité par la variable maxNbParties
                {
                    nbParties = Math.min(mapObjet.getOpenningMap().get(this.coup).size(), this.maxNbParties);
                }
                Thread t = new Thread(this::calcule);
                t.setPriority(Thread.MAX_PRIORITY);
                t.start();
            } else reiterationSurParties("premier coup : " + this.coup);
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