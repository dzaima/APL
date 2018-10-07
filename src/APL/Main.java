package APL;

import APL.types.*;
import APL.errors.*;

import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.stream.Collectors;

public class Main {
  public static boolean debug = false;
  public static boolean prettyprint = false;
  public static boolean quotestrings = false;
  static final Arr alphabet = toAPL("ABCDEFGHIJKLMNOPQRSTUVWXYZ", null);
  static final Arr lowercaseAlphabet = toAPL("abcdefghijklmnopqrstuvwxyz", null);
  static int printlvl = 0;
  public static final Error up = null;//new Error("A problem has been detected and APL has been shut down to prevent damage to your computer.");
  static final long startingMillis = System.currentTimeMillis();
  public static Scanner console;
  public static void main(String[] args) {
    console = new Scanner(System.in);
    Scope global = new Scope();
    Throwable lastError = null;
    if (args.length > 0) {
      debug = args[0].contains("d");
      prettyprint = args[0].contains("p");
      quotestrings = args[0].contains("q");
      if (args.length > 1) {
        int rest = args[0].contains("e") ? 2 : 1;
        for (int i = rest; i < args.length; i++) {
          String s = readFile(args[i]);
          if (s == null) colorprint("File " + args[i] + " not found", 246);
          else try {
          exec(s, global);
          } catch (APLError e) {
            e.print();
          }
        }
        if (args[0].contains("e")) {
          try {
            Obj r = exec(args[1], global);
            println(r.toString());
          } catch (APLError e) {
            e.print();
          }
        }
      }
    }
    if (args.length == 0 || args[0].contains("r")) { // REPL
      REPL: while (true) {
        print("> ");
        try {
          String cr = console.nextLine();
          if (cr.equals("exit")) break;
          if (cr.startsWith(")")) {
            String[] parts = cr.split(" ");
            String t = parts[0].toUpperCase();
            String rest = cr.substring(t.length());
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
              case ")TOKENIZE": println(Tokenizer.tokenize(rest).toTree("")); break;
              case ")TOKENIZEREPR": println(Tokenizer.tokenize(rest).toRepr()); break;
              case ")TYPE": println(Main.human(exec(rest, global).type())); break;
              case ")ERR": new NotErrorError("", exec(rest, global)).print(); break;
              case ")CLASS": println(exec(rest, global).getClass().getCanonicalName()); break;
              case ")ATYPE": println( ((Value) exec(rest, global)).humanType(false) ); break;
              case ")STACK":
                if (lastError != null) {
                  lastError.printStackTrace();
                }
                break;
              default:
                throw new SyntaxError("Undefined user command");
            }
          } else {
            Obj r = exec(cr, global);
            println(r.toString());
          }
        } catch (APLError e) {
          e.print();
          lastError = e;
        } catch (java.util.NoSuchElementException e) {
          break; // EOF; REPL ended
        } catch (Throwable e) {
          e.printStackTrace();
          colorprint(e + ": " + e.getMessage(), 246);
        }
      }
    }
  }
  
  
  static void print(String s) {
    System.out.print(s);
  }
  
  public static void println(String s) {
    System.out.println(s);
  }
  public static String human(Type t) {
    switch (t) {
      case array: return "array";
      case var: return "variable";
      case  fn: case  bfn: return "function";
      case mop: case bmop: return "monadic operator";
      case dop: case bdop: return "dyadic operator";
      default: throw new IllegalStateException();
    }
  }
  public static String formatAPL (int[] ia) {
    return Arrays.stream(ia).mapToObj(String::valueOf).collect(Collectors.joining(" "));
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
  
  static public Obj execLines(Token lines, Scope sc) {
    assert (lines.type == TType.lines || lines.type == TType.usr);
    Obj res = null;
    for (Token ln : lines.tokens) {
      List<Token> tokens = ln.tokens;
      int guardPos = -1;
      boolean endAfter = tokens.size() > 0 && tokens.get(0).type == TType.set;
      if (endAfter) tokens = tokens.subList(1, tokens.size());
      else for (int i = 0; i < tokens.size(); i++) {
        if (tokens.get(i).type == TType.guard ) {
          guardPos = i;
          if (i == tokens.size()-1) throw new SyntaxError("Guard without success expression");
          if (tokens.get(i+1).type == TType.set) endAfter = true;
          break;
        }
      }
      if (guardPos >= 0) {
        var guard = new Token(ln.type, tokens.subList(0, guardPos));
        if (bool((Value) execTok(guard, sc), sc)) {
          var expr = new Token(ln.type, tokens.subList(guardPos+(endAfter? 2 : 1), tokens.size()));
          res = execTok(expr, sc);
          if (endAfter) return res;
        }
      } else {
        res = execTok(endAfter? new Token(ln.type, tokens) : ln, sc);
        if (endAfter) return res;
      }
    }
    return res;
  }
  
  static Obj execTok(Token ln, Scope sc) {
    return new Exec(ln, sc).exec();
  }
  
  public static void colorprint(String s, int col) {
    println("\u001b[38;5;" + col + "m" + s + "\u001b[0m");
    //println("\u001b["+col+"m"+s+"\u001b[0m");
  }
  
  public static Value toAPL(int[] arr) {
    var va = new Value[arr.length];
    for (int i = 0; i < arr.length; i++) {
      va[i] = new Num(arr[i]);
    }
    return new Arr(va);
  }
  
  public static Value toAPL(int[] arr, int[] sh) {
    var va = new Value[arr.length];
    for (int i = 0; i < arr.length; i++) {
      va[i] = new Num(arr[i]);
    }
    return new Arr(va, sh);
  }
  
  public static Arr toAPL(String s, Token t) {
    var vs = new ArrayList<Value>();
    for (char c : s.toCharArray()) {
      Char chr = new Char(c);
      chr.token = t;
      vs.add(chr);
    }
    Arr a = new Arr(vs);
    a.token = t;
    a.prototype = Char.SPACE;
    return a;
  }
//  public static int compare(Value a, Value w) {
//    if (a instanceof Num && w instanceof Num) return ((Num)a).compareTo((Num)w);
//    throw new DomainError("comparing non-numbers"); // TODO not do that
//  }
//  public static Num compareObj(Value a, Value w) {
//    int c = compare(a, w);
//    if (c > 0) return Num.ONE;
//    if (c < 0) return Num.MINUS_ONE;
//    return Num.ZERO;
//  }
  public static boolean bool(Value v, Scope sc) {
    String cond = ((Arr)sc.get("⎕COND")).string(false);
    assert cond != null;
    if (cond.endsWith(" ")) {
      if (v instanceof Char) {
        return ((Char) v).chr != ' ';
      }
      cond = cond.substring(0, cond.length()-2);
    }
    if (!(v instanceof Num)) throw new DomainError("⎕COND='01' but got type "+human(v.type()));
    Num n = (Num) v;
    switch (cond) {
      case "01":
        if (n.equals(Num.ZERO)) return false;
        if (n.equals(Num.ONE)) return true;
        throw new DomainError("⎕COND='01' expected condition to be 0 or 1, got "+n.toInt(null));
      case ">0":
        return n.compareTo(Num.ZERO)>0;
      case "≠0":
        return n.compareTo(Num.ZERO)!=0;
      default: throw new IllegalStateException("unknown ⎕COND "+cond);
    }
  }
}
