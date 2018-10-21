package APL;

import java.util.*;
import APL.errors.*;

class Tokenizer {
  private static final char[] validNames = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_".toCharArray();
  private static final String ops = "⍺⍳⍴⍵!%*+,-./<=>?@\\^|~⍬⊢∆⊣⌷¨⍨⌿⍀≤≥≠∨∧÷×∊↑↓○⌈⌊∇∘⊂⊃∩∪⊥⊤⍱⍲⍒⍋⍉⌽⊖⍟⌹⍕⍎⍫⍪≡≢⍷→⎕⍞⍣⍶⍸⍹⌸⌺⍇⍢⍤⍁⍂⊆⊇⊙⌾⌻⌼⍃⍄⍅⍆⍈⍊⍌⍍⍏⍐⍑⍓⍔⍖⍗⍘⍚⍛⍜⍠⍡⍥⍦⍧⍩⍭⍮⍯⍰√‽⊗ϼ∍⋾∞…"; // stolen from https://bitbucket.org/zacharyjtaylor/rad/src/master/RAD_document.txt?fileviewer=file-view-default // "+-/⍳⍬⍴∘⎕⊂÷⍺⍵≢¨";
  private static boolean validName(char i) {
    for (char c : validNames) if (c == i) return true;
    return false;
  }
  static class Expr {
    final ArrayList<ArrayList<Token>> a;
    final char b;
    Expr(ArrayList<ArrayList<Token>> a, char b) {
      this.a = a;
      this.b = b;
    }
    public String toString() {
      return "<"+a+","+b+">";
    }
  }
  static Token tokenize(String s) {
    Token uexpr = new Token(TType.expr, s, 0, s);
    var levels = new ArrayList<Expr>();
    levels.add(new Expr(new ArrayList<>(), '⋄'));
    levels.get(0).a.add(new ArrayList<>());
    int li = 0;
    int len = s.length();
    String[] rlines = s.split("\n", -1);
    String crline = rlines[0];
    int crlinei = 0;
    int reprpos = 0;
    for (int i = 0; i < len; li = i) {
      var expr = levels.get(levels.size()-1);
      var lines = expr.a;
      var tokens = lines.get(lines.size()-1);
      int si = i;
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
        levels.add(new Expr(new ArrayList<>(),match));
        lines = levels.get(levels.size()-1).a;
        lines.add(new ArrayList<>());
        //lines = new ArrayList();
        i++;
      } else if (c == ')' || c == '}' || c == ']') {
        if (lines.size() > 0 && lines.get(lines.size()-1).size() == 0) lines.remove(lines.size()-1); // no trailing empties!!
        Expr closed = levels.remove(levels.size()-1);
        if (c != closed.b) throw new SyntaxError("mismatched parentheses of "+c+" and "+closed.b);
        TType type;
        switch(c) {
          case ')': type = TType.expr; break;
          case '}': type = TType. usr; break;
          case ']': type = TType.pick; break;
          default: throw new Error("this should really not happen");
        }
        var lineTokens = new ArrayList<Token>();
        for (var ta : closed.a) lineTokens.add(new Token(TType.expr, ta, uexpr));
        Token t = new Token(type, lineTokens, uexpr);
        lines = levels.get(levels.size()-1).a;
        tokens = lines.get(lines.size()-1);
        tokens.add(t);
        i++;
      } else if (validName(c)  ||  c=='⎕' && validName(next)) {
        i++;
        while (i < len && (validName(s.charAt(i))  ||  s.charAt(i)>='0' && s.charAt(i)<='9')) i++;
        var name = s.substring(si, i);
        if (c == '⎕') name = name.toUpperCase();
        tokens.add(new Token(TType.name, name, reprpos, crline));
      } else if (c >= '0' && c <= '9' || c == '¯' || c == '.' && next >= '0' && next <= '9') {
        i++;
        boolean foundPoint = false;
        while (i < len && (c = s.charAt(i)) >= '0' && c <= '9'  ||  c == '.' && !foundPoint) {
          if (c == '.') foundPoint = true;
          i++;
        }
        tokens.add(new Token(TType.number, s.substring(si, i), reprpos, crline));
      } else if (ops.contains(cS)) {
        tokens.add(new Token(TType.op, cS, reprpos, crline));
        i++;
      } else if (c == '←') {
        tokens.add(new Token(TType.set, reprpos, crline));
        i++;
      } else if (c == ':') {
        if (next == ':') {
          tokens.add(new Token(TType.errGuard, reprpos, crline));
          i++;
        } else tokens.add(new Token(TType.guard, reprpos, crline));
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
        tokens.add(new Token(TType.chr, str.toString(), reprpos, crline));
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
        tokens.add(new Token(TType.str, str.toString(), reprpos, crline));
      } else if (c == '\n' || c == '⋄' || c == '\r' || c == ';') {
  
        if (c == ';') tokens.add(new Token(TType.semi, reprpos, crline));
        
        if (tokens.size() > 0) {
          lines.add(new ArrayList<>());
        }
        if (c == '\n') {
          reprpos-= 1;
          crline = rlines[++crlinei];
        }
        i++;
      } else if (c == '⍝') {
        i++;
        while (i < len && s.charAt(i) != '\n') i++;
      } else if (c == ' ') {i++;} else {
        Main.colorprint("warning: unknown token `"+c+"`", 206);
        i++;
      }
      //if (c != ' ') {
      //  printdbg("> "+(c+"").replace("\n","\\n"));
      //  printdbg("curr: "+join(levels, "|"));
      //}
      assert li < i; // nothing changed!
      reprpos+= i-li;
    }
    if (levels.size() != 1) throw new SyntaxError("error matching parentheses"); // or too many
    var lines = levels.get(0).a;
    if (lines.size() > 0 && lines.get(lines.size()-1).size() == 0) lines.remove(lines.size()-1); // no trailing empties!!
    var expressions = new ArrayList<Token>();
    for (ArrayList<Token> line : lines) {
      expressions.add(new Token(TType.expr, line, uexpr));
    }
    return new Token(TType.lines, expressions, uexpr);
  }
}
