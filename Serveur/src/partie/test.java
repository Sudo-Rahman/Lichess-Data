package partie;

import java.io.*;
import java.util.*;

public class test
{
    public static void main(String[] args)
    {
        File f = new File("/Users/sr-71/Documents/GitHub/Projet-INFO-4B/others/lichess_db_standard_rated_2016-07.pgn");
        String blanc = null;
        String noir = null;
        long lines = 0L;
        long lineDeb = 0L;
        long lineFin;
        int comptligne = 0;
        long tempsRecherche = System.currentTimeMillis();
        HashMap<String, List<long[]>> lst = new HashMap<>();
        String str;
        try (BufferedReader reader = new BufferedReader(new FileReader(f)))
        {
            while ((str = reader.readLine()) != null)
            {
                if (str.equals(""))
                {
                    comptligne++;
                }
                String[] buf = str.replaceAll("[\\[\\]]", "").split("\"");
                buf[0] = buf[0].replaceAll(" ", "");
                if (buf[0].equals("White"))
                {
                    blanc = buf[1];
                }
                if (buf[0].equals("Black"))
                {
                    noir = buf[1];
                }
                if (comptligne == 2)
                {
                    lineFin = lines;
                    long[] tab = new long[2];
                    tab[0] = lineDeb;
                    tab[1] = lineFin;
                    if (lst.containsKey(blanc))
                        lst.get(blanc).add(tab);
                    else
                        lst.put(blanc, new ArrayList<>(Collections.singletonList(tab)));
                    if (lst.containsKey(noir))
                        lst.get(noir).add(tab);
                    else
                        lst.put(noir, new ArrayList<>(Collections.singletonList(tab)));
                    comptligne = 0;
                    lineDeb = lines;
                }
                lines++;
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        System.out.println((double) (System.currentTimeMillis() - tempsRecherche) / 1000 + "  " + lines);
        for (long[] t : lst.get("eisaaaa"))
        {
            System.out.println(Arrays.toString(t));
        }
    }
}
