package recherche;

import utils.Log;

import java.io.*;

public abstract class Recherche
{
    private BufferedReader reader;
    private ObjectInputStream clientReader;
    private BufferedWriter clientWriter;
    public final Log log = new Log();

    public Recherche(String pathFile, ObjectInputStream clientReader, BufferedWriter clientWriter)
    {
        this.clientReader = clientReader;
        this.clientWriter = clientWriter;
        try
        {
            this.reader = new BufferedReader(new FileReader(pathFile));
        } catch (FileNotFoundException e)
        {
            log.error("Impossible de trouver le fichier !!");
        }
    }

    public abstract void cherche();

    public abstract String toString();

    public BufferedWriter getBufferedWriter()
    {
        return clientWriter;
    }

    public ObjectInputStream getClientReader()
    {
        return clientReader;
    }

    public BufferedReader getReader()
    {
        return reader;
    }
    public void envoieMessage(String message){
        try
        {
            this.clientWriter.write(message);
            this.clientWriter.newLine();
            this.clientWriter.flush();
        } catch (IOException e)
        {
            log.error("Impossible d'envoyer le message");
            System.exit(-1);
        }
    }

    public String litMess(){
        String mess = null;
        try {
            mess = (String) this.clientReader.readObject();
        } catch (Exception e)
        {
            log.error("Impossible de lire le message");
            System.exit(-1);
        }
        return mess;
    }
}
