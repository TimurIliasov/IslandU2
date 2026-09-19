package ru.javarush.iliasov.islandproject.Services;


import ru.javarush.iliasov.islandproject.Cell;
import ru.javarush.iliasov.islandproject.Island;
import ru.javarush.iliasov.islandproject.Organisms.Plants;


import java.util.concurrent.*;

public class SimulationService {
    private ScheduledExecutorService executorService;
    private ScheduledFuture<?> simulationTask;

    private final InitializationService initializationService;
    private final MovementService movementService;
    private final EatingService eatingService;
    private final ReproduceService reproduceService;
    private final StatisticsService statisticsService;
    private final RandomService randomService;
    private final Island island;

    public SimulationService(InitializationService initializationService, MovementService movementService, EatingService eatingService, ReproduceService reproduceService, StatisticsService statisticsService, RandomService randomService, Island island) {
        this.initializationService = initializationService;
        this.movementService = movementService;
        this.eatingService = eatingService;
        this.reproduceService = reproduceService;
        this.statisticsService = statisticsService;
        this.randomService = randomService;
        this.island = island;
    }

    public void startSimulation() {
        if (isSimulationRunning()) {
            return;
        }

        executorService = Executors.newSingleThreadScheduledExecutor();

        simulationTask = executorService.scheduleAtFixedRate(this::simulateStep, 0, 300, TimeUnit.MILLISECONDS);

    }


    public boolean isSimulationRunning() {
        return simulationTask != null
                && !simulationTask.isCancelled()
                && !simulationTask.isDone();
    }

    public void simulateStep() {
       growPlants();
       animalLifeCycle();
       getStatistics();
    }

    public void stopSimulation() {
        if (simulationTask != null) {
            simulationTask.cancel(false);
            simulationTask = null;
        }

        if (executorService != null) {
            executorService.shutdownNow();
            executorService = null;
        }
    }

    public void shutdownReproduceService() {
        movementService.shutdown();
        reproduceService.shutdown();
    }

    public void getStatistics() {
        System.out.println(statisticsService.countAnimalsByType());
        System.out.println(statisticsService.getPlantsCount());
    }

    public void startIsland() {
        initializationService.startIsland(island);
    }

    public void growPlants(Cell cell) {
        int plantCount = cell.getCurrentPlantsCount();
        int numberLeftToMax = Plants.getMaxInCell() - plantCount;
        if (plantCount >= Plants.getMaxInCell()){
            return;
        }
        int random = ThreadLocalRandom.current().nextInt(numberLeftToMax + 1);
        
        for (int i = 0; i < random; i++) {
            Plants plants = new Plants();
            cell.addPlant(plants);
        }
    }

    public void growPlants(){
        for (int i = 0; i < island.getCells().length; i++) {
            for (int j = 0; j < island.getCells()[i].length; j++) {
                Cell cell = island.getCells()[i][j];
                growPlants(cell);
            }
        }
    }

    public void animalLifeCycle(){
        movementService.moveAll(randomService);
        eatingService.eatAll(randomService);
        eatingService.eatAllPlants();
        reproduceService.reproduceAll();
    }

}
