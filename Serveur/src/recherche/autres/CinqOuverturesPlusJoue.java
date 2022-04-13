package recherche.autres;

import maps.MapsObjet;
import recherche.Recherche;
import utils.Colors;

import java.io.BufferedWriter;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


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
        Map<Long, String> map = new TreeMap<>(Collections.reverseOrder());
        for (Map.Entry<Object, List<Long>> element : mapObjet.getOpenningMap().entrySet())
        {
            map.put((long) element.getValue().size(), (String) element.getKey());
        }

        int i = 0;
        envoieMessage("\n" + Colors.BLUE_BOLD + "Classements des ouvertures les plus joué sur " + mapObjet.getNbParties() + " parties : " + Colors.reset);
        for (Map.Entry<Long, String> element : map.entrySet())
        {
            envoieMessage(i + ". L'ouverture : " + element.getValue() + " avec " + element.getKey() + " fois.");
            i++;
            if (i == 5) break;
        }
        envoieMessage("\n");
    }
}
