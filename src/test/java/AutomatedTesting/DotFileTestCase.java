package AutomatedTesting;

import java.nio.file.Paths;

enum DotFileTestCase {

    NODES4_PROC2_FROM_SLIDES("Nodes4_Proc2_FromSlides.dot", 2, 8),
    NODES7_PROC2_TWOTREE("Nodes_7_TwoTree.dot", 2, 28);

    private final String _filePath;
    private final int _numOfProcUsed;
    private final int _optimalSol;

    DotFileTestCase(String fileName, int numOfProcUsed, int optimalSol) {
        _filePath = Paths.get(fileName).toAbsolutePath().toString();
        _numOfProcUsed = numOfProcUsed;
        _optimalSol = optimalSol;
    }

    public String getFilePath() {
        return _filePath;
    }

    public int getNumOfProcUsed() {
        return _numOfProcUsed;
    }

    public int getOptimalSol() {
        return _optimalSol;
    }
}
