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

package serveur;


import utils.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;



public class Serveur
{

    private final int maxClients;
    private final int nbThreadsPerClient;
    private final List<ConnexionClient> lstConnexion;
    private final ServerSocket serverSocket;
    private static final Log log = new Log();

    public Serveur(ServerSocket serverSocket, int maxClients)
    {
        this.lstConnexion = new ArrayList<>(maxClients);
        this.serverSocket = serverSocket;
        this.maxClients = maxClients;
        this.nbThreadsPerClient =Runtime.getRuntime().availableProcessors() / maxClients;
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
                    ConnexionClient connexionClient = new ConnexionClient(client, this.nbThreadsPerClient,this.lstConnexion);
                    this.lstConnexion.add(connexionClient);
                    connexionClient.start();
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            closeAllSocket();
        }
    }


    /**
     * méthode qui envoie le message en parametre au client en parametre
     */
    private void envoieMessage(String message, Socket socket)
    {
        try
        {
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            writer.print(message);
            writer.flush();
            writer.close();

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
            for (ConnexionClient connexionClient : this.lstConnexion)
            {
                connexionClient.closeAll();
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
