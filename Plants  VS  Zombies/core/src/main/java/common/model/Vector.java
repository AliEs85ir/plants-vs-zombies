package common.model;

public class Vector {
    public double x;
    public double y;
    public double z;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
        this.z = 0;
    }

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public static double findDist(Position vec1, Position vec2) {
        double dx = vec1.getX() - vec2.getX();
        double dy = vec1.getY() - vec2.getY();

        return Math.sqrt(dx * dx + dy * dy);
    }

    public Vector() {
    }
}
