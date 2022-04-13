package serveur;


import maps.CreeMapsOrRead;
import utils.Log;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


/**
 * Classe serveur, qui revoit les demandes de connexion des clients et crée un thread qu'il le gère.
 *
 * @author Yilmaz Rahman
 * @version 1.0
 * @date 22/02/2022
 */
public class Serveur
{

    private static final Log log = new Log();
    private final int maxClients;
    private final int nbDemandePerClient;
    private final List<ConnexionClient> lstConnexion;
    private final ServerSocket serverSocket;
    private final CreeMapsOrRead creeMapsOrRead;

    /**
     * @param serverSocket Socket du serveur.
     * @param maxClients   Nombre de clients maximum.
     * @param file         Fichier contenant les données.
     */
    public Serveur(ServerSocket serverSocket, int maxClients, File file)
    {
        this.lstConnexion = new ArrayList<>(maxClients);
        this.serverSocket = serverSocket;
        this.maxClients = maxClients;
        this.nbDemandePerClient = Runtime.getRuntime().availableProcessors() / maxClients;
        this.creeMapsOrRead = new CreeMapsOrRead(file);
        new Thread(creeMapsOrRead::charge).start();
    }

    /**
     * Lance le serveur.
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
                    ConnexionClient connexionClient = new ConnexionClient(client, this.nbDemandePerClient, this.lstConnexion, this.creeMapsOrRead);
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
     * méthode qui envoie le message en paramètre au client en paramètre
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
     * ferme le socket du serveur et le socket de tous les clients
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
