package com.ams.booking.component;

public class BookingConfirmationMessage
{
    private String flightNumber;
    private String flightDate;
    private int    inventory;

    public String getFlightNumber()
    {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber)
    {
        this.flightNumber = flightNumber;
    }

    public String getFlightDate()
    {
        return flightDate;
    }

    public void setFlightDate(String flightDate)
    {
        this.flightDate = flightDate;
    }

    public int getInventory()
    {
        return inventory;
    }

    public void setInventory(int inventory)
    {
        this.inventory = inventory;
    }

}
