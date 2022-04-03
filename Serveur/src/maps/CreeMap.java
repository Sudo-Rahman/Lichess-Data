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
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Classe qui crée un MapsObject à partir d'un fichier de données.
 *
 * @author Yilmaz Rahman
 * @version 1.0
 * @date 02/04/2022
 */
public class CreeMap
{

    private final File file;

    private final MapsObjet mapsObjet;
    private final int nbThreads = Runtime.getRuntime().availableProcessors();
    private final Log log;
    private long nbOctetsLu;
    private long nbOctetsParThread;
    private boolean creeMapOk;

    private final long posDeb;
    private final long posFin;

    /**
     * @param mo     MapsObjet qui va contenir les données.
     * @param file   le fichier de données.
     * @param posDeb la position de début du fichier.
     * @param posFin la position de fin du fichier.
     */
    public CreeMap(MapsObjet mo, File file, long posDeb, long posFin)
    {
        this.file = file;
        this.mapsObjet = mo;
        this.creeMapOk = false;

        this.posDeb = posDeb;
        this.posFin = posFin;
        log = new Log();
        this.nbOctetsLu = 0;
        this.nbOctetsParThread = (this.posFin - this.posDeb) / this.nbThreads;

    }

    /**
     * @return Renvoie true si la création des maps est terminée. Sinon false.
     */
    public boolean isCreeMapOk()
    {
        return creeMapOk;
    }

    /**
     * Ajoutes les données dans les hashmaps.
     */
    public void createMaps()
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
                    calcule(this.posDeb + (nbOctetsParThread * I));
                } catch (IOException e) {e.printStackTrace();}
            });
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
            partie += element.getValue().size();

        this.mapsObjet.setNbParties(partie / 2L);
        log.info("Creation des maps effectué en  : " + (System.currentTimeMillis() - tempsRecherche) / 1000 + " secondes");
        System.gc();
    }

    private void calcule(long deb) throws IOException
    {

        FileInputStream in = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        in.getChannel().position(deb);


        // variables pour connaitre l'octet de debut et fin d'une partie
        Long octetDeb = in.getChannel().position();
        int comptLigne = 0;

        int partie = 0;

        int octetOffset = 0;// variable qui sert à compter le nombre d'octets de chaque partie

        List<String> lstStr = new ArrayList<>();
        String str;

        while ((str = reader.readLine()) != null && octetDeb <= deb + nbOctetsParThread + 5000)// ajout de 5000 pour finir de lire la dernière partie.
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
                octetOffset += 2;// dans chaque partie il y a deux sauts de lignes
                for (String string : lstStr)
                {
                    octetOffset += string.getBytes(UTF_8).length + 1;// +1, car a la fin de la ligne il y a le character de retour ligne '\n'
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

                        // on enlève -1, car le dernier "coup" est le résultat
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
                comptLigne = 0;
                addOctetsLu(octetOffset + 1);
                octetDeb += octetOffset;
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
                if (this.mapsObjet.getNameMap().containsKey(sr.split("\"")[1]))
                {
                    if (this.mapsObjet.getNameMap().get(sr.split("\"")[1]).contains(octetDeb))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Ajoute a l'attribut qui compte le nombre d'octets lus, le nombre d'octets en parametre.
     *
     * @param l Le nombre d'octets lus.
     */
    private synchronized void addOctetsLu(long l)
    {
        this.nbOctetsLu += l;
    }

    /**
     * Méthode qui permet de connaitre l'avancement du traitement.
     */
    private void afficheOctetLu()
    {
        long tailleFichier = file.length();
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
