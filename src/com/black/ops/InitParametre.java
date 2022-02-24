/*
 * Nom de classe : InitParametre
 *
 * Description   : Classe qui contient des fonctions pour initialisé les ports, sockets et quelques attributs des classes Clients et Serveur.
 *
 * Version       : 1.0
 *
 * Date          : 24/02/2022
 *
 * Copyright     : Yilmaz Rahman, Colliat Maxime
 *
 */

package com.black.ops;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

class InitParametre
{
    private static final Log log = new Log();

    /**
     * initialise le socket du serveur.
     */
    protected static ServerSocket initServer()
    {
        int port = initPort();
        ServerSocket socketServeur;
        try
        {
            socketServeur = new ServerSocket(port);
            return socketServeur;
        } catch (IOException e)
        {
            log.error("Le port ne marche pas");
            System.exit(-1);
        }
        return null;
    }


    /**
     * initialise le socket de connexion au serveur.
     */
    protected static Socket initSocket()
    {
        int port = initPort();
        InetAddress ip = initIp();
        Socket socket;
        try
        {
            socket = new Socket(ip, port);
            return socket;
        } catch (IOException e)
        {
            log.error("Le socket n'a pas pu etres initialisé");
            System.exit(-1);
        }
        return null;
    }

    /**
     * initialisation du nombre maximal de clients que le serveur principale pourra taiter en meme temps.
     */
    protected static int initMaxClients()
    {
        int maxClients = 0;
        int nbThreads = Runtime.getRuntime().availableProcessors() / 4; // ici on a choisit d'alouer 4 thread au mini pour le calcule pur et dur, pas les ecoutes,et envoies.
        while (maxClients < 1 || maxClients > nbThreads)
        {
            System.out.println("Donner le nombre max de clients en simultané sachant que le serveur alloue 4 threads au minimum par clients, le nombre max de client est : " + nbThreads);
            Scanner sn = new Scanner(System.in);
            maxClients = Integer.parseInt(sn.next());
        }
        return maxClients;
    }


    /**
     * initialisation d'un port pour un socket.
     */
    protected static int initPort()
    {
        int port = 0;
        while (65535 < port || port < 1024) // Un port est identifié par un entier de 1 à 65535. Par convention les 1024 premiers sont réservés pour des services standard
        {
            System.out.print("Donner le port de connexion (1025 à 65535) : ");
            Scanner sn = new Scanner(System.in);
            port = Integer.parseInt(sn.next());
        }
        return port;
    }

    /**
     * initialisation de l'adresse ip de connexion.
     */
    protected static InetAddress initIp()
    {
        InetAddress ipserveur;
        System.out.print("Donner l'adresse ip de connexion : "); Scanner sn = new Scanner(System.in);
        try
        {
            ipserveur = InetAddress.getByName(sn.next());
            return ipserveur;

        } catch (UnknownHostException e)
        {
            log.error("L'adresse ip n'est pas bon.");
            return initIp();
        }
    }
}
