package com.project.draw.processing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AgentPass {
    private final int maxRow;
    private final int maxCol;
    private final int[][] referencePixelsArray;
    public volatile AtomicInteger sumPixelsCurrentArray = new AtomicInteger(0);
    Object mutex;
    private final int[][] currentPixelsArray;

    public AgentPass(int[][] referencePixelsArray, int[][] currentPixelsArray) {
        this.referencePixelsArray = referencePixelsArray;
        this.currentPixelsArray = currentPixelsArray;
        maxRow = currentPixelsArray.length - 1;
        maxCol = currentPixelsArray[0].length - 1;
        mutex = currentPixelsArray;
    }

    public void calculateWithThreads(int numberOfThreads) {
        double startTime = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Future> futures = new ArrayList<>();
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadId = i + 1;
            Future<Object> future = executorService.submit(() -> calculateProcessedImage(threadId));
            futures.add(future);
        }
        executorService.shutdown();
//        int ind = 0;
//        while (!futures.get(ind).isDone()) {
//        }
//        executorService.shutdownNow();
        try {
            if (!executorService.awaitTermination(50000, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
        System.out.println("Elapsed time: " + (System.currentTimeMillis() - startTime) + " ms");
    }

    public Object calculateProcessedImage(int threadId) {
        int sumPixelsReferenceArray = sumPixels(referencePixelsArray, 0, 0, maxRow, maxCol);
        int currRow = maxRow / threadId;
        int currCol = maxCol / threadId;
        while (sumPixelsCurrentArray.get() <= sumPixelsReferenceArray) {
            int[] nextCoords = calculateNecessaryCell(referencePixelsArray, currentPixelsArray, currRow, currCol,
                    sumPixelsReferenceArray);
            if (sumPixelsCurrentArray.get() > sumPixelsReferenceArray) return "OK";
            currRow = nextCoords[0];
            currCol = nextCoords[1];
            synchronized (mutex) {
                currentPixelsArray[currRow][currCol]++;
            }
            sumPixelsCurrentArray.getAndIncrement();
        }
        return "OK";
    }

    public int[] calculateNecessaryCell(int[][] referenceArray, int[][] currentArray, int currRow, int currCol,
                                        int sumReference) {
        int nextRow, nextCol;
        if (sumPixelsCurrentArray.get() > sumReference) return new int[] {0, 0};
        List<List<Integer>> allSamples = calculateAllSample(referenceArray, currentArray, currRow,
                currCol, sumReference);
        Collections.shuffle(allSamples);
        Collections.sort(allSamples, Comparator.comparingInt(sample -> sample.get(0)));
        int index = 1;
        for (int i = 1; i < allSamples.size(); i++) {
            if (allSamples.get(i).get(0) == allSamples.get(0).get(0)) index++;
            else break;
        }

        nextRow = allSamples.get(0).get(1);
        nextCol = allSamples.get(0).get(2);
        return new int[]{nextRow, nextCol};
    }

    private List<List<Integer>> calculateAllSample(int[][] referenceArray, int[][] currentArray, int currRow, int currCol,
                                                   int sumReference) {
        int startRow = currRow - 1;
        int startCol = currCol - 1;
        int endRow = currRow + 1;
        int endCol = currCol + 1;
        int sumCurrentSquare = sumPixels(currentArray, startRow, startCol, endRow, endCol);
        if (sumPixelsCurrentArray.get() > sumReference) return new ArrayList<>();
        if (sumPixelsCurrentArray.get() == 0) sumPixelsCurrentArray.getAndSet(1);
        List<List<Integer>> allSamples = new ArrayList<>();
        List<Integer> currSample = new ArrayList<>();

        for (int i = startRow; i <= endRow; i++) {
            if (i < 0 || i > maxRow) continue;
            for (int j = startCol; j <= endCol; j++) {
                currSample.clear();
                if (i == currRow && j == currCol || j < 0 || j > maxCol) continue;
                int differenceCriterion = Math.max(referenceArray[i][j] - sumReference /
                        sumPixelsCurrentArray.get() * currentArray[i][j], 0);
                int cellSample = sumCurrentSquare - differenceCriterion + Math.max(referenceArray[i][j] - sumReference /
                        sumPixelsCurrentArray.get() * (currentArray[i][j] + 1), 0);
                currSample.add(cellSample);
                currSample.add(i);
                currSample.add(j);
                allSamples.add(new ArrayList<>(currSample));
            }
        }
        return new ArrayList<>(allSamples);
    }

    private int sumPixels(int[][] array, int startRow, int startCol, int endRow, int endCol) {
        int sum = 0;
        for (int i = startRow; i <= endRow; i++) {
            if (i < 0 || i > maxRow) continue;
            for (int j = startCol; j <= endCol; j++) {
                if (j < 0 || j > maxCol) continue;
                sum += array[i][j];
            }
        }
        return sum;
    }


    public int[][] getCalculatedPixelsArray() {
        return currentPixelsArray;
    }
}
