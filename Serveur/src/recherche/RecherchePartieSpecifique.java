package recherche;

import choix.InitChoix;
import maps.CreeMapIteration;
import maps.MapsObjet;
import partie.Partie;
import utils.Colors;

import java.io.BufferedWriter;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe qui hérite de Recherche, il engendre des classes pour des recherches spécifiques de partie.
 *
 * @author Yilmaz Rahman
 * @version 1.0
 * @date 12/03/2022
 */
public abstract class RecherchePartieSpecifique extends Recherche
{

    protected List<Partie> lstPartie;

    // liste qui contiendra toutes les lignes pour créer une partie
    protected List<String> lstStrLigne;

    protected long tempsRecherche = 0;
    protected int nbParties;

    // List qui va contenir toutes les positions de chaque partie.
    protected List<Long> lstPosParties;

    protected boolean iterative = false;

    public boolean isIterative()
    {
        return iterative;
    }

    protected String description;

    public String getDescription()
    {
        return description;
    }

    protected void setDescription(String description)
    {
        this.description = description;
    }


    /**
     * @param clientReader L'ObjectInputStream du client.
     * @param clientWriter Le BufferedWriter du client.
     * @param mapObjet     L'instance de la classe MapsObjet.
     */
    public RecherchePartieSpecifique(ObjectInputStream clientReader, BufferedWriter clientWriter, MapsObjet mapObjet)
    {
        super(clientReader, clientWriter, mapObjet);
        this.lstPartie = new ArrayList<>();
        this.lstStrLigne = new ArrayList<>();
        this.lstPosParties = new ArrayList<>();
    }

    public abstract void calcule();

    public abstract void initDemande();


    /**
     * @return MapsObjet avec les hashmaps des parties recherchées.
     */
    public MapsObjet getMapsObjetReiteration()
    {
        MapsObjet mp = new MapsObjet(this.mapObjet.getFile());
        new CreeMapIteration(mp, this.mapObjet.getFile(), this.lstPosParties).cree();
        return mp;
    }

    @Override
    public String toString()
    {
        StringBuilder mess = new StringBuilder(Colors.BLUE_BOLD + "\n-----------------------------------------------------\n" + Colors.reset);

        for (Partie p : lstPartie)
        {
            mess.append(p.toString());
            mess.append(Colors.BLUE_BOLD + "\n-----------------------------------------------------\n" + Colors.reset);
        }
        if (this.lstPartie.size() == 0)
        {
            mess.append("Rien n'a été trouvé");
            mess.append(Colors.BLUE_BOLD + "\n-----------------------------------------------------\n" + Colors.reset);
        }
        mess.append("Le temps de recherche est de : ").append(this.tempsRecherche / 1000).append(" secondes. Nombre partie : ").append(lstPartie.size()).append(".\n");

        return mess.toString();
    }
}
