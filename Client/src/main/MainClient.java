package main;

import client.Client;
import client.info.ClientInfo;
import utils.Log;

import java.net.Socket;
import java.util.Scanner;

/**
 * Main qui lance le client.
 *
 * @author Yilmaz Rahman
 * @version 1.0
 * @date 12/03/2022
 */
public class MainClient
{
    public static void main(String[] args)
    {
        Log log = new Log();
        Scanner sn = new Scanner(System.in);
        System.out.println("Donner votre username : ");
        String username = sn.next();

        //initialisation du port
        //        int port = 0;
        //        while (65535 < port || port < 1024) // Un port est identifié par un entier de 1 à 65535. Par convention les
        //        // 1024 premiers sont réservés pour des services standard
        //        {
        //            System.out.print("Donner le port de connexion (1025 à 65535) : ");
        //            port = Integer.parseInt(sn.next());
        //        }

        //        System.out.print("Donner l'adresse ip de connexion : ");
        //        InetAddress ipserveur;

        try
        {
            //            ipserveur = InetAddress.getByName(sn.next());
            //            Client client = new Client(new Socket(ipserveur,port),username);
            Client client = new Client(new Socket("localhost", 1025), new ClientInfo(username));
            client.startClient();
        } catch (Exception e)
        {
            log.fatal("Impossible de lancer la conenxion a serveur !!!");
            System.exit(-1);

        }
    }
}
