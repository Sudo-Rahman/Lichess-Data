/*
 * Nom de classe : Serveur
 *
 * Description   : Cette classe est un serveur qui recevera des requetes des Clients et les traitera.
 *
 * Version       : 1.0
 *
 * Date          : 22/02/2022
 *
 * Copyright     : Yilmaz Rahman, Colliat Maxime
 *
 */

package com.black.ops;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


class Serveur
{

    private int port;
    private int maxClients;
    private int nbThreadsPerClient;
    private List<Client> clients;
    private ServerSocket socketServeur;
    private final Log log;

    protected Serveur()
    {
        this.clients = new ArrayList<>(maxClients);
        this.port = -1;
        this.maxClients = -1;
        this.log = new Log();
    }

    /**
     * methode run qui lance le parametrage du serveur, ensuite traite les clients et leurs requetes.
     */
    public void run()
    {
        initServer();
        try
        {
            while (true)
            {
                Socket socketClient = this.socketServeur.accept();
                System.out.println("Connexion avec : " + socketClient.getInetAddress());
                envoieMessage(afficheChoix(), socketClient);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * initialise tous les parametre pour le serveur et son bon fonctionnement crée un socketServer avec les informations initialisé dans initPort() et initIp().
     */
    private void initServer()
    {
        initPort();
        initMaxClients();
        try
        {
            this.socketServeur = new ServerSocket(this.port);
            this.log.info("Lancement du serveur sur le port " + this.port);
        } catch (IOException e)
        {
            this.log.error("Le port ne marche pas");
        }
    }

    /**
     * initialisation du port du serveur.
     */
    private void initPort()
    {
        this.port = 1025;
        while (65535 < this.port || this.port < 1024) // Un port est identifié par un entier de 1 à 65535. Par convention les 1024 premiers sont réservés pour des services standard
        {
            System.out.println("Donner le port de connexion (1025 à 65535) : ");
            Scanner sn = new Scanner(System.in);
            this.port = Integer.parseInt(sn.next());
        }
    }

    /**
     * initialisation du nombre maximal de clients que le serveur pourra taiter en meme temps.
     */
    private void initMaxClients()
    {
        int nbThreads = Runtime.getRuntime().availableProcessors() / 4; // ici on a choisit d'alouer 4 thread au mini pour un client
        while (this.maxClients < 1 || this.maxClients > nbThreads)
        {
            System.out.println("Donner le nombre max de clients en simultané sachant que le serveur alloue 4 threads au minimum par clients, le nombre max de client est : " + nbThreads);
            Scanner sn = new Scanner(System.in);
            this.maxClients = Integer.parseInt(sn.next());
        }
        this.nbThreadsPerClient = Runtime.getRuntime().availableProcessors() / this.maxClients;
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
}
