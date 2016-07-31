grammar BoardGrammar;


//Modified from 6.005 Recitation 13 code.  

// This puts a Java package statement at the top of the output Java files.
@header {
package BoardGrammar;
}

// This adds code to the generated lexer and parser.
@members {
    /**
     * Call this method to have the lexer or parser throw a RuntimeException if
     * it encounters an error.
     */
    public void reportErrorsAsExceptions() {
        addErrorListener(new ExceptionThrowingErrorListener());
    }
    
    private static class ExceptionThrowingErrorListener extends BaseErrorListener {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer,
                Object offendingSymbol, int line, int charPositionInLine,
                String msg, RecognitionException e) {
            throw new RuntimeException(msg);
        }
    }
}

/*
 * These are the lexical rules. They define the tokens used by the lexer.
 * *** ANTLR requires tokens to be CAPITALIZED, like START_ITALIC, END_ITALIC, and TEXT.
 */
SPACE : [ \t\f]+;
ENDLINE : '\r'?'\n';
COMMENT : '#' ~[\n\r]*;
INT : '-'?[0-9]+;
FLOAT : '-'?[0-9]+('.'[0-9]+);
WORD : [a-z_A-Z0-9]+;
EQUALS : [ \t\f]*'='[ \t\f]*;

/*
 * These are the parser rules. They define the structures used by the parser.
 * *** ANTLR requires grammar nonterminals to be lowercase, like html, normal, and italic.
 */
 flt : INT | FLOAT;
 comment : COMMENT;
 endline : SPACE? ENDLINE SPACE?;
 intvar : SPACE WORD EQUALS INT;
 fltvar : SPACE WORD EQUALS flt;
 namevar : SPACE 'name' EQUALS WORD;
 wordvar : SPACE WORD EQUALS WORD;
 keypress : 'keyup' | 'keydown';
 
 board : 'board' namevar? fltvar* SPACE?; 
 ball : 'ball' namevar fltvar+;
 
 gadgetdef : WORD namevar intvar+ wordvar*;
 
 keydef : keypress wordvar wordvar;
 
 trigger : SPACE 'trigger' EQUALS WORD;
 fireInstance : 'fire' trigger wordvar;
 ignore : SPACE | comment;
 term : (gadgetdef | ball | fireInstance | keydef);
 line : (endline term (SPACE?)) | (endline) | (endline ignore);
 file : board line* EOF;
  
