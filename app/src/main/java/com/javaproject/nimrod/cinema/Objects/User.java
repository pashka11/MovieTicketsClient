package com.javaproject.nimrod.cinema.Objects;

public class User
{
    private String _user;
    private String _password;

    public User()
    {

    }

    public User(String user, String password)
    {
        this._user = user;
        this._password = password;
    }

    public String getPassword()
    {
        return _password;
    }

    public String getUser()
    {
        return _user;
    }
}
