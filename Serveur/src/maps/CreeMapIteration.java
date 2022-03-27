package maps;

import utils.Log;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

public class CreeMapIteration
{
    private final File file;

    private final MapsObjet mapsObjet;
    private final int nbThreads = Runtime.getRuntime().availableProcessors() / 4;
    private final Log log;
    private long nbOctetsLu;
    private boolean creeMapOk;

    private final List<Long> lstPosParties;
    private int compteurPourList;

    public CreeMapIteration(MapsObjet mo, File file, List<Long> lstPosParties)
    {
        this.file = file;
        this.mapsObjet = mo;
        this.creeMapOk = false;

        this.lstPosParties = lstPosParties;
        this.compteurPourList = 0;

        log = new Log();
    }

    public boolean isCreeMapOk()
    {
        return creeMapOk;
    }

    public void cree()
    {
        this.nbOctetsLu = 0;
        createMaps();
    }

    private synchronized long getPos()
    {
        if (lstPosParties.size() > compteurPourList)
        {
            compteurPourList++;
            return lstPosParties.get(compteurPourList - 1);
        } else {return -1L;}
    }


    private void createMaps()
    {
        long tempsRecherche = System.currentTimeMillis();

        List<Thread> lstThreads = new ArrayList<>();
        for (int i = 0; i < this.nbThreads; i++)
        {
            Thread t = new Thread(() ->
            {
                try
                {
                    calcule();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            });
            lstThreads.add(t);
            t.setPriority(Thread.MAX_PRIORITY);
            t.start();
        }
        Thread th = new Thread(this::afficheOctetLu);
        th.start();
        try
        {
            for (Thread t : lstThreads)
            {
                t.join();

            }
            this.creeMapOk = true;
            th.join();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        long partie = 0;
        for (Map.Entry<Object, List<Long>> element : this.mapsObjet.getNameMap().entrySet())
        {
            partie += element.getValue().size();
        }
        this.mapsObjet.setNbParties(partie / 2);
        log.info("Creation des maps effectué en  : " + (System.currentTimeMillis() - tempsRecherche) / 1000 + " secondes");
    }

    private void calcule() throws IOException
    {
        long pos;
        // variables pour connaitre l'octet de debut et fin d'une partie
        Long octetDeb;

        List<String> lstStr;

        while (true)
        {
            if((pos = getPos())==-1) break;
            octetDeb = pos;

            lstStr = getLstStringPartieInFile(pos);

            for (String string : lstStr)
            {
                String[] buf = string.replaceAll("[\\[\\]]", "").split("\"");
                buf[0] = buf[0].replaceAll(" ", "");
                switch (buf[0])
                {
                    case "White", "Black" -> {
//                        System.out.println(octetDeb + " " + lstStr + "\n\n");
                        if (this.mapsObjet.getNameMap().containsKey(buf[1]))
                        {
                            this.mapsObjet.getNameMap().get(buf[1]).add(octetDeb);

                        } else
                            this.mapsObjet.getNameMap().putIfAbsent(buf[1], Collections.synchronizedList(new ArrayList<>(Collections.singleton(octetDeb))));
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
                        if (this.mapsObjet.getUtcDateMap().containsKey(utcDate))
                        {
                            this.mapsObjet.getUtcDateMap().get(utcDate).add(octetDeb);
                        } else
                            this.mapsObjet.getUtcDateMap().put(utcDate, Collections.synchronizedList(new ArrayList<>(Collections.singletonList(octetDeb))));
                    }
                    case "UTCTime" -> {
                        if (this.mapsObjet.getUtcTimeMap().containsKey(buf[1]))
                        {
                            this.mapsObjet.getUtcTimeMap().get(buf[1]).add(octetDeb);
                        } else
                            this.mapsObjet.getUtcTimeMap().put(buf[1], Collections.synchronizedList(new ArrayList<>(Collections.singletonList(octetDeb))));
                    }
                    case "WhiteElo", "BlackElo" -> {
                        try
                        {
                            int elo = Integer.parseInt(buf[1]);
                            if (this.mapsObjet.getEloMap().containsKey(elo))
                            {
                                this.mapsObjet.getEloMap().get(elo).add(octetDeb);
                            } else
                                this.mapsObjet.getEloMap().put(elo, Collections.synchronizedList(new ArrayList<>(Collections.singletonList(octetDeb))));
                        } catch (NumberFormatException e)
                        {
                            log.warning("Elo inconnue");
                        }
                    }
                }
                if (string.split(" ")[0].equals("1."))
                {
                    //map pour les nombre de coups
                    List<String> lst = new ArrayList<>(List.of(string.split("[{}]")));
                    lst.removeIf(strr -> strr.contains("%eval") || strr.contains("%clk"));
                    lst = new ArrayList<>(List.of(String.join("", lst).split(" ")));
                    lst.removeIf(strr -> strr.equals("") || strr.contains("."));
                    // on enleve -1 car le dernier "coup" est le resultat
                    if (this.mapsObjet.getNbCoupsMap().containsKey(lst.size() - 1))
                    {
                        this.mapsObjet.getNbCoupsMap().get(lst.size() - 1).add(octetDeb);
                    } else
                        this.mapsObjet.getNbCoupsMap().put(lst.size() - 1, Collections.synchronizedList(new ArrayList<>(Collections.singletonList(octetDeb))));

                    //map pour les ouvertures
                    if (this.mapsObjet.getOpenningMap().containsKey(string.split(" ")[1]))
                    {
                        this.mapsObjet.getOpenningMap().get(string.split(" ")[1]).add(octetDeb);
                    } else
                        this.mapsObjet.getOpenningMap().put(string.split(" ")[1], Collections.synchronizedList((new ArrayList<>(Collections.singletonList(octetDeb)))));
                }
            }
            lstStr.clear();
        }
    }

    private List<String> getLstStringPartieInFile(long pos)
    {
        int comptLigneVide = 0;
        String str;
        List<String> lstStr = new ArrayList<>();

        try
        {
            //création d'un nouveau bufferedReader car impossible de vider le buffer autrement
            FileInputStream in = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            in.getChannel().position(pos);
            while (comptLigneVide < 2)
            {
                str = reader.readLine();
                if (str.equals("")) comptLigneVide++;
                else lstStr.add(str);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            System.exit(-1);
        }

        return lstStr;
    }


    private void afficheOctetLu()
    {
        int taille = lstPosParties.size();
        log.info("Lecture en cours : 0%");
        while (!this.creeMapOk)
        {
            log.info("Lecture en cours... : " +(compteurPourList *100)/taille + "%");
            try
            {
                sleep(1000);
            } catch (InterruptedException e) {e.printStackTrace();}
        }
        log.info("Lecture en cours : 100%");
    }
}