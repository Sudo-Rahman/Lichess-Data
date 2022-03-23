/*
 * Nom de classe : partie
 *
 * Description   : Class partie qui regroupe tous les elements d'une partie, il n'y a pas tout comme le "TimeControl" c'est un choix personnel.
 *
 * Version       : 1.0
 *
 * Date          : 12/03/2022
 *
 * Copyright     : Yilmaz Rahman, Colliat Maxime
 *
 */


package partie;


import utils.Colors;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Partie implements Serializable
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
    private List<String> lstCoup;
    private String premierCoup;
    private String termination;

    /**
     * Constructeur qui parse les données de la liste pour recuperer les elements d'une partie a partir de String.
     */
    public Partie(List<String> allLines)
    {
        for (String str : allLines)
        {
            String[] buff = str.replaceAll("[\\[\\]]", "").split("[\"]");// on eneleve les espaces et les crochets et on divise la chine a partire des "
            buff[0] = buff[0].replace(" ", "");
            switch (buff[0])
            {
                case "White" -> this.blanc = buff[1];
                case "Black" -> this.noir = buff[1];
                case "Site" -> this.partieLink = buff[1];
                case "Result" -> {
                    this.resultat = buff[1];
                    if (buff[1].charAt(0) == '1') {this.gagnant = this.blanc;}
                    else this.gagnant = this.noir;
                }
                case "UTCDate" -> {
                    try
                    {
                        this.utcDate = new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("yy.MM.dd").parse(buff[1]));
                    } catch (ParseException e)
                    {
                        e.printStackTrace();
                    }
                }
                case "UTCTime" -> this.utcTime = buff[1];
                case "WhiteElo" -> {try {this.whiteElo = Integer.parseInt(buff[1]);} catch (NumberFormatException e) {}}
                case "BlackElo" -> {try {this.blackElo = Integer.parseInt(buff[1]);} catch (NumberFormatException e) {}}
                case "Opening" -> {
                    if (buff[1].equals("?")) {this.ouverture = "";}
                    else this.ouverture = buff[1];
                }
                case "Termination" -> this.termination = buff[1];
            }
        }
        try
        {
            this.premierCoup = allLines.get(allLines.size() - 1).split(" ")[1];// on recupere le premier coup
            if (this.premierCoup.equals(this.resultat))
            {
                this.premierCoup = "";
            }// si le premier coup est egale au resultat alors il n'y a pas de premier coup
            else
            {
                this.lstCoup = new ArrayList<>(List.of(allLines.get(allLines.size() - 1).split("[{}]")));
                removAcollade();
            }
        } catch (java.lang.ArrayIndexOutOfBoundsException e)
        {
            System.out.println("Impossible de trouver le premier coup!!");
            this.premierCoup = "";
        }
    }

    /**
     * accesseur
     */
    public int getWhiteElo()
    {
        return whiteElo;
    }

    public int getBlackElo()
    {
        return blackElo;
    }

    private void removAcollade()
    {
        this.lstCoup.removeIf(str -> str.contains("%eval"));
        this.lstCoup = new ArrayList<>(List.of(String.join("", this.lstCoup).split(" ")));
        this.lstCoup.removeIf(str -> str.contains("..."));
        this.lstCoup = Collections.singletonList(String.join(" ", this.lstCoup));
    }


    public String toString()
    {
        return "Date de la partie : " + Colors.reset + Colors.YELLOW_BOLD + utcDate + " " + utcTime + Colors.reset + ".\n" + "Joueur blanc : " + Colors.cyan + blanc + Colors.reset + " contre joueur noir : " + Colors.green + noir + Colors.reset + ".\n" + "Elo " + Colors.cyan + blanc + Colors.reset + " : " + whiteElo + ", elo " + Colors.green + noir + Colors.reset + " : " + blackElo + ".\n" + "Lien de la partie : " + Colors.reset + Colors.purple + partieLink + Colors.reset + ".\n" + "Ouverture : " + Colors.reset + ouverture + ". Premier coup : " + premierCoup + ".\n" + "Etat de la partie : " + this.termination + ".\n" + "Partie : " + this.lstCoup + "\n" + "Resultat : " + Colors.yellow + resultat + Colors.reset + ". Le gagnant est : " + Colors.redBold + this.gagnant + Colors.reset + ".";
    }
}
