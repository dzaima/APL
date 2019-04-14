package APL.tokenizer;

import java.util.*;

import APL.*;
import APL.errors.*;
import APL.tokenizer.types.*;

public class Tokenizer {
  private static final char[] validNames = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_".toCharArray();
  private static final String ops = "⍺⍳⍴⍵!%*+,-./<=>?@\\^|~⍬⊢∆⊣⌷¨⍨⌿⍀≤≥≠∨∧÷×∊↑↓○⌈⌊∇∘⊂⊃∩∪⊥⊤⍱⍲⍒⍋⍉⌽⊖⍟⌹⍕⍎⍫⍪≡≢⍷→⎕⍞⍣⍶⍸⍹⌸⌺⍇⍢⍤⍁⍂⊆⊇⊙⌾⌻⌼⍃⍄⍅⍆⍈⍊⍌⍍⍏⍐⍑⍓⍔⍖⍗⍘⍚⍛⍜⍠⍡⍥⍦⍧⍩⍭⍮⍯⍰√‽⊗ϼ∍⋾∞…"; // stolen from https://bitbucket.org/zacharyjtaylor/rad/src/master/RAD_document.txt?fileviewer=file-view-default // "+-/⍳⍬⍴∘⎕⊂÷⍺⍵≢¨";
  private static boolean validName(char i) {
    for (char c : validNames) if (c == i) return true;
    return false;
  }
  static class Line {
    final ArrayList<Token> ts;
    final String line;
    final int pos;
  
    Line(String line, int pos, ArrayList<Token> ts) {
      this.ts = ts;
      this.line = line;
      this.pos = pos;
    }
  
    Line(String line, int pos) {
      this(line, pos, new ArrayList<>());
    }
  
    public int size() {
      return ts.size();
    }
  
    public void add(Token r) {
      ts.add(r);
    }
    LineTok tok() {
      int epos = size() == 0? pos : ts.get(size()-1).epos;
      return new LineTok(line, pos, epos, ts);
    }
  }
  static class Block { // temp storage of multiple lines
    final ArrayList<Line> a;
    final char b;
    private final String raw;
    private final int pos;
  
