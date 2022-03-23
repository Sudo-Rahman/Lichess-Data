/*
 * Nom de classe : MainServeur
 *
 * Description   : Main qui lance le serveur.
 *
 * Version       : 1.0
 *
 * Date          : 12/03/2022
 *
 * Copyright     : Yilmaz Rahman, Colliat Maxime
 *
 */

package main;

import serveur.Serveur;
import utils.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

public class MainServeur
{
    public static void main(String[] args)
    {
        Log log = new Log();

        Scanner sn = new Scanner(System.in);

        int maxClients = 0;
        int nbThreads = Runtime.getRuntime().availableProcessors() / 4; // ici on a choisit d'alouer 4 thread au mini pour le calcule pur et dur, pas les ecoutes,et envoies.
        while (maxClients < 1 || maxClients > nbThreads)
        {
            System.out.println("Donner le nombre max de clients en simultané sachant que le serveur alloue 4 threads " + "au minimum par clients pour les calcules pas les ecoutes, le nombre max de client est : " + nbThreads);
            maxClients = Integer.parseInt(sn.next());
        }

        //initialisation du port
        //        int port = 0;
        //        while (65535 < port || port < 1024) // Un port est identifié par un entier de 1 à 65535. Par convention les
        //        // 1024 premiers sont réservés pour des services standard
        //        {
        //            System.out.print("Donner le port de connexion (1025 à 65535) : ");
        //            port = Integer.parseInt(sn.next());
        //        }


        ServerSocket s;
        try
        {
            s = new ServerSocket(1025);
            //            s = new ServerSocket(port);
            Serveur serveur = new Serveur(s, maxClients);
            serveur.run();
        } catch (IOException e)
        {
            log.fatal("Impossible de crée et lancer le serveur !!!");
            System.exit(-1);
        }
    }
}
