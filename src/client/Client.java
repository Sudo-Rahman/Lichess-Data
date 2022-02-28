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


package client;


import utils.Log;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;


public class Client
{
    private final String username;
    private Socket socket;
    private static final Log log = new Log();
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client(Socket socket, String username)
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
        String message;
        while (!this.socket.isClosed() && this.socket.isConnected())
        {
            Scanner sn = new Scanner(System.in);
            message = sn.nextLine();
            if (message.equals("-1"))
            {
                closeAll();
                log.info("Vous vous etes deconnecter");
            } else envoieMessage(message);
        }
    }


    /**
     * intercepte les messages envoyé par le serveur et les affiche dans la console.
     */
    private void litMess()
    {
        String mess;
        // permet d'intercepter tout le message y compris si ya des sauts de ligne tant que le serveur est connecté et qu'il y a un message.
        while (!this.socket.isClosed() && this.socket.isConnected())
        {
            try
            {
                if ((mess = bufferedReader.readLine()) != null) System.out.println(mess);
                else// si le serveur a été fermé par force et que son socket n'a pas été fermé il enverra que des null, donc on quitte le programme
                {
                    log.fatal("Le serveur ne repond pas !!");
                    closeAll();
                    System.exit(-1);
                }

            } catch (IOException e)
            {
                closeAll();
                break;
            }
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
        Thread ecouteurBluetooth = new Thread(this::litMess);
        ecouteurBluetooth.start();
    }

    private void closeAll()
    {
        try
        {
            this.socket.close();
            this.bufferedReader.close();
            this.bufferedWriter.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
