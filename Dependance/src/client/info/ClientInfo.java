package client.info;

import java.io.Serializable;

public class ClientInfo implements Serializable
{
    private final String username;

    public ClientInfo(String username)
    {
        this.username = username;
    }


    public String getUsername()
    {
        return username;
    }

    @Override
    public String toString()
    {
        return "Username : " + getUsername();
    }
}
