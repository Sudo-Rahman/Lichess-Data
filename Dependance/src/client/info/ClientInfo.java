package client.info;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Classe qui contient les informations du client.
 *
 * @author Yilmaz Rahman
 * @version 1.0
 * @date 10/03/2022
 */
public class ClientInfo implements Serializable
{
    private final String username;
    private final String dateConnexion;

    /**
     * @param username Le nom de l'utilisateur.
     */
    public ClientInfo(String username)
    {
        this.username = username;
        this.dateConnexion = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    }


    /**
     * @return Le nom de l'utilisateur.
     */
    public String getUsername() {return this.username;}

    /**
     * @return La date de connexion.
     */
    public String getDateConnexion() {return this.dateConnexion;}

    @Override
    public String toString()
    {
        return "Username : " + getUsername();
    }
}
