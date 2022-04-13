package maps;

import utils.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Thread.sleep;

/**
 * Classe qui crée un MapsObject de positions de partie données, cela permet de faire un filtrage des parties.
 *
 * @author Yilmaz Rahman
 * @version 1.0
 * @date 02/04/2022
 */
public class CreeMapIteration
{
    private final File file;

    private final MapsObjet mapsObjet;
    private final int nbThreads = Runtime.getRuntime().availableProcessors();
    private final Log log;
    private final List<Long> lstPosParties;
    private boolean creeMapOk;
    private int compteurPourList;

    /**
     * @param mo            MapsObjet qui va contenir les données.
     * @param file          fichier de données.
     * @param lstPosParties liste des positions des parties.
     */
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
        createMaps();
    }

    /**
     * @return La position de la prochaine partie. S'il n'y a plus de partie, retourne -1.
     */
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
            Thread t = new Thread(this::calcule);
            lstThreads.add(t);
            t.setPriority(Thread.MAX_PRIORITY);
            t.start();
        }
        Thread th = new Thread(this::afficheOctetLu);
        th.start();

        try
        {
            for (Thread t : lstThreads) t.join();
            this.creeMapOk = true;
            th.join();
        } catch (InterruptedException e) {e.printStackTrace();}

        long partie = 0L;
        for (Map.Entry<Object, List<Long>> element : this.mapsObjet.getNameMap().entrySet())
        {partie += element.getValue().size();}
        this.mapsObjet.setNbParties((long) Math.ceil(partie / 2.0));
        this.mapsObjet.setUtcDateMap(new TreeMap<>(this.mapsObjet.getUtcDateMap()));
        log.info("Creation des maps effectué en  : " + (System.currentTimeMillis() - tempsRecherche) / 1000 + " secondes");
        System.gc();
    }

    private void calcule()
    {
        long pos;
        // variables pour connaitre l'octet de debut et fin d'une partie
        Long octetDeb;

        List<String> lstStr;

        while ((pos = getPos()) != -1L)
        {
            octetDeb = pos;

            lstStr = getLstStringPartieInFile(pos);

            for (String string : lstStr)
            {
                String[] buf = string.replaceAll("[\\[\\]]", "").split("\"");
                buf[0] = buf[0].replaceAll(" ", "");
                switch (buf[0])
                {
                    case "White", "Black" -> {
                        if (this.mapsObjet.getNameMap().containsKey(buf[1]))
                        {
                            this.mapsObjet.getNameMap().get(buf[1]).add(octetDeb);

                        } else
                            this.mapsObjet.getNameMap().putIfAbsent(buf[1], Collections.synchronizedList(new ArrayList<>(Collections.singleton(octetDeb))));
                    }
                    case "Site" -> {}
                    case "Result" -> {}
                    case "UTCDate" -> {
                        long utcDate = 0L;
                        try
                        {
                            utcDate = new SimpleDateFormat("yy" + ".MM.dd").parse(buf[1]).getTime();
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
                    case "UTCTime" -> {}
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

    /**
     * @param pos position de la partie dans le fichier.
     * @return Une liste de string contenant les lignes de la partie a la position pos du fichier.
     */
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
            reader.close();
            in.close();
        } catch (Exception e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
        return lstStr;
    }


    /**
     * Méthode qui permet de connaitre l'avancement du traitement.
     */
    private void afficheOctetLu()
    {
        long avant = 0L;
        long apres;
        int taille = lstPosParties.size();
        log.info("Lecture en cours : 0%");
        while (!this.creeMapOk)
        {
            if ((apres = (compteurPourList * 100L) / taille) > avant)
            {
                log.info("Lecture en cours... : " + apres + "%");
                avant = apres;
            }
            try
            {
                sleep(1000);
            } catch (InterruptedException e) {e.printStackTrace();}
        }
        log.info("Lecture en cours : 100%");
    }
}