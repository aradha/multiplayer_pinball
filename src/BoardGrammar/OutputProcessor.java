package BoardGrammar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import BoardGrammar.BoardGrammarParser.FltvarContext;
import BoardGrammar.BoardGrammarParser.IntvarContext;
import BoardGrammar.BoardGrammarParser.KeydefContext;
import BoardGrammar.BoardGrammarParser.WordvarContext;
import client.Board;
import sim.Absorber;
import sim.Ball;
import sim.CircleBumper;
import sim.Flipper;
import sim.SquareBumper;
import sim.TriangleBumper;
import sim.Flipper.FlipperType;


/**
 * Handles parsing a board file.  Contains the board parser, along with 
 * a listener for the parse tree, which generates the board.
 *
 */

//Somewhat based on the code given in recitation 13, called ExpressionFactory
public class OutputProcessor {
    
    /**The various types of gadgets to be parsed from the board
     *
     */
    private enum GadgetType{
        ABSORBER, SQUAREBUMPER, CIRCLEBUMPER, TRIANGLEBUMPER, LEFTFLIPPER, RIGHTFLIPPER, PORTAL, INVALID}
    
    /**
     * parses the board file.  Takes the tokens in from the lexer, and 
     * instantiates the board
     * @param file the file to parse into the board
     * @return board, the board generated from the file
     * @throws FileNotFoundException in case the file is missing
     * @throws IOException in case file is missing or unreadable
     */
    
    public static Board parse(File file) throws FileNotFoundException, IOException{
        //lexes a bunch of strings to make a set of tokens
        CharStream stream = new ANTLRInputStream(new FileReader(file));
        BoardGrammarLexer lexer = new BoardGrammarLexer(stream);
        lexer.reportErrorsAsExceptions();
        TokenStream tokens = new CommonTokenStream(lexer);
        //parses the tokens
        BoardGrammarParser parser = new BoardGrammarParser(tokens);
        parser.reportErrorsAsExceptions();
        ParseTree tree = parser.file();
        ParseTreeWalker treeWalker = new ParseTreeWalker();
        CreateBoardGrammarListener boardListener = new CreateBoardGrammarListener();
        treeWalker.walk(boardListener, tree);
        
        return boardListener.getBoard();
    
    }
    /**
     * Given a string gadget type, returns the GadgetType (enum) type
     * @param type, the String type of gadget
     * @return GadgetType gadget type, or invalid if type is not a valid gadget type.  
     */
    private static GadgetType convertType(String type){
        
        for(GadgetType gType : GadgetType.values()){
            if(type.equalsIgnoreCase("" + gType)){
                return gType;
            }
        }
        return GadgetType.INVALID;
        
    }
    
    /**
     * Listens to the parse tree as tree is walked
     * Creates obstacles based on the tokens on the board
     * 
     *
     */
    private static class CreateBoardGrammarListener extends BoardGrammarBaseListener{
        private Board board;
        
        /**
         * When exiting a board token
         * creates a new Board object
         */

        @Override
        public void exitBoard(BoardGrammarParser.BoardContext ctx) {   
            String boardName = "";
            double friction1 = Board.DEFAULT_FRICTION1;
            double friction2 = Board.DEFAULT_FRICTION2;
            double gravity = Board.DEFAULT_GRAVITY;
            if(ctx.namevar() != null){
                boardName = ctx.namevar().WORD().getText();
            }
            List<FltvarContext> fltVars = ctx.fltvar();
            for(FltvarContext fvctx : fltVars){
                String field = fvctx.WORD().getText();
                if(field.equals("gravity")){
                    gravity = Double.parseDouble(fvctx.flt().getText());
                }else if(field.equals("friction1")){
                    friction1 = Double.parseDouble(fvctx.flt().getText());
                }else if(field.equals("friction2")){
                    friction2 = Double.parseDouble(fvctx.flt().getText());
                }
            }
            board = new Board(boardName, gravity, friction1, friction2);
        }
        
