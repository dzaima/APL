package APL.tokenizer;

import APL.errors.SyntaxError;
import APL.tokenizer.types.*;
import APL.types.BigValue;

import java.math.BigInteger;
import java.util.ArrayList;

public class Tokenizer {
  private static final char[] validNames = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_∆".toCharArray();
  private static final String ops = "⍺⍳⍴⍵!%*+,-./<=>?@\\^|~⍬⊢⊣⌷¨⍨⌿⍀≤≥≠∨∧÷×∊↑↓○⌈⌊∇∘⊂⊃∩∪⊥⊤⍱⍲⍒⍋⍉⌽⊖⍟⌹⍕⍎⍫⍪≡≢⍷⎕⍞⍣⍶⍸⍹⌸⌺⍇⍢⍤⍁⍂⊆⊇⊙⌾⌻⌼⍃⍄⍅⍆⍈⍊⍌⍍⍏⍐⍑⍓⍔⍖⍗⍘⍚⍛⍜⍠⍡⍥⍦⍧⍩⍭⍮⍯⍰√‽⊗ϼ∍⋾…ᑈᐵ"; // stolen from https://bitbucket.org/zacharyjtaylor/rad/src/master/RAD_document.txt?fileviewer=file-view-default
  private static boolean validNameStart(char c) {
    for (char l : validNames) if (l == c) return true;
    return false;
  }
  public static boolean validNameMid(char c) {
    return validNameStart(c) || c >= '0' && c <= '9';
  }
  static class Line {
    final ArrayList<Token> ts;
    final String line;
    final int pos;
    Integer annoyingBacktickPos;
    
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
      if (annoyingBacktickPos != null) {
        ts.add(new BacktickTok(line, annoyingBacktickPos, r.epos, r));
        annoyingBacktickPos = null;
      } else {
        ts.add(r);
      }
    }
    
