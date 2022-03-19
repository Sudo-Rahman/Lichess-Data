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
        long nbParties = 0;
        Map<Integer, String> lst = new HashMap<>();
        for (Map.Entry<String, List<long[]>> element : getOpenningMap().entrySet())
        {
            lst.put(element.getValue().size(), element.getKey());
            nbParties += element.getValue().size();
        }
        for (int i = 0; i < getOpenningMap().size() - 5; i++)
        {
            lst.remove(Collections.min(lst.keySet()));
        }

        // hasmap trié du plus grand au plus petit
        TreeMap<Object, Object> lstTrier = new TreeMap<>(Collections.reverseOrder());
        lstTrier.putAll(lst);

        int i = 1;
        envoieMessage("\n" + Colors.BLUE_BOLD + "Classements des ouvertures les plus joué sur " + nbParties + " parties : " + Colors.reset);
        for (Map.Entry<Object, Object> element : lstTrier.entrySet())
        {
            envoieMessage(i + ". L'ouverture : " + element.getValue() + " avec " + element.getKey() + " fois.");
            i++;
        }
    }
}
