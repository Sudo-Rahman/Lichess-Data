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

    private Map<String, List<long[]>> nameMap;
    private Map<Integer, List<long[]>> eloMap;
    private Map<String, List<long[]>> utcDateMap;
    private Map<String, List<long[]>> utcTimeMap;
    private Map<String, List<long[]>> openningMap;

    private final Log log = new Log();

    public String getPathFile(){
        return this.file.getAbsolutePath();
    }

    public MapsObjets(String pathFile)
    {
        this.file = new File(pathFile);
        this.nameMap = new ConcurrentHashMap<>();
        this.eloMap = new ConcurrentHashMap<>();
        this.utcDateMap = new ConcurrentHashMap<>();
        this.utcTimeMap = new ConcurrentHashMap<>();
        this.openningMap = new ConcurrentHashMap<>();

        // on crée une objet fichier qui a le meme nom que le fichier classique mais avec l'extension .hasmap
        this.fileMaps = new File(file.getAbsoluteFile().toString().replaceAll(file.getName().substring(file.getName().lastIndexOf(".")), ".hasmap"));

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
                            case "1." -> {
                                if (this.openningMap.containsKey(buf[1]))
                                {
                                    this.openningMap.get(buf[1]).add(tab);
                                } else
                                    this.openningMap.put(buf[1], new ArrayList<>(Collections.singletonList(tab)));
                            }
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
        } catch (IOException e)
        {
            log.fatal("Impossible d'ecrire les maps dans le fichers");
        }

        log.info("Creation des maps effectué en  : " + (System.currentTimeMillis() - tempsRecherche) / 1000 + " secondes");
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
                log.info("Chargement des maps effectué en  : " + (System.currentTimeMillis() - tempsRecherche) / 1000 + " secondes");
                return true;
            } catch (Exception e)
            {
                log.error("Impossible d'importer les hasmap !!!");
            }
        }
        return false;
    }
}