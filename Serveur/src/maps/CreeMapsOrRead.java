package maps;

import utils.Log;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * Classe qui crée MapsObjet si le fichier .hshmap le stockant n'existe pas ou lit MapsObjet dans le fichier .hashmap.
 *
 * @author Yilmaz Rahaman
 * @version 1.0
 * @date 02/04/2022
 */
public class CreeMapsOrRead
{
    private final File file;
    private final File fileMaps;
    private final Log log = new Log();
    private boolean chargementMap;
    private MapsObjet mapsObjet;

    /**
     * @param file Fichier de données.
     */
    public CreeMapsOrRead(File file)
    {
        this.file = file;

        this.chargementMap = false;

        // on crée un objet File qui a le meme nom que le fichier classique, mais avec l'extension .hashmap.
        this.fileMaps = new File(file.getAbsoluteFile().toString().replaceAll(file.getName().substring(file.getName().lastIndexOf(".")), ".hashmap"));

        this.mapsObjet = new MapsObjet(this.file);
    }

    public MapsObjet getMapsObjet()
    {
        return mapsObjet;
    }


    public boolean getChargementMap()
    {
        return this.chargementMap;
    }

    /**
     * Si le fichier .hashmap existe, on charge les données dans l'objet MapsObjet.
     * Sinon, on crée l'objet MapsObjet et on l'écrit dans le fichier .hashmap.
     *
     */
    public void charge()
    {
        try
        {
            if (Files.exists(Path.of(fileMaps.getAbsolutePath())))
            {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileMaps));
                this.mapsObjet = (MapsObjet) ois.readObject();
                this.chargementMap = this.mapsObjet.isChargementMapOk();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
            log.fatal("Impossible de lire les hasmap !!");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        if (!this.mapsObjet.isChargementMapOk())
        {
            CreeMap cr = new CreeMap(this.mapsObjet, file, 0L, file.length());
            cr.cree();
            try
            {
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileMaps));
                this.chargementMap = cr.isCreeMapOk();
                oos.writeObject(this.mapsObjet);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        log.info("Il y a  " + this.mapsObjet.getNbParties() + " parties dans le fichiers " + this.file.getName());
        System.gc();
    }
}
