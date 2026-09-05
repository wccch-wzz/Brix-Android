package com.google.android.material.color.utilities;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/* JADX INFO: loaded from: classes.dex */
public final class QuantizerWsmeans {
    private static final int MAX_ITERATIONS = 10;
    private static final double MIN_MOVEMENT_DISTANCE = 3.0d;

    private QuantizerWsmeans() {
    }

    private static final class Distance implements Comparable<Distance> {
        int index = -1;
        double distance = -1.0d;

        Distance() {
        }

        @Override // java.lang.Comparable
        public int compareTo(Distance other) {
            return Double.valueOf(this.distance).compareTo(Double.valueOf(other.distance));
        }
    }

    /* JADX WARN: Code duplicated, block: B:65:0x01c9  */
    public static Map<Integer, Integer> quantize(int[] inputPixels, int[] startingClusters, int maxColors) {
        double[] componentBSums;
        double[] componentCSums;
        Random random = new Random(272008L);
        Map<Integer, Integer> pixelToCount = new LinkedHashMap<>();
        double[][] points = new double[inputPixels.length][];
        int[] pixels = new int[inputPixels.length];
        PointProvider pointProvider = new PointProviderLab();
        int pointCount = 0;
        for (int inputPixel : inputPixels) {
            Integer pixelCount = pixelToCount.get(Integer.valueOf(inputPixel));
            if (pixelCount == null) {
                points[pointCount] = pointProvider.fromInt(inputPixel);
                pixels[pointCount] = inputPixel;
                pointCount++;
                pixelToCount.put(Integer.valueOf(inputPixel), 1);
            } else {
                pixelToCount.put(Integer.valueOf(inputPixel), Integer.valueOf(pixelCount.intValue() + 1));
            }
        }
        int[] counts = new int[pointCount];
        for (int i = 0; i < pointCount; i++) {
            int pixel = pixels[i];
            counts[i] = pixelToCount.get(Integer.valueOf(pixel)).intValue();
        }
        int clusterCount = Math.min(maxColors, pointCount);
        if (startingClusters.length != 0) {
            clusterCount = Math.min(clusterCount, startingClusters.length);
        }
        double[][] clusters = new double[clusterCount][];
        int clustersCreated = 0;
        for (int i2 = 0; i2 < startingClusters.length; i2++) {
            clusters[i2] = pointProvider.fromInt(startingClusters[i2]);
            clustersCreated++;
        }
        int i3 = clusterCount - clustersCreated;
        if (i3 > 0) {
            for (int i4 = 0; i4 < i3; i4++) {
            }
        }
        int[] clusterIndices = new int[pointCount];
        for (int i5 = 0; i5 < pointCount; i5++) {
            clusterIndices[i5] = random.nextInt(clusterCount);
        }
        int[][] indexMatrix = new int[clusterCount][];
        int i6 = 0;
        while (i6 < clusterCount) {
            int i7 = i6;
            indexMatrix[i7] = new int[clusterCount];
            i6 = i7 + 1;
        }
        Distance[][] distanceToIndexMatrix = new Distance[clusterCount][];
        int j = 0;
        while (j < clusterCount) {
            int i8 = j;
            distanceToIndexMatrix[i8] = new Distance[clusterCount];
            for (int j2 = 0; j2 < clusterCount; j2++) {
                distanceToIndexMatrix[i8][j2] = new Distance();
            }
            j = i8 + 1;
        }
        int[] pixelCountSums = new int[clusterCount];
        int iteration = 0;
        while (true) {
            Random random2 = random;
            if (iteration >= 10) {
                break;
            }
            int i9 = 0;
            while (i9 < clusterCount) {
                int iteration2 = iteration;
                int iteration3 = i9 + 1;
                while (iteration3 < clusterCount) {
                    Map<Integer, Integer> pixelToCount2 = pixelToCount;
                    double distance = pointProvider.distance(clusters[i9], clusters[iteration3]);
                    distanceToIndexMatrix[iteration3][i9].distance = distance;
                    distanceToIndexMatrix[iteration3][i9].index = i9;
                    distanceToIndexMatrix[i9][iteration3].distance = distance;
                    distanceToIndexMatrix[i9][iteration3].index = iteration3;
                    iteration3++;
                    pixelToCount = pixelToCount2;
                    points = points;
                    pixels = pixels;
                }
                Map<Integer, Integer> pixelToCount3 = pixelToCount;
                double[][] points2 = points;
                int[] pixels2 = pixels;
                Arrays.sort(distanceToIndexMatrix[i9]);
                for (int j3 = 0; j3 < clusterCount; j3++) {
                    indexMatrix[i9][j3] = distanceToIndexMatrix[i9][j3].index;
                }
                i9++;
                pixelToCount = pixelToCount3;
                iteration = iteration2;
                points = points2;
                pixels = pixels2;
            }
            int iteration4 = iteration;
            Map<Integer, Integer> pixelToCount4 = pixelToCount;
            double[][] points3 = points;
            int[] pixels3 = pixels;
            int newClusterIndex = 0;
            int i10 = 0;
            while (i10 < pointCount) {
                double[] point = points3[i10];
                int previousClusterIndex = clusterIndices[i10];
                double[] previousCluster = clusters[previousClusterIndex];
                double previousDistance = pointProvider.distance(point, previousCluster);
                double minimumDistance = previousDistance;
                int pointsMoved = newClusterIndex;
                int newClusterIndex2 = -1;
                int newClusterIndex3 = i10;
                int i11 = 0;
                while (i11 < clusterCount) {
                    int j4 = i11;
                    int previousClusterIndex2 = previousClusterIndex;
                    double[] previousCluster2 = previousCluster;
                    if (distanceToIndexMatrix[previousClusterIndex][j4].distance < 4.0d * previousDistance) {
                        double distance2 = pointProvider.distance(point, clusters[j4]);
                        if (distance2 < minimumDistance) {
                            minimumDistance = distance2;
                            newClusterIndex2 = j4;
                        }
                    }
                    i11 = j4 + 1;
                    previousClusterIndex = previousClusterIndex2;
                    previousCluster = previousCluster2;
                }
                if (newClusterIndex2 == -1) {
                    newClusterIndex = pointsMoved;
                } else {
                    double distanceChange = Math.abs(Math.sqrt(minimumDistance) - Math.sqrt(previousDistance));
                    if (distanceChange <= 3.0d) {
                        newClusterIndex = pointsMoved;
                    } else {
                        clusterIndices[newClusterIndex3] = newClusterIndex2;
                        newClusterIndex = pointsMoved + 1;
                    }
                }
                i10 = newClusterIndex3 + 1;
            }
            if (newClusterIndex == 0 && iteration4 != 0) {
                break;
            }
            double[] componentASums = new double[clusterCount];
            double[] componentBSums2 = new double[clusterCount];
            double[] componentCSums2 = new double[clusterCount];
            char c = 0;
            Arrays.fill(pixelCountSums, 0);
            int i12 = 0;
            while (i12 < pointCount) {
                int clusterIndex = clusterIndices[i12];
                double[] point2 = points3[i12];
                char c2 = c;
                int count = counts[i12];
                pixelCountSums[clusterIndex] = pixelCountSums[clusterIndex] + count;
                double[] componentASums2 = componentASums;
                componentASums2[clusterIndex] = componentASums[clusterIndex] + (point2[c2] * ((double) count));
                componentBSums2[clusterIndex] = componentBSums2[clusterIndex] + (point2[1] * ((double) count));
                componentCSums2[clusterIndex] = componentCSums2[clusterIndex] + (point2[2] * ((double) count));
                i12++;
                c = c2;
                pixelCountSums = pixelCountSums;
                componentASums = componentASums2;
            }
            int[] pixelCountSums2 = pixelCountSums;
            double[] componentASums3 = componentASums;
            char c3 = c;
            int i13 = 0;
            while (i13 < clusterCount) {
                int count2 = pixelCountSums2[i13];
                if (count2 == 0) {
                    clusters[i13] = new double[]{0.0d, 0.0d, 0.0d};
                    componentBSums = componentBSums2;
                    componentCSums = componentCSums2;
                } else {
                    componentBSums = componentBSums2;
                    componentCSums = componentCSums2;
                    double a = componentASums3[i13] / ((double) count2);
                    double b = componentBSums[i13] / ((double) count2);
                    double b2 = count2;
                    double c4 = componentCSums[i13] / b2;
                    clusters[i13][c3] = a;
                    clusters[i13][1] = b;
                    clusters[i13][2] = c4;
                }
                i13++;
                componentBSums2 = componentBSums;
                componentCSums2 = componentCSums;
            }
            iteration = iteration4 + 1;
            random = random2;
            pixelToCount = pixelToCount4;
            points = points3;
            pixels = pixels3;
            pixelCountSums = pixelCountSums2;
        }
        Map<Integer, Integer> argbToPopulation = new LinkedHashMap<>();
        for (int i14 = 0; i14 < clusterCount; i14++) {
            int count3 = pixelCountSums[i14];
            if (count3 != 0) {
                int possibleNewCluster = pointProvider.toInt(clusters[i14]);
                if (!argbToPopulation.containsKey(Integer.valueOf(possibleNewCluster))) {
                    argbToPopulation.put(Integer.valueOf(possibleNewCluster), Integer.valueOf(count3));
                }
            }
        }
        return argbToPopulation;
    }
}
