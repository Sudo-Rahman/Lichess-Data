package serveur;


import choix.InitChoix;
import client.info.ClientInfo;
import maps.CreeMapsOrRead;
import utils.Colors;
import utils.Log;

import java.io.*;
import java.net.Socket;
import java.util.List;


/**
 * Cette classe traitera la connexion d'un client sur le serveur, il sera lancer par la classe Serveur.
 *
 * @author Yilmaz Rahman
 * @version 1.0
 * @date 23/02/2022
 */
public class ConnexionClient extends Thread
{
    private static final Log log = new Log();
    private final List<ConnexionClient> lstConnexion;
    private final CreeMapsOrRead creeMapsOrRead;
    private int nbDemande;
    private ClientInfo clientInfo;
    private Socket socketClient;
    private BufferedWriter writer;
    private ObjectInputStream objectInputStream;

    /**
     * @param clientSocket      socket du client.
     * @param nbDemandePossible nombre de thread max.
     * @param lst               liste des connexions clients.
     * @param creeMapsOrRead    Instance de la classe CreeMapsOrRead.
     */
    public ConnexionClient(Socket clientSocket, int nbDemandePossible, List<ConnexionClient> lst, CreeMapsOrRead creeMapsOrRead)
    {
        this.lstConnexion = lst;
        try
        {
            this.socketClient = clientSocket;
            this.writer = new BufferedWriter(new OutputStreamWriter(this.socketClient.getOutputStream()));
            this.objectInputStream = new ObjectInputStream(this.socketClient.getInputStream());
        } catch (IOException e) {e.printStackTrace();}
        this.nbDemande = nbDemandePossible;
        this.creeMapsOrRead = creeMapsOrRead;
    }

    public int getNbDemande()
    {
        return nbDemande;
    }

    public void setNbDemande(int nbDemande)
    {
        this.nbDemande = nbDemande;
    }

    @Override
    public void run()
    {
        try
        {
            this.clientInfo = (ClientInfo) objectInputStream.readObject();
            System.out.println("Connexion avec : " + getClientInfo());
            while (!creeMapsOrRead.getChargementMap())
            {
                envoieMessage(Colors.clear + " Chargement des donn??es patienter ");
                sleep(5000);
            }
        } catch (Exception e)
        {
            log.error(e.toString());
            this.lstConnexion.remove(this);
            closeAll();
        }
        envoieMessage("\033[H\033[2J" + Colors.PURPLE_UNDERLINED + "Bonjour " +
                this.getClientInfo() + " saisissez votre choix :\n" + Colors.reset);
        litMess();
    }


    /**
     * Envoie le message en param??tre au client.
     *
     * @param message message ?? envoyer.
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
     * intercepte les messages envoy??s par le client et les affiche dans la console.
     */
    private void litMess()
    {
        new InitChoix(0, "", objectInputStream, writer, creeMapsOrRead.getMapsObjet(), this);// gere toute la partie choix du client
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
                    log.info(getClientInfo() + " ?? quitt?? le serveur");
                    this.lstConnexion.remove(this);
                    closeAll();
                    break;
                }
                System.out.println(mess);
            }
        } catch (EOFException e)
        {
            log.error("connexion interompu avec " + clientInfo.getUsername());
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

    /**
     * @return Le username du client.
     */
    public String getClientInfo()
    {
        return this.clientInfo.getUsername();
    }

    /**
     * Ferme les flux et le socket, du client et l'enl??ve de la liste des clients connect??e au serveur.
     */
    private void connexionFailed()
    {
        log.info(getClientInfo() + " ?? quitt?? le serveur");
        this.lstConnexion.remove(this);
        closeAll();
    }

}
