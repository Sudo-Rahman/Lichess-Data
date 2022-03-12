package partie;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Time;
import java.util.*;


public class testMain
{
    public static void main(String[] args)
    {
        File testFile = new File("/home/rahman/Documents/GitHub/Projet-INFO-4B/others/lichess_db_standard_rated_2016-08.pgn");
        FileReader reader;
        BufferedReader bufferedReader = null;
        List<String> lst = new ArrayList<>();
        int nbLines = 0;
//        try
//        {
//            nbLines = (int) Files.lines(Path.of(testFile.getAbsolutePath())).count();
//            System.out.println(""+nbLines);
//        } catch (IOException e)
//        {
//            e.printStackTrace();
//        }

        {
            try
            {
                reader = new FileReader(testFile);
                bufferedReader = new BufferedReader(reader);
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        int compt = 1;
        HashMap<Long, Partie> partieHashMap = new HashMap<>();
        Date d = new Date();
        long t = d.getTime();
        System.out.println(t);
        String ligne = "";
        long partiecount = 0L;
        try
        {
            while ((ligne = bufferedReader.readLine()) != null && partiecount < 1000)
            {
                lst.add(ligne);
                compt++;
                if (compt == 18)
                {
                    compt = 1;
                    partieHashMap.put(partiecount, new Partie(lst));
                    partiecount++;

                }

            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println(partieHashMap.get(1L).toString());
        t = d.getTime() - t;
    }
}

