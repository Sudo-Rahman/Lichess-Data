package recherche;

import partie.Partie;
import utils.Colors;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class RecherchePartieSpecifique extends Recherche
{

    private List<Partie> lstPartie;
    private List<String> lstLigne;
    private long tempsRecherche;
    private int nbParties;


    public RecherchePartieSpecifique(String pathFile, ObjectInputStream clientReader, BufferedWriter clientWriter)
    {
        super(pathFile, clientReader, clientWriter);
        this.lstPartie = new ArrayList<>();
        this.lstLigne = new ArrayList<>();
        this.nbParties = 5;
    }

    @Override
    public void cherche()
    {

        envoieMessage("Vous avez choisie l'Option Recherche de Partie.\n");
        envoieMessage(afficheChoix());
        int choix = 5;
        while (choix > 3 || choix < 1)
        {
            envoieMessage("Choisissez votre choix : ");
            try
            {
                choix = litInt();
            } catch (NumberFormatException e)
            {
                log.warning("Choix pas bon!!");
            }
        }
        switch (choix)
        {
            case 1 -> {
                recherchePremierCoup();
                envoieMessage(toString());
            }
        }
        closeFileReader();
        closeWriter();
    }

    @Override
    public String toString()
    {
        String mess = Colors.BLUE_BOLD + "\n---------------------------------------\n" + Colors.reset;

        for (Partie p : lstPartie)
        {
            mess += p.toString();
            mess += Colors.BLUE_BOLD + "\n---------------------------------------\n" + Colors.reset;
        }
        if (this.lstPartie.size() == 0)
        {
            mess += "Rien a été trouvé";
            mess += Colors.BLUE_BOLD + "\n---------------------------------------\n" + Colors.reset;
        }
        mess += "Le temps de recherche est de : " + this.tempsRecherche / 1000 + " secondes.\n";

        return mess;
    }

    private String afficheChoix()
    {
        return Colors.green + "1/ Recherche de partie(s) en fonction du premier coup" + Colors.reset + ".\n" +
                Colors.cyan + "2/ Recherche de partie(s) en fonction de l'elo des joueur" + Colors.reset + ".\n" +
                Colors.green + "3/ Recherche de partie(s) en fonction de la date" + Colors.reset + ".\n";
    }

    private void recherchePremierCoup()
    {
        envoieMessage("Donner le premier coup, ex : \" d4\"");
        String coup;
        coup = litMess();
        envoieMessage("Combien de partie voulez vous rechercher ?");
        this.nbParties = litInt();
        closeReader();
        String ligne;
        int comptLigne = 0;
        this.tempsRecherche = System.currentTimeMillis();
        try
        {
            while ((ligne = getFileReader().readLine()) != null && this.lstPartie.size() < this.nbParties)
            {
                if (ligne.equals("")) {comptLigne++;} else
                {
                    lstLigne.add(ligne);
                }
                if (comptLigne == 2)
                {
                    if (premierCoupEstDedans(coup)) lstPartie.add(new Partie(lstLigne));
                    lstLigne.clear();
                    comptLigne = 0;
                }
            }
            this.tempsRecherche = System.currentTimeMillis() - this.tempsRecherche;

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private boolean premierCoupEstDedans(String string)
    {
        return this.lstLigne.get(this.lstLigne.size() - 1).split(" ")[1].equals(string);
    }
}
