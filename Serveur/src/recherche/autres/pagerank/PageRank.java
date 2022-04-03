package recherche.autres.pagerank;

import maps.MapsObjet;
import partie.Partie;
import partie.PartiesFile;
import semaphore.Semaphore;
import utils.Colors;
import utils.Log;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Algorithme de PageRank pour les parties d'échec stockées dans un fichier de données lichess.
 *
 * @author Yilmaz Rahman
 * @version 1.0
 * @date 12/04/2020
 * @see <a href="https://en.wikipedia.org/wiki/PageRank">PageRank</a>
 */
public class PageRank
{
    private final MapsObjet mapObjet;
    private final Map<String, Noeud> mapNoeuds;
    private final PartiesFile partiesFile;
    private final Log log = new Log();
    private final Semaphore semaphore;
    private final int nbIterations = 10;
    private TreeMap<Double, String> mapPagerank;
    private String description;

    /**
     * @param mapObjet mapObjet qui contient toutes les hashmaps.
     */
    public PageRank(MapsObjet mapObjet,String description)
    {
        this.mapNoeuds = new ConcurrentHashMap<>();
        this.mapObjet = mapObjet;
        this.partiesFile = new PartiesFile(mapObjet.getFile());
        this.semaphore = new Semaphore(Runtime.getRuntime().availableProcessors() / 2);
        this.mapPagerank = new TreeMap<>(Collections.reverseOrder());
        this.description = description;
    }

    public void cherche()
    {
        String nomFichier = String.join("_",description.replaceAll("[,:]","").split(" {2}"))  ;
        File pageRankFile = new File(mapObjet.getFile().getAbsolutePath().split("\\.")[0]+nomFichier + ".pageRankMap");
        if (pageRankFile.exists())
        {
            log.info("PageRank trouvé dans le fichier pageRank.");
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(pageRankFile)))
            {
                mapPagerank = (TreeMap<Double, String>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {e.printStackTrace();}
        } else
        {
            log.info("Calcul du PageRank");
            calcule();
            try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(pageRankFile))){
                oos.writeObject(mapPagerank);
            } catch (IOException e) {e.printStackTrace();}
        }
    }

    private void calcule()
    {
        long temp = System.currentTimeMillis();

        //Creation des nœuds.
        for (Object joueur : mapObjet.getNameMap().keySet())
        {
            mapNoeuds.put((String) joueur, new Noeud((String) joueur, 1.0 / mapObjet.getNameMap().size()));
        }

        // variables qui servent à connaitre l'état d'avancement.
        int nbEntry = 0;
        int pourcentageAvant = 0;
        int pourcentageApres;

        //Recherche des liens entrant et sortant.
        for (Map.Entry<Object, List<Long>> entry : mapObjet.getNameMap().entrySet())
        {
            new Thread(() -> calculeNoeuds(partiesFile.getAllParties(entry.getValue()))).start();
            pourcentageApres = nbEntry * 100 / mapObjet.getNameMap().size();
            if (pourcentageApres != pourcentageAvant) log.info("pageRank creation Noeud : " + pourcentageApres + "%");
            pourcentageAvant = pourcentageApres;
            nbEntry++;
        }
        log.info("pageRank creation Noeud : 100%");
        semaphore.finished();// on attend que tous les threads aient finis.

        nbEntry = 0;
        pourcentageAvant = 0;

        //nbIterations de calcul de pageRank.
        for (int i = 0; i < this.nbIterations; i++)
        {
            // pour chaque nœud on calcule son score de pageRank.
            for (Map.Entry<String, Noeud> entry : mapNoeuds.entrySet())
            {
                new Thread(() -> calculePageRank(entry)).start();
                pourcentageApres = i * 100 / this.nbIterations;
                if (pourcentageApres != pourcentageAvant) log.info("pageRank calcule : " + pourcentageApres + "%");
                pourcentageAvant = pourcentageApres;
                nbEntry++;
            }
            semaphore.finished();

            // on remplace l'ancien score par le score calculer pour tous les noeuds.
            for (Noeud noeud : mapNoeuds.values())
            {
                noeud.setAncienScore(noeud.getScore());
            }
        }
        log.info("pageRank calcule : 100%");
        log.info("Temps de calcul : " + (System.currentTimeMillis() - temp) / 1000 + " s");

        // on trie les scores par ordre décroissants.
        for (Map.Entry<String, Noeud> entry : mapNoeuds.entrySet())
        {
            this.mapPagerank.put(entry.getValue().getScore(), entry.getKey());
        }
    }

    /**
     * calculePageRank calcule pour chaque nœud le score de pageRank en fonction de ses liens Entrants c'es à dire des victoires du joueur.
     *
     * @param entry entry qui contient le nom du joueur et le nœud associé.
     */
    private void calculePageRank(Map.Entry<String, Noeud> entry)
    {
        semaphore.acquire();
        double somme = 0.0;
        for (String joueur : entry.getValue().getLiensEtrants().keySet())
        {
            somme += (double) mapNoeuds.get(joueur).nbLiensSortants(entry.getKey()) / mapNoeuds.get(joueur).nbLiensSortants() * mapNoeuds.get(joueur).getAncienScore();
        }
        entry.getValue().setScore(0.15 / mapObjet.getNameMap().size() + 0.85 * somme);
        semaphore.release();
    }


    /**
     * calculeNoeuds ajoute les liens entrant et sortant dans les nœuds.
     *
     * @param parties liste des parties.
     */
    private void calculeNoeuds(List<Partie> parties)
    {
        semaphore.acquire();
        for (Partie partie : parties)
        {
            if (partie.getGagnant().equals(partie.getNoir()))
            {
                mapNoeuds.get(partie.getBlanc()).addLienSortants(partie.getNoir());
                mapNoeuds.get(partie.getNoir()).addLienEtrants(partie.getBlanc());
            } else
            {
                mapNoeuds.get(partie.getNoir()).addLienSortants(partie.getBlanc());
                mapNoeuds.get(partie.getBlanc()).addLienEtrants(partie.getNoir());
            }
        }
        semaphore.release();
    }

    /**
     * @return retour des 10 meilleurs scores.
     */
    public String toString()
    {
        StringBuilder message = new StringBuilder(Colors.BLUE_BRIGHT + "PageRank : ").append(description).append("\n").append(Colors.reset);
        int limite = 0;
        for (Map.Entry<Double, String> entry : this.mapPagerank.entrySet())
        {
            message.append(Colors.GREEN_BOLD).append("Joueur ").append(entry.getValue()).append(", PageRank : ").append(entry.getKey()).append("\n").append(Colors.reset);
            limite++;
            if (limite == 10) break;
        }
        return message.toString();
    }
}