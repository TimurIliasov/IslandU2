package ru.javarush.iliasov.cryptoanalyzer.Services;

import ru.javarush.iliasov.cryptoanalyzer.Cell;

public record Movement(Cell oldCell, Cell newCell) {
}
