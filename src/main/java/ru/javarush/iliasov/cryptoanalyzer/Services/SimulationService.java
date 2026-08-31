package ru.javarush.iliasov.cryptoanalyzer.Services;

import java.util.concurrent.*;

public class SimulationService {
    private ScheduledExecutorService executorService;
    private ScheduledFuture<?> simulationTask;
    private final MovementService movementService;
    private final EatingService eatingService;
    private final ReproduceService reproduceService;
    private final StatisticsService statisticsService;
    private final RandomService randomService;

    public SimulationService(MovementService movementService, EatingService eatingService, ReproduceService reproduceService, StatisticsService statisticsService, RandomService randomService) {
        this.movementService = movementService;
        this.eatingService = eatingService;
        this.reproduceService = reproduceService;
        this.statisticsService = statisticsService;
        this.randomService = randomService;
    }

    public void startSimulation() {
        if (isSimulationRunning()){
            return;
        }

        executorService = Executors.newSingleThreadScheduledExecutor();

        simulationTask = executorService.scheduleAtFixedRate(this::simulateStep, 0, 300, TimeUnit.MILLISECONDS);
    }

    private boolean isSimulationRunning() {
        return simulationTask != null
                && !simulationTask.isCancelled()
                && !simulationTask.isDone();
    }

    private void simulateStep() {
        movementService.move();
        eatingService.eatAll(randomService);
        eatingService.eatAllPlants();
        reproduceService.reproduceAll();
        getStatistics();
    }

    public void stopSimulation() {
        if (simulationTask != null){
            simulationTask.cancel(false);
            simulationTask = null;
        }

        if (executorService != null){
            executorService.shutdownNow();
            executorService = null;
        }
    }

    private void getStatistics(){
        System.out.println(statisticsService.countAnimalsByType());
        System.out.println(statisticsService.getPlantsCount());
    }
}
