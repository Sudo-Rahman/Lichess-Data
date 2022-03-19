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

import maps.MapsObjets;
import recherche.Recherche;
import recherche.autres.CinqOuverturesPlusJoue;
import recherche.partie.specifique.RechercheEnFonctionDate;
import recherche.partie.specifique.RechercheEnFonctionEloJoueur;
import recherche.partie.specifique.RecherchePartieJoueur;
import recherche.partie.specifique.RechereEnFonctionDuPremierCoup;
import utils.Colors;
import utils.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;

public class InitChoix
{
    private final BufferedWriter writer;
    private final ObjectInputStream objectInputStream;
    private final MapsObjets mapObjets;
    private final Log log = new Log();

    public InitChoix(int choix, ObjectInputStream o, BufferedWriter b, MapsObjets mapObjets)
    {
        this.objectInputStream = o;
        this.writer = b;

        this.mapObjets = mapObjets;

        switch (choix)
        {
            case 1 -> choix1();
            case 2 -> choix2();
            case 3 -> choix3();
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
            if(choix ==-1){
                log.fatal("Impossible de lire le flux du client !!!");
                break;
            }
        } while (choix > 3 || choix < 1);
        switch (choix)
        {
            case 1 -> {
                RechereEnFonctionDuPremierCoup recherche = new RechereEnFonctionDuPremierCoup(objectInputStream, writer, mapObjets);
                recherche.cherche();
            }
            case 2 -> {
                RechercheEnFonctionEloJoueur recherche = new RechercheEnFonctionEloJoueur(objectInputStream, writer, mapObjets);
                recherche.cherche();
            }
            case 3 -> {
                RechercheEnFonctionDate recherche = new RechercheEnFonctionDate(objectInputStream, writer, mapObjets);
                recherche.cherche();
            }
        }
    }

    private void choix2()
    {
        RecherchePartieJoueur recherche = new RecherchePartieJoueur(objectInputStream, writer, mapObjets);
        recherche.cherche();
    }

    private void choix3(){
        CinqOuverturesPlusJoue recherche = new CinqOuverturesPlusJoue(objectInputStream,writer,mapObjets);
        recherche.cherche();
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
