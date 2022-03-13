package recherche.partie.specifique;

import partie.Partie;
import recherche.RecherchePartieSpecifique;
import utils.Colors;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;

public class RechereEnFonctionDuPremierCoup extends RecherchePartieSpecifique
{

    private String coup;

    public RechereEnFonctionDuPremierCoup(String pathFile, ObjectInputStream clientReader, BufferedWriter clientWriter)
    {
        super(pathFile, clientReader, clientWriter);
    }

    private void initDemande()
    {
        envoieMessage("Donner le premier coup, ex : \"d4\"");
        this.coup = litMess();
        envoieMessage("Combien de partie voulez vous rechercher ?");
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
        String ligne;
        int comptLigne = 0;
        this.tempsRecherche = System.currentTimeMillis();
        try
        {
            while ((ligne = getFileReader().readLine()) != null && this.lstPartie.size() < this.nbParties)
            {
                if (ligne.equals("")) {comptLigne++;} else
                {
                    lstLigne.add(ligne);
                }
                if (comptLigne == 2)
                {
                    if (premierCoupEstDedans(coup)) lstPartie.add(new Partie(lstLigne));
                    lstLigne.clear();
                    comptLigne = 0;
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
