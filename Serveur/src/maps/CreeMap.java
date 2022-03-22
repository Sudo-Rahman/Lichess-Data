package maps;


import utils.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreeMap
{

    private final String file;

    private final WriteAndReadMaps warm;

    private long nbLines;
    private long nbLinesParThread;
    private final int nbThreads = Runtime.getRuntime().availableProcessors() / 2;

    public boolean isCreeMapOk()
    {
        return creeMapOk;
    }

    private boolean creeMapOk;

    private final Log log;

    public CreeMap(WriteAndReadMaps mo, String path)
    {
        this.file = path;
        this.warm = mo;
        this.creeMapOk = false;
        log = new Log();
    }

    public void cree(){
        try
        {
            this.nbLines = Files.lines(Path.of(this.file)).count();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        this.nbLinesParThread = this.nbLines / this.nbThreads;
        createMaps();
    }


    private void createMaps()
    {
        long tempsRecherche = System.currentTimeMillis();

        List<Thread> lstThreads = new ArrayList<>();

        for (int i = this.nbThreads - 1; i >= 0; i--)
        {
            int I = i;
            Thread t = new Thread(() -> calcule(nbLinesParThread * I));
            lstThreads.add(t);
            t.start();
        }
        for (Thread t : lstThreads)
        {
            try
            {
                t.join();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        this.creeMapOk = true;
        log.info("Creation des maps effectué en  : " + (System.currentTimeMillis() - tempsRecherche) / 1000 + " " + "secondes");
        System.out.println(this.warm.getNameMap().size() + " - " + this.warm.getEloMap().size() + " - " + this.warm.getUtcDateMap().size() + " " + "- " + this.warm.getUtcTimeMap().size()  + " - " + this.warm.getOpenningMap().size()  + " - " + this.warm.getNbCoupsMap().size() );
    }

    private void calcule(long deb)
    {
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new FileReader(file));
        } catch (Exception e)
        {
            System.exit(-1);
            log.error("Fichier existe pas");
        }

        //        System.out.println(this.nbLignesParThread + deb);
        // variables pour connaitre les lignes de debut et fin d'une partie
        long lines = 0L;
        long lineDeb = 0L;
        long lineFin;
        int comptLigne = 0;


        List<String> lstStr = new ArrayList<>();
        String str;
        try
        {
            while (lines <= deb)
            {
                reader.readLine();
                lines++;
            }
            while ((str = reader.readLine()) != null && lines <= deb + nbLinesParThread + 16)
            {
                if (str.equals("") && lines < 16)
                {
                    comptLigne = 0;
                    lstStr.clear();
                }
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
                                if (this.warm.getNameMap().containsKey(buf[1])) this.warm.getNameMap().get(buf[1]).add(tab);
                                else
                                    this.warm.getNameMap().putIfAbsent(buf[1], Collections.synchronizedList(new ArrayList<>(Collections.singleton(tab))));
                            }
                            case "Site" -> {}
                            case "Result" -> {}
                            case "UTCDate" -> {
                                String utcDate = null;
                                try
                                {
                                    utcDate = new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("yy" + ".MM.dd").parse(buf[1]));
                                } catch (ParseException e)
                                {
                                    log.error("Impossible de parser la date !!");
                                }
                                if (this.warm.getUtcDateMap().containsKey(utcDate)) this.warm.getUtcDateMap().get(utcDate).add(tab);
                                else
                                    this.warm.getUtcDateMap().put(utcDate, Collections.synchronizedList(new ArrayList<>(Collections.singletonList(tab))));
                            }
                            case "UTCTime" -> {
                                if (this.warm.getUtcTimeMap().containsKey(buf[1])) this.warm.getUtcTimeMap().get(buf[1]).add(tab);
                                else
                                    this.warm.getUtcTimeMap().put(buf[1], Collections.synchronizedList(new ArrayList<>(Collections.singletonList(tab))));
                            }
                            case "WhiteElo", "BlackElo" -> {
                                try
                                {
                                    int elo = Integer.parseInt(buf[1]);
                                    if (this.warm.getEloMap().containsKey(elo)) {this.warm.getEloMap().get(elo).add(tab);}
                                    else
                                        this.warm.getEloMap().put(elo, Collections.synchronizedList(new ArrayList<>(Collections.singletonList(tab))));
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
                            if (this.warm.getNbCoupsMap().containsKey(lst.size() - 1))
                            {this.warm.getNbCoupsMap().get(lst.size() - 1).add(tab);}
                            else
                                this.warm.getNbCoupsMap().put(lst.size() - 1, Collections.synchronizedList(new ArrayList<>(Collections.singletonList(tab))));

                            //map pour les ouvertures
                            if (this.warm.getOpenningMap().containsKey(string.split(" ")[1]))
                            {this.warm.getOpenningMap().get(string.split(" ")[1]).add(tab);}
                            else
                                this.warm.getOpenningMap().put(string.split(" ")[1], Collections.synchronizedList((new ArrayList<>(Collections.singletonList(tab)))));
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
    }
}
