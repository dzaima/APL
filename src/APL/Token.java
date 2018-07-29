package APL;

import java.util.*;

public class Token {
  public TType type;
  public String repr;
  public ArrayList<Token> tokens;
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
  /* tree format
  String toString(String p) {
    String r = "";
    r+= p+type;
    if (repr != null) r+= ": "+repr;
    p+= "  ";
    if (tokens != null) {
      r+= "\n";
      for (Token t : tokens) r+= t.toString(p);
    }
    return r;
  } */
  public String toRepr() {
    switch (type) {
      case usr:case expr:case pick:
        StringBuilder res = new StringBuilder(type == TType.expr ? "(" : type == TType.usr ? "{" : "[");
        for (Token t : tokens) {
          if (res.length() > 1) res.append(" ");
          res.append(t.toRepr());
        }
        return res + (type==TType.expr?")":type==TType.usr?"}":"]");
      case lines:
        res = new StringBuilder();
        for (Token t : tokens) {
          if (res.length() > 0) res.append(" ⋄ ");
          res.append(t.toRepr());
        }
        return res + (type==TType.expr?")":type==TType.usr?"}":"]");

      case name:case op:case number: return repr;
      case set: return "←";
      default:
        res = new StringBuilder(type + "");
        return res.toString();
    }
  }
}
