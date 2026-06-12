package tools;


// used to store the X and Y value of a position on a graph
public class Position {
    
    private double Xvalue;
    private double Yvalue;

    public Position(double X, double Y) {
        Xvalue = X;
        Yvalue = Y;
    }

    public double getX() {
        return this.Xvalue;
    }

    public double getY() {
        return this.Yvalue;
    }

    public String toString() {
        return "(" + this.Xvalue + ", " + this.Yvalue + ")";
    }
}
