package client.info;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientInfo implements Serializable
{
    private final String username;
    private final String dateConnexion;

    public ClientInfo(String username)
    {
        this.username = username;
        this.dateConnexion = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    }


    public String getUsername() {return this.username;}

    public String getDateConnexion() {return this.dateConnexion;}

    @Override
    public String toString()
    {
        return "Username : " + getUsername();
    }
}
