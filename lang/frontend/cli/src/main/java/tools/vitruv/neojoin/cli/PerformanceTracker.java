package tools.vitruv.neojoin.cli;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class PerformanceTracker {
    private final int runId;
    private long lastTimestamp;
    private final List<String> records = new ArrayList<>();

    public PerformanceTracker(int runId) {
        this.runId = runId;
        this.lastTimestamp = System.nanoTime();
    }

    public void checkpoint(String stepName) {
        final long now = System.nanoTime();
        final double durationMs = (now - lastTimestamp) / 1_000_000.0;
        lastTimestamp = now;

        records.add(String.format("%d;%s;%.4f", runId, stepName, durationMs));
    }

    public void appendToCsv(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, true))) {
            for (String record : records) {
                writer.println(record);
            }
        } catch (IOException e) {
            System.err.println("Failed to write benchmark results: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
