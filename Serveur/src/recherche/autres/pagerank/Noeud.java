package recherche.autres.pagerank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Noeud
{
    private String joueur;

    public List<String> getLiens()
    {
        return liens;
    }

    private List<String> liens;
    private long score;


    public Noeud(String joueur, long score)
    {
        this.joueur = joueur;
        this.liens = new ArrayList<>();
        this.score = score;
    }

    public void addLien(String joueur)
    {
        this.liens.add(joueur);
    }

    public String getJoueur()
    {
        return this.joueur;
    }

    public int nbLien(String joueur)
    {
        return Collections.frequency(this.liens, joueur);
    }


}
