/*
 * Nom de classe : InitChoix
 *
 * Description   : Classe qui gere tous les choix du client.
 *
 * Version       : 1.0
 *
 * Date          : 13/03/2022
 *
 * Copyright     : Yilmaz Rahman, Colliat Maxime
 */

package choix;

import maps.MapsObjet;
import recherche.autres.CinqOuverturesPlusJoue;
import recherche.autres.JoueursLesplusActifs;
import recherche.partie.specifique.*;
import utils.Colors;
import utils.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;

import static utils.Colors.reset;

public class InitChoix
{
    private final BufferedWriter writer;
    private final ObjectInputStream objectInputStream;
    private final MapsObjet mapObjet;
    private int mode;

    private final Log log = new Log();

    public InitChoix(int mode, ObjectInputStream o, BufferedWriter b, MapsObjet mapObjet)
    {
        this.mode = mode;
        this.objectInputStream = o;
        this.writer = b;
        this.mapObjet = mapObjet;
        boolean quitte = false;

        while (envoieMessage(afficheChoix()) != -1)
        {
            switch (litInt())
            {
                case 0 -> {
                    if (mode == 1) quitte = true;
                }
                case 1 -> choix1();
                case 2 -> choix2();
                case 3 -> choix3();
                case 4 -> choix4();
                case 5 -> choix5();
                default -> {
                    envoieMessage("Le nombre n'est pas bon !!");
                }
            }
            if (quitte)
            {
                envoieMessage(Colors.PURPLE_BOLD + "Vous avez quitter le mode iterative!!" + reset);
                break;
            }
        }
    }

    private void choix1()
    {
        envoieMessage("\n" + Colors.green + "1/ Recherche de partie(s) en fonction du premier coup" + reset + ".\n" +
                Colors.cyan + "2/ Recherche de partie(s) en fonction de l'elo des joueur" + reset + ".\n" +
                Colors.green + "3/ Recherche de partie(s) en fonction de la date" + reset + ".\n");
        int choix;
        do
        {

            envoieMessage("Donner votre choix");
            choix = litInt();
            if (choix == -1)
            {
                log.fatal("Impossible de lire le flux du client !!!");
                break;
            }
        } while (choix > 3 || choix < 1);
        switch (choix)
        {
            case 1 -> {
                RechereEnFonctionDuPremierCoup recherche = new RechereEnFonctionDuPremierCoup(objectInputStream, writer, mapObjet);
                recherche.cherche();
            }
            case 2 -> {
                RechercheEnFonctionEloJoueur recherche = new RechercheEnFonctionEloJoueur(objectInputStream, writer, mapObjet);
                recherche.cherche();
            }
            case 3 -> {
                RechercheEnFonctionDate recherche = new RechercheEnFonctionDate(objectInputStream, writer, mapObjet);
                recherche.cherche();
            }
        }
    }

    private void choix2()
    {
        RecherchePartieJoueur recherche = new RecherchePartieJoueur(objectInputStream, writer, mapObjet);
        recherche.cherche();
    }

    private void choix3()
    {
        CinqOuverturesPlusJoue recherche = new CinqOuverturesPlusJoue(objectInputStream, writer, mapObjet);
        recherche.cherche();
    }

    private void choix4()
    {
        RechercheEnFonctionDuNombreDeCoup recherche = new RechercheEnFonctionDuNombreDeCoup(objectInputStream, writer, mapObjet);
        recherche.cherche();
    }


    private void choix5()
    {
        JoueursLesplusActifs recherche = new JoueursLesplusActifs(objectInputStream, writer, mapObjet);
        recherche.cherche();
    }

    /**
     * envoie le message en parametre au client.
     */
    private int envoieMessage(String message)
    {

        try
        {
            this.writer.write(message);
            this.writer.newLine();
            this.writer.flush();
        } catch (Exception e)
        {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    /**
     * affiche les differents choix donner au client.
     */
    public String afficheChoix()
    {
        String retour = "";
        if (this.mode == 1) retour += Colors.YELLOW_BRIGHT + "Vous etes sur des données itératives." + reset + "\n" +
                Colors.BLUE_BOLD_BRIGHT + "0 / Pour quitter le mode itérative." + reset + "\n";
        retour += Colors.cyan + "1 / Consulter une partie spécifique et la visualiser pas à pas." + reset + "\n" +
                Colors.green + "2 / Trouver toutes les parties d’un joueur." + reset + "\n" +
                Colors.cyan + "3 / Consulter les 5 ouvertures les plus jouées." + reset + "\n" +
                Colors.green + "4 / Consulter les parties terminé avec n coups." + reset + "\n" +
                Colors.cyan + "5 / Lister les joueurs les plus actifs, les plus actifs sur une semaine, etc." + reset + "\n" +
                Colors.green + "6 / Calculer le joueur le plus fort au sens du PageRank." + reset + "\n" +
                Colors.cyan + "7 / Consulter le plus grand nombre de coups consécutifs cc qui soient communs à p parties.\n" + reset +
                Colors.RED_BOLD_BRIGHT + "-1 / Pour quitter le serveur." + reset;
        //        Colors.green + "" + Colors.reset;
        //        Colors.cyan + "" + Colors.reset;
        return retour;

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
        } catch (NumberFormatException e)
        {
            log.error("Impossible de lire l'entier");
        } catch (ClassNotFoundException | IOException e)
        {
            return -1;
        }
        return nb;
    }
}
