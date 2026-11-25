package common.model;

public abstract class Offset {
    public double x;
    public double y;
    public double z;

    public Offset(double x, double y) {
        this.x = x;
        this.y = y;
        this.z = 0;
    }

    public Offset(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Offset() {
    }
}
