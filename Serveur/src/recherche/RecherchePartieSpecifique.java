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

import maps.MapsObjets;
import partie.Partie;
import utils.Colors;

import java.io.BufferedWriter;
import java.io.ObjectInputStream;
import java.util.*;

public abstract class RecherchePartieSpecifique extends Recherche
{

    protected List<Partie> lstPartie;

    // liste qui contiendra toutes les lignes pour crée une parties
    protected List<String> lstStrLigne;

    protected long tempsRecherche = 0;
    protected int nbParties;

    // tableau d'entier qui contient les lignes des parties ex : [[0, 18], [19, 37]]
    protected List<long[]> lstLigneParties;


    public RecherchePartieSpecifique(ObjectInputStream clientReader, BufferedWriter clientWriter, MapsObjets mapObjets)
    {
        super(clientReader, clientWriter, mapObjets);
        this.lstPartie = new ArrayList<>();
        this.lstStrLigne = new ArrayList<>();
        this.lstLigneParties = new ArrayList<>();
    }

    public abstract void calcule();

    public abstract void initDemande();

    protected void trieMapList(Map<Object, List<long[]>> hashMap, Object objet)
    {
        TreeMap<Long, Long> map = new TreeMap<>();
        for (long[] t : hashMap.get(objet))
        {
            map.put(t[0], t[1]);
        }
        this.lstLigneParties.clear();
        for (Map.Entry<Long, Long> t : map.entrySet())
        {
            long[] tab = new long[2];
            tab[0] = t.getKey();
            tab[1] = t.getValue();
            this.lstLigneParties.add(tab);
        }
    }

    protected void trieMapList(Map<Object, List<long[]>> hashMap, List<Object> lstObjet)
    {
        TreeMap<Long, Long> map = new TreeMap<>();
        for (Object obj : lstObjet)
        {
            if (hashMap.containsKey(obj))
            {
                for (long[] l : hashMap.get(obj))
                {
                    map.put(l[0], l[1]);
                }
            }
        }

        // ajout des lignes du debu et de fin des parties dans lstLigneParties
        for (Map.Entry<Long, Long> element : map.entrySet())
        {
            long[] tab = new long[2];
            tab[0] = element.getKey();
            tab[1] = element.getValue();
            this.lstLigneParties.add(tab);
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
