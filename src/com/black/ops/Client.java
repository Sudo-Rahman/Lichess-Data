/*
 * Nom de classe : Client
 *
 * Description   : Cette classe jouera le role de client, il se connectera au serveur, et lui enverra des requetes.
 *
 * Version       : 1.0
 *
 * Date          : 22/02/2022
 *
 * Copyright     : Yilmaz Rahman, Colliat Maxime
 *
 */


package com.black.ops;


import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;


class Client
{
    private int port;
    private InetAddress ipServer;
    private Socket socket;
    private final Log log;

    protected Client()
    {
        this.port = -1;
        this.log = new Log();

    }

    public void run()
    {
        initClient();
        while (true){

        }

    }

    /**
     * initialise tous les parametre pour le client et son bon fonctionnement, crée un socket avec les informations initialisé dans initPort() et initIp().
     */
    private void initClient()
    {
        initPort();
        initIp();
        try
        {
            this.socket = new Socket(this.ipServer, this.port);
        } catch (IOException e)
        {
            this.log.fatal("Connexion refusé, port ou ip mauvais");
            System.exit(-1);
        }
        System.out.println("Connexion réalisez avec succès au seveur");

    }

    /**
     * initialisation du port pour se connecter au serveur.
     */
    private void initPort()
    {
        while (65535 < this.port || this.port < 1024) // Un port est identifié par un entier de 1 à 65535. Par convention les 1024 premiers sont réservés pour des services standard
        {
            System.out.println("Donner le port de connexion (1025 à 65535) : ");
            Scanner sn = new Scanner(System.in);
            this.port = Integer.parseInt(sn.next());
        }
    }

    /**
     * initialisation de l'adresse ip de connexion.
     */
    private void initIp()
    {
        System.out.print("Donner l'adresse ip de connexion : ");
        Scanner sn = new Scanner(System.in);
        try
        {
            this.ipServer = InetAddress.getByName(sn.next());


        } catch (IOException e)
        {
            this.log.error("L'adresse ip n'est pas bon.");
            initIp();
        }
    }
}
