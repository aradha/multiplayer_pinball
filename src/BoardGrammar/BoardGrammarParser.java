// Generated from BoardGrammar.g4 by ANTLR 4.0

package BoardGrammar;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class BoardGrammarParser extends Parser {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__6=1, T__5=2, T__4=3, T__3=4, T__2=5, T__1=6, T__0=7, SPACE=8, ENDLINE=9, 
		COMMENT=10, INT=11, FLOAT=12, WORD=13, EQUALS=14;
	public static final String[] tokenNames = {
		"<INVALID>", "'keyup'", "'fire'", "'name'", "'board'", "'ball'", "'trigger'", 
		"'keydown'", "SPACE", "ENDLINE", "COMMENT", "INT", "FLOAT", "WORD", "EQUALS"
	};
	public static final int
		RULE_flt = 0, RULE_comment = 1, RULE_endline = 2, RULE_intvar = 3, RULE_fltvar = 4, 
		RULE_namevar = 5, RULE_wordvar = 6, RULE_keypress = 7, RULE_board = 8, 
		RULE_ball = 9, RULE_gadgetdef = 10, RULE_keydef = 11, RULE_trigger = 12, 
		RULE_fireInstance = 13, RULE_ignore = 14, RULE_term = 15, RULE_line = 16, 
		RULE_file = 17;
	public static final String[] ruleNames = {
		"flt", "comment", "endline", "intvar", "fltvar", "namevar", "wordvar", 
		"keypress", "board", "ball", "gadgetdef", "keydef", "trigger", "fireInstance", 
		"ignore", "term", "line", "file"
	};

	@Override
	public String getGrammarFileName() { return "BoardGrammar.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public ATN getATN() { return _ATN; }


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

	public BoardGrammarParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class FltContext extends ParserRuleContext {
		public TerminalNode FLOAT() { return getToken(BoardGrammarParser.FLOAT, 0); }
		public TerminalNode INT() { return getToken(BoardGrammarParser.INT, 0); }
		public FltContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).enterFlt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).exitFlt(this);
		}
	}

	public final FltContext flt() throws RecognitionException {
		FltContext _localctx = new FltContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_flt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(36);
			_la = _input.LA(1);
			if ( !(_la==INT || _la==FLOAT) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CommentContext extends ParserRuleContext {
		public TerminalNode COMMENT() { return getToken(BoardGrammarParser.COMMENT, 0); }
		public CommentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).enterComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).exitComment(this);
		}
	}

	public final CommentContext comment() throws RecognitionException {
		CommentContext _localctx = new CommentContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_comment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(38); match(COMMENT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EndlineContext extends ParserRuleContext {
		public TerminalNode SPACE(int i) {
			return getToken(BoardGrammarParser.SPACE, i);
		}
		public TerminalNode ENDLINE() { return getToken(BoardGrammarParser.ENDLINE, 0); }
		public List<TerminalNode> SPACE() { return getTokens(BoardGrammarParser.SPACE); }
		public EndlineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_endline; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).enterEndline(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).exitEndline(this);
		}
	}

	public final EndlineContext endline() throws RecognitionException {
		EndlineContext _localctx = new EndlineContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_endline);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(41);
			_la = _input.LA(1);
			if (_la==SPACE) {
				{
				setState(40); match(SPACE);
				}
			}

			setState(43); match(ENDLINE);
			setState(45);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				{
				setState(44); match(SPACE);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IntvarContext extends ParserRuleContext {
		public TerminalNode WORD() { return getToken(BoardGrammarParser.WORD, 0); }
		public TerminalNode INT() { return getToken(BoardGrammarParser.INT, 0); }
		public TerminalNode EQUALS() { return getToken(BoardGrammarParser.EQUALS, 0); }
		public TerminalNode SPACE() { return getToken(BoardGrammarParser.SPACE, 0); }
		public IntvarContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_intvar; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).enterIntvar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).exitIntvar(this);
		}
	}

	public final IntvarContext intvar() throws RecognitionException {
		IntvarContext _localctx = new IntvarContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_intvar);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(47); match(SPACE);
			setState(48); match(WORD);
			setState(49); match(EQUALS);
			setState(50); match(INT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FltvarContext extends ParserRuleContext {
		public TerminalNode WORD() { return getToken(BoardGrammarParser.WORD, 0); }
		public TerminalNode EQUALS() { return getToken(BoardGrammarParser.EQUALS, 0); }
		public FltContext flt() {
			return getRuleContext(FltContext.class,0);
		}
		public TerminalNode SPACE() { return getToken(BoardGrammarParser.SPACE, 0); }
		public FltvarContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fltvar; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).enterFltvar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).exitFltvar(this);
		}
	}

	public final FltvarContext fltvar() throws RecognitionException {
		FltvarContext _localctx = new FltvarContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_fltvar);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(52); match(SPACE);
			setState(53); match(WORD);
			setState(54); match(EQUALS);
			setState(55); flt();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NamevarContext extends ParserRuleContext {
		public TerminalNode WORD() { return getToken(BoardGrammarParser.WORD, 0); }
		public TerminalNode EQUALS() { return getToken(BoardGrammarParser.EQUALS, 0); }
		public TerminalNode SPACE() { return getToken(BoardGrammarParser.SPACE, 0); }
		public NamevarContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_namevar; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).enterNamevar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).exitNamevar(this);
		}
	}

	public final NamevarContext namevar() throws RecognitionException {
		NamevarContext _localctx = new NamevarContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_namevar);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(57); match(SPACE);
			setState(58); match(3);
			setState(59); match(EQUALS);
			setState(60); match(WORD);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WordvarContext extends ParserRuleContext {
		public List<TerminalNode> WORD() { return getTokens(BoardGrammarParser.WORD); }
		public TerminalNode WORD(int i) {
			return getToken(BoardGrammarParser.WORD, i);
		}
		public TerminalNode EQUALS() { return getToken(BoardGrammarParser.EQUALS, 0); }
		public TerminalNode SPACE() { return getToken(BoardGrammarParser.SPACE, 0); }
		public WordvarContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_wordvar; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).enterWordvar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).exitWordvar(this);
		}
	}

	public final WordvarContext wordvar() throws RecognitionException {
		WordvarContext _localctx = new WordvarContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_wordvar);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(62); match(SPACE);
			setState(63); match(WORD);
			setState(64); match(EQUALS);
			setState(65); match(WORD);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class KeypressContext extends ParserRuleContext {
		public KeypressContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_keypress; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).enterKeypress(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).exitKeypress(this);
		}
	}

	public final KeypressContext keypress() throws RecognitionException {
		KeypressContext _localctx = new KeypressContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_keypress);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(67);
			_la = _input.LA(1);
			if ( !(_la==1 || _la==7) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BoardContext extends ParserRuleContext {
		public TerminalNode SPACE() { return getToken(BoardGrammarParser.SPACE, 0); }
		public FltvarContext fltvar(int i) {
			return getRuleContext(FltvarContext.class,i);
		}
		public NamevarContext namevar() {
			return getRuleContext(NamevarContext.class,0);
		}
		public List<FltvarContext> fltvar() {
			return getRuleContexts(FltvarContext.class);
		}
		public BoardContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_board; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).enterBoard(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).exitBoard(this);
		}
	}

	public final BoardContext board() throws RecognitionException {
		BoardContext _localctx = new BoardContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_board);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(69); match(4);
			setState(71);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				{
				setState(70); namevar();
				}
				break;
			}
			setState(76);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					{
					{
					setState(73); fltvar();
					}
					} 
				}
				setState(78);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			}
			setState(80);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				{
				setState(79); match(SPACE);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BallContext extends ParserRuleContext {
		public FltvarContext fltvar(int i) {
			return getRuleContext(FltvarContext.class,i);
		}
		public NamevarContext namevar() {
			return getRuleContext(NamevarContext.class,0);
		}
		public List<FltvarContext> fltvar() {
			return getRuleContexts(FltvarContext.class);
		}
		public BallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ball; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).enterBall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).exitBall(this);
		}
	}

	public final BallContext ball() throws RecognitionException {
		BallContext _localctx = new BallContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_ball);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(82); match(5);
			setState(83); namevar();
			setState(85); 
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(84); fltvar();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(87); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			} while ( _alt!=2 && _alt!=-1 );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class GadgetdefContext extends ParserRuleContext {
		public TerminalNode WORD() { return getToken(BoardGrammarParser.WORD, 0); }
		public List<WordvarContext> wordvar() {
			return getRuleContexts(WordvarContext.class);
		}
		public IntvarContext intvar(int i) {
			return getRuleContext(IntvarContext.class,i);
		}
		public NamevarContext namevar() {
			return getRuleContext(NamevarContext.class,0);
		}
		public List<IntvarContext> intvar() {
			return getRuleContexts(IntvarContext.class);
		}
		public WordvarContext wordvar(int i) {
			return getRuleContext(WordvarContext.class,i);
		}
		public GadgetdefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gadgetdef; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).enterGadgetdef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).exitGadgetdef(this);
		}
	}

	public final GadgetdefContext gadgetdef() throws RecognitionException {
		GadgetdefContext _localctx = new GadgetdefContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_gadgetdef);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(89); match(WORD);
			setState(90); namevar();
			setState(92); 
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(91); intvar();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(94); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			} while ( _alt!=2 && _alt!=-1 );
			setState(99);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					{
					{
					setState(96); wordvar();
					}
					} 
				}
				setState(101);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class KeydefContext extends ParserRuleContext {
		public List<WordvarContext> wordvar() {
			return getRuleContexts(WordvarContext.class);
		}
		public KeypressContext keypress() {
			return getRuleContext(KeypressContext.class,0);
		}
		public WordvarContext wordvar(int i) {
			return getRuleContext(WordvarContext.class,i);
		}
		public KeydefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_keydef; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).enterKeydef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).exitKeydef(this);
		}
	}

	public final KeydefContext keydef() throws RecognitionException {
		KeydefContext _localctx = new KeydefContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_keydef);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(102); keypress();
			setState(103); wordvar();
			setState(104); wordvar();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TriggerContext extends ParserRuleContext {
		public TerminalNode WORD() { return getToken(BoardGrammarParser.WORD, 0); }
		public TerminalNode EQUALS() { return getToken(BoardGrammarParser.EQUALS, 0); }
		public TerminalNode SPACE() { return getToken(BoardGrammarParser.SPACE, 0); }
		public TriggerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_trigger; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).enterTrigger(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).exitTrigger(this);
		}
	}

	public final TriggerContext trigger() throws RecognitionException {
		TriggerContext _localctx = new TriggerContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_trigger);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(106); match(SPACE);
			setState(107); match(6);
			setState(108); match(EQUALS);
			setState(109); match(WORD);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FireInstanceContext extends ParserRuleContext {
		public TriggerContext trigger() {
			return getRuleContext(TriggerContext.class,0);
		}
		public WordvarContext wordvar() {
			return getRuleContext(WordvarContext.class,0);
		}
		public FireInstanceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fireInstance; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).enterFireInstance(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).exitFireInstance(this);
		}
	}

	public final FireInstanceContext fireInstance() throws RecognitionException {
		FireInstanceContext _localctx = new FireInstanceContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_fireInstance);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(111); match(2);
			setState(112); trigger();
			setState(113); wordvar();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IgnoreContext extends ParserRuleContext {
		public TerminalNode SPACE() { return getToken(BoardGrammarParser.SPACE, 0); }
		public CommentContext comment() {
			return getRuleContext(CommentContext.class,0);
		}
		public IgnoreContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ignore; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).enterIgnore(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).exitIgnore(this);
		}
	}

	public final IgnoreContext ignore() throws RecognitionException {
		IgnoreContext _localctx = new IgnoreContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_ignore);
		try {
			setState(117);
			switch (_input.LA(1)) {
			case SPACE:
				enterOuterAlt(_localctx, 1);
				{
				setState(115); match(SPACE);
				}
				break;
			case COMMENT:
				enterOuterAlt(_localctx, 2);
				{
				setState(116); comment();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TermContext extends ParserRuleContext {
		public FireInstanceContext fireInstance() {
			return getRuleContext(FireInstanceContext.class,0);
		}
		public BallContext ball() {
			return getRuleContext(BallContext.class,0);
		}
		public KeydefContext keydef() {
			return getRuleContext(KeydefContext.class,0);
		}
		public GadgetdefContext gadgetdef() {
			return getRuleContext(GadgetdefContext.class,0);
		}
		public TermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).enterTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).exitTerm(this);
		}
	}

	public final TermContext term() throws RecognitionException {
		TermContext _localctx = new TermContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_term);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(123);
			switch (_input.LA(1)) {
			case WORD:
				{
				setState(119); gadgetdef();
				}
				break;
			case 5:
				{
				setState(120); ball();
				}
				break;
			case 2:
				{
				setState(121); fireInstance();
				}
				break;
			case 1:
			case 7:
				{
				setState(122); keydef();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LineContext extends ParserRuleContext {
		public TermContext term() {
			return getRuleContext(TermContext.class,0);
		}
		public EndlineContext endline() {
			return getRuleContext(EndlineContext.class,0);
		}
		public TerminalNode SPACE() { return getToken(BoardGrammarParser.SPACE, 0); }
		public IgnoreContext ignore() {
			return getRuleContext(IgnoreContext.class,0);
		}
		public LineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_line; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).enterLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).exitLine(this);
		}
	}

	public final LineContext line() throws RecognitionException {
		LineContext _localctx = new LineContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_line);
		try {
			setState(134);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(125); endline();
				setState(126); term();
				{
				setState(128);
				switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
				case 1:
					{
					setState(127); match(SPACE);
					}
					break;
				}
				}
				}
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(130); endline();
				}
				}
				break;

			case 3:
				enterOuterAlt(_localctx, 3);
				{
				{
				setState(131); endline();
				setState(132); ignore();
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FileContext extends ParserRuleContext {
		public LineContext line(int i) {
			return getRuleContext(LineContext.class,i);
		}
		public List<LineContext> line() {
			return getRuleContexts(LineContext.class);
		}
		public TerminalNode EOF() { return getToken(BoardGrammarParser.EOF, 0); }
		public BoardContext board() {
			return getRuleContext(BoardContext.class,0);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BoardGrammarListener ) ((BoardGrammarListener)listener).exitFile(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_file);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(136); board();
			setState(140);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SPACE || _la==ENDLINE) {
				{
				{
				setState(137); line();
				}
				}
				setState(142);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(143); match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\2\3\20\u0094\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b"+
		"\4\t\t\t\4\n\t\n\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t"+
		"\20\4\21\t\21\4\22\t\22\4\23\t\23\3\2\3\2\3\3\3\3\3\4\5\4,\n\4\3\4\3\4"+
		"\5\4\60\n\4\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3"+
		"\7\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\n\3\n\5\nJ\n\n\3\n\7\nM\n\n\f\n\16\n"+
		"P\13\n\3\n\5\nS\n\n\3\13\3\13\3\13\6\13X\n\13\r\13\16\13Y\3\f\3\f\3\f"+
		"\6\f_\n\f\r\f\16\f`\3\f\7\fd\n\f\f\f\16\fg\13\f\3\r\3\r\3\r\3\r\3\16\3"+
		"\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\20\3\20\5\20x\n\20\3\21\3\21"+
		"\3\21\3\21\5\21~\n\21\3\22\3\22\3\22\5\22\u0083\n\22\3\22\3\22\3\22\3"+
		"\22\5\22\u0089\n\22\3\23\3\23\7\23\u008d\n\23\f\23\16\23\u0090\13\23\3"+
		"\23\3\23\3\23\2\24\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$\2\4\3\r"+
		"\16\4\3\3\t\t\u0091\2&\3\2\2\2\4(\3\2\2\2\6+\3\2\2\2\b\61\3\2\2\2\n\66"+
		"\3\2\2\2\f;\3\2\2\2\16@\3\2\2\2\20E\3\2\2\2\22G\3\2\2\2\24T\3\2\2\2\26"+
		"[\3\2\2\2\30h\3\2\2\2\32l\3\2\2\2\34q\3\2\2\2\36w\3\2\2\2 }\3\2\2\2\""+
		"\u0088\3\2\2\2$\u008a\3\2\2\2&\'\t\2\2\2\'\3\3\2\2\2()\7\f\2\2)\5\3\2"+
		"\2\2*,\7\n\2\2+*\3\2\2\2+,\3\2\2\2,-\3\2\2\2-/\7\13\2\2.\60\7\n\2\2/."+
		"\3\2\2\2/\60\3\2\2\2\60\7\3\2\2\2\61\62\7\n\2\2\62\63\7\17\2\2\63\64\7"+
		"\20\2\2\64\65\7\r\2\2\65\t\3\2\2\2\66\67\7\n\2\2\678\7\17\2\289\7\20\2"+
		"\29:\5\2\2\2:\13\3\2\2\2;<\7\n\2\2<=\7\5\2\2=>\7\20\2\2>?\7\17\2\2?\r"+
		"\3\2\2\2@A\7\n\2\2AB\7\17\2\2BC\7\20\2\2CD\7\17\2\2D\17\3\2\2\2EF\t\3"+
		"\2\2F\21\3\2\2\2GI\7\6\2\2HJ\5\f\7\2IH\3\2\2\2IJ\3\2\2\2JN\3\2\2\2KM\5"+
		"\n\6\2LK\3\2\2\2MP\3\2\2\2NL\3\2\2\2NO\3\2\2\2OR\3\2\2\2PN\3\2\2\2QS\7"+
		"\n\2\2RQ\3\2\2\2RS\3\2\2\2S\23\3\2\2\2TU\7\7\2\2UW\5\f\7\2VX\5\n\6\2W"+
		"V\3\2\2\2XY\3\2\2\2YW\3\2\2\2YZ\3\2\2\2Z\25\3\2\2\2[\\\7\17\2\2\\^\5\f"+
		"\7\2]_\5\b\5\2^]\3\2\2\2_`\3\2\2\2`^\3\2\2\2`a\3\2\2\2ae\3\2\2\2bd\5\16"+
		"\b\2cb\3\2\2\2dg\3\2\2\2ec\3\2\2\2ef\3\2\2\2f\27\3\2\2\2ge\3\2\2\2hi\5"+
		"\20\t\2ij\5\16\b\2jk\5\16\b\2k\31\3\2\2\2lm\7\n\2\2mn\7\b\2\2no\7\20\2"+
		"\2op\7\17\2\2p\33\3\2\2\2qr\7\4\2\2rs\5\32\16\2st\5\16\b\2t\35\3\2\2\2"+
		"ux\7\n\2\2vx\5\4\3\2wu\3\2\2\2wv\3\2\2\2x\37\3\2\2\2y~\5\26\f\2z~\5\24"+
		"\13\2{~\5\34\17\2|~\5\30\r\2}y\3\2\2\2}z\3\2\2\2}{\3\2\2\2}|\3\2\2\2~"+
		"!\3\2\2\2\177\u0080\5\6\4\2\u0080\u0082\5 \21\2\u0081\u0083\7\n\2\2\u0082"+
		"\u0081\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0089\3\2\2\2\u0084\u0089\5\6"+
		"\4\2\u0085\u0086\5\6\4\2\u0086\u0087\5\36\20\2\u0087\u0089\3\2\2\2\u0088"+
		"\177\3\2\2\2\u0088\u0084\3\2\2\2\u0088\u0085\3\2\2\2\u0089#\3\2\2\2\u008a"+
		"\u008e\5\22\n\2\u008b\u008d\5\"\22\2\u008c\u008b\3\2\2\2\u008d\u0090\3"+
		"\2\2\2\u008e\u008c\3\2\2\2\u008e\u008f\3\2\2\2\u008f\u0091\3\2\2\2\u0090"+
		"\u008e\3\2\2\2\u0091\u0092\7\1\2\2\u0092%\3\2\2\2\17+/INRY`ew}\u0082\u0088"+
		"\u008e";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
	}
}