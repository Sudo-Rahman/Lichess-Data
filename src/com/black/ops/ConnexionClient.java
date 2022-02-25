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

package com.black.ops;

import java.io.*;
import java.net.Socket;
import java.util.List;

class ConnexionClient extends Thread
{
    private final List<ConnexionClient> lstConnexion;
    private String username;
    private Socket socketClient;
    private final int nbMaxThread;
    private static final Log log = new Log();
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    protected ConnexionClient(Socket clientSocket, int nbMaxThread, List<ConnexionClient> lst)
    {
        this.lstConnexion = lst;
        try
        {
            this.socketClient = clientSocket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(this.socketClient.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.socketClient.getOutputStream()));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        this.nbMaxThread = nbMaxThread;
    }

    @Override
    public void run()
    {
        try
        {
            this.username = bufferedReader.readLine();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        System.out.println("Connexion avec : " + this.username);
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

            this.bufferedWriter.write(message);
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * affiche les differents choix donner au client.
     */
    private String afficheChoix()
    {
        return Colors.cyan + "1 / Consulter une partie spécifique et la visualiser pas à pas." + Colors.reset + "\n" +
                Colors.green + "2 / Trouver toutes les parties d’un joueur." + Colors.reset + "\n" +
                Colors.cyan + "3 / Consulter les 5 ouvertures les plus jouées" + Colors.reset + "\n" +
                Colors.green + "4 / Consulter les parties les plus courtes." + Colors.reset + "\n" +
                Colors.cyan + "5 / Lister les joueurs les plus actifs, les plus actifs sur une semaine, etc." + Colors.reset + "\n" +
                Colors.green + "6 / Calculer le joueur le plus fort au sens du PageRank" + Colors.reset + "\n" +
                Colors.cyan + "7 / Consulter le plus grand nombre de coups consécutifs cc qui soient communs à p parties" + Colors.reset;
//        System.out.println(Colors.green + "" + Colors.reset);
//        System.out.println(Colors.cyan + "" + Colors.reset);
    }

    /**
     * intercepte les messages envoyé par le client et les affiche dans la console.
     */
    private void litMess()
    {
        try
        {
                String mess;
                while ((mess = bufferedReader.readLine()) != null) // permet d'intercepter tout le message y compris si ya des sauts de ligne.
                {
                    if(Integer.parseInt(mess) == -1){
                        log.info(getUsername()+ " à quitté le serveur");
                        this.lstConnexion.remove(this);
                        closeSocket();
                        break;
                    }
//                    System.out.println(this.socketClient.getPort() + "");
                    System.out.println(mess);
                }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    protected void closeSocket(){
        try
        {
            this.socketClient.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public String getUsername()
    {
        return username;
    }
}
