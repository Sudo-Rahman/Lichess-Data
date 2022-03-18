package recherche.partie.specifique;

import maps.MapsObjets;
import partie.Partie;
import recherche.RecherchePartieSpecifique;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;

public class RechereEnFonctionDuPremierCoup extends RecherchePartieSpecifique
{

    private String coup;

    public RechereEnFonctionDuPremierCoup(String pathFile, ObjectInputStream clientReader, BufferedWriter clientWriter, MapsObjets mapObjets)
    {
        super(pathFile, clientReader, clientWriter, mapObjets);
    }


    private void initDemande()
    {
        envoieMessage("Donner le premier coup, ex : \"d4\"");
        this.coup = litMess();
        envoieMessage("Combien de partie voulez vous rechercher ? (-1) pour toutes les parties.");
        this.nbParties = litInt();
    }


    @Override
    public void cherche()
    {
        initDemande();
        Thread t = new Thread(this::calcule);
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
    }

    private void calcule()
    {
        Map<String, List<long[]>> openningMap = mapObjets.getOpenningMap();
        if (nbParties == -1) nbParties = openningMap.get(coup).size();

        String ligne;
        int comptLigne = 0;
        this.tempsRecherche = System.currentTimeMillis();

        List<long[]> lstLigneParties = openningMap.get(coup);
        int partie = 0;
        try
        {
            while ((ligne = getFileReader().readLine()) != null && this.lstPartie.size() < this.nbParties)
            {
                if (comptLigne >= lstLigneParties.get(partie)[0] && comptLigne <= lstLigneParties.get(partie)[1])
                {
                    if (ligne.equals("")) {comptLigne++;} else lstLigne.add(ligne);
                    if (comptLigne == 2)
                    {
                        if (premierCoupEstDedans(coup)) lstPartie.add(new Partie(lstLigne));
                        lstLigne.clear();
                        comptLigne = 0;
                        partie++;
                    }
                }
            }
            this.tempsRecherche = System.currentTimeMillis() - this.tempsRecherche;

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        envoieMessage(toString());
        closeFileReader();
    }


    // revoie vrai si le coup en parametre est le premier coup de la partie
    private boolean premierCoupEstDedans(String string)
    {
        return this.lstLigne.get(this.lstLigne.size() - 1).split(" ")[1].equals(string);
    }


}
