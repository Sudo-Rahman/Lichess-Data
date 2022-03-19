package recherche.partie.specifique;

import maps.MapsObjets;
import partie.Partie;
import recherche.RecherchePartieSpecifique;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;

public class RechercheEnFonctionDate extends RecherchePartieSpecifique
{
    private String date;


    public RechercheEnFonctionDate(ObjectInputStream clientReader, BufferedWriter clientWriter, MapsObjets mapObjets)
    {
        super(clientReader, clientWriter, mapObjets);
    }

    @Override
    public void cherche()
    {
        initDemande();
        if (getUtcDateMap().containsKey(this.date))
        {
            this.lstLigneParties = getUtcDateMap().get(this.date);
            if (nbParties == -1)
            {
                nbParties = Math.min(getUtcDateMap().get(this.date).size(), this.maxNbParties);
            }
            Thread t = new Thread(this::calcule);
            t.setPriority(Thread.MAX_PRIORITY);
            t.start();
        } else envoieMessage(toString());
    }

    @Override
    public void calcule()
    {
        String ligne;
        int comptLigneVide = 0;
        long lignes = 0L;


        tempsRecherche = System.currentTimeMillis();

        int partie = 0;
        try
        {
            while ((ligne = getFileReader().readLine()) != null && lstPartie.size() < nbParties)
            {
                if (lignes >= lstLigneParties.get(partie)[0] && lignes <= lstLigneParties.get(partie)[1])
                {
                    if (ligne.equals("")) {comptLigneVide++;} else lstLigne.add(ligne);
                    if (comptLigneVide == 2)
                    {
                        lstPartie.add(new Partie(lstLigne));
                        lstLigne.clear();
                        partie++;
                        comptLigneVide = 0;
                    }
                }
                lignes++;
            }
            this.tempsRecherche = System.currentTimeMillis() - this.tempsRecherche;

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        envoieMessage(toString());
        closeFileReader();
        this.lstPartie = null;
        System.gc();
    }

    @Override
    public void initDemande()
    {
        String dateDeb = (String) getUtcDateMap().keySet().toArray()[0];
        String dateFin = (String) getUtcDateMap().keySet().toArray()[getUtcDateMap().size() - 1];
        envoieMessage("Donner la date, (compris entre " + dateDeb + " et " + dateFin + " sur le fichier" + mapObjets.getFile().getName());
        this.date = litMess();
        envoieMessage("Combien de partie voulez vous rechercher ? (-1) pour toutes les parties.");
        nbParties = litInt();
    }
}
