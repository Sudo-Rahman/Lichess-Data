/*
 * Nom de classe : ServeurThreadClient
 *
 * Description   : Cette classe traitera un client, il sera lancer par la classe Serveur pour gerer plusieurs clients a la fois.
 *
 * Version       : 1.0
 *
 * Date          : 23/02/2022
 *
 * Copyright     : Yilmaz Rahman, Colliat Maxime
 *
 */

package com.black.ops;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

class ServeurThreadClient extends Thread
{
    private Socket socketClient;
    private int nbMaxThread;
    private Log log;

    protected ServeurThreadClient(Socket clientSocket, int nbMaxThread)
    {
        this.socketClient = clientSocket;
        this.nbMaxThread = nbMaxThread;
        this.log = new Log();
    }

    @Override
    public void run()
    {
        System.out.println("Connexion avec : " + this.socketClient.getInetAddress());
        envoieMessage(afficheChoix(), socketClient);
        int choix = -1;
        while (choix < 1 || choix > 7)
        {
//            envoieMessage("wsh",socketClient);
        }
        log.debug(choix + " " + this.nbMaxThread);

    }


    /**
     * méthode qui envoie le message en parametre au client en parametre
     */
    private void envoieMessage(String message, Socket socketClient)
    {
        try
        {

            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
            PrintWriter pw = new PrintWriter(wr, true);
            pw.println(message);

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * affiche les differents choix donner aux clients
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
     * méthode qui intercepte les message envoyé par le client et les affiche dans la console.
     */
    private void litMess()
    {
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(this.socketClient.getInputStream()));

            String mess = in.readLine();
            while (mess != null)
            {
                System.out.println(mess);
                mess = in.readLine();
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
