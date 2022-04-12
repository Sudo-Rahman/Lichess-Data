package choix;

import maps.MapsObjet;
import recherche.RecherchePartieSpecifique;
import recherche.autres.AfficheToutesLesParties;
import recherche.autres.CinqOuverturesPlusJoue;
import recherche.autres.JoueursLesplusActifs;
import recherche.autres.NbCoupsConsecutifsParties;
import recherche.autres.pagerank.PageRank;
import recherche.partie.specifique.*;
import utils.Colors;
import utils.Log;

import java.io.*;

import static java.lang.Thread.sleep;
import static utils.Colors.reset;

/**
 * Classe qui gere tous les choix du client.
 *
 * @author Yilmaz Rahman
 * @version 1.0
 * @date 13/03/2022
 */
public class InitChoix
{
    private final BufferedWriter writer;
    private final ObjectInputStream objectInputStream;
    private final MapsObjet mapObjet;
    private final int mode;
    private final String description;
    private boolean quitte;

    private final Log log = new Log();

    /**
     * @param mode        Le mode de recherche : si c'est une iteration ou une recherche classique.
     * @param description La description de la recherche.
     * @param o           L'ObjectInputStream du client.
     * @param b           Le BufferedWriter du client.
     * @param mapObjet    L'instance de la classe MapsObjet.
     */
    public InitChoix(int mode, String description, ObjectInputStream o, BufferedWriter b, MapsObjet mapObjet)
    {
        this.mode = mode;
        this.objectInputStream = o;
        this.writer = b;
        this.mapObjet = mapObjet;
        this.quitte = false;
        this.description = description;

        while (envoieMessage(afficheChoix()) != -1)
        {
            while (demandeRestant() == 0)
            {
                envoieMessage("Trop de demande patienter");
                try
                {
                    sleep(10000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            envoieMessage("Saissisez votre choix : ");
            switch (litInt())
            {
                case 0 -> {
                    if (mode == 1) quitte = true;
                }
                case 1 -> choix1();
                case 2 -> choix2();
                case 3 -> choix3();
                case 4 -> choix4();
                case 5 -> choix5();
                case 6 -> choix6();
                case 7 -> choix7();
                case 8 -> choix8();
                case 9 -> choix9();
                default -> envoieMessage("Le nombre n'est pas bon !!");
            }
            if (quitte)
            {
                envoieMessage(Colors.PURPLE_BOLD + "Vous avez quitter le mode iterative!!" + reset);
                break;
            }
            System.gc();
        }
    }


    private void choix1()
    {
        envoieMessage("\n" + Colors.green + "1/ Recherche de partie(s) en fonction du premier coup" + reset + ".\n" +
                Colors.cyan + "2/ Recherche de partie(s) en fonction de l'elo des joueur" + reset + ".\n" +
                Colors.green + "3/ Recherche de partie(s) en fonction de la date" + reset + ".\n");
        int choix;
        do
        {

            envoieMessage("Donner votre choix");
            choix = litInt();
            if (choix == -1)
            {
                log.fatal("Impossible de lire le flux du client !!!");
                break;
            }
        } while (choix > 3 || choix < 1);
        incrementeDemande(-1);
        RecherchePartieSpecifique recherche = null;
        switch (choix)
        {
            case 1 -> {
                recherche = new RechereEnFonctionDuPremierCoup(objectInputStream, writer, mapObjet);
                recherche.cherche();
            }
            case 2 -> {
                recherche = new RechercheEnFonctionEloJoueur(objectInputStream, writer, mapObjet);
                recherche.cherche();
            }
            case 3 -> {
                recherche = new RechercheEnFonctionDate(objectInputStream, writer, mapObjet);
                recherche.cherche();
            }
        }
        assert recherche != null;
        incrementeDemande(1);
        iteration(recherche);
    }

    private void choix2()
    {
        incrementeDemande(-1);
        RecherchePartieJoueur recherche = new RecherchePartieJoueur(objectInputStream, writer, mapObjet);
        recherche.cherche();
        incrementeDemande(1);
        iteration(recherche);
    }

    private void choix3()
    {
        CinqOuverturesPlusJoue recherche = new CinqOuverturesPlusJoue(objectInputStream, writer, mapObjet);
        recherche.cherche();
    }

    private void choix4()
    {
        incrementeDemande(-1);
        RechercheEnFonctionDuNombreDeCoup recherche = new RechercheEnFonctionDuNombreDeCoup(objectInputStream, writer, mapObjet);
        recherche.cherche();
        incrementeDemande(1);
        iteration(recherche);
    }


    private void choix5()
    {
        incrementeDemande(-1);
        JoueursLesplusActifs recherche = new JoueursLesplusActifs(objectInputStream, writer, mapObjet);
        recherche.cherche();
        incrementeDemande(1);
    }


    private void choix6()
    {
        incrementeDemande(-1);
        new Thread(() ->
        {
            PageRank pageRank = new PageRank(mapObjet, description);
            pageRank.cherche();
            envoieMessage(pageRank.toString());
            pageRank = null;
            incrementeDemande(1);
        }).start();
    }

    private void choix7()
    {
        incrementeDemande(-1);
        NbCoupsConsecutifsParties recherche = new NbCoupsConsecutifsParties(objectInputStream, writer, mapObjet, description);
        new Thread(() ->
        {
            recherche.cherche();
            incrementeDemande(-1);
        }).start();
    }

    private void choix8()
    {
        AfficheToutesLesParties recherche = new AfficheToutesLesParties(objectInputStream, writer, mapObjet);
        recherche.cherche();
    }

    private void choix9()
    {
        envoieMessage(Colors.BLUE_BRIGHT + "\nIl y à : " + mapObjet.getNbParties() + " parties.\n" + reset);
    }


    /**
     * methode qui prend en parametre un objet de type RecherchePartieSpecifique,
     * Il regarde si la recherche est en mode iterative ou non,
     * Si elle l'est, il regarde si il y a un fichier qui contient deja un MapObjet pour les donnees,
     * Si le fichier exist on charge l'objet, sinon on lance la creation de l'objet on l'écrit dans un fichier pour ne pas à le recree a chaque fois,
     * ensuite on initialise un nouveau InitChoix avec le MapObjet et la description de l'iteration.
     * Si la recherche n'est pas en mode iterative on ne fait rien.
     *
     * @param recherche Instance d'une recherche
     */
    private void iteration(RecherchePartieSpecifique recherche)
    {
        if (recherche == null) return;
        if (recherche.isIterative())
        {
            String nomFichier = String.join("_", (this.description + recherche.getDescription()).replaceAll("[,:]", "").replaceAll(" {2}", " ").split("[/ ]"));
            File iterateFile = new File(mapObjet.getFolderData() + "/" + nomFichier + ".hashmap");

            MapsObjet mp = null;
            if (iterateFile.exists())
            {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(iterateFile)))
                {
                    mp = (MapsObjet) ois.readObject();
                } catch (IOException | ClassNotFoundException e) {e.printStackTrace();}
            } else
            {
                mp = recherche.getMapsObjetReiteration();
                MapsObjet finalMp = mp;
                new Thread(() ->
                {
                    try
                    {
                        if (iterateFile.createNewFile())
                        {
                            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(iterateFile)))
                            {
                                oos.writeObject(finalMp);
                            } catch (IOException e) {e.printStackTrace();}
                        }
                    } catch (IOException e) {e.printStackTrace();}
                }).start();
            }
            new InitChoix(1, this.description + recherche.getDescription(), objectInputStream, writer, mp);
            mp = null;
            System.gc();
        }
    }


    /**
     * envoie le message en parametre au client.
     */
    private int envoieMessage(String message)
    {
        try
        {
            this.writer.write(message);
            this.writer.newLine();
            this.writer.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    /**
     * Affiche les differents choix donner au client.
     */
    public String afficheChoix()
    {
        String retour = "";
        if (this.mode == 1)
            retour += Colors.YELLOW_BRIGHT + "Vous etes sur des données itératives" + this.description + "." + reset + "\n" +
                    Colors.BLUE_BOLD_BRIGHT + "0 / Pour quitter le mode itérative." + reset + "\n";
        retour += Colors.cyan + "1 / Consulter une partie spécifique et la visualiser pas à pas." + reset + "\n" +
                Colors.green + "2 / Trouver toutes les parties d’un joueur." + reset + "\n" +
                Colors.cyan + "3 / Consulter les 5 ouvertures les plus jouées." + reset + "\n" +
                Colors.green + "4 / Consulter les parties terminé avec n coups." + reset + "\n" +
                Colors.cyan + "5 / Lister les joueurs les plus actifs, les plus actifs sur une semaine, etc." + reset + "\n" +
                Colors.green + "6 / Calculer le joueur le plus fort au sens du PageRank." + reset + "\n" +
                Colors.cyan + "7 / Consulter le plus grand nombre de coups consécutifs cc qui soient communs à p parties.\n" + reset +
                Colors.green + "8 / Afficher toutes les parties." + Colors.reset + "\n" +
                Colors.cyan + "9 / Afficher le nombre de parties parties.\n" + reset +
                Colors.RED_BOLD_BRIGHT + "-1 / Pour quitter le serveur." + reset;

        //        Colors.cyan + "" + Colors.reset;
        return retour;

    }

    /**
     * @return Retourne le message envoyé par le client.
     */
    public String litMess()
    {
        String mess = null;
        try
        {
            mess = (String) this.objectInputStream.readObject();
        } catch (IOException e)
        {
            this.quitte = true;
            return "-1";
        } catch (ClassNotFoundException e)
        {
            log.error("Impossible de lire le message");
        }
        return mess;
    }

    /**
     * @return Retourne l'entier saisi par le client. Si celle si n'est pas bonne retourne -2.
     */
    public int litInt()
    {
        int nb;
        try
        {
            nb = Integer.parseInt(litMess());
        } catch (NumberFormatException e)
        {
            log.error("Impossible de lire l'entier");
            return -2;
        }
        return nb;
    }

    /**
     * @return retourne le nombre de demande simultané restant du client.
     */
    private int demandeRestant()
    {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("Threads.count")))
        {
            return (int) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {e.printStackTrace();}
        return 0;
    }

    /**
     * increment le nombre de demande.
     */
    private void incrementeDemande(int nb)
    {
        int result = demandeRestant()+nb;
        new File("Threads.count").delete();

        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("Threads.count")))
        {
            System.out.println("demande : " + result);
            objectOutputStream.reset();
            objectOutputStream.writeObject(result);
            System.out.println("demande : " + demandeRestant());

        } catch (IOException e) {e.printStackTrace();}
    }
}
