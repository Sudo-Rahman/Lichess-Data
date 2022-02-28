/*
 * Nom de classe : Main
 *
 * Description   : La classe Main qui executera le programme.
 *
 * Version       : 1.0,1.1
 *
 * Date          : 22/02/2022, 24/02/2022
 *
 * Copyright     : Yilmaz Rahman, Colliat Maxime
 *
 */

package main;

import client.Client;
import serveur.Serveur;
import utils.Colors;
import utils.InitParametre;

import java.util.Scanner;


public class Main
{
    public static void main(String[] args)
    {
        int choix = -1;
        while (choix != 1 && choix != 2)
        {
            System.out.println(Colors.cyan + "1 / Serveur" + Colors.reset);
            System.out.println(Colors.green + "2 / Client" + Colors.reset);
            System.out.print("Votre choix : ");
            Scanner sn = new Scanner(System.in);
            choix = Integer.parseInt(sn.nextLine());
        }
        switch (choix)
        {
            case 1:
            {
                Serveur serveur = new Serveur(InitParametre.initServer(),InitParametre.initMaxClients());
                serveur.run();
            }
            break;
            case 2:
            {
                Scanner sn = new Scanner(System.in);
                System.out.print("Donner votre username : ");
                String username = sn.nextLine();
                Client client = new Client(InitParametre.initSocket(),username);
                client.startClient();
            }
        }
    }

}