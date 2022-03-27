package recherche.autres;

import maps.MapsObjet;
import partie.Partie;
import recherche.Recherche;
import utils.Colors;

import java.io.BufferedWriter;
import java.io.ObjectInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class JoueursLesplusActifs extends Recherche
{

    private String mois;
    private String anne;
    private final SimpleDateFormat sdformat;
    private Date debut;
    private Date fin;

    public JoueursLesplusActifs(ObjectInputStream clientReader, BufferedWriter clientWriter, MapsObjet mapObjet)
    {
        super(clientReader, clientWriter, mapObjet);
        this.sdformat = new SimpleDateFormat("dd/MM/yyyy");
    }

    @Override
    public void cherche()
    {
        getDate();
        new Thread(this::lePlusActifSurLemois).start();
        new Thread(this::lePlusActifDeChaqueSemaine).start();
    }

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
        envoieMessage(Colors.cyan + "Le joueur le plus actifs sur le " + this.mois + "eme mois de l'anné : " + this.anne + " est : " + joueur + " avec " + compteur + " parties.\n" + Colors.reset);
    }

    private void lePlusActifDeChaqueSemaine()
    {
        List<Thread> lstThreads = new ArrayList<>();
        int compteur = 0;
        int coompt = 0;
        String joueur = "";
        long timeDiff = 7 * 60 * 60 * 24 * 1000;
        long fin = this.debut.getTime() + timeDiff;

        List<Long> lstLong = new ArrayList<>();

        for (int i = 1; i <= (mapObjet.getUtcDateMap().size() / 7) + 1; i++)
        {
            for (Map.Entry<Object, List<Long>> elmt : mapObjet.getUtcDateMap().entrySet())
            {
                try
                {
                    if (fin - sdformat.parse((String) elmt.getKey()).getTime() > 0 && fin - sdformat.parse((String) elmt.getKey()).getTime() <= timeDiff)
                    {
                        lstLong.addAll(elmt.getValue());
                    }

                } catch (ParseException e)
                {
                    e.printStackTrace();
                }
            }
            Thread th = new Thread(() -> calcule(lstLong));
            lstThreads.add(th);
            th.start();
            fin += timeDiff;
            lstLong.clear();
        }
        for (Thread th : lstThreads)
        {
            try
            {
                th.join();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        envoieMessage(Colors.cyan + "Le joueur le plus actifs sur le " + this.mois + "eme mois de l'anné : " + this.anne + " est :" + joueur + " avec " + compteur + " parties.\n" + Colors.reset);
    }

    private void calcule(List<Long> lstPos)
    {
        System.out.println(lstPos);
        HashMap<String, Integer> map = new HashMap<>();
        for (Partie partie : this.partiesFile.getAllParties(lstPos))
        {
            if (map.containsKey(partie.getBlanc()))
            {
                map.replace(partie.getBlanc(), map.get(partie.getBlanc() + 1));
            } else map.put(partie.getBlanc(), 1);
            if (map.containsKey(partie.getNoir()))
            {
                map.replace(partie.getNoir(), map.get(partie.getNoir() + 1));
            } else map.put(partie.getNoir(), 1);
            System.out.println(partie.getBlanc() + " " + partie.getNoir());
        }
        int max = 0;
        for (Map.Entry<String, Integer> element : map.entrySet())
        {
            if (element.getValue() > max)
            {
                max = element.getValue();
            }
        }
        System.out.println(max);
    }

    private void getDate()
    {
        try
        {
            Date dateDeb = sdformat.parse((String) mapObjet.getUtcDateMap().keySet().toArray()[0]);
            Date dateFin = sdformat.parse((String) mapObjet.getUtcDateMap().keySet().toArray()[1]);
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
            this.mois = sdformat.format(dateFin).split("/")[1];
            this.anne = sdformat.format(dateDeb).split("/")[2];
        } catch (ParseException e)
        {
            log.error("Impossible de convertir le String en date !!");
        }
    }
}
