package recherche.autres;

import maps.MapsObjet;
import partie.Partie;
import recherche.Recherche;
import semaphore.Semaphore;
import utils.Colors;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Thread.sleep;


/**
 * Classe qui cherche le plus grand nombre de coups consécutifs cc qui soient communs à p parties.
 *
 * @author Yilmaz Rahman
 * @version 1.0
 * @date 02/04/2022
 */
public class NbCoupsConsecutifsParties extends Recherche
{

    private final String description;
    private List<Long> lstPosParties;
    private Map<String, Integer> mapCoups;
    private Semaphore sem;
    private int posList;
    private int longueur;
    private long nbPartieTraiter;
    private String coups;
    private int nbCoups;
    private int compteur = 0;

    /**
     * @param clientReader L'ObjectInputStream du client.
     * @param clientWriter Le BufferedWriter du client.
     * @param mapObjet     L'instance de la classe MapsObjet.
     * @param description  La description de la recherche.
     */
    public NbCoupsConsecutifsParties(ObjectInputStream clientReader, BufferedWriter clientWriter, MapsObjet mapObjet, String description)
    {
        super(clientReader, clientWriter, mapObjet);
        this.lstPosParties = new ArrayList<>();
        this.mapCoups = new ConcurrentHashMap<>();
        this.sem = new Semaphore(Runtime.getRuntime().availableProcessors());
        this.nbPartieTraiter = 0;
        this.description = description;
    }

    @Override
    public void cherche()
    {
        dejaCalculer();
        envoieMessage("\n" + Colors.BLUE_BOLD + "Le plus grand nombre de coups consécutifs cc qui soient communs à p parties est :\n" + coups + " avec  : " + nbCoups + " apparition." + Colors.reset);
    }

    /**
     * Lance le calcul des coups consécutifs si et seulement si il n'a pas deja été calculé et stocker dans un fichier.
     */
    private void dejaCalculer()
    {
        String nomFichier = String.join("_", ("coupsConsecutifs" + this.description).replaceAll("[,:]", "").replaceAll(" {2}", " ").split("[/ ]"));
        File fichier = new File(mapObjet.getFolderData() + "/" + nomFichier + ".cccpp");
        if (fichier.exists())
        {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichier)))
            {
                coups = (String) ois.readObject();
                nbCoups = (int) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {e.printStackTrace();}
        } else
        {
            calcule();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fichier)))
            {
                oos.writeObject(coups);
                oos.writeObject(nbCoups);
            } catch (IOException e) {e.printStackTrace();}
        }
    }

    /**
     * methode qui lance plusieurs threads pour calculer les coups consécutifs.
     */
    private void calcule()
    {
        longueur = 0;
        new Thread(this::afficheAvancement).start();
        new Thread(this::check).start();
        for (Map.Entry<Object, List<Long>> entry : this.mapObjet.getOpenningMap().entrySet())
        {
            posList = 0;
            lstPosParties = entry.getValue();
            for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++)
            {
                new Thread(this::calculeCoups).start();
            }
            sem.finished();

            // on regarde dans la map si il y a un nombre de coups consécutifs plus grand que le nombre de coups consécutifs actuel.
            for (Map.Entry<String, Integer> entry1 : mapCoups.entrySet())
            {
                int taille = entry1.getKey().split("\\|").length;
                if (entry1.getValue() == taille && taille > longueur)
                {
                    longueur = taille;
                    coups = entry1.getKey();
                    nbCoups = entry1.getValue();
                }
            }
            sem.finished();
            this.mapCoups.clear();
            System.gc();
            if (longueur == 30) break;
        }
    }


    /**
     * Methode qui permet le multi-threading sans probleme de concurrence pour calculer les coups consécutifs.
     *
     * @return La position de la partie dans la liste des parties.
     */
    private synchronized long getpos()
    {
        if (posList < lstPosParties.size())
        {
            posList++;
            compteur++;
            if (compteur == 1E5)
            {
                compteur = 0;
                System.gc();
            }
            nbPartieTraiter++;
            return lstPosParties.get(posList - 1);
        }
        return -1L;
    }


    /**
     * methode qui pour chaque partie, stocks les coups des parties dans la map.
     * Un appelle au ramasse miette est effectué souvent pour pas que la memoire sois saturé de string inutile.
     */
    private void calculeCoups()
    {
        sem.acquire();
        Partie p;
        long pos;
        int count = 0;
        while ((pos = getpos()) != -1L)
        {
            p = partiesFile.getPartieInFile(pos);
            for (int i = 1; i < Math.min(p.getLstCoup().size(), 40); i = i + 10)// on avance de 2 par 2 pour ne pas prendre trop de temps pour le calcul.
            {
                if (i + longueur < p.getLstCoup().size() && longueur < 40)// on limite la longueur des coups consécutifs à 30, pour ne pas prendre trop de temps.
                {
                    List<String> lstCoups = new ArrayList<>(p.getLstCoup().subList(0, i + longueur));
                    lstCoups.removeIf(c -> c.contains(".") || c.equals(""));
                    if (lstCoups.size() >= longueur)
                        addCoups(String.join("|", lstCoups));
                    lstCoups = null;
                }
                count++;
                if (count == 10E5)
                {
                    System.gc();
                    count = 0;
                }
            }
            p = null;

        }
        System.gc();
        sem.release();
    }


    /**
     * Methode qui ajoute un coup dans la map.
     *
     * @param coups Coups d'une partie.
     */
    private void addCoups(String coups)
    {
        if (mapCoups.containsKey(coups))
        {
            mapCoups.replace(coups, mapCoups.get(coups) + 1);
        } else
        {
            mapCoups.put(coups, 1);
        }
    }

    /**
     * Methode qui affiche l'avancement du calcul.
     */
    private void afficheAvancement()
    {
        int avant = 0;
        int apres = 0;
        long total = 0;
        for (Map.Entry<Object, List<Long>> entry : this.mapObjet.getOpenningMap().entrySet())
        {
            total += entry.getValue().size();
        }
        try
        {
            while (apres != 100)
            {
                if ((apres = (int) ((nbPartieTraiter * 100) / total)) > avant)
                {
                    log.info("Avancement coups consecutive : " + apres + "%");
                    avant = apres;
                }
                sleep(1000);
            }
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * regarde si le nb de partie ayant de meme coups consecutifs.
     */
    private void check()
    {
        while (true)
        {
            for (Map.Entry<String, Integer> entry : mapCoups.entrySet())
            {
                if (entry.getValue() >= entry.getKey().split("\\|").length)
                {
                    sup(entry.getKey().split("\\|").length);
                    System.gc();
                }
            }
        }
    }

    /**
     * enleve tous les coups qui ont une longueur inferieur à la longueur en parametre.
     *
     * @param longueur Longueur des coups consécutifs.
     */
    private void sup(int longueur)
    {
        for (Map.Entry<String, Integer> entry : mapCoups.entrySet())
        {
            if (entry.getKey().split("\\|").length < longueur)
            {
                mapCoups.remove(entry.getKey());
            }
        }
    }

}
