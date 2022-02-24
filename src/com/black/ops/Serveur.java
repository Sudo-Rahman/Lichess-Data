/*
 * Nom de classe : Serveur
 *
 * Description   : Cette classe est un serveur qui recevera des requetes des Clients et les traitera.
 *
 * Version       : 1.0, 1.1
 *
 * Date          : 22/02/2022, 24/02/2022
 *
 * Copyright     : Yilmaz Rahman, Colliat Maxime
 *
 */

package com.black.ops;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


class Serveur
{

    private final int maxClients;
    private int nbThreadsPerClient;
    private List<ConnexioClient> lstConnexion;
    private ServerSocket serverSocket;
    private static final Log log = new Log();

    protected Serveur(ServerSocket serverSocket, int maxClients)
    {
        this.lstConnexion = new ArrayList<>(maxClients);
        this.serverSocket = serverSocket;
        this.maxClients = maxClients;
    }

    /**
     * methode run qui lance le parametrage du serveur, ensuite traite les clients et leurs requetes.
     */
    public void run()
    {
        log.info("Lancement du serveur sur le port " + this.serverSocket.getLocalPort());
        try
        {
            while (!this.serverSocket.isClosed())
            {
                Socket client = this.serverSocket.accept();
                if (this.maxClients <= this.lstConnexion.size())
                {
                    envoieMessage("Trop de clients, retentez dans quelques instants", client);
                    client.close();
                } else
                {
                    log.debug(client.toString());
                    ConnexioClient connexioClient = new ConnexioClient(client, this.nbThreadsPerClient);
                    this.lstConnexion.add(connexioClient);
                    connexioClient.start();
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            closeAllSocket();
        }
    }


    /**
     * méthode qui envoie le message en parametre au serveur en parametre
     */
    private void envoieMessage(String message, Socket socket)
    {
        try
        {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * ferme le socket du serveur et le socket de tout les clients
     */
    public void closeAllSocket()
    {
        try
        {
            for (ConnexioClient connexioClient : this.lstConnexion)
            {
                connexioClient.closeSocket();
            }
            if (this.serverSocket != null)
            {
                this.serverSocket.close();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
