/*
 * Nom de classe : CinqOuverturesPlusJoue
 *
 * Description   : classe qui cherche les 5 ouvertures les plus joué.
 *
 * Version       : 1.0
 *
 * Date          : 19/03/2022
 *
 * Copyright     : Yilmaz Rahman, Colliat Maxime
 *
 */

package recherche.autres;

import maps.MapsObjets;
import recherche.Recherche;
import utils.Colors;

import java.io.BufferedWriter;
import java.io.ObjectInputStream;
import java.util.*;

public class CinqOuverturesPlusJoue extends Recherche
{
    public CinqOuverturesPlusJoue(ObjectInputStream clientReader, BufferedWriter clientWriter, MapsObjets mapObjets)
    {
        super(clientReader, clientWriter, mapObjets);
    }

    @Override
    public void cherche()
    {
        Map<Integer, String> lst = new HashMap<>();
        for (Map.Entry<Object, List<long[]>> element : getOpenningMap().entrySet())
        {
            lst.put(element.getValue().size(), (String) element.getKey());
        }
        // enleve toutes les plus petites valeurs et laisse les 5 plus grandes
        for (int i = 0; i < getOpenningMap().size() - 5; i++)
        {
            lst.remove(Collections.min(lst.keySet()));
        }

        // hasmap trié du plus grand au plus petit
        TreeMap<Object, Object> lstTrier = new TreeMap<>(Collections.reverseOrder());
        lstTrier.putAll(lst);

        int i = 1;
        envoieMessage("\n" + Colors.BLUE_BOLD + "Classements des ouvertures les plus joué sur " + getNbParties() + " parties : " + Colors.reset);
        for (Map.Entry<Object, Object> element : lstTrier.entrySet())
        {
            envoieMessage(i + ". L'ouverture : " + element.getValue() + " avec " + element.getKey() + " fois.");
            i++;
        }
    }
}
