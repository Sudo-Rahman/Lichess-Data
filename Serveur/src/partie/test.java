package partie;

import utils.Colors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class test
{
    public static void main(String[] args)
    {
        File f = new File("/home/rahman/Documents/GitHub/Projet-INFO-4B/others/lichess_db_standard_rated_2020-03.pgn");
        try
        {
            RandomAccessFile randomAccessFile = new RandomAccessFile(f, "r");
            FileReader fileReader = new FileReader(randomAccessFile.getFD());
            BufferedReader reader = new BufferedReader(fileReader);
            randomAccessFile.seek(89032341554L);
            int compt = 0;
            for (int i = 0; i < 19; i++)
            {
                System.out.println(reader.readLine());
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

