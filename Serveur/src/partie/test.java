package partie;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;

public class test
{
    public static void main(String[] args)
    {
        File f = new File("/home/rahman/Documents/GitHub/Projet-INFO-4B/others/lichess_db_standard_rated_2013-01.pgn");
        try
        {
            RandomAccessFile randomAccessFile = new RandomAccessFile(f, "r");
            FileReader fileReader = new FileReader(randomAccessFile.getFD());
            BufferedReader reader = new BufferedReader(fileReader);
            randomAccessFile.seek(0);
            int compt = 0;
            int partie = 0;
            String str;
            while ((str = reader.readLine()) != null)
            {
                if (str.equals("")) compt++;
                if (compt == 2)
                {
                    partie++;
                    compt = 0;
                }
            }
            System.out.println(partie);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

