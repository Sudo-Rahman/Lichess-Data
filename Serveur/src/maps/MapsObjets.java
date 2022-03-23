package maps;

import utils.Log;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public class MapsObjets
{
    private final File file;
    private final File fileMaps;
    private final Log log = new Log();
    private boolean chargementMap;
    private long nbParties;
    private WriteAndReadMaps warm;

    public MapsObjets(String pathFile)
    {
        this.file = new File(pathFile);


        this.chargementMap = false;

        this.nbParties = 0L;

        // on crée une objet fichier qui a le meme nom que le fichier classique mais avec l'extension .hasmap
        this.fileMaps = new File(file.getAbsoluteFile().toString().replaceAll(file.getName().substring(file.getName().lastIndexOf(".")), ".hashmap"));

        this.warm = new WriteAndReadMaps();
    }

    public long getNbParties()
    {
        return nbParties;
    }

    public WriteAndReadMaps getWarm()
    {
        return warm;
    }

    public File getFile()
    {
        return this.file;
    }

    public boolean getChargementMap()
    {
        return this.chargementMap;
    }

    public void charge()
    {
        try
        {
            if (Files.exists(Path.of(fileMaps.getAbsolutePath())))
            {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileMaps));
                this.warm = (WriteAndReadMaps) ois.readObject();
                this.chargementMap = this.warm.isChargementMapOk();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
            log.fatal("Impossible de lire les hasmap !!");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        if (!this.warm.isChargementMapOk())
        {
            CreeMap cr = new CreeMap(this.warm, file.getAbsolutePath());
            cr.cree();
            try
            {
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileMaps));
                this.chargementMap = cr.isCreeMapOk();
                oos.writeObject(this.warm);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        for (List<long[]> t : this.warm.getNameMap().values())
        {this.nbParties += t.size();}
        log.info("Il y a  " + this.nbParties / 2 + " parties dans le fichiers " + this.file.getName());
    }
}
