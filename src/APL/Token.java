package APL;

import java.util.*;

public class Token {
  public TType type;
  public String repr;
  public ArrayList<Token> tokens;
  public String line;
  public int pos;
  Token (TType t, String s) {
    type = t;
    repr = s;
  }
  Token (TType t, ArrayList<Token> s) {
    assert(t==TType.expr||t==TType.lines||t==TType.pick||t==TType.usr);
    type = t;
    tokens = s;
  }
  Token (TType t) {
    type = t;
    if (type == TType.set) repr = "←";
    if (type == TType.lines) tokens = new ArrayList<>();
  }
  public String toString() {
    return toRepr();
  }
  
  String toTree(String p) {
    StringBuilder r = new StringBuilder();
    r.append(p).append(type);
    if (repr != null) r.append(": ").append(repr);
    r.append('\n');
    p+= "  ";
    if (tokens != null) {
      r.append("\n");
      for (Token t : tokens) r.append(t.toTree(p));
    }
    return r.toString();
  }
  
  public String toRepr() {
    switch (type) {
      case expr: case pick:
        StringBuilder res = new StringBuilder(type == TType.expr? "(" : "[");
        for (Token t : tokens) {
          if (res.length() > 1) res.append(" ");
          res.append(t.toRepr());
        }
        return res + (type==TType.expr? ")" : "]");
      case usr: case lines:
        res = new StringBuilder(type == TType.usr ? "{" : "(");
        for (Token t : tokens) {
          if (res.length() > 1) res.append(" ⋄ ");
          res.append(t.toRepr());
        }
        return res + (type==TType.usr? "}" : ")");

      case name:case op:case number: return repr;
      case set: return "←";
      case guard: return ":";
      case errGuard: return "::";
      default:
        res = new StringBuilder(type + "");
        return res.toString();
    }
  }
}
