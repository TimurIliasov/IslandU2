package ru.javarush.iliasov.islandproject;

public class Island {
    private final Cell[][] cells;

    public Island(Cell[][] cells) {
        this.cells = cells;
    }

    public Cell[][] getCells(){
        return cells;
    }

    public boolean isValidCoordinate(int row, int col){
        return row >= 0
                && row<cells.length
                &&col >= 0
                && col < cells[row].length;
    }
}
