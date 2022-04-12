package recherche.autres;

import maps.CreeMapIteration;
import maps.MapsObjet;
import recherche.Recherche;
import semaphore.Semaphore;
import utils.Colors;

import java.io.BufferedWriter;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Affiche les joueurs les plus actifs sur le mois du fichier, ainsi que le joueur le plus actif de chaque semaine du fichier.
 *
 * @author Yilmaz Rahman
 * @version 1.0
 * @date 25/03/2022
 */
public class JoueursLesplusActifs extends Recherche
{

    private int mois;
    private int anne;
    private final Calendar calendar;
    private String message;
    private Semaphore semaphore;

    /**
     * @param clientReader L'ObjectInputStream du client.
     * @param clientWriter Le BufferedWriter du client.
     * @param mapObjet     L'instance de la classe MapsObjet.
     */
    public JoueursLesplusActifs(ObjectInputStream clientReader, BufferedWriter clientWriter, MapsObjet mapObjet)
    {
        super(clientReader, clientWriter, mapObjet);
        this.calendar = Calendar.getInstance(Locale.FRANCE);
        this.message = "";
        this.semaphore = new Semaphore(6);
    }

    @Override
    public void cherche()
    {
        new Thread(() ->
        {
            if (mapObjet.getUtcDateMap().size() > 0) getDate();
            Thread th1 = new Thread(this::lePlusActifSurLemois);
            th1.start();
            Thread th2 = new Thread(this::lePlusActifDeChaqueSemaine);
            th2.start();
            semaphore.finished();
            envoieMessage(this.message);
        }).start();
    }

    /**
     * cherche le joueur le plus actif du mois, du fichier.
     */
    private void lePlusActifSurLemois()
    {
        semaphore.acquire();
        int compteur = 0;
        String joueur = "";
        int nbParties;


        for (Map.Entry<Object, List<Long>> element : mapObjet.getNameMap().entrySet())
        {
            nbParties = element.getValue().size();
            if (nbParties > compteur)
            {
                joueur = (String) element.getKey();
                compteur = element.getValue().size();
            }
        }
        this.message += Colors.cyan + "Le joueur le plus actifs sur le " + this.mois + "eme mois de l'année : " + this.anne + " est " + joueur + " avec " + compteur + " parties." + Colors.reset + "\n";
        semaphore.release();
    }

    /**
     * Cherche pour chaque semaine le joueur le plus actif.
     * Les semaines sont faites automatiquement, la premiere semaine est les 7 premiers jours du fichier et ainsi de suite pour les autres semaines.
     */
    private void lePlusActifDeChaqueSemaine()
    {
        List<Long> lstDates = new ArrayList<>();

        for (int i = 0; i < mapObjet.getUtcDateMap().size() / 7 + 1; i++)
        {
            for (int j = 0; j < 7; j++)
            {
                if (i * 7 + j < mapObjet.getUtcDateMap().size())
                    lstDates.addAll((Collection<? extends Long>) mapObjet.getUtcDateMap().values().toArray()[i * 7 + j]);
            }
            calendar.setTime(new Date((Long) mapObjet.getUtcDateMap().keySet().stream().toList().get(i * 7)));
            List<Long> lsclone = new ArrayList<>(lstDates);
            int finalI = i + 1;
            new Thread(() -> getJoueurLePlusActifDeLaSemaine(finalI, lsclone)).start();
            lstDates.clear();
        }
    }


    /**
     * cherche le joueur le plus fort de la semaine désigné en paramètre.
     *
     * @param semaine désigne la semaine à traiter
     * @param lstPos  liste des positions des parties de la semaine
     */
    private void getJoueurLePlusActifDeLaSemaine(int semaine, List<Long> lstPos)
    {
        semaphore.acquire();

        MapsObjet mp = new MapsObjet(mapObjet.getFile());
        CreeMapIteration cr = new CreeMapIteration(mp, mapObjet.getFile(), lstPos);
        cr.cree();

        int compteur = 0;
        String joueur = "";
        int nbParties;

        for (Map.Entry<Object, List<Long>> element : mp.getNameMap().entrySet())
        {
            nbParties = element.getValue().size();
            if (nbParties > compteur)
            {
                joueur = (String) element.getKey();
                compteur = element.getValue().size();
            }
        }
        this.message += Colors.cyan + "Le joueur le plus actifs sur la " + semaine + "eme semaine du " + this.mois + "eme mois de l'année : " + this.anne + " est " + joueur + " avec " + compteur + " parties." + Colors.reset + "\n";
        mp = null;
        cr = null;
        System.gc();
        semaphore.release();
    }

    /**
     * recuperation de la date la plus ancienne et la date la plus récente des parties du fichier.
     */
    private void getDate()
    {
        Date dateFin = new Date((Long) mapObjet.getUtcDateMap().keySet().toArray()[mapObjet.getUtcDateMap().keySet().size() - 1]);
        calendar.setTime(dateFin);
        this.mois = calendar.get(Calendar.MONTH) + 1;
        this.anne = calendar.get(Calendar.YEAR);
    }
}
