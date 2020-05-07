package APL.types.functions.builtins.fns;

import APL.Main;
import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.Builtin;

import java.math.BigInteger;

public class MulBuiltin extends Builtin {
  @Override public String repr() {
    return "×";
  }
  
  
  
  public Value identity() {
    return Num.ONE;
  }
  
  private static final NumMV NF = new NumMV() {
    public Value call(Num w) {
      double d = w.num;
      return d>0? Num.ONE : d<0? Num.MINUS_ONE : Num.ZERO;
    }
    public void call(double[] res, double[] a) {
      for (int i = 0; i < a.length; i++) res[i] = a[i]>0? 1 : a[i]<0? -1 : 0;
    }
    public Value call(BigValue w) {
      return Num.of(w.i.signum());
    }
  };
  public Value call(Value w) {
    return numChrMapM(NF, c -> Num.of(c.getCase()), c -> c.size()>0? Num.ONE : Num.ZERO, w);
  }
  
  static final D_NNeN DNF = new D_NNeN() {
    public double on(double a, double w) {
      return a*w;
    }
    public void on(double[] res, double a, double[] w) {
      for (int i = 0; i < w.length; i++) res[i] = a * w[i];
    }
    public void on(double[] res, double[] a, double w) {
      for (int i = 0; i < a.length; i++) res[i] = a[i] * w;
    }
    public void on(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) res[i] = a[i] * w[i];
    }
    public Value call(BigValue a, BigValue w) {
      return new BigValue(a.i.multiply(w.i));
    }
  };
  public Value call(Value a, Value w) {
    return numD(DNF, a, w);
  }
  
  public Value callInvW(Value a, Value w) {
    try {
      return new DivBuiltin().call(w, a);
    } catch (DomainError e) {
      throw new DomainError(e.getMessage(), this, e.cause);
    }
  }
  
  @Override public Value callInvA(Value a, Value w) {
    return callInvW(w, a);
  }
  
  
  private static final D_NNeN SET_SGN = new D_NNeN() {
    public double on(double o, double n) {
      if (n==0) return 0;
      if (o==0) throw new DomainError("⍢×: cannot set sign of 0 to "+Num.format(n));
      if (n== 1) return  Math.abs(o);
      if (n==-1) return -Math.abs(o);
      throw new DomainError("⍢×: cannot set sign to "+n);
    }
    public void on(double[] res, double o, double[] n) {
      for (int i = 0; i < res.length; i++) {
        double nc = n[i];
        if (o==0 && nc!=0) throw new DomainError("⍢×: cannot set sign of 0 to " + Num.format(nc));
        if (nc==0 || nc==1 || nc==-1) res[i] = Math.abs(o)*nc;
        else throw new DomainError("⍢×: cannot set sign to " + nc);
      }
    }
    public void on(double[] res, double[] o, double n) {
      for (int i = 0; i < res.length; i++) {
        double oc = o[i];
        if (oc==0 && n!=0) throw new DomainError("⍢×: cannot set sign of 0 to " + Num.format(n));
        if (n==0 || n==1 || n==-1) res[i] = Math.abs(oc)*n;
        else throw new DomainError("⍢×: cannot set sign to " + n);
      }
    }
    public void on(double[] res, double[] o, double[] n) {
      for (int i = 0; i < res.length; i++) {
        double oc = o[i];
        double nc = n[i];
        if (oc==0 && nc!=0) throw new DomainError("⍢×: cannot set sign of 0 to " + Num.format(nc));
        if (nc==0 || nc==1 || nc==-1) res[i] = Math.abs(oc)*nc;
        else throw new DomainError("⍢×: cannot set sign to " + nc);
      }
    }
    public Value call(BigValue o, BigValue n) {
      BigInteger oi = o.i;
      int ni = BigValue.safeInt(n.i);
      if (oi.signum()==0 && ni!=0) throw new DomainError("⍢×: cannot set sign of 0 to " + ni);
      if (ni== 0) return BigValue.ZERO;
      boolean neg = oi.signum() == -1;
      if (ni== 1 ^ neg) return o;
      if (ni==-1 ^ neg) return new BigValue(oi.negate());
      else throw new DomainError("⍢×: cannot set sign to " + ni);
    }
  };
  public Value under(Obj o, Value w) {
    Main.faulty = this;
    Value v = o instanceof Fun? ((Fun) o).call(call(w)) : (Value) o;
    return numD(SET_SGN, w, v);
  }
}