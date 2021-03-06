package client;

import client.info.ClientInfo;
import utils.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;


/**
 * Classe qui jouera le role de client, il se connectera au serveur, et lui enverra des requêtes.
 *
 * @author Yilmaz Rahman
 * @version 1.0
 * @date 22/02/2022
 */
public class Client
{
    private static final Log log = new Log();
    private final ClientInfo clientInfo;
    private Socket socket;
    private BufferedReader bufferedReader;
    private ObjectOutputStream objectOutputStream;

    public Client(Socket socket, ClientInfo info)
    {
        this.clientInfo = info;
        try
        {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Lance la connexion au serveur, envoie et écoute les messages du serveur.
     */
    public void startClient()
    {
        try
        {
            objectOutputStream.writeObject(this.clientInfo);
        } catch (IOException e)
        {
            log.error("L'objet n'a pas put etre envoyé !!");
        }
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
     * intercepte les messages envoyés par le serveur et les affiche dans la console.
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
     * Envoie le message en paramètre au serveur.
     */
    private void envoieMessage(String message)
    {
        try
        {
            this.objectOutputStream.writeObject(message);

        } catch (IOException e)
        {
            log.fatal("Le serveur ne repond pas !!");
            System.exit(-1);
        }
    }

    /**
     * Thread qui écoute les messages venant du serveur, ainsi le client n'est pas bloqué uniquement sur l'écoute.
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
            this.objectOutputStream.close();
        } catch (IOException e)
        {
            System.exit(-1);
        }
    }
}