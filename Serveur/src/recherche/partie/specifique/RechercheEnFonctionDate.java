package recherche.partie.specifique;

import maps.MapsObjet;
import recherche.RecherchePartieSpecifique;

import java.io.BufferedWriter;
import java.io.ObjectInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * classe qui cherche les parties en fonction d'une date precise.
 *
 * @author Yilmaz Rahman
 * @version 1.0
 * @date 12/03/2022
 */
public class RechercheEnFonctionDate extends RecherchePartieSpecifique
{
    private String date;

    /**
     * @param clientReader L'ObjectInputStream du client.
     * @param clientWriter Le BufferedWriter du client.
     * @param mapObjet     L'instance de la classe MapsObjet.
     */
    public RechercheEnFonctionDate(ObjectInputStream clientReader, BufferedWriter clientWriter, MapsObjet mapObjet)
    {
        super(clientReader, clientWriter, mapObjet);
    }


    @Override
    public void initDemande()
    {
        SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy");

        Date dateDeb = null;
        Date dateFin = null;
        try
        {
            dateDeb = sdformat.parse((String) mapObjet.getUtcDateMap().keySet().toArray()[0]);
            dateFin = sdformat.parse((String) mapObjet.getUtcDateMap().keySet().toArray()[1]);
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
        } catch (ParseException e)
        {
            log.error("Impossible de convertir le String en date !!");
        }
        envoieMessage("Donner la date, (compris entre " + sdformat.format(dateDeb) + " et " + sdformat.format(dateFin) + " sur le fichier" + mapObjet.getFile().getName());
        this.date = litMess();
        envoieMessage("Voulez vous réiterez sur les parties (yes) ou afficher les parties (no) :");
        if (litMess().equals("yes"))
        {
            this.afficheParties = false;
        } else
        {
            envoieMessage("Combien de partie voulez vous rechercher ? (0) pour toutes les parties.");
            nbParties = litInt();
        }
    }

    /*
    lance le calcule pour chercher les parties si l'element demander par le client est bien dans l'hasmap
     */
    @Override
    public void cherche()
    {
        initDemande();
        if (mapObjet.getUtcDateMap().containsKey(this.date))
        {
            this.lstPosParties = mapObjet.getUtcDateMap().get(this.date);
            if (this.afficheParties)
            {
                if (nbParties == 0)
                {
                    nbParties = Math.min(mapObjet.getUtcDateMap().get(this.date).size(), this.maxNbParties);
                }
                Thread t = new Thread(this::calcule);
                t.setPriority(Thread.MAX_PRIORITY);
                t.start();
            }else {this.iterative = true;setDescription(", date : " + this.date);}
        } else envoieMessage(toString());
    }


    /**
     * lit le fichier tant que le nombre de partie n'a pas été atteint,
     * cree une Partie si les lignes du fichier correspondent avec les lignes dans lstPosParties
     */
    @Override
    public void calcule()
    {
        tempsRecherche = System.currentTimeMillis();

        this.lstPartie = this.partiesFile.getAllParties(this.lstPosParties, nbParties);

        this.tempsRecherche = System.currentTimeMillis() - this.tempsRecherche;
        if (this.afficheParties) envoieMessage(toString());

        //Libere la liste de la memoire
        this.lstPartie = null;
        System.gc();
    }
}
