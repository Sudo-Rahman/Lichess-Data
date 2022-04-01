package recherche.autres;

import maps.MapsObjet;
import recherche.Recherche;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Thread.sleep;
import static java.nio.charset.StandardCharsets.UTF_8;

public class NbCoupsConsecutifsParties extends Recherche
{
    private ConcurrentHashMap<String, List<Long>> map;
    private long nbOctetsParThread;
    private long nbOctetsLu;
    private boolean creeMapOk;


    public NbCoupsConsecutifsParties(ObjectInputStream clientReader, BufferedWriter clientWriter, MapsObjet mapObjet)
    {
        super(clientReader, clientWriter, mapObjet);
        this.map = new ConcurrentHashMap<>();
        this.creeMapOk = false;

    }

    @Override
    public void cherche()
    {
        int nbThreads = Runtime.getRuntime().availableProcessors();
        this.nbOctetsParThread = mapObjet.getFile().length() / nbThreads;
        List<Thread> lstThreads = new ArrayList<>();
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++)
        {
            int finalI = i;
            Thread thread = new Thread(() ->
            {
                try {calcule(nbOctetsParThread * finalI);} catch (IOException e)
                {
                    e.printStackTrace();
                    System.exit(-1);
                }
            });
            thread.start();
            lstThreads.add(thread);
        }
        new Thread(this::afficheOctetLu).start();
        for (Thread thread : lstThreads) {try {thread.join();} catch (InterruptedException e) {e.printStackTrace();}}
        this.creeMapOk = true;
        int max = 0;
        Object key = new Object();
        for (Map.Entry<String, List<Long>> ele : map.entrySet())
        {
            if(max<ele.getValue().size() && ele.getKey().split("\\|").length>10){
                max = ele.getValue().size();
                key = ele.getKey();
            }
        }
        System.out.println("Nombre de coups consecutifs : " + key + " (" + max + ")");

    }

    private void calcule(long deb) throws IOException
    {
        BufferedReader reader = null;
        FileInputStream in = null;
        in = new FileInputStream(mapObjet.getFile());
        reader = new BufferedReader(new InputStreamReader(in));
        in.getChannel().position(deb);

        // variables pour connaitre l'octet de debut et fin d'une partie
        Long octetDeb = in.getChannel().position();
        int comptLigne = 0;

        int partie = 0;

        int octetOffset = 0;

        List<String> lstStr = new ArrayList<>();
        String str;

        while ((str = reader.readLine()) != null && octetDeb <= deb + nbOctetsParThread + 5000)
        {
            if (str.equals("") && partie == 0)
            {
                octetDeb += 1;
            } else if (!str.contains("[Event \"") && partie == 0)
            {
                octetDeb += str.getBytes(UTF_8).length + 1;
            } else
            {
                partie++;
                if (str.equals("")) comptLigne++;
                else lstStr.add(str);
            }

            if (comptLigne == 2)
            {
                octetOffset += 1;// dans chaque partie il y a deux sauts de ligne, mais ils sont comptabilisés à 1 et non 2
                for (String string : lstStr)
                {
                    octetOffset += string.getBytes(UTF_8).length + 1;// +1, car a la fin de la ligne il y a le character de retour ligne '\n'
                    String[] buf = string.replaceAll("[\\[\\]]", "").split("\"");
                    buf[0] = buf[0].replaceAll(" ", "");
                    if (string.split(" ")[0].equals("1."))
                    {
                        //map pour les nombre de coups
                        List<String> lst = new ArrayList<>(List.of(string.split("[{}]")));
                        lst.removeIf(strr -> strr.contains("%eval") || strr.contains("%clk"));
                        lst = new ArrayList<>(List.of(String.join("", lst).split(" ")));
                        lst.removeIf(strr -> strr.equals("") || strr.contains("."));
                        lst.remove(lst.size() - 1);
                        String couts;
                        List<String> lstCouts = new ArrayList<>();
                        for (int i = 1; i < lst.size(); i++)
                        {
                           lstCouts.addAll(lst.subList(0,i));
                            couts = String.join("|", lstCouts).replaceAll("[!?]", "");
                            if (this.map.containsKey(couts)) this.map.get(couts).add(octetDeb);
                            else
                                this.map.put(couts, Collections.synchronizedList(new ArrayList<>(Collections.singletonList(octetDeb))));
                            lstCouts.clear();

                            lstCouts.addAll(lst.subList(lst.size() - 1-i,lst.size()-1));
                            couts = String.join("|", lstCouts).replaceAll("[!?]", "");
                            if (this.map.containsKey(couts)) this.map.get(couts).add(octetDeb);
                            else
                                this.map.put(couts, Collections.synchronizedList(new ArrayList<>(Collections.singletonList(octetDeb))));
                            lstCouts.clear();
                        }
                    }
                }
                comptLigne = 0;
                addOctetsLu(octetOffset + 1);
                octetDeb += octetOffset + 1;
                lstStr.clear();
                octetOffset = 0;
            }
        }
    }

    private synchronized void addOctetsLu(long l)
    {
        this.nbOctetsLu += l;
    }

    private void afficheOctetLu()
    {
        long tailleFichier = mapObjet.getFile().length();
        long avant = 0L;
        long apres;
        log.info("Lecture en cours : 0%");
        while (!this.creeMapOk)
        {
            apres = this.nbOctetsLu * 100 / tailleFichier;
            if (avant < apres)
            {
                log.info("Lecture en cours : " + apres + "%");
                avant = apres;
            }
            try
            {
                sleep(1000);
            } catch (InterruptedException e) {e.printStackTrace();}
        }
        log.info("Lecture en cours : 100%");
    }
}