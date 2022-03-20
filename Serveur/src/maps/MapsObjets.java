package maps;

import utils.Log;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MapsObjets
{
    private final File file;
    private final File fileMaps;
    private boolean chargementMap;


    // acceseurs
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
        return this.nbCoupsMap;
    }

    public void setNbCoupsMap(Map<Integer, List<long[]>> nbCoupsMap)
    {
        this.nbCoupsMap = nbCoupsMap;
    }

    private Map<String, List<long[]>> nameMap;
    private Map<Integer, List<long[]>> eloMap;
    private Map<String, List<long[]>> utcDateMap;
    private Map<String, List<long[]>> utcTimeMap;
    private Map<String, List<long[]>> openningMap;
    private Map<Integer, List<long[]>> nbCoupsMap;

    private final Log log = new Log();

    public File getFile()
    {
        return this.file;
    }

    public boolean getChargementMap()
    {
        return this.chargementMap;
    }


    public MapsObjets(String pathFile)
    {
        this.file = new File(pathFile);
        this.nameMap = new ConcurrentHashMap<>();
        this.eloMap = new ConcurrentHashMap<>();
        this.utcDateMap = new ConcurrentHashMap<>();
        this.utcTimeMap = new ConcurrentHashMap<>();
        this.openningMap = new ConcurrentHashMap<>();
        this.nbCoupsMap = new ConcurrentHashMap<>();

        this.chargementMap = false;

        // on crée une objet fichier qui a le meme nom que le fichier classique mais avec l'extension .hasmap
        this.fileMaps = new File(file.getAbsoluteFile().toString().replaceAll(file.getName().substring(file.getName().lastIndexOf(".")), ".hasmap"));
    }

    public void charge()
    {
        if (!chargementMap()) createMaps();
    }


    private void createMaps()
    {

        long tempsRecherche = System.currentTimeMillis();


        // variables pour connaitre les lignes de debut et fin d'une partie
        long lines = 0L;
        long lineDeb = 0L;
        long lineFin;
        int comptLigne = 0;


        List<String> lstStr = new ArrayList<>();
        String str;
        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            while ((str = reader.readLine()) != null)
            {
                if (str.equals("")) comptLigne++;
                else lstStr.add(str);
                if (comptLigne == 2)
                {
                    lineFin = lines + 1;
                    long[] tab = new long[2];
                    tab[0] = lineDeb;
                    tab[1] = lineFin;
                    for (String string : lstStr)
                    {
                        String[] buf = string.replaceAll("[\\[\\]]", "").split("\"");
                        buf[0] = buf[0].replaceAll(" ", "");
                        switch (buf[0])
                        {
                            case "White", "Black" -> {
                                if (this.nameMap.containsKey(buf[1])) this.nameMap.get(buf[1]).add(tab);
                                else
                                    this.nameMap.put(buf[1], new ArrayList<>(Collections.singleton(tab)));

                            }
                            case "Site" -> {}
                            case "Result" -> {}
                            case "UTCDate" -> {
                                String utcDate = null;
                                try
                                {
                                    utcDate = new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("yy.MM.dd").parse(buf[1]));
                                } catch (ParseException e)
                                {
                                    log.error("Impossible de parser la date !!");
                                }
                                if (this.utcDateMap.containsKey(utcDate)) this.utcDateMap.get(utcDate).add(tab);
                                else this.utcDateMap.put(utcDate, new ArrayList<>(Collections.singletonList(tab)));

                            }
                            case "UTCTime" -> {
                                if (this.utcTimeMap.containsKey(buf[1])) this.utcTimeMap.get(buf[1]).add(tab);
                                else this.utcTimeMap.put(buf[1], new ArrayList<>(Collections.singletonList(tab)));
                            }
                            case "WhiteElo", "BlackElo" -> {
                                try
                                {
                                    int elo = Integer.parseInt(buf[1]);
                                    if (this.eloMap.containsKey(elo))
                                    {
                                        this.eloMap.get(elo).add(tab);
                                    } else this.eloMap.put(elo, new ArrayList<>(Collections.singletonList(tab)));
                                } catch (NumberFormatException e)
                                {
                                    log.warning("Elo inconnue");
                                }
                            }
                        }
                        if (string.split(" ")[0].equals("1."))
                        {
                            //map pour les nombre de coups
                            List<String> lst = new ArrayList<>(List.of(lstStr.get(lstStr.size() - 1).split("[{}]")));
                            lst.removeIf(strr -> strr.contains("%eval"));
                            lst = new ArrayList<>(List.of(String.join("", lst).split(" ")));
                            lst.removeIf(strr -> strr.equals("") || strr.contains("."));
                            // on enleve -1 car le dernier "coup" est le resultat
                            if (this.nbCoupsMap.containsKey(lst.size() - 1))
                            {
                                this.nbCoupsMap.get(lst.size() - 1).add(tab);
                            } else
                                this.nbCoupsMap.put(lst.size() - 1, new ArrayList<>(Collections.singletonList(tab)));

                            //map pour les ouvertures
                            if (this.openningMap.containsKey(string.split(" ")[1]))
                            {
                                this.openningMap.get(string.split(" ")[1]).add(tab);
                            } else
                                this.openningMap.put(string.split(" ")[1], new ArrayList<>(Collections.singletonList(tab)));
                        }

                    }
                    comptLigne = 0;
                    lineDeb = lines + 2;
                    lstStr.clear();
                }
                lines++;
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.fileMaps)))
        {
            oos.writeObject(this.nameMap);
            oos.flush();
            oos.writeObject(this.eloMap);
            oos.flush();
            oos.writeObject(this.utcDateMap);
            oos.flush();
            oos.writeObject(this.utcTimeMap);
            oos.flush();
            oos.writeObject(this.openningMap);
            oos.flush();
            oos.writeObject(this.nbCoupsMap);
            oos.flush();
            log.info("Creation des maps effectué en  : " + (System.currentTimeMillis() - tempsRecherche) / 1000 + " secondes");
            this.chargementMap = true;
        } catch (IOException e)
        {
            log.fatal("Impossible d'ecrire les maps dans le fichers");
        }
    }

    private boolean chargementMap()
    {
        long tempsRecherche = System.currentTimeMillis();

        // on charge les hasmap dans leur variable
        if (this.fileMaps.exists())
        {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.fileMaps)))
            {
                this.nameMap = (ConcurrentHashMap) ois.readObject();
                this.eloMap = (ConcurrentHashMap) ois.readObject();
                this.utcDateMap = (ConcurrentHashMap) ois.readObject();
                this.utcTimeMap = (ConcurrentHashMap) ois.readObject();
                this.openningMap = (ConcurrentHashMap) ois.readObject();
                this.nbCoupsMap = (ConcurrentHashMap) ois.readObject();
                log.info("Chargement des maps effectué en  : " + (System.currentTimeMillis() - tempsRecherche) / 1000 + " secondes");
                this.chargementMap = true;
                return true;
            } catch (Exception e)
            {
                log.error("Impossible d'importer les hasmap !!!");
            }
        }
        return false;
    }
}