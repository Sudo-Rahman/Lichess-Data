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
    private final List<Map> lstMaps;

    private Map<String, List<long[]>> nameMap;
    private Map<Integer, List<long[]>> eloMap;
    private Map<String, List<long[]>> utcDateMap;
    private Map<String, List<long[]>> utcTimeMap;
    private Map<String, List<long[]>> openningMap;
    private Map<Integer, List<long[]>> nbCoupsMap;

    private int ecriturePoucentage;
    private int lecturePoucentage;

    private final Log log = new Log();


    public List<Map> getLstMaps()
    {
        return lstMaps;
    }

    public Map<String, List<long[]>> getNameMap()
    {
        return nameMap;
    }

    public Map<Integer, List<long[]>> getEloMap()
    {
        return eloMap;
    }

    public Map<String, List<long[]>> getUtcDateMap()
    {
        return utcDateMap;
    }

    public Map<String, List<long[]>> getUtcTimeMap()
    {
        return utcTimeMap;
    }

    public Map<String, List<long[]>> getOpenningMap()
    {
        return openningMap;
    }

    public Map<Integer, List<long[]>> getNbCoupsMap()
    {
        return nbCoupsMap;
    }

    public boolean isWriteMapsOk()
    {
        return this.ecriturePoucentage == 100;
    }

    public boolean isChargementMapOk()
    {
        return this.lecturePoucentage == 100;
    }

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
