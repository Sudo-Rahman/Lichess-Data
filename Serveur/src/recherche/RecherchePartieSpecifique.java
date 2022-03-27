/*
 * Nom de classe : RecherchePartieSpecifique
 *
 * Description   : classe qui herite de Recherche, il engendre des classes pour des recherche specifiques de partie.
 *
 * Version       : 1.0
 *
 * Date          : 12/03/2022
 *
 * Copyright     : Yilmaz Rahman, Colliat Maxime
 *
 */

package recherche;

import maps.MapsObjet;
import partie.Partie;
import utils.Colors;

import java.io.BufferedWriter;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class RecherchePartieSpecifique extends Recherche
{

    protected List<Partie> lstPartie;

    // liste qui contiendra toutes les lignes pour crée une parties
    protected List<String> lstStrLigne;

    protected long tempsRecherche = 0;
    protected int nbParties;

    // tableau d'entier qui contient les lignes des parties ex : [[0, 18], [19, 37]]
    protected List<Long> lstLigneParties;


    public RecherchePartieSpecifique(ObjectInputStream clientReader, BufferedWriter clientWriter, MapsObjet mapObjet)
    {
        super(clientReader, clientWriter, mapObjet);
        this.lstPartie = new ArrayList<>();
        this.lstStrLigne = new ArrayList<>();
        this.lstLigneParties = new ArrayList<>();
    }

    public abstract void calcule();

    public abstract void initDemande();

    protected void trieMapList(Map<Object, List<Long>> hashMap, Object objet)
    {
        Collections.sort(hashMap.get(objet));
    }

    protected void trieMapList(Map<Object, List<Long>> hashMap, List<Object> lstObjet)
    {
        for (Object obj : lstObjet)
        {
            Collections.sort(hashMap.get(obj));
        }
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
