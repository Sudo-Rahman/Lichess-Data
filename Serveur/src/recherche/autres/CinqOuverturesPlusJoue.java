package recherche.autres;

import maps.MapsObjet;
import recherche.Recherche;
import utils.Colors;

import java.io.BufferedWriter;
import java.io.ObjectInputStream;
import java.util.*;


/**
 * Classe qui cherche les 5 ouvertures les plus joué.
 *
 * @author Yilmaz Rahman
 * @version 1.0
 * @date 19/03/2022
 */
public class CinqOuverturesPlusJoue extends Recherche
{
    /**
     * @param clientReader L'ObjectInputStream du client.
     * @param clientWriter Le BufferedWriter du client.
     * @param mapObjet     L'instance de la classe MapsObjet.
     */
    public CinqOuverturesPlusJoue(ObjectInputStream clientReader, BufferedWriter clientWriter, MapsObjet mapObjet)
    {
        super(clientReader, clientWriter, mapObjet);
    }

    @Override
    public void cherche()
    {
        Map<Integer, String> lst = new HashMap<>();
        for (Map.Entry<Object, List<Long>> element : mapObjet.getOpenningMap().entrySet())
        {
            lst.put(element.getValue().size(), (String) element.getKey());
        }
        // enleve toutes les plus petites valeurs et laisse les 5 plus grandes
        for (int i = 0; i < mapObjet.getOpenningMap().size() - 5; i++)
        {
            lst.remove(Collections.min(lst.keySet()));
        }

        // hasmap trié du plus grand au plus petit
        TreeMap<Object, Object> lstTrier = new TreeMap<>(Collections.reverseOrder());
        lstTrier.putAll(lst);

        int i = 1;
        envoieMessage("\n" + Colors.BLUE_BOLD + "Classements des ouvertures les plus joué sur " + mapObjet.getNbParties() + " parties : " + Colors.reset);
        for (Map.Entry<Object, Object> element : lstTrier.entrySet())
        {
            envoieMessage(i + ". L'ouverture : " + element.getValue() + " avec " + element.getKey() + " fois.");
            i++;
        }
        envoieMessage("\n");
    }
}
