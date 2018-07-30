package APL.types.functions.userDefined;

import APL.*;
import APL.types.*;

import static APL.APL.*;

public class UserDefined {
  public static Obj of(Token ts, Scope sc) {
    // TODO: dops
    assert(ts.type == TType.usr);
    Type type = funType(ts);
    if (type == Type.fn) return new Dfn(ts, sc);
    else if (type == Type.mop) return new Dmop(ts, sc);
    else throw up; // TEMP
  }
  private static Type funType(Token i) {
    Type type = Type.fn;
    for (Token t : i.tokens) {
      if (t.type == TType.op) {
        if (t.repr.equals("⍶")) type = Type.mop;
        else if (t.repr.equals("⍹")) return Type.dop;
      } else if (t.tokens != null) {
        Type n = funType(t);
        if (n == Type.mop) type = Type.mop;
        else if (n.equals(Type.dop)) return Type.dop;
      }
    }
    return type;
  }
}
