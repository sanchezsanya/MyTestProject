package com.example.alexander.vkclient;

import org.joda.time.DateTime;

/**
 * Created by Alexander on 17.06.15.
 */
public class Friend {
    public String name;
    public DateTime date;
    public Friend()
    {
        super();
    }
    public Friend(String friendName, DateTime dt)
    {
        super();
        name=friendName;
        date=dt;
    }
}
