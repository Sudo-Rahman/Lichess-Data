package recherche.autres;

import maps.MapsObjet;
import recherche.Recherche;
import utils.Colors;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;

/**
 * Affiche toutes les parties existantes, grace a leurs positions stocker dans une des HashMaps de MapsObjet.
 *
 * @author Yilmaz Rahman
 * @version 1.0
 * @date 02/04/2022
 */
public class AfficheToutesLesParties extends Recherche
{
    /**
     * @param clientReader L'ObjectInputStream du client.
     * @param clientWriter Le BufferedWriter du client.
     * @param mapObjet     L'instance de la classe MapsObjet.
     */
    public AfficheToutesLesParties(ObjectInputStream clientReader, BufferedWriter clientWriter, MapsObjet mapObjet)
    {
        super(clientReader, clientWriter, mapObjet);
    }

    @Override
    public void cherche()
    {
        long nbParties = Math.min(maxNbParties, mapObjet.getNbParties());
        long compteParties = 0;

        for (Map.Entry<Object, List<Long>> entry : mapObjet.getNameMap().entrySet())
        {
            if (compteParties >= nbParties) break;
            else
            {
                for (Long l : entry.getValue())
                {
                    if (compteParties < nbParties)
                    {
                        try
                        {
                            envoieMessage(partiesFile.getPartieInFile(l).toString() + Colors.BLUE_BOLD_BRIGHT + "\n---------------------------------------------------------------" + Colors.reset);
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        compteParties++;
                    } else break;
                }
            }
        }
        if (compteParties == maxNbParties)
            envoieMessage(Colors.BLUE_BOLD_BRIGHT + "Nombre de parties : " + compteParties + " limite atteinte" + Colors.reset);
        else envoieMessage(Colors.BLUE_BOLD_BRIGHT + "Nombre de parties : " + compteParties + Colors.reset);
    }
}
