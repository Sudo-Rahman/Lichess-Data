package choix;

import recherche.partie.specifique.RechercheEnFonctionEloJoueur;
import recherche.partie.specifique.RechereEnFonctionDuPremierCoup;
import utils.Colors;
import utils.Log;

import java.io.BufferedWriter;
import java.io.ObjectInputStream;

public class InitChoix
{
    private BufferedWriter writer;
    private ObjectInputStream objectInputStream;
    private final Log log = new Log();
    private final String pathFile = "/home/rahman/Documents/GitHub/Projet-INFO-4B/others/lichess_db_standard_rated_2016-08.pgn";

    public InitChoix(int choix, ObjectInputStream o, BufferedWriter b)
    {
        this.objectInputStream = o;
        this.writer = b;

        switch (choix)
        {
            case 1 -> {
                choix1();
            }
        }
    }

    private void choix1()
    {
        envoieMessage("\n" + Colors.green + "1/ Recherche de partie(s) en fonction du premier coup" + Colors.reset + ".\n" +
                Colors.cyan + "2/ Recherche de partie(s) en fonction de l'elo des joueur" + Colors.reset + ".\n" +
                Colors.green + "3/ Recherche de partie(s) en fonction de la date" + Colors.reset + ".\n");
        int choix;
        do
        {
            envoieMessage("Donner votre choix");
            choix = litInt();
        } while (choix > 3 || choix < 1);
        switch (choix)
        {
            case 1 -> {
                RechereEnFonctionDuPremierCoup recherche = new RechereEnFonctionDuPremierCoup(pathFile, objectInputStream, writer);
                recherche.cherche();
            }
            case 2 -> {
                RechercheEnFonctionEloJoueur recherche = new RechercheEnFonctionEloJoueur(pathFile, objectInputStream, writer);
                recherche.cherche();
            }
        }
    }

    /**
     * envoie le message en parametre au client.
     */
    private void envoieMessage(String message)
    {

        try
        {
            this.writer.write(message);
            this.writer.newLine();
            this.writer.flush();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String litMess()
    {
        String mess = null;
        try
        {
            mess = (String) this.objectInputStream.readObject();
        } catch (Exception e)
        {
            log.error("Impossible de lire le message");

        }
        return mess;
    }

    public int litInt()
    {
        int nb = 5;
        try
        {
            nb = Integer.parseInt((String) this.objectInputStream.readObject());
        } catch (Exception e)
        {
            log.error("Impossible de lire l'entier");
        }
        return nb;
    }
}
