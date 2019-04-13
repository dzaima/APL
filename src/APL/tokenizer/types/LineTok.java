package APL.tokenizer.types;

import APL.tokenizer.Token;

import java.util.*;

public class LineTok extends TokArr<Token> {
  private Integer colonPos;
  
  public LineTok(String raw, int spos, int epos, List<Token> tokens) {
    super(raw, spos, epos, tokens);
  }
  
  public static LineTok inherit(List<Token> tokens) {
    Token fst = tokens.get(0);
    return new LineTok(fst.raw, fst.spos, tokens.get(tokens.size()-1).epos, tokens);
  }
  
  public static LineTok inherit(Token tk) {
    ArrayList<Token> a = new ArrayList<>();
    a.add(tk);
    return new LineTok(tk.raw, tk.spos, tk.epos, a);
  }
  
  public int colonPos() {
    if (colonPos == null) {
      colonPos = -1;
      for (int i = 0; i < tokens.size(); i++) {
        if (tokens.get(i) instanceof ColonTok) {
          colonPos = i;
          break;
        }
      }
    }
    return colonPos;
  }
  private Integer eguardPos;
  public int eguardPos() {
    if (eguardPos == null) {
      eguardPos = -1;
      for (int i = 0; i < tokens.size(); i++) {
        if (tokens.get(i) instanceof DColonTok) {
          eguardPos = i;
          break;
        }
      }
    }
    return eguardPos;
  }
  
  @Override public String toRepr() {
    StringBuilder s = new StringBuilder();
    boolean tail = false;
    for (var v : tokens) {
      if (tail) s.append(" ");
      s.append(v.toRepr());
      tail = true;
    }
    return s.toString();
  }
}