    Block(ArrayList<Line> a, char b, String raw, int pos) {
      this.a = a;
      this.b = b;
      this.raw = raw;
      this.pos = pos;
    }
    public String toString() {
      return "<"+a+","+b+">";
    }
  }
  public static BasicLines tokenize(String raw) {
    int li = 0;
    int len = raw.length();
    
  
    var levels = new ArrayList<Block>();
    levels.add(new Block(new ArrayList<>(), '⋄', raw, 0));
    levels.get(0).a.add(new Line(null, 0, new ArrayList<>()));

    for (int i = 0; i < len; li = i) {
      Block expr = levels.get(levels.size()-1);
      ArrayList<Line> lines = expr.a;
      Line tokens = lines.get(lines.size()-1);
      char c = raw.charAt(i);
      char next = i+1<len? raw.charAt(i+1) : ' ';
      String cS = String.valueOf(c);
      if (c == '(' || c == '{' || c == '[') {
        char match;
        switch(c) {
          case '(': match = ')'; break;
          case '{': match = '}'; break;
          case '[': match = ']'; break;
          default: throw new Error("this should really not happen");
        }
        levels.add(new Block(new ArrayList<>(), match, raw, i));
        lines = levels.get(levels.size()-1).a;
        lines.add(new Line(raw, i));

        i++;
      } else if (c == ')' || c == '}' || c == ']') {
        if (lines.size() > 0 && lines.get(lines.size()-1).size() == 0) lines.remove(lines.size()-1); // no trailing empties!!
        Block closed = levels.remove(levels.size()-1);
        if (c != closed.b) throw new SyntaxError("mismatched parentheses of "+c+" and "+closed.b);
        
        var lineTokens = new ArrayList<LineTok>();
        for (Line ta : closed.a) lineTokens.add(ta.tok());
        Token r;
        switch(c) {
          case ')': r = new ParenTok  (raw, closed.pos, i+1, lineTokens); break;
          case '}': r = new DfnTok    (raw, closed.pos, i+1, lineTokens); break;
          case ']': r = new BracketTok(raw, closed.pos, i+1, lineTokens); break;
          default: throw new Error("this should really not happen");
        }
        lines = levels.get(levels.size()-1).a;
        tokens = lines.get(lines.size()-1);
        tokens.add(r);
        i++;
      } else if (validName(c)  ||  c=='⎕' && validName(next)) {
        i++;
        while (i < len && (validName(raw.charAt(i))  ||  raw.charAt(i)>='0' && raw.charAt(i)<='9')) i++;
        var name = raw.substring(li, i);
        if (c == '⎕') name = name.toUpperCase();
        tokens.add(new NameTok(raw, li, i, name));
      } else if (c >= '0' && c <= '9' || c == '¯' || c == '.' && next >= '0' && next <= '9') {
        i++;
        boolean foundPoint = false;
        while (i < len && (c = raw.charAt(i)) >= '0' && c <= '9'  ||  c == '.' && !foundPoint) {
          if (c == '.') foundPoint = true;
          i++;
        }
        tokens.add(new NumTok(raw, li, i, raw.substring(li, i)));
      } else if (ops.contains(cS)) {
        tokens.add(new OpTok(raw, i, i+1, cS));
        i++;
      } else if (c == '←') {
        tokens.add(new SetTok(raw, i, i+1));
        i++;
      } else if (c == ':') {
        if (next == ':') {
          tokens.add(new DColonTok(raw, i, i+1));
          i++;
        } else tokens.add(new ColonTok(raw, i, i+1));
        i++;
      } else if (c == '\'') {
        StringBuilder str = new StringBuilder();
        i++;
        if (i >= len) throw new SyntaxError("unfinished string");
        while (true) {
          if (raw.charAt(i) == '\'') {
            if (i+1 < len && raw.charAt(i+1) == '\'') {
              str.append("'");
              i++;
            } else break;
          } else str.append(raw.charAt(i));
          i++;
          if (i >= len) throw new SyntaxError("unfinished string");
        }
        i++;
        tokens.add(new ChrTok(raw, li, i, str.toString()));
      } else if (c == '"') {
        StringBuilder str = new StringBuilder();
        i++;
        while (true) {
          if (raw.charAt(i) == '\"') {
            if (i+1 < len && raw.charAt(i+1) == '\"') {
              str.append("\"");
              i++;
            } else break;
          } else str.append(raw.charAt(i));
          i++;
          if (i >= len) throw new SyntaxError("unfinished string");
        }
        i++;
        tokens.add(new StrTok(raw, li, i, str.toString()));
      } else if (c == '\n' || c == '⋄' || c == '\r' || c == ';') {
  
        if (c == ';') tokens.add(new SemiTok(raw, i, i+1));
        
        if (tokens.size() > 0) {
          lines.add(new Line(raw, li));
        }
        i++;
      } else if (c == '⍝') {
        i++;
        while (i < len && raw.charAt(i) != '\n') i++;
      } else if (c == ' ' || c == '\t') {i++;} else {
        Main.colorprint("warning: unknown token `"+c+"`", 206);
        i++;
      }
      //if (c != ' ') {
      //  printdbg("> "+(c+"").replace("\n","\\n"));
      //  printdbg("curr: "+join(levels, "|"));
      //}
      assert li < i; // error if nothing changed!
    }
    if (levels.size() != 1) throw new SyntaxError("error matching parentheses"); // or too many
    var lines = levels.get(0).a;
    if (lines.size() > 0 && lines.get(lines.size()-1).size() == 0) lines.remove(lines.size()-1); // no trailing empties!!
    var expressions = new ArrayList<LineTok>();
    for (Line line : lines) {
      expressions.add(line.tok());
    }
    return new BasicLines(raw, 0, len, expressions);
  }
}
