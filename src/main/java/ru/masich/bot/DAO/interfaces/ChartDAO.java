package ru.masich.bot.DAO.interfaces;

import ru.masich.bot.entity.Chart;

import java.util.List;

public interface ChartDAO {
    Chart getActiveChart(Long userId);
    Chart get(int chartId);
    List<Chart> getClosedChartList(int userId);
    Chart updateOrAdd(Chart chart);
}
