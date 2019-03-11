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
  }
  static class Block { // temp storage of multiple lines
    final ArrayList<Line> a;
    final char b;
    private final String line;
    private final int pos;
  
    Block(ArrayList<Line> a, char b, String line, int pos) {
      this.a = a;
      this.b = b;
      this.line = line;
      this.pos = pos;
    }
    public String toString() {
      return "<"+a+","+b+">";
    }
  }
  public static BasicLines tokenize(String s) {
    int li = 0;
    int len = s.length();
    String[] rLines = s.split("\n", -1);
    if (rLines.length == 0) return new BasicLines(s, 0, new ArrayList<>());
    String cL = rLines[0]; // current line
    int cLP = 0; // current lines index in rLines
    int cP = 0; // current pos in line
  
    var levels = new ArrayList<Block>();
    levels.add(new Block(new ArrayList<>(), '⋄', rLines[0], 0));
    levels.get(0).a.add(new Line(null, 0, new ArrayList<>()));

    for (int i = 0; i < len; li = i) {
      Block expr = levels.get(levels.size()-1);
      ArrayList<Line> lines = expr.a;
      Line tokens = lines.get(lines.size()-1);
      char c = s.charAt(i);
      char next = i+1<len? s.charAt(i+1) : ' ';
      String cS = String.valueOf(c);
      if (c == '(' || c == '{' || c == '[') {
        char match;
        switch(c) {
          case '(': match = ')'; break;
          case '{': match = '}'; break;
          case '[': match = ']'; break;
          default: throw new Error("this should really not happen");
        }
        levels.add(new Block(new ArrayList<>(),match, cL, cP));
        lines = levels.get(levels.size()-1).a;
        lines.add(new Line(cL, cP));

        i++;
      } else if (c == ')' || c == '}' || c == ']') {
        if (lines.size() > 0 && lines.get(lines.size()-1).size() == 0) lines.remove(lines.size()-1); // no trailing empties!!
        Block closed = levels.remove(levels.size()-1);
        if (c != closed.b) throw new SyntaxError("mismatched parentheses of "+c+" and "+closed.b);
        
        var lineTokens = new ArrayList<LineTok>();
        for (var ta : closed.a) lineTokens.add(new LineTok(ta.line, ta.pos, ta.ts));
        Token r;
        switch(c) {
          case ')': r = new ParenTok  (s, closed.pos, lineTokens); break;
          case '}': r = new DfnTok    (s, closed.pos, lineTokens); break;
          case ']': r = new BracketTok(s, closed.pos, lineTokens); break;
          default: throw new Error("this should really not happen");
        }
        lines = levels.get(levels.size()-1).a;
        tokens = lines.get(lines.size()-1);
        tokens.add(r);
        i++;
      } else if (validName(c)  ||  c=='⎕' && validName(next)) {
        i++;
        while (i < len && (validName(s.charAt(i))  ||  s.charAt(i)>='0' && s.charAt(i)<='9')) i++;
        var name = s.substring(li, i);
        if (c == '⎕') name = name.toUpperCase();
        tokens.add(new NameTok(cL, cP, name));
      } else if (c >= '0' && c <= '9' || c == '¯' || c == '.' && next >= '0' && next <= '9') {
        i++;
        boolean foundPoint = false;
        while (i < len && (c = s.charAt(i)) >= '0' && c <= '9'  ||  c == '.' && !foundPoint) {
          if (c == '.') foundPoint = true;
          i++;
        }
        tokens.add(new NumTok(cL, cP, s.substring(li, i)));
      } else if (ops.contains(cS)) {
        tokens.add(new OpTok(cL, cP, cS));
        i++;
      } else if (c == '←') {
        tokens.add(new SetTok(cL, cP));
        i++;
      } else if (c == ':') {
        if (next == ':') {
          tokens.add(new DColonTok(cL, cP));
          i++;
        } else tokens.add(new ColonTok(cL, cP));
        i++;
      } else if (c == '\'') {
        StringBuilder str = new StringBuilder();
        i++;
        while (true) {
          if (s.charAt(i) == '\'') {
            if (i+1 < len && s.charAt(i+1) == '\'') {
              str.append("'");
              i++;
            } else break;
          } else str.append(s.charAt(i));
          i++;
          if (i >= len) throw new SyntaxError("unfinished string");
        }
        i++;
        tokens.add(new ChrTok(cL, cP, str.toString()));
      } else if (c == '"') {
        StringBuilder str = new StringBuilder();
        i++;
        while (true) {
          if (s.charAt(i) == '\"') {
            if (i+1 < len && s.charAt(i+1) == '\"') {
              str.append("\"");
              i++;
            } else break;
          } else str.append(s.charAt(i));
          i++;
          if (i >= len) throw new SyntaxError("unfinished string");
        }
        i++;
        tokens.add(new StrTok(cL, cP, str.toString()));
      } else if (c == '\n' || c == '⋄' || c == '\r' || c == ';') {
  
        if (c == ';') tokens.add(new SemiTok(cL, cP));
        
        if (tokens.size() > 0) {
          lines.add(new Line(cL, cP));
        }
        if (c == '\n') {
          cP = -1;
          cL = rLines[++cLP];
        }
        i++;
      } else if (c == '⍝') {
        i++;
        while (i < len && s.charAt(i) != '\n') i++;
      } else if (c == ' ' || c == '\t') {i++;} else {
        Main.colorprint("warning: unknown token `"+c+"`", 206);
        i++;
      }
      //if (c != ' ') {
      //  printdbg("> "+(c+"").replace("\n","\\n"));
      //  printdbg("curr: "+join(levels, "|"));
      //}
      assert li < i; // nothing changed!
      cP+= i-li;
    }
    if (levels.size() != 1) throw new SyntaxError("error matching parentheses"); // or too many
    var lines = levels.get(0).a;
    if (lines.size() > 0 && lines.get(lines.size()-1).size() == 0) lines.remove(lines.size()-1); // no trailing empties!!
    var expressions = new ArrayList<LineTok>();
    for (Line line : lines) {
      expressions.add(new LineTok(line.line, line.pos, line.ts));
    }
    return new BasicLines(rLines[0], 0, expressions);
  }
}
