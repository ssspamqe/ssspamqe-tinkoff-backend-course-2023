package edu.hw7.Task4.simulation;

import edu.hw7.Task4.MonteCarloPiCalculator;

public class Simulator {

    private static final MonteCarloPiCalculator CALCULATOR = new MonteCarloPiCalculator();
    private static final double ORIGINAL = Math.PI;

    public SimulationResult getAsyncResult(int simulations, int threads, int randomPointsPerThread) {
        double executingTimeSum = 0;
        double deltaSum = 0;

        for (int i = 0; i < simulations; i++) {
            var startTime = System.nanoTime();
            double val = CALCULATOR.getPiAsync(threads, randomPointsPerThread);
            var endTime = System.nanoTime();

            double delta = Math.abs(val - ORIGINAL);

            executingTimeSum += convertNanosecondsToMilliseconds(endTime - startTime);
            deltaSum += delta;
        }

        double averageExecutingTime = executingTimeSum / simulations;
        double averageDelta = deltaSum / simulations;

        return new SimulationResult(averageExecutingTime, averageDelta);
    }

    @SuppressWarnings("MagicNumber")
    private double convertNanosecondsToMilliseconds(double nanoseconds) {
        return nanoseconds / 1_000_000d;
    }

}
