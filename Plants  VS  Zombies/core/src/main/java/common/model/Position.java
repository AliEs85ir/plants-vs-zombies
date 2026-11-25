package common.model;


import com.badlogic.gdx.math.Vector2;

// >>> Represents a coordinate inside game world.
public class Position
{
    // -> Properties
    private double initialY;
    private static final float EPSILON = 0.5f;
    private double x; // Column
    private double y; // Row

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // -> Methods

    // Returns x coordinate.
    public double getX()
    {
        // TODO: implement
        return x;
    }

    // Returns y coordinate.
    public double getY()
    {
        // TODO: implement
        return y;
    }

    // Changes x coordinate.
    public void setX(double x)
    {
        // TODO: implement
        this.x = x;
    }

    // Changes y coordinate.
    public void setY(double y)
    {
        // TODO: implement
        this.y = y;
    }

    public void setInitialY(double y) {
        this.initialY = y;
    }

    public double getInitialY() {
        return initialY;
    }

    public Vector2 toVector() {
        return new Vector2((float) x, (float) y);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Position position)) return false;

        double diffX = Math.abs(this.getX() - position.getX());
        double diffY = Math.abs(this.getY() - position.getY());

        return diffX < EPSILON && diffY < EPSILON;
    }

    @Override
    public String toString() {
        return "( " + x + " , " + y + " )";
    }
}
