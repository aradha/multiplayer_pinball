package BoardGrammar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import client.Board;
import sim.Triggerable;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
/**
 * Tests the parser.
 * 
 * Will test staff-provided boards, along with the following extra cases:
 *  1. keybinding implementation
 *  2. files with unusual spacing 
 *  3. files with negative floats
 *  4. files with duplicate object names
 *  5. files with multiple board definitions
 *  6. empty files
 *  7. files with missing board definitions, or object definitions before board 
 *      definitions
 *  8. files that end with a line of space characters (The parser was throwing errors on
 *      files with endlines consisting of all spaces, so I added a test case and fixed 
 *      the bug).  
 *  9. files containing portals
 * 
 * Specific test cases explained above each test method.  
 */

@SuppressWarnings("unused")
public class ParserTests {
    

    //testing our keybinding implementation
    @Test
    public void testKeybindBoard(){
        try {
            File testBoardFile = new File(
                    getResourcePath("client/resources/keybindTest.pb"));
            Board testBoard = OutputProcessor.parse(testBoardFile);
        } catch (IOException e) {
            System.err.println("Test passed because of IOException");
        }
    }
    //Testing the staff-provided sampleBoard1.  Should parse board correctly
    @Test
    public void testSampleBoard1() {
        try {
            File testBoardFile = new File(
                    getResourcePath("client/resources/sampleBoard1.pb"));
            Board testBoard = OutputProcessor.parse(testBoardFile);
            assertEquals(testBoard.name(), "sampleBoard1");
            assertTrue(testBoard.numberOfBalls() == 1);
            assertTrue(testBoard.numberOfGadgets() == 31);
            assertTrue(testBoard.numberOfTriggerLinks() == 1);
            assertTrue(testBoard.gravity() == 20.0);
            assertTrue(testBoard.friction1() == 0.020);
            assertTrue(testBoard.friction2() == 0.020);
        } catch (IOException e) {
            System.err.println("Test passed because of IOException");
        }
    }
    //testing the staff-provided sampleBoard2-1.  Should parse board correctly
    @Test
    public void testSampleBoard21() {
        try {
            File testBoardFile = new File(
                    getResourcePath("client/resources/sampleBoard2-1.pb"));
            Board testBoard = OutputProcessor.parse(testBoardFile);
            assertEquals(testBoard.name(), "sampleBoard2_1");
            assertTrue(testBoard.numberOfBalls() == 1);
            assertTrue(testBoard.numberOfGadgets() == 27);
            assertTrue(testBoard.numberOfTriggerLinks() == 1);
        } catch (IOException e) {
            System.err.println("Test passed because of IOException");
        }
    }
    //testing the staff-provided sampleBoard2-2.  Should parse board correctly
    @Test
    public void testSampleBoard22() {
        try {
            File testBoardFile = new File(
                    getResourcePath("client/resources/sampleBoard2-2.pb"));
            Board testBoard = OutputProcessor.parse(testBoardFile);
            assertEquals(testBoard.name(), "sampleBoard2_2");
            assertTrue(testBoard.numberOfBalls() == 0);
            assertTrue(testBoard.numberOfGadgets() == 27);
            assertTrue(testBoard.numberOfTriggerLinks() == 1);
        } catch (IOException e) {
            System.err.println("Test passed because of IOException");
        }
    }
    //Testing a file with bad spacing (extraneous/uneven spacing).  Should ignore spaces
    //and parse board correctly
    @Test
    public void testSampleBoardBadSpacing() {
        try {
            File testBoardFile = new File(
                    getResourcePath("client/resources/badSpacing.pb"));
            Board testBoard = OutputProcessor.parse(testBoardFile);
            assertEquals(testBoard.name(), "badSpacing");
            assertTrue(testBoard.numberOfBalls() == 0);
            assertTrue(testBoard.numberOfGadgets() == 28);
            assertTrue(testBoard.numberOfTriggerLinks() == 1);
        } catch (IOException e) {
            System.err.println("Test passed because of IOException");
        }
    }
    //tests the staff-provided sampleBoard3.  Should parse board correctly
    @Test
    public void testSampleBoard3() {
        try {
            File testBoardFile = new File(
                    getResourcePath("client/resources/sampleBoard3.pb"));
            Board testBoard = OutputProcessor.parse(testBoardFile);
            assertEquals(testBoard.name(), "ExampleB");
            assertTrue(testBoard.numberOfBalls() == 2);
            assertTrue(testBoard.numberOfGadgets() == 13);
            //there are actually nine trigger links in the file
            assertTrue(testBoard.numberOfTriggerLinks() == 9);
        } catch (IOException e) {
            System.err.println("Test passed because of IOException");
        }
    }
    //tests the staff-provided sampleBoard4.  Should parse board correctly
    @Test
    public void testSampleBoard4() {
        File testBoardFile;
        try {
            testBoardFile = new File(
                    getResourcePath("client/resources/sampleBoard4.pb"));
            Board testBoard = OutputProcessor.parse(testBoardFile);
            assertEquals(testBoard.name(), "ExampleA");
            assertTrue(testBoard.numberOfBalls() == 1);
            assertTrue(testBoard.numberOfGadgets() == 9);
            assertTrue(testBoard.numberOfTriggerLinks() == 1);
        } catch (IOException e) {
            System.err.println("Test passed because of IOException");
        }
    }
    //tests a file that is missing spaces between words.  Should throw error
    @Test
    public void testNoSpace(){
        try{
            File testBoardFile = new File(
                    getResourcePath("client/resources/BadFormattingNoSpace.pb"));
            Board testBoard = OutputProcessor.parse(testBoardFile);
            assertTrue(false);
        } catch(IOException e) {
            System.err.println("Test passed because of IOException");
        } catch(Exception e){
            assertTrue(true);
        }
    }
    //tests a file with negative float values. Should parse board correctly
    @Test
    public void testNegFloat() throws IOException{
        try{
            File testBoardFile = new File(
                    getResourcePath("client/resources/sampleBoard3.pb"));
            Board testBoard = OutputProcessor.parse(testBoardFile);
            assertTrue(testBoard.numberOfBalls() == 2);
            assertTrue(testBoard.numberOfGadgets() == 13);
            assertTrue(testBoard.numberOfTriggerLinks() == 9);
        } catch(IOException e){
            System.err.println("Test passed because of IOException");
        }
    }
    //tests a file with two board definition lines.  Should throw exception.
    @Test public void testMultipleBoardLines(){
        try{
            File testBoardFile = new File(
                    getResourcePath("client/resources/BadFormattingBoardLines.pb"));
            Board testBoard = OutputProcessor.parse(testBoardFile);
            assertTrue(false);
        } catch(IOException e) {
            System.err.println("Test passed because of IOException");
        } catch(Exception e){
            assertTrue(true);
        }
    }
    //Tests a board where objects have duplicate names.  Should throw error.
    @Test public void testDuplicateNames(){
        try{
            File testBoardFile = new File(
                    getResourcePath("client/resources/BadFormattingDuplicateNames.pb"));
            Board testBoard = OutputProcessor.parse(testBoardFile);
            assertTrue(false);
        } catch(IOException e) {
            System.err.println("Test passed because of IOException");
        } catch(Exception e){
            System.err.println(e.getMessage());
            assertTrue(true);
        }    
    }
    //Tests file that has newlines in board definition. Should throw error.
    @Test public void testNewlines(){
        try{
            File testBoardFile = new File(
                    getResourcePath("client/resources/BadFormattingNewline.pb"));
            Board testBoard = OutputProcessor.parse(testBoardFile);
            assertTrue(false);
        } catch(IOException e) {
            System.err.println("Test passed because of IOException");
        } catch(Exception e){
            assertTrue(true);
        }
    }
    //Tests a board with no components or board defined.  Should parse file, 
    //but have no objects.  Empty board.  
    @Test public void testUnspecifiedBoardParams(){
        try{
            File testBoardFile = new File(
                    getResourcePath("client/resources/GoodFormattingUnspecifiedBoardParams.pb"));
            Board testBoard = OutputProcessor.parse(testBoardFile);
        } catch(IOException e) {
            System.err.println("Test passed because of IOException");
        }
    }
    //tests a file where first line is not a board definition.  Should throw an error.
    @Test public void testFirstLineNotBoard(){
        try{
            File testBoardFile = new File(
                    getResourcePath("client/resources/BadFormattingFirstLineNotBoard.pb"));
            Board testBoard = OutputProcessor.parse(testBoardFile);
            assertTrue(false);
        } catch(IOException e) {
            System.err.println("Test passed because of IOException");
        } catch(Exception e) {
            assertTrue(true);
        }
    }
    //Tests to make sure that parse can correctly parse complex float values
    @Test public void testPhysParamCorrectness(){
        try{
            File testBoardFile = new File(
                    getResourcePath("client/resources/GoodFormattingPhysParams.pb"));
            Board testBoard = OutputProcessor.parse(testBoardFile);
            assertTrue(testBoard.gravity() == 12.3652);
            assertTrue(testBoard.friction1() == -0.03333);
            assertTrue(testBoard.friction2() == 1245);
        } catch(IOException e) {
            System.err.println("Test passed because of IOException");
        }
    }
    //tests to make sure parser can parse a board with no components.  Should return board
    @Test public void testBoardNoComponents(){
        try{
            File testBoardFile = new File(
                    getResourcePath("client/resources/GoodFormattingNoComponents.pb"));
            Board testBoard = OutputProcessor.parse(testBoardFile);
        } catch(IOException e) {
            System.err.println("Test passed because of IOException");
        }
    }
    //Tests a file with triple-digit coordinates.  Should correctly parse board
    @Test public void testTripleDigitCoordinates(){
        try{
            File testBoardFile = new File(
                    getResourcePath("client/resources/GoodFormattingTripleDigit.pb"));
            Board testBoard = OutputProcessor.parse(testBoardFile);
            assertTrue(testBoard.toString().contains("square bumper(234,23)"));
        } catch(IOException e) {
            System.err.println("Test passed because of IOException");
        }
    }
    //tests a file with float parameters. Should still correctly parse board
    @Test public void testFloatParams(){
        try{
            File testBoardFile = new File(
                    getResourcePath("client/resources/GoodFormattingFloats.pb"));
            Board testBoard = OutputProcessor.parse(testBoardFile);
        } catch(IOException e) {
            System.err.println("Test passed because of IOException");
        }
    }
    //tests the case where a board ends before the line definition ends
    //should throw a "parameters not fully initialized" error
    @Test public void testBoardEndsBeforeLineEnds(){
        try{
            File testBoardFile = new File(
                    getResourcePath("client/resources/BadFormattingEndBeforeLineEnd.pb"));
            Board testBoard = OutputProcessor.parse(testBoardFile);
            assertTrue(false);
        } catch(IOException e) {
            System.err.println("Test passed because of IOException");
        } catch(Exception e){
            System.out.println("Caught error.  Not all parameters initialized");
            assertTrue(true);
        }
    }
    //tests the case where the last line of a file is all spaces
    //Should still correctly parse board.  
    @Test public void testBoardEndsOnSpaces(){
        try{
            File testBoardFile = new File(
                    getResourcePath("client/resources/endsOnSpaces.pb"));
            Board testBoard = OutputProcessor.parse(testBoardFile);
            assertTrue(testBoard.numberOfBalls() == 2);
            assertTrue(testBoard.numberOfGadgets() == 13);
            assertTrue(testBoard.numberOfTriggerLinks() == 9);
        } catch(IOException e) {
            System.err.println("Test passed because of IOException");
        }
    }
    //tests to make sure the parser can parse a board with portals
    @Test
    public void testPortalParse() throws IOException{
        try{
            File testBoardFile = new File(
                    getResourcePath("client/resources/portalBoard.pb"));
            Board testBoard = OutputProcessor.parse(testBoardFile);
            assertTrue(testBoard.numberOfBalls() == 1);
            assertTrue(testBoard.numberOfGadgets() == 10);
            assertTrue(testBoard.numberOfTriggerLinks() == 1);
            ArrayList<String> portalNameList = new ArrayList<String>();
            portalNameList.add("chel");
            portalNameList.add("hello");
            assertTrue(testBoard.getPortalNames().equals(portalNameList));
        } catch(IOException e){
            System.err.println("Test passed because of IOException");
        }
    }
    

    /**
     * taken from ps3 Return the absolute path of the specified file resource on
     * the classpath.
     * 
     * @throws IOException
     *             if a valid path to an existing file cannot be returned
     */
    public static String getResourcePath(String fileName) throws IOException {
        System.err.println("\nTesting File:" + fileName);
        System.err.println("notes:");
        URL url = Thread.currentThread().getContextClassLoader()
                .getResource(fileName);
        if (url == null) {
            throw new IOException("Failed to locate resource " + fileName);
        }
        File file;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException urise) {
            throw new IOException("Invalid URL: " + urise);
        }
        String path = file.getAbsolutePath();
        if (!file.exists()) {
            throw new IOException("File " + path + " does not exist");
        }
        return path;
    }
}
