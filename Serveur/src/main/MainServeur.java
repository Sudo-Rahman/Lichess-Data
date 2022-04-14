package main;

import serveur.Serveur;
import utils.Colors;
import utils.Log;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Classe Main pour le serveur.
 *
 * @author Yilmaz Rahman
 * @version 1.0
 * @date 02/04/2022
 */
public class MainServeur
{
    public static void main(String[] args)
    {
        Log log = new Log();

        //        on fait la liste des fichiers pgn dans le dossier data courant au programme.
        List<File> lstFiles = new ArrayList<>();
        if (!new File("data").exists()) new File("data").mkdir();

        for (File file : new File("data/").listFiles())
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
        // si il n'y a pas de fichier pgn dans le dossier data on quitte le programme.
        if (lstFiles.size() == 0)
        {
            log.fatal("Aucun fichier pgn trouvé dans le dossier data, veuillez ajouter un fichier pgn dans le dossier data pour pouvoir lancer le serveur.");
            System.exit(0);
        }

        Scanner sn = new Scanner(System.in);

        int maxClients = 0;
        int nbThreads = Runtime.getRuntime().availableProcessors() / 4; // ici on a choisi d'allouer de diviser par 4 le nombre de threads disponibles pour avoir le nombre de client max.
        while (maxClients < 1 || maxClients > nbThreads)
        {
            System.out.println("Donner le nombre max de clients en simultané, le nombre max de client est : " + nbThreads);
            maxClients = Integer.parseInt(sn.next());
        }


        StringBuilder mes = new StringBuilder("Choisissez entre tous ces fichiers\n");
        int o = 1;
        for (File file : lstFiles)
        {
            mes.append(Colors.cyan).append(o).append(" / ").append(file.getName()).append(" : ").append(new DecimalFormat("0.000").format(file.length() / 1073741824.0)).append(" go.").append("\n");// 1 073 741 824 octets = 1 Go
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

//        initialisation du port
        int port = 0;
        while (65535 < port || port < 1024) // Un port est identifié par un entier de 1 à 65535. Par convention les
        // 1024 premiers sont réservés pour des services standard
        {
            System.out.print("Donner le port de connexion (1025 à 65535) : ");
            port = Integer.parseInt(sn.next());
        }


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
