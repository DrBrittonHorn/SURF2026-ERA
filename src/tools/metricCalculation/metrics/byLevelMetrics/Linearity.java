package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import tools.metricCalculation.metricTools;

public class Linearity {
    public static double calculateMetric(String levelString){
        char emptyChar = '.';
        String levelmap = metricTools.getLevelTiles(levelString);
        ArrayList<ArrayList<Character>> tileList = metricTools.toArray(levelString);
        ArrayList<Double> X = new ArrayList<Double>();
        ArrayList<Double> Y = new ArrayList<Double>();

        for (int i = 0; i < tileList.size(); i++){
            //Only non-empty characters count towards linearity scores
            for (int j = 0; j < tileList.get(i).size(); j++){
                if (tileList.get(i).get(j) != emptyChar){
                    Y.add((double) j);
                    X.add((double) i);
                }
            }
        }

        //System.out.println(X);
        //System.out.println(Y);

        double[] xArray = X.stream().mapToDouble(Double::doubleValue).toArray();
        double[] yArray = Y.stream().mapToDouble(Double::doubleValue).toArray();


        // Check to make sure that at least one of the arrays varies (otherwise perfectly linear)
        // This prevents a NaN error that occurs when the regression algorithm is run
        boolean doRegression = false;
        for (int i = 0; i < xArray.length; i++){
            if (xArray[0] != xArray[i]){doRegression = true;}
            //System.out.println(xArray[0] != xArray[i]);
        }
        //System.out.println();

        if (doRegression == false){return 1.0;} // Return perfectly linear (because it is)

        LinearRegression regression = new LinearRegression(xArray, yArray);

        double totalError = 0;
        for (int i = 0; i < xArray.length; i++){
            double predictedY = regression.predict(xArray[i]);
            double actualY = yArray[i];
            totalError += Math.abs(predictedY - actualY);
        }
        //System.out.println(regression.toString());
        //Returns average error (for now)
        if (Double.isNaN(regression.R2())){
            throw new ArithmeticException("Nan detected for \n" + levelString);
        }
        return regression.R2();
    }

    public static void main(String[] args) throws IOException{
        String testLevel1 = Files.readString(Path.of("generatedExamples\\geminiLevelGenerator\\aliens\\aliens_lvl001.txt"));
        String testLevel3 = Files.readString(Path.of("generatedExamples\\\\constructiveLevelGenerator\\\\asteroids\\\\asteroids_lvl202.txt"));
        String testLevel2 = Files.readString(Path.of("generatedExamples/geminiLevelGenerator/realsokoban/realsokoban_lvl002.txt"));
        System.out.println(calculateMetric(testLevel1));
    }
    
        /**
         * From: https://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/LinearRegression.java.html
     *  The {@code LinearRegression} class performs a simple linear regression
     *  on an set of <em>n</em> data points (<em>y<sub>i</sub></em>, <em>x<sub>i</sub></em>).
     *  That is, it fits a straight line <em>y</em> = &alpha; + &beta; <em>x</em>,
     *  (where <em>y</em> is the response variable, <em>x</em> is the predictor variable,
     *  &alpha; is the <em>y-intercept</em>, and &beta; is the <em>slope</em>)
     *  that minimizes the sum of squared residuals of the linear regression model.
     *  It also computes associated statistics, including the coefficient of
     *  determination <em>R</em><sup>2</sup> and the standard deviation of the
     *  estimates for the slope and <em>y</em>-intercept.
     *
     *  @author Robert Sedgewick
     *  @author Kevin Wayne
     */
    private static class LinearRegression {
        private final double intercept, slope;
        private final double r2;
        private final double svar0, svar1;

    /**
         * Performs a linear regression on the data points {@code (y[i], x[i])}.
         *
         * @param  x the values of the predictor variable
         * @param  y the corresponding values of the response variable
         * @throws IllegalArgumentException if the lengths of the two arrays are not equal
         */
        public LinearRegression(double[] x, double[] y) {
            if (x.length != y.length) {
                throw new IllegalArgumentException("array lengths are not equal");
            }
            int n = x.length;

            // first pass
            double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;
            for (int i = 0; i < n; i++) {
                sumx  += x[i];
                sumx2 += x[i]*x[i];
                sumy  += y[i];
            }
            double xbar = sumx / n;
            double ybar = sumy / n;

            // second pass: compute summary statistics
            double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
            for (int i = 0; i < n; i++) {
                xxbar += (x[i] - xbar) * (x[i] - xbar);
                yybar += (y[i] - ybar) * (y[i] - ybar);
                xybar += (x[i] - xbar) * (y[i] - ybar);
            }
            slope  = xybar / xxbar;
            intercept = ybar - slope * xbar;

            // more statistical analysis
            double rss = 0.0;      // residual sum of squares
            double ssr = 0.0;      // regression sum of squares
            for (int i = 0; i < n; i++) {
                double fit = slope*x[i] + intercept;
                rss += (fit - y[i]) * (fit - y[i]);
                ssr += (fit - ybar) * (fit - ybar);
            }

            int degreesOfFreedom = n-2;
            r2    = ssr / yybar;
            double svar  = rss / degreesOfFreedom;
            svar1 = svar / xxbar;
            svar0 = svar/n + xbar*xbar*svar1;
        }

    /**
         * Returns the <em>y</em>-intercept &alpha; of the best of the best-fit line <em>y</em> = &alpha; + &beta; <em>x</em>.
         *
         * @return the <em>y</em>-intercept &alpha; of the best-fit line <em>y = &alpha; + &beta; x</em>
         */
        public double intercept() {
            return intercept;
        }

    /**
         * Returns the slope &beta; of the best of the best-fit line <em>y</em> = &alpha; + &beta; <em>x</em>.
         *
         * @return the slope &beta; of the best-fit line <em>y</em> = &alpha; + &beta; <em>x</em>
         */
        public double slope() {
            return slope;
        }

    /**
         * Returns the coefficient of determination <em>R</em><sup>2</sup>.
         *
         * @return the coefficient of determination <em>R</em><sup>2</sup>,
         *         which is a real number between 0 and 1
         */
        public double R2() {
            return r2;
        }

    /**
         * Returns the standard error of the estimate for the intercept.
         *
         * @return the standard error of the estimate for the intercept
         */
        public double interceptStdErr() {
            return Math.sqrt(svar0);
        }

    /**
         * Returns the standard error of the estimate for the slope.
         *
         * @return the standard error of the estimate for the slope
         */
        public double slopeStdErr() {
            return Math.sqrt(svar1);
        }

    /**
         * Returns the expected response {@code y} given the value of the predictor
         * variable {@code x}.
         *
         * @param  x the value of the predictor variable
         * @return the expected response {@code y} given the value of the predictor
         *         variable {@code x}
         */
        public double predict(double x) {
            return slope*x + intercept;
        }

    /**
         * Returns a string representation of the simple linear regression model.
         *
         * @return a string representation of the simple linear regression model,
         *         including the best-fit line and the coefficient of determination
         *         <em>R</em><sup>2</sup>
         */
        public String toString() {
            StringBuilder s = new StringBuilder();
            s.append(String.format("%.2f n + %.2f", slope(), intercept()));
            s.append("  (R^2 = " + String.format("%.3f", R2()) + ")");
            return s.toString();
        }

    }
}
