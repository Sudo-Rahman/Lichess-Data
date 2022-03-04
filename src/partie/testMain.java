package partie;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class testMain
{
    public static void main(String[] args)
    {
        File testFile = new File("/Users/sr-71/Documents/GitHub/Projet-INFO-4B/lichess_db_standard_rated_2016-07.pgn");
        FileReader reader;
        BufferedReader bufferedReader = null;
        List<String> lst = new ArrayList<>();
        int nbLines=0;
        try
        {
            nbLines = (int) Files.lines(Path.of(testFile.getAbsolutePath())).count();
            System.out.println(""+nbLines);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

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
        int compt = 0;
        while (compt < nbLines)
        {
            try
            {
                if(compt >= nbLines - 18) lst.add(bufferedReader.readLine());
                compt++;
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        System.out.println(lst);
        Partie p = new Partie(lst);
    }
}