    LineTok tok() {
      if (annoyingBacktickPos != null) throw new SyntaxError("Nothing after backtick");
      int epos = size() == 0? pos : ts.get(size()-1).epos;
      return new LineTok(line, pos, epos, ts);
    }
  }
  static class Block { // temp storage of multiple lines
    final ArrayList<Line> a;
    final char b;
    private final int pos;
    
    Block(ArrayList<Line> a, char b, int pos) {
      this.a = a;
      this.b = b;
      this.pos = pos;
    }
    public String toString() {
      return "<"+a+","+b+">";
    }
  }
  
  public static BasicLines tokenize(String raw) {
    return tokenize(raw, false);
  }
  
  public static BasicLines tokenize(String raw, boolean pointless) { // pointless means unevaled things get tokens
    int li = 0;
    int len = raw.length();
    
    var levels = new ArrayList<Block>();
    levels.add(new Block(new ArrayList<>(), '⋄', 0));
    levels.get(0).a.add(new Line(raw, 0, new ArrayList<>()));
    
    for (int i = 0; i < len; li = i) {
      Block expr = levels.get(levels.size() - 1);
      ArrayList<Line> lines = expr.a;
      Line tokens = lines.get(lines.size() - 1);
      try {
        char c = raw.charAt(i);
        char next = i + 1 < len? raw.charAt(i + 1) : ' ';
        String cS = String.valueOf(c);
        if (c == '(' || c == '{' || c == '[') {
          char match;
          switch (c) {
            case '(':
              match = ')';
              break;
            case '{':
              match = '}';
              break;
            case '[':
              match = ']';
              break;
            default:
              throw new Error("this should really not happen");
          }
          levels.add(new Block(new ArrayList<>(), match, i));
          lines = levels.get(levels.size() - 1).a;
          lines.add(new Line(raw, i));
          
          i++;
        } else if (c == ')' || c == '}' || c == ']') {
          Block closed = levels.remove(levels.size() - 1);
          if (c != closed.b) {
            if (pointless) {
              levels.add(closed);
              tokens.add(new ErrTok(raw, i));
              // and leave running for quick exit
            }
            throw new SyntaxError("mismatched parentheses of " + c + " and " + closed.b);
          }
          if (lines.size() > 0 && lines.get(lines.size() - 1).size() == 0) lines.remove(lines.size() - 1); // no trailing empties!!
          
          var lineTokens = new ArrayList<LineTok>();
          for (Line ta : closed.a) lineTokens.add(ta.tok());
          Token r;
          switch (c) {
            case ')':
              r = new ParenTok(raw, closed.pos, i + 1, lineTokens);
              break;
            case '}':
              r = new DfnTok(raw, closed.pos, i + 1, lineTokens);
              break;
            case ']':
              r = new BracketTok(raw, closed.pos, i + 1, lineTokens);
              break;
            default:
              throw new Error("this should really not happen "+c);
          }
          lines = levels.get(levels.size() - 1).a;
          tokens = lines.get(lines.size() - 1);
          tokens.add(r);
          i++;
        } else if (validNameStart(c) || c == '⎕' && validNameStart(next)) {
          i++;
          while (i < len && validNameMid(raw.charAt(i))) i++;
          var name = raw.substring(li, i);
          if (c == '⎕') name = name.toUpperCase();
          tokens.add(new NameTok(raw, li, i, name));
        } else if (c=='¯' && next=='∞') {
          i+= 2;
          tokens.add(new NumTok(raw, li, i, Double.NEGATIVE_INFINITY));
        } else if (c == '∞') {
          i++;
          tokens.add(new NumTok(raw, li, i, Double.POSITIVE_INFINITY));
        } else if (c>='0' && c<='9' || c=='¯' || c=='.' && next>='0' && next<='9') {
          boolean negative = c=='¯';
          if (negative) i++;
          int si = i;
          boolean hasPoint = false;
          while(i < len) {
            c = raw.charAt(i);
            if (hasPoint) {
              if (c<'0' || c>'9') break;
            } else if (c<'0' || c>'9') {
              if (c == '.') hasPoint = true;
              else break;
            }
            i++;
          }
          double f = Double.parseDouble(raw.substring(si,i));
          if (negative) f = -f;
          if (i < len) {
            c = raw.charAt(i);
            boolean hasE = c=='e' | c=='E';
            if (hasE && i+1==len) throw new SyntaxError("unfinished number");
            boolean hasExp = hasE && !validNameStart(raw.charAt(i+1));
            if (hasExp) {
              i++;
              c = raw.charAt(i);
              boolean negExp = c == '¯';
              if (negExp) i++;
              si = i;
              while (i < len) {
                c = raw.charAt(i);
                if (c < '0' || c > '9') break;
                i++;
              }
              int exp = Integer.parseInt(raw.substring(si, i));
              if (negExp) exp = -exp;
              f *= Math.pow(10, exp);
            }
            if (i<len && raw.charAt(i)=='L' && (i+1 == len || !validNameMid(raw.charAt(i+1)))) {
              if (hasExp || hasPoint) {
                if (hasExp) throw new SyntaxError("biginteger literal with exponent");
                throw new SyntaxError("biginteger literal with decimal part");
              }
              i++;
              BigInteger big = new BigInteger(raw.substring(si, i-1));
              if (negative) big = big.negate();
              tokens.add(new BigTok(raw, li, i, new BigValue(big)));
            } else tokens.add(new NumTok(raw, li, i, f));
          } else tokens.add(new NumTok(raw, li, i, f));
        } else if (ops.contains(cS)) {
          tokens.add(new OpTok(raw, i, i + 1, cS));
          i++;
        } else if (c == '→') {
          tokens.add(new SetTok(raw, i, i + 1));
          i++;
        } else if (c == '`') {
          if (tokens.annoyingBacktickPos != null) throw new SyntaxError("` after `");
          tokens.annoyingBacktickPos = i;
          i++;
        } else if (c == ':') {
          if (next == ':') {
            tokens.add(new DColonTok(raw, i, i + 2));
            i++;
          } else tokens.add(new ColonTok(raw, i, i + 1));
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
          while (raw.charAt(i) != '"') {
            if (raw.charAt(i) == '\\') {
              i++;
              SyntaxError.must(i < len, "unfinished string");
              char esc = raw.charAt(i);
              switch (esc) {
                case  'n': str.append('\n'); break;
                case  'r': str.append('\r'); break;
                case  '"': str.append('\"'); break;
                case '\'': str.append('\''); break;
                case '\\': str.append('\\'); break;
                case  't': str.append('\t'); break;
                case  'x': {
                  SyntaxError.must(i+2 < len, "unfinished string");
                  int num = Integer.parseInt(raw.substring(i+1, i+3), 16);
                  str.append(Character.toChars(num));
                  i+= 2;
                  break;
                }
                case  'u': {
                  SyntaxError.must(i+4 < len, "unfinished string");
                  int num = Integer.parseInt(raw.substring(i+1, i+5), 16);
                  str.append(Character.toChars(num));
                  i+= 4;
                  break;
                }
                default: throw new SyntaxError("invalid escape character "+esc);
              }
            } else {
              str.append(raw.charAt(i));
            }
            i++;
            SyntaxError.must(i < len, "unfinished string");
          }
          i++;
          tokens.add(new StrTok(raw, li, i, str.toString()));
        } else if (c == '\n' || c == '⋄' || c == '\r' || c == ';') {
          if (c == '⋄' && pointless) tokens.add(new DiamondTok(raw, i));
          
          if (c == ';') tokens.add(new SemiTok(raw, i, i + 1));
          
          if (tokens.size() > 0) {
            lines.add(new Line(raw, li));
          }
          i++;
        } else if (c == '⍝') {
          i++;
          while (i < len && raw.charAt(i) != '\n') i++;
          if (pointless) tokens.add(new CommentTok(raw, li, i));
        } else if (c == '#') {
          tokens.add(new ScopeTok(raw, i, i+1));
          i++;
        } else if (c == ' ' || c == '\t') {i++;} else {
          if (pointless) tokens.add(new ErrTok(raw, i, i + 1));
          else {
            String hex = Integer.toHexString(c);
            
            while(hex.length() < 4)
              //noinspection StringConcatenationInLoop \\ shut UUuuppp
              hex = "0"+hex;
            throw new SyntaxError("unknown token `" + c + "` (\\u"+hex+")");
          }
          i++;
        }
        //if (c != ' ') {
        //  printdbg("> "+(c+"").replace("\n","\\n"));
        //  printdbg("curr: "+join(levels, "|"));
        //}
        assert li < i; // error if nothing changed!
      } catch (Throwable e) {
        // System.out.println("BAD");
        // e.printStackTrace();
        if (!pointless) throw e;
        if (li == i) i = li + 1; // lazy exit out of infinite loops
        tokens.add(new ErrTok(raw, li, i));
      }
    }
    if (levels.size() != 1) {
      if (!pointless) throw new SyntaxError("error matching parentheses"); // or too many
      // else, attempt to recover
      while (levels.size() > 1) {
        Block closed = levels.remove(levels.size() - 1);
        
        var lineTokens = new ArrayList<LineTok>();
        for (Line ta : closed.a) lineTokens.add(ta.tok());
        Token r;
        switch (closed.b) {
          case ')':
            r = new ParenTok(raw, closed.pos, len, lineTokens);
            break;
          case '}':
            r = new DfnTok(raw, closed.pos, len, lineTokens);
            break;
          case ']':
            r = new BracketTok(raw, closed.pos, len, lineTokens);
            break;
          default:
            throw new Error("this should really not happen "+closed.b);
        }
        var lines = levels.get(levels.size() - 1).a;
        Line tokens = lines.get(lines.size() - 1);
        tokens.add(r);
      }
    }
    var lines = levels.get(0).a;
    if (lines.size() > 0 && lines.get(lines.size()-1).size() == 0) lines.remove(lines.size()-1); // no trailing empties!!
    var expressions = new ArrayList<LineTok>();
    for (Line line : lines) {
      expressions.add(line.tok());
    }
    return new BasicLines(raw, 0, len, expressions);
  }
}