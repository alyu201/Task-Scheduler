package it;

import Model.Main;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.fail;

public class testIT {
    @Test
    public void test4nodes(){
        try {
            String[] argument = {"Nodes_4_OneTree.dot", "2"};
            Main.main(argument);
        }catch (Exception e){
            fail("Test not pass!");
        }
    }
    @Test
    public void test5nodes(){
        try {
            String[] argument = {"Nodes_5_OneTree.dot", "2"};
            Main.main(argument);
        }catch (Exception e){
            fail("Test not pass!");
        }
    }

    @Test
    public void test6Nodes2trees(){
        try {
            String[] argument = {"Nodes_6_TwoTree.dot", "2"};
            Main.main(argument);
        }catch (Exception e){
            fail("Test not pass!");
        }
    }

    @Test
    public void test7Nodes1trees(){
        try {
            String[] argument = {"Nodes_7_OneTree.dot", "2"};
            Main.main(argument);
        }catch (Exception e){
            fail("Test not pass!");
        }
    }

    @Test
    public void test8NodesRandom(){
        try {
            String[] argument = {"Nodes_8_Random.dot", "2"};
            Main.main(argument);
        }catch (Exception e){
            fail("Test not pass!");
        }
    }

    @Test
    public void test9NodesSeriesparallel(){
        try {
            String[] argument = {"Nodes_9_SeriesParallel.dot", "2"};
            Main.main(argument);
        }catch (Exception e){
            fail("Test not pass!");
        }
    }

    @Test
    public void test10NodesRandom(){
        try {
            String[] argument = {"Nodes_10_Random.dot", "2"};
            Main.main(argument);
        }catch (Exception e){
            fail("Test not pass!");
        }
    }

    @Test
    public void test11Nodes1trees(){
        try {
            String[] argument = {"Nodes_11_OutTree.dot", "2"};
            Main.main(argument);
        }catch (Exception e){
            fail("Test not pass!");
        }
    }
}
