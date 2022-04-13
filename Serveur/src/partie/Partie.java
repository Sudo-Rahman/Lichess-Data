package partie;

import utils.Colors;
import utils.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Class partie qui regroupe tous les elements d'une partie, il n'y a pas tout comme le "TimeControl" c'est un choix personnel.
 *
 * @author Yilmaz Rahman
 * @version 1.0
 * @date 12/03/2022
 */
public class Partie
{
    private static final Log log = new Log();
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
     * Constructeur qui cherche les données de la partie et les stockes dans les attributs de la classe.
     *
     * @param allLines liste de toutes les lignes de la partie.
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
                    if (buff[1].charAt(0) == '1') {this.gagnant = this.blanc;} else this.gagnant = this.noir;
                }
                case "UTCDate" -> {
                    try
                    {
                        this.utcDate = new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("yy.MM.dd").parse(buff[1]));
                    } catch (ParseException e)
                    {
                        log.warning("Error parsing");
                    }
                }
                case "UTCTime" -> {
                    try {this.utcTime = buff[1];} catch (ArrayIndexOutOfBoundsException e) {System.out.println(str);}
                }
                case "WhiteElo" -> {
                    try {this.whiteElo = Integer.parseInt(buff[1]);} catch (NumberFormatException e)
                    {
                        log.warning("Impossible de recuperer l'elo");
                    }
                }
                case "BlackElo" -> {
                    try {this.blackElo = Integer.parseInt(buff[1]);} catch (NumberFormatException e)
                    {
                        log.warning("Impossible de recuperer l'elo");
                    }
                }
                case "Opening" -> {
                    if (buff[1].equals("?")) {this.ouverture = "";} else this.ouverture = buff[1];
                }
                case "Termination" -> this.termination = buff[1];
            }
            try
            {
                if (str.split(" ")[0].equals("1."))
                {
                    this.premierCoup = str.split(" ")[1];// on recupere le premier coup
                    if (this.premierCoup.equals(this.resultat))
                    {
                        this.premierCoup = "";
                    }// si le premier coup est égale au resultat alors il n'y a pas de premier coup
                    else
                    {
                        this.lstCoup = new ArrayList<>(List.of(str.split("[{}]")));
                        removAcollade();
                    }
                }
            } catch (java.lang.ArrayIndexOutOfBoundsException e)
            {
                System.out.println("Impossible de trouver le premier coup!!");
                this.premierCoup = "";
            }
        }

    }

    /**
     * accesseurs.
     */
    public String getGagnant()
    {
        return gagnant;
    }

    public String getBlanc()
    {
        return blanc;
    }

    public String getNoir()
    {
        return noir;
    }

    public int getWhiteElo()
    {
        return whiteElo;
    }

    public int getBlackElo()
    {
        return blackElo;
    }

    public List<String> getLstCoup()
    {
        return lstCoup;
    }

    public String getResultat()
    {
        return resultat;
    }

    public String getUtcDate()
    {
        return utcDate;
    }

    public String getUtcTime()
    {
        return utcTime;
    }

    /**
     * Cette fonction permet de supprimer les accolades et les autres character non désirés de la liste de coup.
     */
    private void removAcollade()
    {
        this.lstCoup.removeIf(str -> str.contains("%eval") || str.contains("%clk"));
        this.lstCoup = new ArrayList<>(List.of(String.join("", this.lstCoup).split(" ")));
        this.lstCoup.removeIf(str -> str.contains("..."));
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof Partie p)
        {
            return p.getNoir().equals(this.noir) && p.getBlanc().equals(this.blanc) && p.getResultat().equals(this.resultat) && p.getUtcDate().equals(this.utcDate)
                    && p.getLstCoup().equals(this.lstCoup) && p.getUtcTime().equals(this.utcTime) && p.getWhiteElo() == this.whiteElo && p.getBlackElo() == this.blackElo;
        }
        return false;
    }


    public String toString()
    {
        return "Date de la partie : " + Colors.reset + Colors.YELLOW_BOLD + utcDate + " " + utcTime + Colors.reset + ".\n" +
                "Joueur blanc : " + Colors.cyan + blanc + Colors.reset + " contre joueur noir : " + Colors.green + noir + Colors.reset + ".\n" +
                "Elo " + Colors.cyan + blanc + Colors.reset + " : " + whiteElo + ", elo " + Colors.green + noir + Colors.reset + " : " + blackElo + ".\n" +
                "Lien de la partie : " + Colors.reset + Colors.purple + partieLink + Colors.reset + ".\n" +
                "Ouverture : " + Colors.reset + ouverture + ". Premier coup : " + premierCoup + ".\n" +
                "Etat de la partie : " + this.termination + ".\n" +
                "Partie : " + String.join(" ", this.lstCoup) + "\n" +
                "Resultat : " + Colors.yellow + resultat + Colors.reset + ". Le gagnant est : " + Colors.redBold + this.gagnant + Colors.reset + ".";
    }
}
