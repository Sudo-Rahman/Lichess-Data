package partie;

import java.io.File;

public class test
{
    public static void main(String[] args)
    {
        File f = new File("/home/rahman/Documents/GitHub/Projet-INFO-4B/others/lichess_db_standard_rated_2016-08.pgn");
        //        try (Stream<String> all_lines = Files.lines(Path.of(f.getAbsolutePath()))) {
        //            String specific_line_15 = all_lines.skip(1111111101).findFirst().get();
        //            System.out.println(specific_line_15);
        //        } catch (IOException e)
        //        {
        //            e.printStackTrace();
        //        }

        //        try
        //        {
        //            RandomAccessFile re = new RandomAccessFile(f,"r");
        //            re.skipBytes(69);
        //            System.out.println(re.readLine() + " "+ re.getFilePointer());
        //        } catch (Exception e)
        //        {
        //            e.printStackTrace();
        //        }

    }
}
