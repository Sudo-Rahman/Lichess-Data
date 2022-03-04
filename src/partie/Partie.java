package partie;


import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Partie
{
    private String blanc;
    private String noir;
    private String stringPartieLink;
    private String gagnant;
    private int whiteElo;
    private int blackElo;
    private String nameOuverture;

    public Partie(List<String> allLines)
    {
        this.blanc = allLines.get(2).split("\"")[1];
        this.noir = allLines.get(3).split("\"")[1];
        if (allLines.get(4).split("[\"-]")[1].equals("1"))
            this.gagnant = this.blanc;
        else
            this.gagnant = this.noir;
        this.stringPartieLink = allLines.get(1).split("\"")[1];

    }
}
