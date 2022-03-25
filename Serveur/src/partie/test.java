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
            randomAccessFile.seek(46409184L);
            int compt = 0;
            for (int i = 0; i < 18; i++)
            {
                System.out.println(reader.readLine());
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

