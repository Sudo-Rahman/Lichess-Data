package recherche.autres.pagerank;

import maps.MapsObjet;
import partie.Partie;
import partie.PartiesFile;
import utils.Log;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PageRank
{
    private MapsObjet mapObjet;
    private Map<String, Noeud> mapNoeuds;
    private PartiesFile partiesFile;
    private int nbThreasOperationelle;
    private Log log = new Log();

    public PageRank(MapsObjet mapObjet)
    {
        this.mapNoeuds = new ConcurrentHashMap<>();
        this.mapObjet = mapObjet;
        this.partiesFile = new PartiesFile(mapObjet.getFile());
        this.nbThreasOperationelle = Runtime.getRuntime().availableProcessors() / 2;
    }

    public void calcule()
    {
        long temp = System.currentTimeMillis();
        for (Object joueur : mapObjet.getNameMap().keySet())
        {
            mapNoeuds.put((String) joueur, new Noeud((String) joueur, 1 / mapObjet.getNbParties()));
        }
        int nbEntry = 0;
        int avant = 0;
        int apres = 0;
        for (Map.Entry<Object, List<Long>> entry : mapObjet.getNameMap().entrySet())
        {
            new Thread(() -> calculeNoeuds(partiesFile.getAllParties(entry.getValue()))).start();
            apres = nbEntry * 100 / mapObjet.getNameMap().size();
            if (apres != avant) log.info("pageRank calcule : " + apres + "%");
            avant = apres;
            nbEntry++;
        }
        log.info("pageRank calcule : 100%");
        finished();
        log.info("Temps de calcul : " + (System.currentTimeMillis() - temp) / 1000 + " s");
    }

    private synchronized void finished()
    {
        if (getNbThreasOperationelle() != 8)
        {
            try {this.wait();} catch (InterruptedException e) {e.printStackTrace();}
        }
    }

    private synchronized int getNbThreasOperationelle()
    {
        return nbThreasOperationelle;
    }

    private synchronized void add(int nbThreasOperationelle)
    {
        this.nbThreasOperationelle += nbThreasOperationelle;
        this.notifyAll();
    }

    private void calculeNoeuds(List<Partie> parties)
    {
        while (getNbThreasOperationelle() == 0)
        {
            try {this.wait();} catch (InterruptedException e) {e.printStackTrace();}
        }
        add(-1);
        for (Partie partie : parties)
        {
            if (partie.getGagnant().equals(partie.getNoir()))
            {
                mapNoeuds.get(partie.getBlanc()).addLien(partie.getNoir());
            } else mapNoeuds.get(partie.getNoir()).addLien(partie.getBlanc());
        }
        add(+1);
    }
}
