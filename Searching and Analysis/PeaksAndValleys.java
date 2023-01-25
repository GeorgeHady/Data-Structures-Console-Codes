import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Assignment1 {
    /**
     * Main method start the program
     *
     * @param args : not used
     */
    public static void main(String[] args) {

        final String FILENAME = "ELEVATIONS-1.txt";
        long startTime = 0;

        // Step 1 - Read from data file into 2D Array.  Note the first line is the correct results
        int[][] theData2D = null;
        int exclusionRadius = 0;
        int peakMinimum = 0;
        int numberOfRows = 0;
        int numberOfCols = 0;
        try {
            File file = new File(FILENAME);
            Scanner inputFile = new Scanner(file);

            // Read the number of Rows and Columns first
            numberOfRows = inputFile.nextInt();
            numberOfCols = inputFile.nextInt();
            peakMinimum = inputFile.nextInt();
            exclusionRadius = inputFile.nextInt();

            startTime = System.nanoTime();
            theData2D = new int[numberOfRows][numberOfCols];
            for (int row = 0; row < numberOfRows; row++) {
                for (int col = 0; col < numberOfCols; col++) {
                    theData2D[row][col] = inputFile.nextInt();
                }
            }
            inputFile.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Error reading data from " + FILENAME + " Exception = " + ex.getMessage());
            System.exit(0);
        }


        System.out.println("Data has been read [Time: " + (System.nanoTime() - startTime) / 1000 + "]");

        // TASK 1
        startTime = System.nanoTime();
        int[] minPeakTimes = findMinimumPeak(theData2D);
        int index = 0;
        System.out.println("\n1- [MinPeak: " + minPeakTimes[index++] +
                " , Count: " + minPeakTimes[index] +
                "] [Time: " + (System.nanoTime() - startTime) / 1000 + " US]");


        // TASK 2
        startTime = System.nanoTime();
        int[][] localPeaks = findLocalPeaksElevations(theData2D, peakMinimum, exclusionRadius, numberOfRows, numberOfCols);
        System.out.println("\n2- local Peak Number: " + localPeaks.length + " [Time: " + (System.nanoTime() - startTime) / 1000 + " US]");


        // TASK 3
        startTime = System.nanoTime();
        double[][] closestPeaksDistanceAndItsIndexes = closestPeaksDistanceAndItsIndexes(localPeaks);

        System.out.printf("\n3- Closest Local Peaks AndThereIndexes: %.2f", closestPeaksDistanceAndItsIndexes[0][0]);
        System.out.print("  [Time: " + ((System.nanoTime() - startTime) / 1000) + " US]");

        for (int i = 1; i < closestPeaksDistanceAndItsIndexes.length; i++) {
            System.out.printf("\n\t\t" + i + "- [%.0f (%.0f,%.0f)]-[%.0f (%.0f,%.0f)]",
                    closestPeaksDistanceAndItsIndexes[i][0],
                    closestPeaksDistanceAndItsIndexes[i][1],
                    closestPeaksDistanceAndItsIndexes[i][2],
                    closestPeaksDistanceAndItsIndexes[i][3],
                    closestPeaksDistanceAndItsIndexes[i][4],
                    closestPeaksDistanceAndItsIndexes[i][5]
            );
        }


        // TASK 4
        startTime = System.nanoTime();
        int mostCommonElevation =  mostCommonElevation(theData2D);
        System.out.print("\n\n4- The most common elevation is: " + mostCommonElevation);
        System.out.print("  [Time: " + ((System.nanoTime() - startTime) / 1000) + " US]\n");
    }







    


    /**
     * Print the lowest elevation value and the number of times it is found in the complete data set.
     *
     * @param data: of text data input
     * @return 1D Array, Two values @minPeak and @minPeakTimes
     */
    public static int[] findMinimumPeak(int[][] data) {
        int minPeak = Integer.MAX_VALUE; // set maximum integer
        int minPeakTimes = 0;
        for (int[] ints : data) {
            for (int anInt : ints) {
                if (anInt < minPeak) {      // reset MinimumPeak and its counter
                    minPeak = anInt;
                    minPeakTimes = 1;
                } else if (anInt == minPeak) {
                    minPeakTimes++;
                }
            }
        }
        return new int[]{minPeak, minPeakTimes};    //return array of two values
    }








    /**
     * Print all the local peaks where the peak elevation is greater or equal to @peakMinimum using an exclusion radius @exclusionRadius
     *
     * @param data:            of text data input
     * @param peakMinimum:     minimum peak value
     * @param exclusionRadius: radius to pick local peaks
     * @param numberOfRows:    rows of 2d array
     * @param numberOfCols:    columns of 2d array
     * @return 2D Array : local Peaks
     */
    public static int[][] findLocalPeaksElevations(int[][] data,
                                                   int peakMinimum,
                                                   int exclusionRadius,
                                                   int numberOfRows,
                                                   int numberOfCols) {
        // (numberOfRows*numberOfCols)/exclusionRadius : is the maximum peaks could be found, which assume to be array length
        int[][] localPeak = new int[(numberOfRows * numberOfCols) / exclusionRadius][3]; // array that saves peaks with there position
        int localPeakIndex = 0; // index of localPeak array

        for (int i = exclusionRadius; i < data.length - exclusionRadius; i++) {
            for (int j = exclusionRadius; j < data[i].length - exclusionRadius; j++) {
                if (data[i][j] >= peakMinimum) {
                    int peak = data[i][j];
                    int k = i - exclusionRadius;
                    int l;
                    int peakFoundNumber = 0;

                    for (; k <= i + exclusionRadius; k++) {
                        for (l = j - exclusionRadius; l <= j + exclusionRadius; l++) {
                            if (peak < data[k][l]) {
                                k = l = 1000000; // quite the two for-loops
                                peakFoundNumber = 0; // cancel found as it's not peak anymore
                            } else if (peak == data[k][l]) {
                                peakFoundNumber += 1;
                            }
                        }
                    }

                    if (peakFoundNumber == 1) {
                        localPeak[localPeakIndex][0] = peak;
                        localPeak[localPeakIndex][1] = i;
                        localPeak[localPeakIndex][2] = j;
                        localPeakIndex++;
                    }
                }
            }
        }

        localPeak = Arrays.copyOf(localPeak, localPeakIndex); // resize the array
        return localPeak;
    }







    /**
     * Print the row and column and elevation of the two closest local peaks using the formula for
     * distance presented below:
     * <p>
     * 𝑑^2 = (𝑟1 − 𝑟2)^2 + (𝑐1 − 𝑐2)^2
     * d is the distance
     * r1,r2 are the row numbers of the two peaks
     * c1,c2 are the column numbers of the two peaks
     *
     * @param localPeaks : output of task2
     * @return 2D Array :  first index [0][0] is closest Peaks Distance, rest rows are peaks indexes
     */
    public static double[][] closestPeaksDistanceAndItsIndexes(int[][] localPeaks) {
        double[][] closestLocalPeaksIndexes = new double[localPeaks.length][3];
        int peaksArrayIndex = 0;
        double closestLocalPeaksDistance = Double.MAX_VALUE;

        for (int i = 0; i < localPeaks.length; i++) {
            double closestLocalPeaksDistanceTest = Double.MAX_VALUE;
            int j = i + 1;  // to avoid dealing with the same peaks
            int iPeakIndex = 0;
            int jPeakIndex = 0;
            for (; j < localPeaks.length; j++) {
                double closestLocalPeaksDistanceTemp = Math.sqrt(
                        Math.pow((localPeaks[i][1] - localPeaks[j][1]), 2)
                                +
                                Math.pow((localPeaks[i][2] - localPeaks[j][2]), 2)
                );

                if (closestLocalPeaksDistanceTemp < closestLocalPeaksDistanceTest) {
                    closestLocalPeaksDistanceTest = closestLocalPeaksDistanceTemp;
                    iPeakIndex = i;
                    jPeakIndex = j;
                }
            }

            if (closestLocalPeaksDistanceTest != Double.MAX_VALUE) {
                closestLocalPeaksIndexes[peaksArrayIndex][0] = closestLocalPeaksDistanceTest;
                closestLocalPeaksIndexes[peaksArrayIndex][1] = iPeakIndex;
                closestLocalPeaksIndexes[peaksArrayIndex][2] = jPeakIndex;
                peaksArrayIndex++;
                if (closestLocalPeaksDistanceTest < closestLocalPeaksDistance) {
                    closestLocalPeaksDistance = closestLocalPeaksDistanceTest;
                }
            }
        }

        double[][] returnResult = new double[localPeaks.length][6];
        returnResult[0][0] = closestLocalPeaksDistance;

        closestLocalPeaksIndexes = Arrays.copyOf(closestLocalPeaksIndexes, peaksArrayIndex); // resize
        int returnResultIndex = 1;
        for (int i = 0; i < closestLocalPeaksIndexes.length; i++) {
            if (closestLocalPeaksIndexes[i][0] == closestLocalPeaksDistance) {

                returnResult[returnResultIndex][0] = localPeaks[(int) closestLocalPeaksIndexes[i][1]][0];
                returnResult[returnResultIndex][1] = localPeaks[(int) closestLocalPeaksIndexes[i][1]][1];
                returnResult[returnResultIndex][2] = localPeaks[(int) closestLocalPeaksIndexes[i][1]][2];
                returnResult[returnResultIndex][3] = localPeaks[(int) closestLocalPeaksIndexes[i][2]][0];
                returnResult[returnResultIndex][4] = localPeaks[(int) closestLocalPeaksIndexes[i][2]][1];
                returnResult[returnResultIndex][5] = localPeaks[(int) closestLocalPeaksIndexes[i][2]][2];
                returnResultIndex++;
            }
        }
        returnResult = Arrays.copyOf(returnResult, returnResultIndex); // // resize
        return returnResult;
    }





    /**
     * Print the most common elevation in the data set.
     * @param data text input file
     *
     * @return most common elevation (int)
     */
    public static int mostCommonElevation(int[][] data) {

        int maxValue = 0; // max value in data

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                if(data[i][j] > maxValue) {
                    maxValue = data[i][j];
                }
            }
        }

        int[] elevation = new int[maxValue+1];   // to store the elevation of each elevation
        int maxCount = 0;
        int mostCommonElevation = 0;

        // counting Elevation
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                elevation[data[i][j]]++;
            }
        }

        // find the most common elevation
        for (int i = 0; i < elevation.length; i++) {
            if (elevation[i] > maxCount) {
                maxCount = elevation[i];
                mostCommonElevation = i;
            }
        }

        return mostCommonElevation;
    }

}
