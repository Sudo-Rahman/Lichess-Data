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
import utils.Colors;
import utils.Log;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        List<File> lstFiles = new ArrayList<>();
        for (File file : Objects.requireNonNull(new File("/Users/sr-71/Documents/GitHub/Projet-INFO-4B/out/production/Projet-INFO-4B/").listFiles()))
        {
            if (file.isFile())
            {
                if (file.getName().contains("."))
                {
                    if (file.getName().split("\\.")[file.getName().split("\\.").length - 1].equals("pgn"))
                        lstFiles.add(file);

                }
            }
        }

        StringBuilder mes = new StringBuilder("Choisissez entre tous ces fichiers\n");
        int o = 1;
        for (File file : lstFiles)
        {
            mes.append(Colors.cyan).append(o).append(" / ").append(file.getName()).append("\n");
            o++;
        }
        mes.append(Colors.reset);

        int entrer = -1;
        while (entrer < 1 || entrer > lstFiles.size())
        {
            System.out.println(mes);
            try
            {
                entrer = sn.nextInt();
            } catch (NumberFormatException e) {System.out.println("Vous devez entrer un nombre");}
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
            Serveur serveur = new Serveur(s, maxClients, lstFiles.get(entrer - 1));
            serveur.run();
        } catch (IOException e)
        {
            log.fatal("Impossible de crée et lancer le serveur !!!");
            System.exit(-1);
        }
    }
}
