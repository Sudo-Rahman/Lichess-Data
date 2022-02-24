/*
 * Nom de classe : Client
 *
 * Description   : Cette classe jouera le role de client, il se connectera au serveur, et lui enverra des requetes.
 *
 * Version       : 1.0, 1.1
 *
 * Date          : 22/02/2022, 24/02/2022
 *
 * Copyright     : Yilmaz Rahman, Colliat Maxime
 *
 */


package com.black.ops;


import java.io.*;
import java.net.Socket;
import java.util.Scanner;


class Client
{
    private String username;
    private Socket socket;
    private static final Log log = new Log();
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    protected Client(Socket socket, String username)
    {
        this.username = username;
        try
        {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Lance la connexion au serveur, envoie et ecoute les messages du serveur.
     */
    public void startClient()
    {
        envoieMessage(this.username);
        listenSocket();
        while (this.socket.isConnected())
        {
            Scanner sn = new Scanner(System.in);
            envoieMessage(sn.nextLine());
        }
    }


    /**
     * intercepte les messages envoyé par le serveur et les affiche dans la console.
     */
    private void litMess()
    {
        try
        {
            while (this.socket.isConnected())
            {
                String mess = bufferedReader.readLine();
                while (mess != null)// permet d'intercepter tout le message y compris si ya des sauts de ligne.
                {
                    System.out.println(mess);
                    mess = bufferedReader.readLine();
                }
            }
            log.fatal("Le serveur ne repond pas !!");
            System.exit(-1);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * envoie le message en parametre au serveur.
     */
    private void envoieMessage(String message)
    {
        try
        {
            this.bufferedWriter.write(message);
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();

        } catch (IOException e)
        {
            log.fatal("Le serveur ne repond pas !!");
            System.exit(-1);
        }
    }

    /**
     * Thread qui ecoute les message venant du serveur, ainsi le client n'est pas bloqué uniquement sur l'écoute.
     */
    private void listenSocket()
    {
        Thread ecouteurBluetooth =new Thread(() ->
        {
            while (socket.isConnected()){
                litMess();
            }
        });
        ecouteurBluetooth.start();
    }
}
