package APL.types.functions.builtins.fns;

import APL.Main;
import APL.errors.DomainError;
import APL.types.*;
import APL.types.functions.Builtin;

import java.math.BigInteger;

public class StileBuiltin extends Builtin {
  @Override public String repr() {
    return "|";
  }
  
  
  
  private static final NumMV NF = new NumMV() {
    public Value call(Num w) {
      return w.abs();
    }
    public void call(double[] res, double[] a) {
      for (int i = 0; i < a.length; i++) res[i] = Math.abs(a[i]);
    }
    public Value call(BigValue w) {
      return new BigValue(w.i.abs());
    }
  };
  
  public Value call(Value w) {
    return numChrMapM(NF, c->{ throw new DomainError("|char", this, w); }, c -> Num.of(c.size()), w);
  }
  
  private static final D_NNeN DNF = new D_NNeN() {
    public double on(double a, double w) {
      double c = w % a;
      if (c < 0) return c + a;
      return c;
    }
    public void on(double[] res, double a, double[] w) {
      int ia = (int) a;
      if (a == ia) {
        if (ia > 0 && (ia & ia-1) == 0) {
          // power of 2 ⍺
          int mask = ia-1;
          for (int i = 0; i < w.length; i++) {
            int intv = (int) w[i];
            if (intv == w[i]) res[i] = (intv & mask) + w[i]-intv;
            else {
              double c = w[i]%ia;
              if (c < 0) res[i] = c + a;
              else res[i] = c;
            }
          }
        } else {
          // integer ⍺
          for (int i = 0; i < w.length; i++) {
            double c = w[i]%ia;
            if (c < 0) res[i] = c + a;
            else res[i] = c;
          }
        }
      } else {
        // arbitrary double ⍺
        for (int i = 0; i < w.length; i++) {
          double c = w[i]%a;
          if (c < 0) res[i] = c + a;
          else res[i] = c;
        }
      }
    }
    public void on(double[] res, double[] a, double w) {
      if (w > 0) for (int i = 0; i < a.length; i++) res[i] = w % a[i];
      else       for (int i = 0; i < a.length; i++) {
        double c = w % a[i];
        if (c < 0) res[i] = c + a[i];
        else res[i] = c;
      }
    }
    public void on(double[] res, double[] a, double[] w) {
      for (int i = 0; i < a.length; i++) {
        double c = w[i] % a[i];
        if (c < 0) res[i] = c + a[i];
        else res[i] = c;
      }
    }
    public Value call(BigValue a, BigValue w) {
      BigInteger r = w.i.remainder(a.i);
      if (r.signum()<0) r = r.add(a.i);
      return new BigValue(r);
    }
  };
  public Value call(Value a0, Value w0) {
    return numD(DNF, a0, w0);
  }
  
  
  
  private static final D_NNeN CPY_SGN = new D_NNeN() {
    public double on(double o, double n) {
      if (o==0 && n!=0) throw new DomainError("⍢|: cannot add sign to "+n+" as original was 0");
      return o<0? -n : n;
    }
    public void on(double[] res, double o, double[] n) {
      for (int i = 0; i < res.length; i++) {
        double nc = n[i];
        if (o==0 && nc!=0) throw new DomainError("⍢|: cannot add sign to "+nc+" as original was 0");
        res[i] = o<0? -nc : nc;
      }
    }
    public void on(double[] res, double[] o, double n) {
      for (int i = 0; i < res.length; i++) {
        double oc = o[i];
        if (oc==0 && n!=0) throw new DomainError("⍢|: cannot add sign to "+n+" as original was 0");
        res[i] = oc<0? -n : n;
      }
    }
    public void on(double[] res, double[] o, double[] n) {
      for (int i = 0; i < res.length; i++) {
        double oc = o[i];
        double nc = n[i];
        if (oc==0 && nc!=0) throw new DomainError("⍢|: cannot add sign to "+nc+" as original was 0");
        res[i] = oc<0? -nc : nc;
      }
    }
    public Value call(BigValue o, BigValue n) {
      BigInteger oi = o.i;
      BigInteger ni = n.i;
      if (oi.signum()==0 && ni.signum()!=0) throw new DomainError("⍢|: cannot add sign to "+ni+" as original was 0");
      return oi.signum()<0? new BigValue(ni.negate()) : n;
    }
  };
  public Value under(Obj o, Value w) {
    Main.faulty = this;
    Value v = o instanceof Fun? ((Fun) o).call(call(w)) : (Value) o;
    return numD(CPY_SGN, w, v);
  }
}