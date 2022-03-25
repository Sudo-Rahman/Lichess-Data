package partie;

import java.io.*;
import java.nio.channels.FileChannel;

public class test
{
    public static void main(String[] args)
    {
        File f = new File("/Users/sr-71/Documents/GitHub/Projet-INFO-4B/others/lichess_db_standard_rated_2013-01.pgn");
        try{
            RandomAccessFile randomAccessFile = new RandomAccessFile(f, "r");
            FileReader fileReader = new FileReader(randomAccessFile.getFD());
            BufferedReader reader = new BufferedReader(fileReader);
            randomAccessFile.seek(16668);
            for (int i = 0; i <18;i++){
                System.out.println(randomAccessFile.getFilePointer() + " " +reader.readLine() );

            }
        }catch (Exception e ){
            e.printStackTrace();
        }
    }
}
