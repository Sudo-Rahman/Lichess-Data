package recherche.partie.specifique;

import partie.Partie;
import recherche.RecherchePartieSpecifique;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;

public class RechercheEnFonctionEloJoueur extends RecherchePartieSpecifique
{

    private int eloSup;
    private int eloInf;

    public RechercheEnFonctionEloJoueur(String pathFile, ObjectInputStream clientReader, BufferedWriter clientWriter)
    {
        super(pathFile, clientReader, clientWriter);
    }

    @Override
    public void cherche()
    {
        initDemande();
        Thread t = new Thread(this::calcule);
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();

    }

    private void initDemande()
    {
        envoieMessage("Donner la borne inferieur ex : 1000 -> les joueurs en dessous de 1000 ne serons pas pris");
        this.eloInf = litInt();
        envoieMessage("Donner la borne superieur ex : 2000 -> les joueurs au dessus de 2000 ne serons pas pris");
        this.eloSup = litInt();
        envoieMessage("Combien de partie voulez vous rechercher ?");
        this.nbParties = litInt();
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
//                    Partie p = new Partie(lstLigne);
//                    if (testElo(p)) lstPartie.add(p);
                    if (testElo()) lstPartie.add(new Partie(lstLigne));
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

//        private boolean testElo(Partie p)
//    {
//        return (p.getWhiteElo() <= this.eloSup && p.getWhiteElo() >= this.eloInf) && (p.getBlackElo() <= this.eloSup && p.getBlackElo() >= this.eloInf);
//    }

    private boolean testElo()
    {
        int nb;
        int comppp = 0;
        for (String str : this.lstLigne)
        {
            String[] buf = str.replaceAll("[\\[\\]]", "").split("\"");
            buf[0] = buf[0].replaceAll(" ", "");
            if (buf[0].equals("WhiteElo"))
            {
                nb = Integer.parseInt(buf[1]);
                if (nb <= this.eloSup && nb >= this.eloInf)
                {
                    comppp++;
                }
            }
            if (buf[0].equals("BlackElo"))
            {
                nb = Integer.parseInt(buf[1]);
                if (nb <= this.eloSup && nb >= this.eloInf)
                {
                    comppp++;
                }
            }
            if (comppp == 2)
            {
                return true;
            }
        }
        return false;
    }

}
