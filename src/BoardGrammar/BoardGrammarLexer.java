// Generated from BoardGrammar.g4 by ANTLR 4.0

package BoardGrammar;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class BoardGrammarLexer extends Lexer {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__6=1, T__5=2, T__4=3, T__3=4, T__2=5, T__1=6, T__0=7, SPACE=8, ENDLINE=9, 
		COMMENT=10, INT=11, FLOAT=12, WORD=13, EQUALS=14;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"'keyup'", "'fire'", "'name'", "'board'", "'ball'", "'trigger'", "'keydown'", 
		"SPACE", "ENDLINE", "COMMENT", "INT", "FLOAT", "WORD", "EQUALS"
	};
	public static final String[] ruleNames = {
		"T__6", "T__5", "T__4", "T__3", "T__2", "T__1", "T__0", "SPACE", "ENDLINE", 
		"COMMENT", "INT", "FLOAT", "WORD", "EQUALS"
	};


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


	public BoardGrammarLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "BoardGrammar.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\2\4\20\u0083\b\1\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b"+
		"\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\3\2"+
		"\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3"+
		"\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\b"+
		"\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\6\tL\n\t\r\t\16\tM\3\n\5\nQ\n\n\3\n\3"+
		"\n\3\13\3\13\7\13W\n\13\f\13\16\13Z\13\13\3\f\5\f]\n\f\3\f\6\f`\n\f\r"+
		"\f\16\fa\3\r\5\re\n\r\3\r\6\rh\n\r\r\r\16\ri\3\r\3\r\6\rn\n\r\r\r\16\r"+
		"o\3\16\6\16s\n\16\r\16\16\16t\3\17\7\17x\n\17\f\17\16\17{\13\17\3\17\3"+
		"\17\7\17\177\n\17\f\17\16\17\u0082\13\17\2\20\3\3\1\5\4\1\7\5\1\t\6\1"+
		"\13\7\1\r\b\1\17\t\1\21\n\1\23\13\1\25\f\1\27\r\1\31\16\1\33\17\1\35\20"+
		"\1\3\2\n\5\13\13\16\16\"\"\4\f\f\17\17\3\62;\3\62;\3\62;\6\62;C\\aac|"+
		"\5\13\13\16\16\"\"\5\13\13\16\16\"\"\u008d\2\3\3\2\2\2\2\5\3\2\2\2\2\7"+
		"\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2"+
		"\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2"+
		"\35\3\2\2\2\3\37\3\2\2\2\5%\3\2\2\2\7*\3\2\2\2\t/\3\2\2\2\13\65\3\2\2"+
		"\2\r:\3\2\2\2\17B\3\2\2\2\21K\3\2\2\2\23P\3\2\2\2\25T\3\2\2\2\27\\\3\2"+
		"\2\2\31d\3\2\2\2\33r\3\2\2\2\35y\3\2\2\2\37 \7m\2\2 !\7g\2\2!\"\7{\2\2"+
		"\"#\7w\2\2#$\7r\2\2$\4\3\2\2\2%&\7h\2\2&\'\7k\2\2\'(\7t\2\2()\7g\2\2)"+
		"\6\3\2\2\2*+\7p\2\2+,\7c\2\2,-\7o\2\2-.\7g\2\2.\b\3\2\2\2/\60\7d\2\2\60"+
		"\61\7q\2\2\61\62\7c\2\2\62\63\7t\2\2\63\64\7f\2\2\64\n\3\2\2\2\65\66\7"+
		"d\2\2\66\67\7c\2\2\678\7n\2\289\7n\2\29\f\3\2\2\2:;\7v\2\2;<\7t\2\2<="+
		"\7k\2\2=>\7i\2\2>?\7i\2\2?@\7g\2\2@A\7t\2\2A\16\3\2\2\2BC\7m\2\2CD\7g"+
		"\2\2DE\7{\2\2EF\7f\2\2FG\7q\2\2GH\7y\2\2HI\7p\2\2I\20\3\2\2\2JL\t\2\2"+
		"\2KJ\3\2\2\2LM\3\2\2\2MK\3\2\2\2MN\3\2\2\2N\22\3\2\2\2OQ\7\17\2\2PO\3"+
		"\2\2\2PQ\3\2\2\2QR\3\2\2\2RS\7\f\2\2S\24\3\2\2\2TX\7%\2\2UW\n\3\2\2VU"+
		"\3\2\2\2WZ\3\2\2\2XV\3\2\2\2XY\3\2\2\2Y\26\3\2\2\2ZX\3\2\2\2[]\7/\2\2"+
		"\\[\3\2\2\2\\]\3\2\2\2]_\3\2\2\2^`\t\4\2\2_^\3\2\2\2`a\3\2\2\2a_\3\2\2"+
		"\2ab\3\2\2\2b\30\3\2\2\2ce\7/\2\2dc\3\2\2\2de\3\2\2\2eg\3\2\2\2fh\t\5"+
		"\2\2gf\3\2\2\2hi\3\2\2\2ig\3\2\2\2ij\3\2\2\2jk\3\2\2\2km\7\60\2\2ln\t"+
		"\6\2\2ml\3\2\2\2no\3\2\2\2om\3\2\2\2op\3\2\2\2p\32\3\2\2\2qs\t\7\2\2r"+
		"q\3\2\2\2st\3\2\2\2tr\3\2\2\2tu\3\2\2\2u\34\3\2\2\2vx\t\b\2\2wv\3\2\2"+
		"\2x{\3\2\2\2yw\3\2\2\2yz\3\2\2\2z|\3\2\2\2{y\3\2\2\2|\u0080\7?\2\2}\177"+
		"\t\t\2\2~}\3\2\2\2\177\u0082\3\2\2\2\u0080~\3\2\2\2\u0080\u0081\3\2\2"+
		"\2\u0081\36\3\2\2\2\u0082\u0080\3\2\2\2\16\2MPX\\adioty\u0080";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
	}
}