package AutomatedTesting;

import java.nio.file.Paths;

/**
 * This enum class represents the input dot files to be tested.
 * The enums instantiated in here should have a corresponding dot file in the project at the same directly level
 * as the 'src' folder.
 * The enums of this class have these as final fields:
 *        * filePath to dot file (NOT fileName, although to instantiate the enum we give the fileName)
 *        * num of processors to use (NOT the amount of threads) in order to generate the optimal solution
 *        * the optimal solution
 * @author Megan Lim
 */
enum DotFileTestCase {

    NODES4_PROC2_FROM_SLIDES("Nodes4_Proc2_FromSlides.dot", 2, 8),
    NODES7_PROC2_TWOTREE("Nodes_7_TwoTree.dot", 2, 28),
    NODES7_PROC4_TWOTREE("Nodes_7_TwoTree.dot", 4, 22),
    NODES8_PROC2_RANDOM("Nodes_8_Random.dot", 2, 581),
    NODES8_PROC4_RANDOM("Nodes_8_Random.dot", 4, 581),
    NODES9_PROC2_SERIESPARALLEL("Nodes_9_SeriesParallel.dot", 2, 55),
    NODES9_PROC4_SERIESPARALLEL("Nodes_9_SeriesParallel.dot", 4, 55),
    NODES10_PROC2_RANDOM("Nodes_10_Random.dot", 2, 50),
    NODES10_PROC4_RANDOM("Nodes_10_Random.dot", 4, 50),
    NODES11_PROC2_OUTTREE("Nodes_11_OutTree.dot", 2, 350),
    NODES11_PROC4_OUTTREE("Nodes_11_OutTree.dot", 4, 227);

    private final String _filePath;
    private final int _numOfProcUsed;
    private final int _optimalSol;

    DotFileTestCase(String fileName, int numOfProcUsed, int optimalSol) {
        _filePath = Paths.get(fileName).toAbsolutePath().toString();
        _numOfProcUsed = numOfProcUsed;
        _optimalSol = optimalSol;
    }

    /**
     * This method returns the filePath (NOT the fileName) of the enum's corresponding dot file.
     * @return (String) filePATH
     */
    public String getFilePath() {
        return _filePath;
    }

    /**
     * This method returns the num of processors that is used in order to generate
     * enum's corresponding dot file's optimal solution.
     * @return (int) num of processors used
     */
    public int getNumOfProcUsed() {
        return _numOfProcUsed;
    }

    /**
     * This method returns the enum's corresponding dot file's optimal solution.
     * @return (int) optimal solution
     */
    public int getOptimalSol() {
        return _optimalSol;
    }
}
