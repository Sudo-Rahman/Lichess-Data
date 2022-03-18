package recherche.partie.specifique;

import maps.MapsObjets;
import partie.Partie;
import recherche.RecherchePartieSpecifique;
import utils.Colors;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;

public class RechercheEnFonctionEloJoueur extends RecherchePartieSpecifique
{

    private int eloSup;
    private int eloInf;
    private final Map<Integer, List<long[]>> eloMap;

    public RechercheEnFonctionEloJoueur(String pathFile, ObjectInputStream clientReader, BufferedWriter clientWriter, MapsObjets mapObjets)
    {
        super(pathFile, clientReader, clientWriter, mapObjets);
        this.eloMap = getEloMap();
    }


    @Override
    public void cherche()
    {
        initDemande();
        if (trouveElo() > 0)
        {
            if (nbParties == -1)
            {
                nbParties = Math.min(this.lstLigneParties.size(), this.maxNbParties);
            }
            Thread t = new Thread(this::calcule);
            t.setPriority(Thread.MAX_PRIORITY);
            t.start();
        } else envoieMessage(toString());

    }

    @Override
    public void initDemande()
    {
        envoieMessage("Donner la borne inferieur ex : 1000 -> les joueurs en dessous de 1000 ne serons pas pris");
        this.eloInf = litInt();
        envoieMessage("Donner la borne superieur ex : 2000 -> les joueurs au dessus de 2000 ne serons pas pris");
        this.eloSup = litInt();
        envoieMessage("Combien de partie voulez vous rechercher ? (-1) pour toutes les parties.");
        this.nbParties = litInt();
    }

    @Override
    public void calcule()
    {
        String ligne;
        int comptLigneVide = 0;
        long lignes = 0L;


        this.tempsRecherche = System.currentTimeMillis();

        int partie = 0;
        try
        {
            while ((ligne = getFileReader().readLine()) != null && this.lstPartie.size() < this.nbParties)
            {
                if (lignes >= lstLigneParties.get(partie)[0] && lignes <= lstLigneParties.get(partie)[1])
                {
                    if (ligne.equals("")) comptLigneVide++;
                    else lstLigne.add(ligne);
                    if (comptLigneVide == 2)
                    {
                        Partie p = new Partie(lstLigne);
                        if (p.getBlackElo() <= this.eloSup && p.getBlackElo() >= this.eloInf && p.getWhiteElo() <= this.eloSup && p.getWhiteElo() >= this.eloInf)
                        {
                            this.lstPartie.add(p);
                        }
                        lstLigne.clear();
                        comptLigneVide = 0;
                        partie++;
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

    private int trouveElo()
    {
        for (int i = this.eloInf; i <= this.eloSup; i++)
        {
            if (this.eloMap.containsKey(i))
            {
                this.lstLigneParties.addAll(this.eloMap.get(i));
            }
        }
        return this.lstLigneParties.size();
    }


}
