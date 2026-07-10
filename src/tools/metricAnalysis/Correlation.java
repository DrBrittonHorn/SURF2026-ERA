package tools.metricAnalysis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import tools.com.google.gson.Gson;
import tools.com.google.gson.JsonElement;
import tools.com.google.gson.JsonObject;
import utils.com.google.gson.JsonSyntaxException;

public class Correlation {
    public static void main(String[] args) throws JsonSyntaxException, IOException{

        System.out.println("Starting Correlation/R^2 analysis...");

        String[] generatorList = {
            "constructiveLevelGenerator",
            "claudeLevelGenerator",
            "enhancedClaudeGenerator",
            //"fineTunedLLMGenerator",
            "geminiLevelGenerator",
            "geneticLevelGenerator",
            //"localLanguageModel",
            "randomLevelGenerator",
            "sturgeonLevelGenerator1x1",
            "sturgeonLevelGenerator2x2",
            "sturgeonLevelGenerator3x3",
            "sturgeonLevelGenerator4x4"};

        
        //System.out.println(metrics);
        // Get set of all metrics
        String jsonSampleRaw = Files.readString(Path.of("finalizedMetrics/" + "constructiveLevelGenerator" + "/levelMetrics.json"));
        Gson g = new Gson();
        JsonObject sampleJson = g.fromJson(jsonSampleRaw, JsonObject.class);
        Map.Entry<String, JsonElement> firstEntry = sampleJson.entrySet().iterator().next();
        Set<String> metrics = sampleJson.get(firstEntry.getKey()).getAsJsonObject().keySet();
        
        // Balance vertical is buggy, will be temporarily removed from the sample.
        metrics.removeIf(m -> m.contains("*"));

        double totalMetricCorrelation = 0;
        for (String metric1 : metrics){
            for (String metric2 : metrics){
                double totalGeneratorCorrelation = 0;
                
                for (String generatorName : generatorList){
                    String jsonRaw = Files.readString(Path.of("finalizedMetrics/" + generatorName + "/levelMetrics.json"));
                    JsonObject generatorJson = g.fromJson(jsonRaw, JsonObject.class);
                    double generatorR2 = getCorrelationByGenerator(generatorJson, metric1, metric2);
                    totalGeneratorCorrelation += generatorR2;
                    //System.out.println(generatorName + ": " + generatorR2);
                    
                }
                double averageGeneratorCorrelation = totalGeneratorCorrelation / 10.0;
                System.out.println("Average correlation for " + metric1 + " and " + metric2 + ": " + averageGeneratorCorrelation);
                if (!Double.isNaN(averageGeneratorCorrelation)){totalMetricCorrelation += averageGeneratorCorrelation;}
            }
            System.out.println(totalMetricCorrelation);
        }

        System.out.println("Average correlation between all metrics among all generators: " + totalMetricCorrelation / (metrics.size() * metrics.size()));
        
        
        

        
    }


    public static double getCorrelationByGenerator(JsonObject generatorJson, String metric1, String metric2){
        double[] xVals = new double[generatorJson.keySet().size()];
        double[] yVals = new double[generatorJson.keySet().size()];

        int index = 0;
        for (String key: generatorJson.keySet()){
            xVals[index] = generatorJson.get(key).getAsJsonObject().get(metric1).getAsDouble();
            yVals[index] = generatorJson.get(key).getAsJsonObject().get(metric2).getAsDouble();
            index++;
        }

        //for (int i = 0; i < xVals.length; i++){System.out.print(yVals[i]);}



        LinearRegression regression = new LinearRegression(xVals, yVals);
        return regression.R2();
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
