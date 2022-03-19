package recherche.partie.specifique;

import maps.MapsObjets;
import partie.Partie;
import recherche.RecherchePartieSpecifique;
import utils.Colors;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RechereEnFonctionDuPremierCoup extends RecherchePartieSpecifique
{

    private String coup;

    public RechereEnFonctionDuPremierCoup(ObjectInputStream clientReader, BufferedWriter clientWriter, MapsObjets mapObjets)
    {
        super(clientReader, clientWriter, mapObjets);
    }

    @Override
    public void initDemande()
    {
        envoieMessage("Donner le premier coup, ex : \"d4\"");
        this.coup = litMess();
        envoieMessage("Combien de partie voulez vous rechercher ? (-1) pour toutes les parties.");
        nbParties = litInt();
    }


    @Override
    public void cherche()
    {
        initDemande();
        if (getOpenningMap().containsKey(this.coup))
        {
            this.lstLigneParties = getOpenningMap().get(this.coup);
            if (nbParties == -1)
            {
                nbParties = Math.min(getOpenningMap().get(this.coup).size(), this.maxNbParties);
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
}
