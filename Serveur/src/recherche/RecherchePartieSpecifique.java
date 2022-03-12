package recherche;

import utils.Colors;

import java.io.BufferedWriter;
import java.io.ObjectInputStream;

public class RecherchePartieSpecifique extends Recherche
{


    public RecherchePartieSpecifique(String pathFile, ObjectInputStream clientReader, BufferedWriter clientWriter)
    {
        super(pathFile, clientReader, clientWriter);
    }

    @Override
    public void cherche()
    {

        envoieMessage("Vous avez choisie l'Option Recherche de Partie");
        envoieMessage(afficheChoix());
        int choix = 5;
        while (choix > 3 || choix < 1)
        {
            envoieMessage("Choisissez votre choix");
            try {
                choix = Integer.parseInt(litMess());
            }catch (NumberFormatException e) {
                log.error("Choix pas bon!!");
            }
        }

    }

    @Override
    public String toString()
    {
        return null;
    }

    private String afficheChoix()
    {
        return Colors.green + "1/ Recherche de partie(s) en fonction de l'ouverture" + Colors.reset + ".\n" +
                Colors.cyan + "2/ Recherche de partie(s) en fonction de l'elo des joueur" + Colors.reset + ".\n" +
                Colors.green + "3/ Recherche de partie(s) en fonction de la date" + Colors.reset + ".\n";
    }
}
