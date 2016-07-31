// Generated from BoardGrammar.g4 by ANTLR 4.0

package BoardGrammar;

import org.antlr.v4.runtime.tree.*;

public interface BoardGrammarListener extends ParseTreeListener {
	void enterFireInstance(BoardGrammarParser.FireInstanceContext ctx);
	void exitFireInstance(BoardGrammarParser.FireInstanceContext ctx);

	void enterTrigger(BoardGrammarParser.TriggerContext ctx);
	void exitTrigger(BoardGrammarParser.TriggerContext ctx);

	void enterBall(BoardGrammarParser.BallContext ctx);
	void exitBall(BoardGrammarParser.BallContext ctx);

	void enterWordvar(BoardGrammarParser.WordvarContext ctx);
	void exitWordvar(BoardGrammarParser.WordvarContext ctx);

	void enterKeydef(BoardGrammarParser.KeydefContext ctx);
	void exitKeydef(BoardGrammarParser.KeydefContext ctx);

	void enterGadgetdef(BoardGrammarParser.GadgetdefContext ctx);
	void exitGadgetdef(BoardGrammarParser.GadgetdefContext ctx);

	void enterLine(BoardGrammarParser.LineContext ctx);
	void exitLine(BoardGrammarParser.LineContext ctx);

	void enterFlt(BoardGrammarParser.FltContext ctx);
	void exitFlt(BoardGrammarParser.FltContext ctx);

	void enterNamevar(BoardGrammarParser.NamevarContext ctx);
	void exitNamevar(BoardGrammarParser.NamevarContext ctx);

	void enterFltvar(BoardGrammarParser.FltvarContext ctx);
	void exitFltvar(BoardGrammarParser.FltvarContext ctx);

	void enterBoard(BoardGrammarParser.BoardContext ctx);
	void exitBoard(BoardGrammarParser.BoardContext ctx);

	void enterIntvar(BoardGrammarParser.IntvarContext ctx);
	void exitIntvar(BoardGrammarParser.IntvarContext ctx);

	void enterTerm(BoardGrammarParser.TermContext ctx);
	void exitTerm(BoardGrammarParser.TermContext ctx);

	void enterKeypress(BoardGrammarParser.KeypressContext ctx);
	void exitKeypress(BoardGrammarParser.KeypressContext ctx);

	void enterFile(BoardGrammarParser.FileContext ctx);
	void exitFile(BoardGrammarParser.FileContext ctx);

	void enterEndline(BoardGrammarParser.EndlineContext ctx);
	void exitEndline(BoardGrammarParser.EndlineContext ctx);

	void enterComment(BoardGrammarParser.CommentContext ctx);
	void exitComment(BoardGrammarParser.CommentContext ctx);

	void enterIgnore(BoardGrammarParser.IgnoreContext ctx);
	void exitIgnore(BoardGrammarParser.IgnoreContext ctx);
}