package main;

import client.Client;

import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

public class MainClient
{
    public static void main(String args[])
    {
        Scanner sn = new Scanner(System.in);
        System.out.println("Donner votre username : ");
        String username = sn.next();

        System.out.println("Donner le port de connexion : ");
        int port = sn.nextInt();

        System.out.print("Donner l'adresse ip de connexion : ");
        InetAddress ipserveur;

        try
        {
//            ipserveur = InetAddress.getByName(sn.next());
//            Client client = new Client(new Socket(ipserveur,port),username);
            Client client = new Client(new Socket("localhost",1025),username);
            client.startClient();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
