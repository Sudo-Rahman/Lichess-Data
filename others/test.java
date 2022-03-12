import java.io.*;
import java.util.*;

public class test
{
    public static ArrayList<String> lst = new ArrayList<String>();

    public static void main(String[] args)
    {
        BufferedReader bufferedReader = null;
        try
        {
            File file = new File("lichess_db_standard_rated_2016-08.pgn");
            FileReader fis = new FileReader(file);
            bufferedReader = new BufferedReader(fis);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        int cont = -1;
        int choix = 0;
        String texte;
        boolean inFile;
        try
        {
            while ((texte = bufferedReader.readLine()) != null && choix != 2)
            {
                lst.add(texte);
                inFile = texte.contains("chesspasky");
                cont++;
                if (inFile)
                {

                }
                if (cont == 17)
                {
//                    print();
                    if(choix != 1){

                        lst.removeAll(lst);
                    }
//                    System.exit(-1);
                    cont = 0;
                    choix++;

                }
            }
            bufferedReader.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        print();
    }

    public static void print()
    {
        for (String s : lst)
        {
            System.out.println(s);

        }
    }
}