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
    private final SimpleDateFormat sdformat;
    private long date;


    /**
     * @param clientReader L'ObjectInputStream du client.
     * @param clientWriter Le BufferedWriter du client.
     * @param mapObjet     L'instance de la classe MapsObjet.
     */
    public RechercheEnFonctionDate(ObjectInputStream clientReader, BufferedWriter clientWriter, MapsObjet mapObjet)
    {
        super(clientReader, clientWriter, mapObjet);
        this.sdformat = new SimpleDateFormat("dd/MM/yyyy");
    }


    @Override
    public void initDemande()
    {

        String dateDeb;
        String dateFin;
        dateDeb = sdformat.format(new Date((Long) mapObjet.getUtcDateMap().keySet().toArray()[0]).getTime());
        dateFin = sdformat.format(new Date((Long) mapObjet.getUtcDateMap().keySet().toArray()[mapObjet.getUtcDateMap().size() - 1]).getTime());
        envoieMessage("Donner la date, (compris entre " + dateDeb + " et " + dateFin + " sur le fichier " + mapObjet.getFile().getName());
        try {this.date = sdformat.parse(litMess()).getTime();} catch (ParseException e) {e.printStackTrace();}
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
                    nbParties = Math.min(mapObjet.getUtcDateMap().get(this.date).size(), this.maxNbParties);
                Thread t = new Thread(this::calcule);
                t.setPriority(Thread.MAX_PRIORITY);
                t.start();
            } else
            {
                this.iterative = true;
                setDescription(", date : " + sdformat.format(new Date(this.date)));
            }
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