        /**
         * When exiting a ball token
         * Creates a new ball object
         */
        @Override
        public void exitBall(BoardGrammarParser.BallContext ctx) {
            List<FltvarContext> fltVars = ctx.fltvar();
            double x = 0;
            double y = 0;
            double xVel = 0;
            double yVel = 0;
            for(FltvarContext fvctx : fltVars){
                String field = fvctx.WORD().getText();
                if(field.equals("x")){
                    x = Double.parseDouble(fvctx.flt().getText());
                }else if(field.equals("y")){
                    y = Double.parseDouble(fvctx.flt().getText());
                }else if(field.equals("xVelocity")){
                    xVel = Double.parseDouble(fvctx.flt().getText());
                }else if(field.equals("yVelocity")){
                    yVel = Double.parseDouble(fvctx.flt().getText());
                }
            }
            board.add(new Ball(x,y,xVel,yVel));
        }
        
        /**
         * When exiting a gadget object
         * Will create a gadget of the type given in the parser tokens
         */
        @Override
        public void exitGadgetdef(BoardGrammarParser.GadgetdefContext ctx){
            GadgetType type = OutputProcessor.convertType(ctx.WORD().getText());
            String name = ctx.namevar().WORD().getText();
            List<IntvarContext> intVars = ctx.intvar();
            List<WordvarContext> wordVars = ctx.wordvar();
            Integer x = null;
            Integer y = null;
            Integer w = null;
            Integer h = null;
            Integer angle = null;
            for(IntvarContext ivctx : intVars){
                String field = ivctx.WORD().getText();
                if(field.equals("x")){
                    x = Integer.parseInt(ivctx.INT().getText());
                }else if(field.equals("y")){
                    y = Integer.parseInt(ivctx.INT().getText());
                }else if(field.equals("width")){
                    w = Integer.parseInt(ivctx.INT().getText());
                }else if(field.equals("height")){
                    h = Integer.parseInt(ivctx.INT().getText());
                }else if(field.equals("orientation")){
                    angle = Integer.parseInt(ivctx.INT().getText());
                }else{
                    throw new RuntimeException("invalid variable assignment: " + field);
                }
            }
            String otherBoard = "";
            String otherPortal = "";
            for(WordvarContext wvctx : wordVars){
                String field = wvctx.WORD(0).getText();
                if(field.equals("otherBoard")){
                    otherBoard = wvctx.WORD(1).getText();
                }else if(field.equals("otherPortal")){
                    otherPortal = wvctx.WORD(1).getText();
                }else{
                    throw new RuntimeException("invalid variable assignment: " + field);
                }
            }
            //checks to see which type of gadget the given gadget is.
            switch(type){
            case ABSORBER:
                board.add(new Absorber(x,y,w,h,name));
                break;
            case SQUAREBUMPER:
                board.add(new SquareBumper(x,y,name));
                break;
            case CIRCLEBUMPER:
                board.add(new CircleBumper(x,y,name));
                break;
            case TRIANGLEBUMPER:
                board.add(new TriangleBumper(x,y,angle,name));
                break;
            case LEFTFLIPPER:
                board.add(new Flipper(x,y,FlipperType.LEFT,angle,name));
                break;
            case RIGHTFLIPPER:
                board.add(new Flipper(x,y,FlipperType.RIGHT,angle,name));
                break;
            case PORTAL:
                board.addPortal(x, y, name, otherBoard, otherPortal);
                break;
                default:
                    throw new IllegalArgumentException("Unrecognized gadget type: " + ctx.WORD().getText());
            }
            
            
        }


        /**
         * When exiting a fire token
         * links trigger with action
         */
        @Override
        public void exitFireInstance(BoardGrammarParser.FireInstanceContext ctx) {
            String action = ctx.wordvar().WORD(1).getText();
            String trigger = ctx.trigger().WORD().getText();
            board.createTriggerLink(trigger,action);
        }
        /**
         * When exiting a key-definition token
         */
        @Override
        public void exitKeydef(KeydefContext ctx){
            String pressType = ctx.keypress().getText();
            String key = "";
            String action = "";
            for(WordvarContext wvctx : ctx.wordvar()){
                String field = wvctx.WORD(0).getText();
                if(field.equals("key")){
                    key = wvctx.WORD(1).getText();
                }else if(field.equals("action")){
                    action = wvctx.WORD(1).getText();
                }else{
                    throw new RuntimeException("invalid variable assignment: " + field);                
                }
            }
            String keyName = pressType + ":" + key;
            board.bindKey(keyName, action);
        }
        
        /**
         * Returns the board created from the parsed file
         * @return board, the board parsed from the file
         */
        public Board getBoard(){
            return this.board;
        }
    }   
}

