/*
 * Nom de classe : ConnexionClient
 *
 * Description   : Cette classe traitera la connexion d'un client sur le serveur, il sera lancer par la classe Serveur.
 *
 * Version       : 1.0, 1.1
 *
 * Date          : 23/02/2022, 24/02/2022
 *
 * Copyright     : Yilmaz Rahman, Colliat Maxime
 *
 */

package serveur;


import choix.InitChoix;
import client.info.ClientInfo;
import maps.CreeMapsOrRead;
import utils.Colors;
import utils.Log;

import java.io.*;
import java.net.Socket;
import java.util.List;


class ConnexionClient extends Thread
{
    private static final Log log = new Log();
    private final List<ConnexionClient> lstConnexion;
    private final int nbMaxThread;
    private final CreeMapsOrRead creeMapsOrRead;
    private ClientInfo username;
    private Socket socketClient;
    private BufferedWriter writer;
    private ObjectInputStream objectInputStream;

    protected ConnexionClient(Socket clientSocket, int nbMaxThread, List<ConnexionClient> lst, CreeMapsOrRead creeMapsOrRead)
    {
        this.lstConnexion = lst;
        try
        {
            this.socketClient = clientSocket;
            this.writer = new BufferedWriter(new OutputStreamWriter(this.socketClient.getOutputStream()));
            this.objectInputStream = new ObjectInputStream(this.socketClient.getInputStream());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        this.nbMaxThread = nbMaxThread;
        this.creeMapsOrRead = creeMapsOrRead;
    }

    @Override
    public void run()
    {

        try
        {
            this.username = (ClientInfo) objectInputStream.readObject();
            System.out.println("Connexion avec : " + getUsername());
            while (!creeMapsOrRead.getChargementMap())
            {
                envoieMessage(Colors.clear + " Chargement des données patienter ");
                sleep(5000);
            }
        } catch (Exception e)
        {
            log.error(e.toString());
            this.lstConnexion.remove(this);
            closeAll();
        }
        envoieMessage("\033[H\033[2J" + Colors.PURPLE_UNDERLINED + "Bonjour " + this.getUsername() + " saisissez votre choix :\n" + Colors.reset);
        litMess();

    }


    /**
     * envoie le message en parametre au client.
     */
    private void envoieMessage(String message)
    {

        try
        {
            this.writer.write(message);
            this.writer.newLine();
            this.writer.flush();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * intercepte les messages envoyé par le client et les affiche dans la console.
     */
    private void litMess()
    {
        new InitChoix(0,"", objectInputStream, writer, creeMapsOrRead.getMapsObjet());// gere toute la partie choix du client
        try
        {
            String mess;
            int nb;
            while ((mess = (String) objectInputStream.readObject()) != null && this.socketClient.isConnected()) // permet d'intercepter tout le message y compris si ya des sauts de ligne.
            {
                try
                {
                    nb = Integer.parseInt(mess);
                } catch (Exception e)
                {
                    log.warning("Le client n'envoie pas des nombres");
                    nb = 0;
                }
                if (nb == -1)
                {
                    log.info(getUsername() + " à quitté le serveur");
                    this.lstConnexion.remove(this);
                    closeAll();
                    break;
                }
                //                    System.out.println(this.socketClient.getPort() + "");
                System.out.println(mess);
            }
        }
        catch (EOFException e)
        {
            log.error("connexion interompu avec "+username.getUsername());
            connexionFailed();
        } catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }


    protected void closeAll()
    {
        try
        {
            this.socketClient.close();
            this.writer.close();
            this.objectInputStream.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public String getUsername()
    {
        return this.username.getUsername();
    }

    private void connexionFailed()
    {
        log.info(getUsername() + " à quitté le serveur");
        this.lstConnexion.remove(this);
        closeAll();
    }

}
