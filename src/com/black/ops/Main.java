/*
 * Nom de classe : Main
 *
 * Description   : La classe Main qui executera le programme.
 *
 * Version       : 1.0
 *
 * Date          : 22/02/2022
 *
 * Copyright     : Yilmaz Rahman, Colliat Maxime
 *
 */

package com.black.ops;

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
                Serveur serveur = new Serveur();
                serveur.run();
            }break;
            case 2:{
                Client client = new Client();
                client.run();
            }
        }
    }
}