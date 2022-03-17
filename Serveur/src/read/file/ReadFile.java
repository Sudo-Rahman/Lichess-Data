package read.file;

import utils.Log;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ReadFile
{
    private File file;
    private Map<String, List<long[]>> nameMap;
    private Map<Integer, List<long[]>> eloMap;
    private Map<String, List<long[]>> utcDateMap;
    private Map<String, List<long[]>> utcTimeMap;
    private Map<String, List<long[]>> openningMap;

    private Log log = new Log();


    public ReadFile(String pathFile)
    {
        this.file = new File(pathFile);
        this.nameMap = new ConcurrentHashMap<>();
        this.eloMap = new ConcurrentHashMap<>();
        this.utcDateMap = new ConcurrentHashMap<>();
        this.utcTimeMap = new ConcurrentHashMap<>();
        this.openningMap = new ConcurrentHashMap<>();
    }

    public void read()
    {

        long tempsRecherche = System.currentTimeMillis();

        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1)
        {

        }
        System.out.println(name.substring(lastIndexOf));

        File f = new File(file.getAbsoluteFile().toString().replaceAll(name.substring(lastIndexOf), ".hasmap"));
        System.out.println(f.getName());
        try
        {
            ObjectInputStream or = new ObjectInputStream(new FileInputStream(f));
            if (f.exists())
            {
                System.out.println("exist");
                this.nameMap = (ConcurrentHashMap) or.readObject();
                for (long[] t : this.nameMap.get("savinka59"))
                {
                    System.out.println(Arrays.toString(t));
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        // variables pour connaitre les lignes de debut et fin d'une partie
        long lines = 0L;
        long lineDeb = 0L;
        long lineFin;
        int comptLigne = 0;

        //
        List<String> lstStr = new ArrayList<>();
        String str;
        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            while ((str = reader.readLine()) != null)
            {
                if (str.equals("")) {comptLigne++;} else {lstStr.add(str);}
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
                                    this.nameMap.put(buf[1], new ArrayList<>(Collections.singletonList(tab)));

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
//                                    log.warning("Elo inconnue");
                                }
                            }
                            case "1." -> {
                                if (this.openningMap.containsKey(buf[1]))
                                {
                                    this.openningMap.get(buf[1]).add(tab);
                                } else this.openningMap.put(buf[1], new ArrayList<>(Collections.singletonList(tab)));
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
        try
        {
            ObjectOutputStream bw = new ObjectOutputStream(new FileOutputStream("/home/rahman/Documents/GitHub/Projet-INFO-4B/others/lichess_db_standard_rated_2013-01.hasmap"));
            bw.writeObject(this.nameMap);
            bw.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        long tempsRecherch = System.currentTimeMillis();
        System.out.println((tempsRecherch - tempsRecherche) / 1000 + "  " + lines);
//        for (long[] t : this.nameMap.get("jclp1102"))
//        {
//            System.out.println(Arrays.toString(t));
//        }
//        for (long[] t : this.utcDateMap.get("31/07/2016"))
//        {
//            System.out.println(Arrays.toString(t));
//        }
//        System.out.println(System.currentTimeMillis() - tempsRecherch);
    }
}
