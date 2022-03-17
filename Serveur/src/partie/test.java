package partie;

import read.file.ReadFile;

import java.io.*;
import java.util.*;

public class test
{
    public static void main(String[] args)
    {
        File f = new File("/home/rahman/Documents/GitHub/Projet-INFO-4B/others/lichess_db_standard_rated_2013-01.pgn");
//        try
//        {
//            RandomAccessFile re = new RandomAccessFile(f,"r");
//            re.skipBytes(69);
//            System.out.println(re.readLine() + " "+ re.getFilePointer());
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//        }

        ReadFile r = new ReadFile(f.getAbsolutePath());
        r.read();
    }
}
