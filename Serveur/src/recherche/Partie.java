package recherche;


import utils.Colors;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class Partie
{
    private String blanc;
    private String noir;
    private String partieLink;
    private String gagnant;
    private String resultat;
    private String utcDate;
    private String utcTime;
    private int whiteElo;
    private int blackElo;
    private String ouverture;
    private String premierCoup;
    private String termination;

    public Partie(List<String> allLines) throws ParseException
    {
        for (String str : allLines)
        {
            String[] buff = str.replaceAll("[\\[\\]]", "").split("[\"]");// on eneleve les espaces et les crochets et on divise la chine a partire des "
            buff[0] = buff[0].replace(" ", "");
            switch (buff[0])
            {
                case "White" -> {this.blanc = buff[1];}
                case "Black" -> {this.noir = buff[1];}
                case "Site" -> {this.partieLink = buff[1];}
                case "Result" -> {
                    this.resultat = buff[1];
                    if (buff[1].charAt(0) == '1') {this.gagnant = this.blanc;} else this.gagnant = this.noir;
                }
                case "UTCDate" -> {
                    this.utcDate = new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("yy.MM.dd").parse(buff[1]));
                }
                case "UTCTime" -> {this.utcTime = buff[1];}
                case "WhiteElo" -> {this.whiteElo = Integer.parseInt(buff[1]);}
                case "BlackElo" -> {this.blackElo = Integer.parseInt(buff[1]);}
                case "Opening" -> {if (buff[1].equals("?")) {this.ouverture = "";} else this.ouverture = buff[1];}
                case "Termination" -> {this.termination = buff[1];}
            }
        }
        try
        {

            this.premierCoup = allLines.get(allLines.size() - 1).split(" ")[1];
            if (this.premierCoup.equals(this.resultat)) {this.premierCoup = "";}
        } catch (java.lang.ArrayIndexOutOfBoundsException e)
        {
            System.out.println("Impossible de trouver le premier coup!!");
            this.premierCoup = "";
        }
    }


    public String toString()
    {
        return "Date de la partie : " + Colors.reset + Colors.YELLOW_BOLD + utcDate + " " + utcTime + Colors.reset + ".\n" +
                "Joueur blanc : " + Colors.cyan + blanc + Colors.reset + " contre joueur noir : " + Colors.green + noir + Colors.reset + ".\n" +
                "Elo " + Colors.cyan + blanc + Colors.reset + " : " + whiteElo + ", elo " + Colors.green + noir + Colors.reset + " : " + blackElo + ".\n" +
                "Lien de la partie : " + Colors.reset + Colors.purple + partieLink + Colors.reset + ".\n" +
                "Ouverture : " + Colors.reset + ouverture + ". Premier coup : " + premierCoup + ".\n" +
                "Etat de la partie : " + this.termination + ".\n" +
                "Resultat : " + Colors.yellow + resultat + Colors.reset + ". Le gagnant est : " + Colors.redBold + this.gagnant + Colors.reset + ".";
    }
}
