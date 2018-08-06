package APL;

import APL.types.*;
import APL.errors.*;

import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class Main {
  public static boolean debug = false;
  public static boolean prettyprint = false;
  public static boolean quotestrings = false;
  static int printlvl = 0;
  public static Error up = new Error("A problem has been detected and APL has been shut down to prevent damage to your computer.");
  static long startingMillis = System.currentTimeMillis();
  
  public static void main(String[] args) {
    try {
      Scope global = new Scope();
      if (args.length > 0) {
        debug = args[0].contains("d");
        prettyprint = args[0].contains("p");
        quotestrings = args[0].contains("q");
        if (args.length > 1) {
          int rest = args[0].contains("e") ? 2 : 1;
          for (int i = rest; i < args.length; i++) {
            String s = readFile(args[i]);
            if (s == null) colorprint("File " + args[i] + " not found", 246);
            else exec(s, global);
          }
          if (args[0].contains("e")) {
            Obj r = exec(args[1], global);
            if (!r.shy) println(r.toString());
          }
        }
      }
      if (args.length == 0 || args[0].contains("r")) { // REPL
        Scanner console = new Scanner(System.in);
        REPL: while (true) {
          print("> ");
          try {
            String cr = console.nextLine();
            if (cr.equals("exit")) break;
            if (cr.startsWith(")")) {
              String[] parts = cr.split(" ");
              String t = parts[0].toUpperCase();
              switch (t) {
                case ")EX":
                  exec(readFile(parts[1]), global);
                  break;
                case ")DEBUG":
                  debug = !debug;
                  break;
                case ")QUOTE":
                  quotestrings = !quotestrings;
                  break;
                case ")ONELINE":
                  prettyprint = !prettyprint;
                  break;
                case ")OFF": case ")EXIT": case ")STOP":
                  break REPL;
                case ")TOKENIZE": println(Tokenizer.tokenize(cr.substring(t.length())).toTree(""));
                case ")TOKENIZEREPR": println(Tokenizer.tokenize(cr.substring(t.length())).toRepr());
                  break ;
                default:
                  throw new SyntaxError("Undefined user command");
              }
            } else {
              Obj r = exec(cr, global);
              if (!r.shy) println(r.toString());
            }
          } catch (APLError | NYIError e) {
            String[] ns = e.getClass().getName().split("[$.]");
            colorprint(ns[ns.length - 1] + ": " + e.getMessage(), 246);
          } catch (java.util.NoSuchElementException e) {
            break; // REPL ended
          }
        }
      }
    } catch (Throwable e) {
      e.printStackTrace();
      colorprint(e + ": " + e.getMessage(), 246);
    }
  }
  
  
  static void print(String s) {
    System.out.print(s);
  }
  
  public static void println(String s) {
    System.out.println(s);
  }
  public static String human(ArrType t) {
    switch (t) {
      case array: return "array";
      case chr: return "character";
      case num: return "number";
      default: throw new IllegalStateException();
    }
  }
  static String human(Type t) {
    switch (t) {
      case array: return "array";
      case var: return "variable";
      case  fn: case  bfn: return "function";
      case mop: case bmop: return "monadic operator";
      case dop: case bdop: return "dyadic operator";
      default: throw new IllegalStateException();
    }
  }
  private static String readFile(String path) {
    try {
      byte[] encoded = Files.readAllBytes(Paths.get(path));
      return new String(encoded, StandardCharsets.UTF_8);
    } catch (IOException e) {
      return null;
    }
  }
  
  @SuppressWarnings("WeakerAccess")
  public static Obj exec(String s, Scope sc) {
    Token t = Tokenizer.tokenize(s);
    printdbg(t);
    return execLines(t, sc);
  }
  
  static public void printdbg(Object... args) {
    if (!debug) return;
    if (args.length > 0) print(args[0].toString());
    for (int i = 1; i < args.length; i++) {
      print(" ");
      print(args[i].toString());
    }
    print("\n");
  }
  
  static public Obj execLines(Token t, Scope sc) {
    assert (t.type == TType.lines || t.type == TType.usr);
    Obj res = null;
    for (Token ln : t.tokens) {
      res = execTok(ln, sc);
    }
    return res;
  }
  
  static Obj execTok(Token ln, Scope sc) {
    return new Exec(ln, sc).exec();
  }
  
  static void colorprint(String s, int col) {
    println("\u001b[38;5;" + col + "m" + s + "\u001b[0m");
    //println("\u001b["+col+"m"+s+"\u001b[0m");
  }
  
  public static Value toAPL(int[] ia) {
    var va = new ArrayList<Value>();
    for (int i : ia) va.add(new Num(i));
    return new Arr(va);
  }
  
  public static Arr string(String s) {
    var vs = new ArrayList<Value>();
    for (char c : s.toCharArray()) vs.add(new Char(c));
    Arr a = new Arr(vs);
    a.prototype = Char.SPACE;
    return a;
  }
  public static int compare(Value a, Value w) {
    if (a.valtype == ArrType.num && w.valtype == ArrType.num) return ((Num)a).compareTo((Num)w);
    throw new DomainError("comparing non-numbers"); // TODO not do that
  }
  public static Num compareObj(Value a, Value w) {
    int c = compare(a, w);
    if (c > 0) return Num.ONE;
    if (c < 0) return Num.MINUS_ONE;
    return Num.ZERO;
  }
  public static boolean bool(Value v, Scope sc) {
    String cond = ((Arr)sc.get("⎕COND")).string(false);
    assert cond != null;
    if (cond.endsWith(" ")) {
      if (v instanceof Char) {
        return ((Char) v).chr != ' ';
      }
      cond = cond.substring(0, cond.length()-2);
    }
    if (!(v instanceof Num)) throw new DomainError("⎕COND='01' but got type "+human(v.type));
    Num n = (Num) v;
    switch (cond) {
      case "01":
        if (n.equals(Num.ZERO)) return false;
        if (n.equals(Num.ONE)) return true;
        throw new DomainError("⎕COND='01' expected 0 or 1, got "+n.toInt());
      case ">0":
        return n.compareTo(Num.ZERO)>0;
      case "≠0":
        return n.compareTo(Num.ZERO)!=0;
      default: throw new IllegalStateException("unknown ⎕COND "+cond);
    }
  }
}
