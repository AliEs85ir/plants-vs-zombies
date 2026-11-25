package common.model;
public class Rect {
    public Position position;
    public float width;
    public float height;

    public Rect(Position position, float width, float height) {
        this.position = position;
        this.width = width;
        this.height = height;
    }
    // >>> Overloaded Constructor
    public Rect(float x, float y, float width, float height) {
        this.position = new Position(x, y);
        this.width = width;
        this.height = height;
    }

    public boolean overlaps(Rect other) {
        return this.position.getX() < other.position.getX() + other.width &&
                this.position.getX() + this.width > other.position.getX() &&
                this.position.getY() < other.position.getY() + other.height &&
                this.position.getY() + this.height > other.position.getY();
    }
}


/*
      >>> Rect is short for Rectangle.
          it is a standard data structure used to represent a 2D bounding box.
 */
