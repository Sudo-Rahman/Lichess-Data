/*
 * Nom de classe : ConnexionClient
 *
 * Description   : Cette classe traitera la connexion d'un client sur le serveur, il sera lancer par la classe Serveur.
 *
 * Version       : 1.0, 1.1
 *
 * Date          : 23/02/2022, 24/02/2022
 *
 * Copyright     : Yilmaz Rahman, Colliat Maxime
 *
 */

package serveur;


import choix.InitChoix;
import client.info.ClientInfo;
import maps.MapsObjets;
import utils.Colors;
import utils.Log;

import java.io.*;
import java.net.Socket;
import java.util.List;


class ConnexionClient extends Thread
{
    private final List<ConnexionClient> lstConnexion;
    private ClientInfo username;
    private Socket socketClient;
    private final int nbMaxThread;
    private static final Log log = new Log();
    private BufferedWriter writer;
    private ObjectInputStream objectInputStream;
    private final MapsObjets mapObjets;

    protected ConnexionClient(Socket clientSocket, int nbMaxThread, List<ConnexionClient> lst, MapsObjets mapObjets)
    {
        this.lstConnexion = lst;
        try
        {
            this.socketClient = clientSocket;
            this.writer = new BufferedWriter(new OutputStreamWriter(this.socketClient.getOutputStream()));
            this.objectInputStream = new ObjectInputStream(this.socketClient.getInputStream());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        this.nbMaxThread = nbMaxThread;
        this.mapObjets = mapObjets;
    }

    @Override
    public void run()
    {

        try
        {
            this.username = (ClientInfo) objectInputStream.readObject();
            System.out.println("Connexion avec : " + getUsername());
            while (!mapObjets.getChargementMap())
            {
                envoieMessage(Colors.clear + " Chargement des données patienter ");
                sleep(5000);
            }
        } catch (Exception e)
        {
            log.error(e.toString());
            this.lstConnexion.remove(this);
            closeAll();
        }
        envoieMessage(afficheChoix());
        litMess();

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

    /**
     * affiche les differents choix donner au client.
     */
    private String afficheChoix()
    {
        return "\033[H\033[2J" +
                Colors.PURPLE_UNDERLINED + "Bonjour " + this.getUsername() + " saisissez votre choix :\n\n" + Colors.reset +
                Colors.cyan + "1 / Consulter une partie spécifique et la visualiser pas à pas." + Colors.reset + "\n" +
                Colors.green + "2 / Trouver toutes les parties d’un joueur." + Colors.reset + "\n" +
                Colors.cyan + "3 / Consulter les 5 ouvertures les plus jouées" + Colors.reset + "\n" +
                Colors.green + "4 / Consulter les parties terminé avec n coups." + Colors.reset + "\n" +
                Colors.cyan + "5 / Lister les joueurs les plus actifs, les plus actifs sur une semaine, etc." + Colors.reset + "\n" +
                Colors.green + "6 / Calculer le joueur le plus fort au sens du PageRank" + Colors.reset + "\n" +
                Colors.cyan + "7 / Consulter le plus grand nombre de coups consécutifs cc qui soient communs à p parties\n" + Colors.reset +
                Colors.RED_BOLD_BRIGHT + "0 / Pour quitter le serveur" + Colors.reset;
//        Colors.green + "" + Colors.reset;
//        Colors.cyan + "" + Colors.reset;
    }

    /**
     * intercepte les messages envoyé par le client et les affiche dans la console.
     */
    private void litMess()
    {
        try
        {
            String mess;
            int nb = 0;
            while ((mess = (String) objectInputStream.readObject()) != null) // permet d'intercepter tout le message y compris si ya des sauts de ligne.
            {
                try
                {
                    nb = Integer.parseInt(mess);
                } catch (Exception e)
                {
                    log.warning("Le client n'envoie pas des nombres");
                }
                if (nb == 0)
                {
                    log.info(getUsername() + " à quitté le serveur");

                    this.lstConnexion.remove(this);
                    closeAll();
                    break;
                }
//                    System.out.println(this.socketClient.getPort() + "");
                System.out.println(mess);
                new InitChoix(nb, objectInputStream, writer, mapObjets);// gere toute la partie choix du client
            }

        } catch (Exception e)
        {
            connexionFailed();
        }
    }

    protected void closeAll()
    {
        try
        {
            this.socketClient.close();
            this.writer.close();
            this.objectInputStream.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public String getUsername()
    {
        return this.username.getUsername();
    }

    private void connexionFailed()
    {
        log.info(getUsername() + " à quitté le serveur");
        this.lstConnexion.remove(this);
        closeAll();
    }

}
