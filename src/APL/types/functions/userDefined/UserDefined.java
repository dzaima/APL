package APL.types.functions.userDefined;

import APL.*;
import APL.tokenizer.Token;
import APL.tokenizer.types.*;
import APL.types.Obj;

public class UserDefined {
  public static Obj of(DfnTok ts, Scope sc) {
    Type type = funType(ts, true);
    if (type == Type.fn) return new Dfn(ts, sc);
    else if (type == Type.mop) return new Dmop(ts, sc);
    else if (type == Type.dop) return new Ddop(ts, sc);
    else throw new IllegalStateException("UserDefined.funType = "+type);
  }
  private static Type funType(TokArr<?> i, boolean first) {
    Type type = Type.fn;
    if (!(i instanceof DfnTok) || first) for (Token t : i.tokens) {
      if (t instanceof OpTok) {
        String op = ((OpTok) t).op;
        if (op.equals("⍹")) type = Type.mop;
        else if (op.equals("⍶")) return Type.dop;
      } else if (t instanceof TokArr<?>) {
        Type n = funType((TokArr<?>) t, false);
        if (n == Type.mop) type = Type.mop;
        else if (n.equals(Type.dop)) return Type.dop;
      } else if (t instanceof BacktickTok) {
        Type n = funType(((BacktickTok) t).value(), false);
        if (n == Type.mop) type = Type.mop;
        else if (n.equals(Type.dop)) return Type.dop;
      }
    }
    return type;
  }
}