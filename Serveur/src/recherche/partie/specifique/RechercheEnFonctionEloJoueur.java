/*
 * Nom de classe : RechereEnFonctionDuPremierCoup
 *
 * Description   : classe qui cherche les parties en fonction de des elos des joueurs.
 *
 * Version       : 1.0 , 1,1 , 1.2
 *
 * Date          : 13/03/2022 , 18/03/2022 , 19/03/2022
 *
 * Copyright     : Yilmaz Rahman, Colliat Maxime
 *
 */

package recherche.partie.specifique;

import maps.MapsObjets;
import partie.Partie;
import recherche.RecherchePartieSpecifique;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

public class RechercheEnFonctionEloJoueur extends RecherchePartieSpecifique
{

    private int eloSup;
    private int eloInf;

    public RechercheEnFonctionEloJoueur(ObjectInputStream clientReader, BufferedWriter clientWriter, MapsObjets mapObjets)
    {
        super(clientReader, clientWriter, mapObjets);
    }


    /*
    lance le calcule pour chercher les parties si l'element demander par le client est bien dans l'hasmap
     */
    @Override
    public void cherche()
    {
        initDemande();
        if (trouveElo() > 0)
        {
            // -1 correspond au maximum de partie, qui est limité par la variable maxNbParties
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

    /*
    lit le fichier tant que le nombre de partie n'a pas été atteint,
    cree une Partie si les lignes du fichier correspondent avec les lignes dans lstLigneParties
     */
    @Override
    public void calcule()
    {
        String ligne;
        int comptLigneVide = 0;
        long lignes = 0L;


        int partie = 0;
        try
        {
            while ((ligne = getFileReader().readLine()) != null && this.lstPartie.size() < this.nbParties)
            {
                if (lignes >= lstLigneParties.get(partie)[0] && lignes <= lstLigneParties.get(partie)[1])
                {
                    System.out.println(this.nbParties);
                    if (ligne.equals("")) comptLigneVide++;
                    else lstStrLigne.add(ligne);
                    if (comptLigneVide == 2)
                    {
                        Partie p = new Partie(lstStrLigne);
                        if (p.getBlackElo() <= this.eloSup && p.getBlackElo() >= this.eloInf && p.getWhiteElo() <= this.eloSup && p.getWhiteElo() >= this.eloInf)
                        {
                            this.lstPartie.add(p);
                        }
                        lstStrLigne.clear();
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

        //Libere la liste de la memoire
        this.lstPartie = null;
        System.gc();
    }


    /*
      recherche de tous les elos dans la borne qui sont dans la hasmap puis,
       on ajoute le tableau contenant les lignes du debut et de la fin de la partie a lstLigneParties
     */
    private int trouveElo()
    {
        this.tempsRecherche = System.currentTimeMillis();

        //creation d'une map trié en ordre croissant
        TreeMap<Long, Long> map = new TreeMap<>();
        for (int i = this.eloInf; i <= this.eloSup; i++)
        {
            if (getEloMap().containsKey(i))
            {
                for(long[] l  : getEloMap().get(i)){
                    map.put(l[0], l[1]);
                }
            }
        }

        // ajout des lignes du debu et de fin des parties dans lstLigneParties
        for(Map.Entry<Long, Long> element : map.entrySet()){
            long[] tab = new long[2];
            tab[0] = element.getKey();
            tab[1] = element.getValue();
            this.lstLigneParties.add(tab);
        }
        return this.lstLigneParties.size();
    }
}
