package partie;


import utils.Colors;

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
        this.nameOuverture = allLines.get(12).split("\"")[2];
        System.out.println(allLines.get(12));
    }


    public String toString()
    {
        return "Joueur blanc : " + Colors.cyan+blanc+Colors.reset + " contre joueur noir : " + Colors.green+noir+Colors.reset + ".\n" +
                "Elo " + Colors.cyan+blanc+Colors.reset + " : " + whiteElo + ", elo " + Colors.green+noir+Colors.reset + " : " + blackElo + ".\n" +
                "Ouverture : " + nameOuverture + ".\n";
    }
}
