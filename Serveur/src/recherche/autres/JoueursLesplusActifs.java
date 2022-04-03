package recherche.autres;

import maps.CreeMapIteration;
import maps.MapsObjet;
import recherche.Recherche;
import utils.Colors;

import java.io.BufferedWriter;
import java.io.ObjectInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private final SimpleDateFormat sdformat;
    private final Calendar calendar;
    private String message;

    private Date debut;
    private Date fin;

    /**
     * @param clientReader L'ObjectInputStream du client.
     * @param clientWriter Le BufferedWriter du client.
     * @param mapObjet L'instance de la classe MapsObjet.
     */
    public JoueursLesplusActifs(ObjectInputStream clientReader, BufferedWriter clientWriter, MapsObjet mapObjet)
    {
        super(clientReader, clientWriter, mapObjet);
        this.sdformat = new SimpleDateFormat("dd/MM/yyyy");
        this.calendar = Calendar.getInstance(Locale.FRANCE);
        this.message = "";
    }

    @Override
    public void cherche()
    {
        new Thread(() ->{
            if (mapObjet.getUtcDateMap().size() > 0) getDate();
            Thread th1 = new Thread(this::lePlusActifSurLemois);
            th1.start();
            Thread th2 = new Thread(this::lePlusActifDeChaqueSemaine);
            th2.start();
            try{
                th1.join();th2.join();
            } catch (InterruptedException e)
            {e.printStackTrace();}
            envoieMessage(this.message);
        }).start();
    }

    /**
     * cherche le joueur le plus actif du mois, du fichier.
     */
    private void lePlusActifSurLemois()
    {
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
        this.message += Colors.cyan + "Le joueur le plus actifs sur le " + this.mois + "eme mois de l'année : " + this.anne + " est " + joueur + " avec " + compteur + " parties." + Colors.reset+"\n";
    }

    /**
     * Cherche pour chaque semaine le joueur le plus actif.
     * Les semaines sont faites automatiquement, la premiere semaine est les 7 premiers jours du fichier et ainsi de suite pour les autres semaines.
     */
    private void lePlusActifDeChaqueSemaine()
    {
        List<Thread> lstThreads = new ArrayList<>();
        List<Long> lstPosParties = new ArrayList<>();

        long unJourEnMillisecondes = 60 * 60 * 24 * 1000;
        long debut = this.debut.getTime();

        for (int i = 0; i < (int) (Math.ceil(mapObjet.getUtcDateMap().size() / 7.0)); i++)
        {
            for (int y = 0; y < 7 && debut + i * 7 * unJourEnMillisecondes + y * unJourEnMillisecondes <= this.fin.getTime(); y++)
            {
                String date = sdformat.format(new Date(debut + i * 7 * unJourEnMillisecondes + y * unJourEnMillisecondes));
                if(mapObjet.getUtcDateMap().containsKey(date)) lstPosParties.addAll(mapObjet.getUtcDateMap().get(date));
            }
            final int I = i+1;
            List<Long> lstClone = new ArrayList<>(lstPosParties);
            Thread th = new Thread(() -> getJoueurLePlusActifDeLaSemaine(I, lstClone));
            lstThreads.add(th);
            th.start();
            lstPosParties.clear();
        }
        for (Thread th : lstThreads)
        {
            try {th.join();} catch (InterruptedException e) {e.printStackTrace();}
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
        this.message += Colors.cyan + "Le joueur le plus actifs sur la " + semaine + "eme semaine du " + this.mois + "eme mois de l'année : " + this.anne + " est " + joueur + " avec " + compteur + " parties." + Colors.reset+"\n";
        mp = null;
        cr = null;
        System.gc();
    }

    /**
     * recuperation de la date la plus ancienne et la date la plus récente des parties du fichier.
     */
    private void getDate()
    {
        try
        {
            Date dateDeb = sdformat.parse((String) mapObjet.getUtcDateMap().keySet().toArray()[0]);
            Date dateFin = dateDeb;
            for (Object date : mapObjet.getUtcDateMap().keySet())
            {
                if (dateDeb.compareTo(sdformat.parse((String) date)) > 0)
                {
                    dateDeb = sdformat.parse((String) date);
                }
                if (dateFin.compareTo(sdformat.parse((String) date)) < 0)
                {
                    dateFin = sdformat.parse((String) date);
                }
            }
            this.debut = dateDeb;
            this.fin = dateFin;
            calendar.setTime(dateFin);
            this.mois = calendar.get(Calendar.MONTH)+1;
            this.anne = calendar.get(Calendar.YEAR);
        } catch (ParseException e)
        {
            log.error("Impossible de convertir le String en date !!");
        }
    }
}
