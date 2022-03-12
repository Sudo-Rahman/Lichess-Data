package recherche;

import java.io.*;
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
                bufferedReader = new BufferedReader(reader,16384);
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        int comptLign = 0;
        HashMap<Long, Partie> partieHashMap = new HashMap<>();
        String ligne = "";
        long partiecount = 0L;
        try
        {
            while ((ligne = bufferedReader.readLine()) != null && partiecount <150)
            {
                if (ligne.equals("")){
                    comptLign++;
                }else{
                lst.add(ligne);}
                if (comptLign == 2)
                {
                    comptLign = 0;
                    partieHashMap.put(partiecount, new Partie(lst));
                    partiecount++;
//                    System.out.println(lst);
                    lst.clear();
                }

            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println(partieHashMap.get(50L).toString());
    }
}

