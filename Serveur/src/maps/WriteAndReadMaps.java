package maps;

import utils.Log;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WriteAndReadMaps implements Externalizable
{
    @Serial
    private static final long serialVersionUID = -8576293727662889893L;
    private final List<Map<Object,List<long[]>>> lstMaps;
    private final Log log = new Log();
    private Map<Object, List<Long>> nameMap;
    private Map<Object, List<Long>> eloMap;
    private Map<Object, List<Long>> utcDateMap;
    private Map<Object, List<Long>> utcTimeMap;
    private Map<Object, List<Long>> openningMap;

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

    private Map<Object, List<Long>> nbCoupsMap;
    private int ecriturePoucentage;
    private int lecturePoucentage;

    public WriteAndReadMaps()
    {
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
                if (field.get(this) instanceof Map) lstMaps.add((ConcurrentHashMap) field.get(this));
            } catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
    }

    public List<Map<Object,List<long[]>>> getLstMaps()
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
        this.lecturePoucentage = (int) (p * i);
        log.info("Lecture des Maps : " + this.lecturePoucentage + "%");
        log.info("Lecture des Maps términé en : " + (System.currentTimeMillis() - temps) / 1000 + " secondes");
    }
}
