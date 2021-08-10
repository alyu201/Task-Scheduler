package it;

import Model.Main;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.nio.file.Paths;

import static org.junit.Assert.fail;

public class testIT {

    @Test (timeout = 10000)
    public void test4nodes(){
        try {
            String[] argument = {"Nodes_4_OneTree.dot", "2"};
            Main.main(argument);
        }catch (Exception e){
            fail("Test not pass!");
        }
    }
    @Test (timeout = 10000)
    public void test5nodes(){
        try {
            String[] argument = {"Nodes_5_OneTree.dot", "2"};
            Main.main(argument);
        }catch (Exception e){
            fail("Test not pass!");
        }
    }

    @Test (timeout = 10000)
    public void test6Nodes2trees(){
        try {
            String[] argument = {"Nodes_6_TwoTree.dot", "2"};
            Main.main(argument);
        }catch (Exception e){
            fail("Test not pass!");
        }
    }

    @Test (timeout = 10000)
    public void test7Nodes1trees(){
        try {
            String[] argument = {"Nodes_7_OneTree.dot", "2"};
            Main.main(argument);
        }catch (Exception e){
            fail("Test not pass!");
        }
    }

    @Test (timeout = 10000)
    public void test8NodesRandom(){
        try {
            String[] argument = {"Nodes_8_Random.dot", "2"};
            Main.main(argument);
        }catch (Exception e){
            fail("Test not pass!");
        }
    }

    @Test (timeout = 10000)
    public void test9NodesSeriesparallel(){
        try {
            String[] argument = {"Nodes_9_SeriesParallel.dot", "2"};
            Main.main(argument);
        }catch (Exception e){
            fail("Test not pass!");
        }
    }

    @Test (timeout = 10000)
    public void test10NodesRandom(){
        try {
            String[] argument = {"Nodes_10_Random.dot", "2"};
            Main.main(argument);
        }catch (Exception e){
            fail("Test not pass!");
        }
    }

    @Test (timeout = 10000)
    public void test11Nodes1trees(){
        try {
            String[] argument = {"Nodes_11_OutTree.dot", "2"};
            Main.main(argument);
        }catch (Exception e){
            fail("Test not pass!");
        }
    }
}
