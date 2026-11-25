package app.controller.greenhouse;

import app.enums.greenhouse.PotStatus;
import app.model.App;
import app.model.greenhouse.Greenhouse;
import app.model.greenhouse.GreenhousePlant;
import app.model.greenhouse.GreenhousePot;
import app.model.user.User;
import app.service.greenhouse.GreenhouseService;
import common.Result;

public class GreenhouseController
{
    private final static Greenhouse greenhouse = App.getCurrentUser().getGreenhouse();
    private final static GreenhouseService greenhouseService= new GreenhouseService(greenhouse);

    public static Result showGreenhouse()
    {
        return new Result(true , greenhouseService.showGreenhouse() );
    }

    public static Result plantPot(String X_coordinates , String Y_coordinates)
    {
        X_coordinates = X_coordinates.trim();
        Y_coordinates = Y_coordinates.trim();
        if (X_coordinates.isEmpty() || Y_coordinates.isEmpty())
            return new Result(false , "The coordinate value is empty.");

        int x , y;
        try {
            x = Integer.parseInt(X_coordinates);
            y = Integer.parseInt(Y_coordinates);
        } catch (NumberFormatException n)
        {
            return new Result(false , "The coordinate values must be Integer.");
        }

        if (x > greenhouse.getCol() || x < 1)
            return new Result(false , "The value of x must be from 1 to " + greenhouse.getCol() + ".");
        if (y > greenhouse.getRow() || y < 1)
            return new Result(true , "The value of x must be from 1 to " + greenhouse.getCol() + ".");

        GreenhousePot pot = greenhouse.getPot(x , y);
        if(!pot.isEmpty()) return new Result( false,"The pot is not suitable for planting. " +
                "Check the greenhouse conditions for more information.");

        if (greenhouseService.plant(x , y))
        {
            return new Result(true , pot.getPlant().getPlantName() + " was successfully planted " +
                    "in the pot at position ("+ x +", "+ y + "). " +
                    "The remaining time to grow is " + pot.getPlant().getRemainingTimeText());
        }

        return new Result(false , "Error. Please try again.");
    }

    public static Result unlockPot(String X_coordinates , String Y_coordinates)
    {
        X_coordinates = X_coordinates.trim();
        Y_coordinates = Y_coordinates.trim();
        if (X_coordinates.isEmpty() || Y_coordinates.isEmpty())
            return new Result(false , "The coordinate value is empty.");

        int x , y;
        try {
            x = Integer.parseInt(X_coordinates);
            y = Integer.parseInt(Y_coordinates);
        } catch (NumberFormatException n)
        {
            return new Result(false , "The coordinate values must be Integer.");
        }

        if (x > greenhouse.getCol() || x < 1)
            return new Result(false , "The value of x must be from 1 to " + greenhouse.getCol() + ".");
        if (y > greenhouse.getRow() || y < 1)
            return new Result(true , "The value of x must be from 1 to " + greenhouse.getCol() + ".");

        GreenhousePot pot = greenhouse.getPot(x , y);
        if(pot.isUnlocked()) return new Result( false,"The selected pot is already unlocked.");

        int cost = pot.getCost();
        if (App.getCurrentUser().getCoins() < cost)
            return new Result(false , "Your coins are not enough to unlock the pot.");

        if (greenhouseService.unlockPot(x , y))
        {
            return new Result(true , "The pot was successfully planted " +
                    "at position ("+ x +", "+ y + "). ");
        }

        return new Result(false , "Error. Please try again.");
    }

    public static Result collect(String X_coordinates , String Y_coordinates)
    {
        X_coordinates = X_coordinates.trim();
        Y_coordinates = Y_coordinates.trim();
        if (X_coordinates.isEmpty() || Y_coordinates.isEmpty())
            return new Result(false , "The coordinate value is empty.");

        int x , y;
        try {
            x = Integer.parseInt(X_coordinates);
            y = Integer.parseInt(Y_coordinates);
        } catch (NumberFormatException n)
        {
            return new Result(false , "The coordinate values must be Integer.");
        }

        if (x > greenhouse.getCol() || x < 1)
            return new Result(false , "The value of x must be from 1 to " + greenhouse.getCol() + ".");
        if (y > greenhouse.getRow() || y < 1)
            return new Result(true , "The value of x must be from 1 to " + greenhouse.getCol() + ".");

        GreenhousePot pot = greenhouse.getPot(x , y);
        if(pot.isEmpty()) return new Result( false,"No plants were found in this pot.");
        if(!pot.isUnlocked()) return new Result( false,"The pot is locked.");
        if (pot.getPotStatus() == PotStatus.GROWING)
            return new Result( false,"The remaining time to grow is "
                    + pot.getPlant().getRemainingTimeText());


        if (greenhouseService.collect(x , y))
        {
            return new Result(true , "The collection was successful.");
        }

        return new Result(false , "Error. Please try again.");
    }

