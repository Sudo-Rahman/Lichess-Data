package maps;

import utils.Log;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapsObjet implements Externalizable
{
    @Serial
    private static final long serialVersionUID = -8576293727662889893L;
    private List<Map<Object, List<Long>>> lstMaps;
    private final Log log = new Log();
    private File file;
    private Map<Object, List<Long>> nameMap;
    private Map<Object, List<Long>> eloMap;
    private Map<Object, List<Long>> utcDateMap;
    private Map<Object, List<Long>> utcTimeMap;
    private Map<Object, List<Long>> openningMap;
    private Map<Object, List<Long>> nbCoupsMap;
    private int ecriturePoucentage;
    private int lecturePoucentage;


    private long nbParties;

    public MapsObjet(File file)
    {
        this.file = file;
        initMapObjet();
    }

    public MapsObjet() {
        initMapObjet();
    }

    private void initMapObjet(){

        this.ecriturePoucentage = 0;
        this.lecturePoucentage = 0;

        this.nameMap = new ConcurrentHashMap<>();
        this.eloMap = new ConcurrentHashMap<>();
        this.utcDateMap = new ConcurrentHashMap<>();
        this.utcTimeMap = new ConcurrentHashMap<>();
        this.openningMap = new ConcurrentHashMap<>();
        this.nbCoupsMap = new ConcurrentHashMap<>();


        // on cree une liste qui contient toutes les hasmap
        this.lstMaps = new ArrayList<>();
        for (Field field : this.getClass().getDeclaredFields())
        {
            try
            {
                if (field.get(this) instanceof Map<?,?>) {
                    lstMaps.add((ConcurrentHashMap) field.get(this));
                }
            } catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
    }

    public long getNbParties()
    {
        return nbParties;
    }
    public void setNbParties(long nbParties)
    {
        this.nbParties = nbParties;
    }

    public File getFile()
    {
        return file;
    }

    public Map<Object, List<Long>> getNameMap()
    {
        return nameMap;
    }

    public Map<Object, List<Long>> getEloMap()
    {
        return eloMap;
    }

    public Map<Object, List<Long>> getUtcDateMap()
    {
        return utcDateMap;
    }

    public Map<Object, List<Long>> getUtcTimeMap()
    {
        return utcTimeMap;
    }

    public Map<Object, List<Long>> getOpenningMap()
    {
        return openningMap;
    }

    public Map<Object, List<Long>> getNbCoupsMap()
    {
        return nbCoupsMap;
    }

    public List<Map<Object, List<Long>>> getLstMaps()
    {
        return lstMaps;
    }

    public boolean isWriteMapsOk()
    {
        return this.ecriturePoucentage == 100;
    }

    public boolean isChargementMapOk()
    {
        return this.lecturePoucentage == 100;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        long temps = System.currentTimeMillis();
        double p = (double) 100 / this.lstMaps.size();
        int i = 0;
        for (Map map : this.lstMaps)
        {
            this.ecriturePoucentage = (int) (p * i);
            i++;
            log.info("Ecriture des Maps : " + this.ecriturePoucentage + "%");
            out.writeObject(map);
        }
        out.writeObject(this.nbParties);
        out.writeObject(this.file);
        this.ecriturePoucentage = (int) (p * i);
        log.info("Ecriture des Maps : " + this.ecriturePoucentage + "%");
        log.info("Ecriture des Maps términé en : " + (System.currentTimeMillis() - temps) / 1000 + " secondes");
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        long temps = System.currentTimeMillis();
        double p = (double) 100 / this.lstMaps.size();
        int i = 0;
        for (Map map : lstMaps)
        {
            this.lecturePoucentage = (int) (p * i);
            i++;
            log.info("Lecture des Maps : " + this.lecturePoucentage + "%");
            map.putAll((ConcurrentHashMap) in.readObject());
        }
        this.nbParties = (long) in.readObject();
        this.file = (File) in.readObject();
        this.lecturePoucentage = (int) (p * i);
        log.info("Lecture des Maps : " + this.lecturePoucentage + "%");
        log.info("Lecture des Maps términé en : " + (System.currentTimeMillis() - temps) / 1000 + " secondes");
    }
}
