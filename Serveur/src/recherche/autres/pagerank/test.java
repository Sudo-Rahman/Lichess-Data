package recherche.autres.pagerank;

import java.util.HashMap;
import java.util.Map;

public class test
{
    public static void main(String[] args)
    {

        Map<String, Noeud> map = new HashMap<>();

        Noeud joueur1 = new Noeud("joueur1", 1 / 3.0);
        Noeud joueur2 = new Noeud("joueur2", 1 / 3.0);
        Noeud joueur3 = new Noeud("joueur3", 1 / 3.0);

        map.put("joueur1", joueur1);
        map.put("joueur2", joueur2);
        map.put("joueur3", joueur3);
        
        joueur1.getLiensEtrants().put("joueur2", 1);
        joueur1.getLiensSortants().put("joueur3", 1);
        
        joueur2.getLiensEtrants().put("joueur3", 1);
        joueur2.getLiensSortants().put("joueur1", 1);
        joueur2.getLiensSortants().put("joueur3", 2);
        
        joueur3.getLiensEtrants().put("joueur1", 1); 
        joueur3.getLiensEtrants().put("joueur2", 2);
        joueur3.getLiensSortants().put("joueur2", 1);
        
        for (int i = 0; i < 10; i++)
        {
            for (Map.Entry<String, Noeud> entry : map.entrySet()){
                double somme = 0.0;
                for(String joueur : entry.getValue().getLiensEtrants().keySet())
                {
                    somme += (double) map.get(joueur).nbLiensSortants(entry.getKey()) /
                            map.get(joueur).nbLiensSortants() * map.get(joueur).getAncienScore();
                }
                entry.getValue().setScore(0.15 / map.size() + 0.85 * somme);
            }
            for (Map.Entry<String, Noeud> entry : map.entrySet()){
                entry.getValue().setAncienScore(entry.getValue().getScore());
                System.out.println(entry.getKey() + " : " + entry.getValue().getScore());
            }
            System.out.println("\n");
        }

    }
}
