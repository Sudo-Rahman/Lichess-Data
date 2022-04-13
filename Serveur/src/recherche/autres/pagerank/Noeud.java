package recherche.autres.pagerank;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Classe représentant un noeud dans le graphe, avec ses liens entrants et sortants.
 *
 * @author Yilmaz Rahman
 * @version 1.0
 * @date 02/04/2022
 */
public class Noeud
{
    private final String joueur;


    private Map<String, Integer> liensEtrants;
    private Map<String, Integer> liensSortants;
    private double AncienScore;
    private double score;

    /**
     * @param joueur Le username du joueur.
     * @param score  Le score pageRank du joueur.
     */
    public Noeud(String joueur, double score)
    {
        this.joueur = joueur;
        this.liensEtrants = new ConcurrentHashMap<>();
        this.liensSortants = new ConcurrentHashMap<>();
        this.AncienScore = score;
    }

    public Map<String, Integer> getLiensEtrants()
    {
        return liensEtrants;
    }

    public Map<String, Integer> getLiensSortants()
    {
        return liensSortants;
    }

    public double getScore()
    {
        return score;
    }

    public void setScore(double score)
    {
        this.score = score;
    }

    public double getAncienScore()
    {
        return AncienScore;
    }

    public void setAncienScore(double acienScore)
    {
        AncienScore = acienScore;
    }

    /**
     * Ajoute a la liste des liens entrants le joueur en paramètre.
     *
     * @param joueur Le username du joueur.
     */
    public void addLienEtrants(String joueur)
    {
        if (!this.liensEtrants.containsKey(joueur))
        {
            this.liensEtrants.put(joueur, 1);
        } else
        {
            this.liensEtrants.replace(joueur, this.liensEtrants.get(joueur) + 1);
        }
    }

    /**
     * Ajoute a la liste des liens sortants le joueur en paramètre.
     *
     * @param joueur Le username du joueur.
     */
    public void addLienSortants(String joueur)
    {
        if (!this.liensSortants.containsKey(joueur))
        {
            this.liensSortants.put(joueur, 1);
        } else
        {
            this.liensSortants.replace(joueur, this.liensSortants.get(joueur) + 1);
        }
    }

    /**
     * @return Le username du joueur du noeud actuel.
     */
    public String getJoueur()
    {
        return this.joueur;
    }

    /**
     * @param joueur Le username du joueur.
     * @return Le nombre d'occurrence du joueur en paramètre dans la liste des liens entrant.
     */
    public int nbLiensEntrants(String joueur)
    {
        return this.liensEtrants.get(joueur);
    }

    /**
     * @return Le nombre de liens entrants.
     */
    public int nbLiensEntrants()
    {
        int nb = 0;
        for (String joueur : this.liensEtrants.keySet())
        {
            nb += this.liensEtrants.get(joueur);
        }
        return nb;
    }

    /**
     * @return Le nombre de liens sortants.
     */
    public int nbLiensSortants()
    {
        int nb = 0;
        for (String joueur : this.liensSortants.keySet())
        {
            nb += this.liensSortants.get(joueur);
        }
        return nb;
    }

    /**
     * @param joueur Le username du joueur.
     * @return Le nombre d'occurrence du joueur en paramètre dans la liste des liens sortant.
     */
    public synchronized int nbLiensSortants(String joueur)
    {
        return this.liensSortants.get(joueur);
    }

}