    public static Result grow(String X_coordinates , String Y_coordinates)
    {
        X_coordinates = X_coordinates.trim();
        Y_coordinates = Y_coordinates.trim();
        if (X_coordinates.isEmpty() || Y_coordinates.isEmpty())
            return new Result(false , "The coordinate value is empty.");

        int x , y;
        try {
            x = Integer.parseInt(X_coordinates);
            y = Integer.parseInt(Y_coordinates);
        } catch (NumberFormatException n)
        {
            return new Result(false , "The coordinate values must be Integer.");
        }

        if (x > greenhouse.getCol() || x < 1)
            return new Result(false , "The value of x must be from 1 to " + greenhouse.getCol() + ".");
        if (y > greenhouse.getRow() || y < 1)
            return new Result(true , "The value of x must be from 1 to " + greenhouse.getCol() + ".");


        GreenhousePot pot = greenhouse.getPot(x , y);
        if(pot.isEmpty()) return new Result( false,"No plants were found in this pot.");
        if(!pot.isUnlocked()) return new Result( false,"The pot is locked.");
        if (pot.getPotStatus() == PotStatus.READY)
            return new Result( false,"The plant is ready to collect.");

        GreenhousePlant plant = pot.getPlant();
        int diamondCost = greenhouseService.calculateGrowCost(plant);

        User user = App.getCurrentUser();
        if (user.getGems() < diamondCost )
            return new Result(false , "There are not enough diamonds.");

        if (greenhouseService.grow(x , y))
        {
            return new Result(true , "Your plant is now ready.");
        }

        return new Result(false , "Error. Please try again.");
    }

    public static Result grow(String X_coordinates , String Y_coordinates , String hoursAmount)
    {
        X_coordinates = X_coordinates.trim();
        Y_coordinates = Y_coordinates.trim();
        hoursAmount = hoursAmount.trim();
        if (X_coordinates.isEmpty() || Y_coordinates.isEmpty())
            return new Result(false , "The coordinate value is empty.");
        if (hoursAmount.isEmpty())
            return new Result(false , "The hours value is empty.");

        int x , y;
        try {
            x = Integer.parseInt(X_coordinates);
            y = Integer.parseInt(Y_coordinates);
        } catch (NumberFormatException n)
        {
            return new Result(false , "The coordinate values must be Integer.");
        }

        int hours;
        try {
            hours = Integer.parseInt(hoursAmount);
        } catch (NumberFormatException n)
        {
            return new Result(false , "The hours values must be Integer.");
        }

        if (x > greenhouse.getCol() || x < 1)
            return new Result(false , "The value of x must be from 1 to " + greenhouse.getCol() + ".");
        if (y > greenhouse.getRow() || y < 1)
            return new Result(true , "The value of x must be from 1 to " + greenhouse.getCol() + ".");


        GreenhousePot pot = greenhouse.getPot(x , y);
        if(pot.isEmpty()) return new Result( false,"No plants were found in this pot.");
        if(!pot.isUnlocked()) return new Result( false,"The pot is locked.");
        if (pot.getPotStatus() == PotStatus.READY)
            return new Result( false,"The plant is ready to collect.");

        GreenhousePlant plant = pot.getPlant();
        int diamondCost = greenhouseService.calculateGrowCost(plant , hours);

        User user = App.getCurrentUser();
        if (user.getGems() < diamondCost )
            return new Result(false , "There are not enough diamonds.");

        if (greenhouseService.grow(x , y , hours))
        {
            return new Result(true , "Your plant is now ready.");
        }

        return new Result(false , "Error. Please try again.");
    }
}
