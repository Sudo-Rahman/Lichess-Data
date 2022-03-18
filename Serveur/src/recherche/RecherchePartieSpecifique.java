package recherche;

import maps.MapsObjets;
import partie.Partie;
import utils.Colors;

import java.io.BufferedWriter;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class RecherchePartieSpecifique extends Recherche
{

    protected List<Partie> lstPartie;
    protected List<String> lstLigne;
    protected long tempsRecherche;
    protected int nbParties;
    protected List<long[]> lstLigneParties;
    protected int maxNbParties = 1000000;



    public RecherchePartieSpecifique(String pathFile, ObjectInputStream clientReader, BufferedWriter clientWriter, MapsObjets mapObjets)
    {
        super(pathFile, clientReader, clientWriter, mapObjets);
        this.lstPartie = new ArrayList<>();
        this.lstLigne = new ArrayList<>();
        this.lstLigneParties = new ArrayList<>();
    }

    public abstract void calcule();
    public  abstract void initDemande();

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
