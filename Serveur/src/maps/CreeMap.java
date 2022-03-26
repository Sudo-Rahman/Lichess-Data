package maps;


import utils.Log;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Thread.sleep;
import static java.nio.charset.StandardCharsets.UTF_8;

public class CreeMap
{

    private final String file;

    private final WriteAndReadMaps warm;
    private final int nbThreads = Runtime.getRuntime().availableProcessors() / 2;
    private final Log log;
    private long nbOctetsLu;
    private long nbOctetsParThread;
    private boolean creeMapOk;

    public CreeMap(WriteAndReadMaps mo, String path)
    {
        this.file = path;
        this.warm = mo;
        this.creeMapOk = false;
        log = new Log();
    }

    public boolean isCreeMapOk()
    {
        return creeMapOk;
    }

    public void cree()
    {
        this.nbOctetsLu = 0;
        this.nbOctetsParThread = new File(file).length() / this.nbThreads;
        createMaps();
    }


    private void createMaps()
    {
        long tempsRecherche = System.currentTimeMillis();

        List<Thread> lstThreads = new ArrayList<>();
        for (int i = 0; i < this.nbThreads; i++)
        {
            int I = i;
            Thread t = new Thread(() ->
            {
                try
                {
                    calcule(nbOctetsParThread * I);
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
        log.info("Creation des maps effectué en  : " + (System.currentTimeMillis() - tempsRecherche) / 1000 + " secondes");
    }

    private void calcule(long deb) throws IOException
    {
        BufferedReader reader = null;
        FileInputStream in = null;
        try
        {
            in = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(in));
            in.getChannel().position(deb);
        } catch (Exception e)
        {
            log.error("Fichier existe pas");
            System.exit(-1);
        }


        // variables pour connaitre l'octet de debut et fin d'une partie
        Long octetDeb = in.getChannel().position();
        int comptLigne = 0;

        int partie = 0;

        int octetOffset = 0;

        List<String> lstStr = new ArrayList<>();
        String str;

        while ((str = reader.readLine()) != null && octetDeb <= deb + nbOctetsParThread + 5000)
        {
            if (str.equals("") && partie == 0)
            {
                octetDeb += 1;
            } else if (!str.contains("[Event \"") && partie == 0)
            {
                octetDeb += str.getBytes(UTF_8).length + 1;
            } else
            {
                partie++;
                if (str.equals("")) comptLigne++;
                else lstStr.add(str);
            }

            if (comptLigne == 2)
            {
                if (in.getChannel().position() > deb + nbOctetsParThread)
                {
                    if (inMap(lstStr, octetDeb)) break;
                }
                octetOffset += 1;// dans chaque partie il y a deux sauts de ligne, mais ils sont comptabilisés à 1 et non 2
                for (String string : lstStr)
                {
                    octetOffset += string.getBytes(UTF_8).length + 1;// +1, car a la fin de la ligne il y a le character de retour ligne '\n'
                    String[] buf = string.replaceAll("[\\[\\]]", "").split("\"");
                    buf[0] = buf[0].replaceAll(" ", "");
                    switch (buf[0])
                    {
                        case "White", "Black" -> {
//                                System.out.println(octetDeb + " " + lstStr + "\n\n");
                            if (this.warm.getNameMap().containsKey(buf[1]))
                            {
                                this.warm.getNameMap().get(buf[1]).add(octetDeb);

                            } else
                                this.warm.getNameMap().putIfAbsent(buf[1], Collections.synchronizedList(new ArrayList<>(Collections.singleton(octetDeb))));
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
                            if (this.warm.getUtcDateMap().containsKey(utcDate))
                            {
                                this.warm.getUtcDateMap().get(utcDate).add(octetDeb);
                            } else
                                this.warm.getUtcDateMap().put(utcDate, Collections.synchronizedList(new ArrayList<>(Collections.singletonList(octetDeb))));
                        }
                        case "UTCTime" -> {
                            if (this.warm.getUtcTimeMap().containsKey(buf[1]))
                            {
                                this.warm.getUtcTimeMap().get(buf[1]).add(octetDeb);
                            } else
                                this.warm.getUtcTimeMap().put(buf[1], Collections.synchronizedList(new ArrayList<>(Collections.singletonList(octetDeb))));
                        }
                        case "WhiteElo", "BlackElo" -> {
                            try
                            {
                                int elo = Integer.parseInt(buf[1]);
                                if (this.warm.getEloMap().containsKey(elo))
                                {
                                    this.warm.getEloMap().get(elo).add(octetDeb);
                                } else
                                    this.warm.getEloMap().put(elo, Collections.synchronizedList(new ArrayList<>(Collections.singletonList(octetDeb))));
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
                        if (this.warm.getNbCoupsMap().containsKey(lst.size() - 1))
                        {
                            this.warm.getNbCoupsMap().get(lst.size() - 1).add(octetDeb);
                        } else
                            this.warm.getNbCoupsMap().put(lst.size() - 1, Collections.synchronizedList(new ArrayList<>(Collections.singletonList(octetDeb))));

                        //map pour les ouvertures
                        if (this.warm.getOpenningMap().containsKey(string.split(" ")[1]))
                        {
                            this.warm.getOpenningMap().get(string.split(" ")[1]).add(octetDeb);
                        } else
                            this.warm.getOpenningMap().put(string.split(" ")[1], Collections.synchronizedList((new ArrayList<>(Collections.singletonList(octetDeb)))));
                    }
                }
                comptLigne = 0;
                addOctetsLu(octetOffset + 1);
                octetDeb += octetOffset + 1;
                lstStr.clear();
                octetOffset = 0;
            }
        }
    }

    /**
     * @param lstStr   liste des lignes d'une partie
     * @param octetDeb l'octet dans le fichier du debut de la partie
     * @return retourne vrai si la partie est deja la hashmap des joueurs sinon faux
     */
    private boolean inMap(List<String> lstStr, Long octetDeb)
    {
        for (String sr : lstStr)
        {
            String[] buf = sr.replaceAll("[\\[\\]]", "").split("\"");
            buf[0] = buf[0].replaceAll(" ", "");
            if (buf[0].equals("White"))
            {
                if (this.warm.getNameMap().containsKey(sr.split("\"")[1]))
                {
                    if (this.warm.getNameMap().get(sr.split("\"")[1]).contains(octetDeb))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private synchronized void addOctetsLu(long l)
    {
        this.nbOctetsLu += l;
    }

    private void afficheOctetLu()
    {
        long tailleFichier = new File(file).length();
        long avant = 0L;
        long apres;
        log.info("Lecture en cours : 0%");
        while (!this.creeMapOk)
        {
            apres = this.nbOctetsLu * 100 / tailleFichier;
            if (avant < apres)
            {
                log.info("Lecture en cours : " + apres + "%");
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
