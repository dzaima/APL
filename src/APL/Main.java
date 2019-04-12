package APL;

import APL.errors.*;
import APL.tokenizer.*;
import APL.tokenizer.types.*;
import APL.types.*;
import APL.types.arrs.*;
import APL.types.functions.VarArr;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

@SuppressWarnings("WeakerAccess") // for use as a library
public class Main {
  public static final String CODEPAGE = "\0\0\0\0\0\0\0\0\0\t\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~÷×↑↓⌈⌊≠∊⍺⍴⍵⍳∍⋾⎕⍞⌸⌺⍇⍁⍂⌻⌼⍃⍄⍈⍌⍍⍐⍓⍔⍗⍯⍰⍠⌹⊆⊇⍶⍸⍹⍘⍚⍛⍜⍊≤≥⍮ϼ⍷⍉⌽⊖⊙⌾○∘⍟⊗¨⍨⍡⍥⍩⍣⍢⍤⊂⊃∩∪⊥⊤∆∇⍒⍋⍫⍱⍲∨∧⍬⊣⊢⌷⍕⍎←→⍅⍆⍏⍖⌿⍀⍪≡≢⍦⍧⍭‽⍑∞…√";
  public static boolean debug = false;
  public static boolean vind = false; // vector indexing
  public static boolean noBoxing = false;
  public static boolean quotestrings = false;
  public static boolean enclosePrimitives = false;
  public static boolean colorful = true;
  static final ChrArr alphabet = toAPL("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
  static final ChrArr lowercaseAlphabet = toAPL("abcdefghijklmnopqrstuvwxyz");
  public static final ChrArr digits = toAPL("0123456789");
  static int printlvl = 0;
  public static final Error up = null;//new Error("A problem has been detected and APL has been shut down to prevent damage to your computer.");
  static final long startingMillis = System.currentTimeMillis();
  public static Scanner console;
  public static Tokenable faulty;
  private static Throwable lastError = null;
  public static void main(String[] args) {
    colorful = System.console() != null && System.getenv().get("TERM") != null;
    console = new Scanner(System.in);
    Scope global = new Scope();
    boolean REPL = false;
    boolean silentREPL = false;
    if (args.length > 0) {
      try {
        for (int i = 0; i < args.length; i++) {
          String p = args[i];
          if (p.length() >= 2 && p.charAt(0) == '-') {
            if (p.charAt(1) == '-') {
              switch (p) {
                case "--help":
                  println("Usage: APL [options]");
                  println("Options:");
                  println("-f file: execute the contents of the file");
                  println("-e code: execute the argument as APL");
                  println("-p code: execute the argument as APL and print its result");
                  println("-i     : execute STDIN as APL");
                  println("-r     : start the REPL after everything else");
                  println("-s     : start the REPL without \">\" after everything else");
                  println("-d     : enable debug mode");
                  println("-q     : enable quoting of strings");
                  println("-b     : disable boxing");
                  println("-c     : disable colorful printing");
                  println("-q     : define quads in the next argument");
                  println("-D file: run the file as SBCS");
                  println("-E a b : encode the file A in the SBCS, save as B");
                  println("If given no arguments, an implicit -r will be added");
                  System.exit(0);
                  break;
                default:
                  throw new DomainError("Unknown command-line argument " + p);
              }
            } else {
              for (char c : p.substring(1).toCharArray()) {
                switch (c) {
                  case 'f':
                    String name = args[++i];
                    exec(readFile(name), global);
                    break;
                  case 'e':
                    String code = args[++i];
                    exec(code, global);
                    break;
                  case 'p':
                    code = args[++i];
                    println(exec(code, global).toString());
                    break;
                  case 'i':
                    StringBuilder s = new StringBuilder();
                    while (console.hasNext()) {
                      s.append(console.nextLine()).append('\n');
                    }
                    exec(s.toString(), global);
                    break;
                  case 'r':
                    REPL = true;
                    break;
                  case 's':
                    REPL = true;
                    silentREPL^= true;
                    break;
                  case 'd':
                    debug = true;
                    break;
                  case 'q':
                    quotestrings = true;
                    break;
                  case 'b':
                    noBoxing = true;
                    break;
                  case 'c':
                    colorful = false;
                    break;
                  case 'E': {
                    String origS = readFile(args[++i]);
                    byte[] res = new byte[origS.length()];
                    for (int j = 0; j < origS.length(); j++) {
                      char chr = origS.charAt(j);
                      int index = CODEPAGE.indexOf(chr);
                      if (index == -1) throw new DomainError("error encoding character " + chr + " (" + (+chr) + ")");
                      res[j] = (byte) index;
                    }
                    String conv = args[++i];
                    try (FileOutputStream stream = new FileOutputStream(conv)) {
                      stream.write(res);
                    } catch (IOException e) {
                      e.printStackTrace();
                      throw new DomainError("couldn't write file");
                    }
                    break;
                  }
                  case 'D':
                    try {
                      byte[] bytes = Files.readAllBytes(new File(args[++i]).toPath());
                      StringBuilder res = new StringBuilder();
                      for (byte b : bytes) {
                        res.append(CODEPAGE.charAt(b & 0xff));
                      }
                      exec(res.toString(), global);
                    } catch (IOException e) {
                      e.printStackTrace();
                      throw new DomainError("couldn't read file");
                    }
                    break;
                  default:
                    throw new DomainError("Unknown command-line argument -" + c);
                }
              }
            }
          } else {
            throw new DomainError("Unknown command-line argument " + p);
          }
        }
      } catch (APLError e) {
        e.print();
        throw e;
      } catch (Throwable e) {
        e.printStackTrace();
        colorprint(e + ": " + e.getMessage(), 246);
        throw e;
      }
    }
    if (args.length == 0 || REPL) {
      if (!silentREPL) print("> ");
      while (console.hasNext()) {
        try {
          String cr = console.nextLine();
          if (cr.equals("exit")) break;
          if (cr.startsWith(")")) {
            if (")OFF".equals(cr) || ")EXIT".equals(cr) || ")STOP".equals(cr)) break /* REPL */;
            ucmd(global, cr.substring(1));
          } else {
            Obj r = exec(cr, global);
            if (r!=null) println(r.toString());
          }
        } catch (APLError e) {
          lastError = e;
          e.print();
        } catch (Throwable e) {
          lastError = e;
          colorprint(e + ": " + e.getMessage(), 246);
          if (faulty != null && faulty.getToken() != null) {
            String s = IntStream.range(0, faulty.getToken().pos).mapToObj(i -> " ").collect(Collectors.joining());
            colorprint(faulty.getToken().line, 217);
            colorprint(s + "^", 217);
          }
          e.printStackTrace();
        }
        if (!silentREPL) print("> ");
      }
    }
  }
  
  
  
  private static void ucmd(Scope global, String cr) {
    String[] parts = cr.split(" ");
    String t = parts[0].toUpperCase();
    String rest = parts.length==1? "" : cr.substring(t.length()+1);
    switch (t) {
      case "EX":
        exec(readFile(parts[1]), global);
        break;
      case "DEBUG":
        debug = !debug;
        break;
      case "QUOTE":
        quotestrings = !quotestrings;
        break;
      case "ONELINE":
        noBoxing = !noBoxing;
        break;
      case "TOKENIZE"    : println(Tokenizer.tokenize(rest).toTree("")); break;
      case "TOKENIZEREPR": println(Tokenizer.tokenize(rest).toRepr()); break;
      case "ERR"         : new NotErrorError("", exec(rest, global)).print(); break;
      case "CLASS"       : var r = exec(rest, global); println(r == null? "nothing" : r.getClass().getCanonicalName()); break;
      case "UOPT"        : var e = (Arr)global.get(rest); global.set(rest, new HArr(e.values(), e.shape)); break;
      case "ATYPE"       : println(exec(rest, global).humanType(false)); break;
      case "STACK":
        if (lastError != null) {
          lastError.printStackTrace();
        }
        break;
      default:
        throw new SyntaxError("Undefined user command");
    }
  }
  
  
  public static void print(String s) {
    System.out.print(s);
  }
  
  public static void println(String s) {
    System.out.println(s);
  }
  public static String formatAPL (int[] ia) {
    return Arrays.stream(ia).mapToObj(String::valueOf).collect(Collectors.joining(" "));
  }
  static String readFile(String path) {
    try {
      byte[] encoded = Files.readAllBytes(Paths.get(path));
      return new String(encoded, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new DomainError("File "+path+" not found");
    }
  }
  
  public static Obj exec(String s, Scope sc) {
    BasicLines t = Tokenizer.tokenize(s);
    printdbg(t);
    return execLines(t, sc);
  }
  
  static public void printdbg(Object... args) {
    if (!debug) return;
    if (args.length > 0) print(args[0] == null? "null" : args[0].toString());
    for (int i = 1; i < args.length; i++) {
      print(" ");
      print(args[i] == null? "null" : args[i].toString());
    }
    print("\n");
  }
  
  public static String explain(Token tok) {
    return tok.toRepr();
  }
  
  enum EType {
    all
  }
  public static Obj exec(LineTok s, Scope sc) {
    return new Exec(s, sc).exec();
  }
  
  
  
  public static Obj oexec(LineTok s, Scope sc) {
    Obj val = Main.exec(s, sc);
    if (val instanceof VarArr) val = ((VarArr) val).materialize();
    if (val instanceof Settable) val = ((Settable) val).get();
    return val;
  }
  
  public static Obj vexec(LineTok s, Scope sc) {
    Obj val = Main.exec(s, sc);
    if (val instanceof VarArr) val = ((VarArr) val).materialize();
    if (val instanceof Settable) val = ((Settable) val).get();
    if (val instanceof Value) return val;
    throw new SyntaxError("expected array, got " + val.humanType(true));
  }
  
  
  static public Obj execLines(TokArr<LineTok> lines, Scope sc) {
    Obj res = null;
    HashMap<EType, LineTok> eGuards = new HashMap<>();
    try {
      for (LineTok ln : lines.tokens) {
        List<Token> tokens = ln.tokens;
        int guardPos = ln.colonPos();
        int eguardPos = ln.eguardPos();
        if (guardPos != -1 && eguardPos != -1) throw new SyntaxError("both : and :: found in line");
        boolean endAfter = tokens.size() > 1 && tokens.get(0) instanceof SetTok;
        if (endAfter) tokens = tokens.subList(1, tokens.size());
        else if (guardPos != -1) {
          if (guardPos == tokens.size()-1) throw new SyntaxError("Guard without success expression");
          if (tokens.get(guardPos+1) instanceof SetTok) endAfter = true;
        } else if (eguardPos != -1) {
          if (eguardPos == tokens.size()-1) throw new SyntaxError("Error guard without success expression");
        }
        if (guardPos != -1) {
          var guard = LineTok.inherit(tokens.subList(0, guardPos));
          if (bool(norm(exec(guard, sc)), sc)) {
            var expr = LineTok.inherit(tokens.subList(guardPos+(endAfter? 2 : 1), tokens.size()));
            res = exec(expr, sc);
            if (endAfter) return res;
          }
        } else if (eguardPos != -1) {
          var guard = LineTok.inherit(tokens.subList(0, eguardPos));
          Value r = norm(exec(guard, sc));
          EType t;
          if (r.equals(Num.ZERO)) t = EType.all;
          else throw new DomainError("guard "+r+" not supported");
          var expr = LineTok.inherit(tokens.subList(eguardPos+(endAfter? 2 : 1), tokens.size()));
          eGuards.put(t, expr);
        } else {
          res = exec(endAfter? LineTok.inherit(tokens) : ln, sc);
          if (endAfter) return res;
        }
      }
      if (res instanceof Settable) return ((Settable) res).get();
    } catch (Throwable e) {
      for (Map.Entry<EType, LineTok> entry : eGuards.entrySet()) {
        EType t = entry.getKey();
        if (t == EType.all) return norm(exec(entry.getValue(), sc));
      }
      throw e;
    }
    return res;
  }
  
  public static void colorprint(String s, int col) {
    if (colorful) println("\u001b[38;5;" + col + "m" + s + "\u001b[0m");
    else println(s);
  }
  
  public static DoubleArr toAPL(int[] arr) {
    var da = new double[arr.length];
    for (int i = 0; i < arr.length; i++) {
      da[i] = arr[i];
    }
    return new DoubleArr(da);
  }
  
  public static DoubleArr toAPL(int[] arr, int[] sh) {
    var da = new double[arr.length];
    for (int i = 0; i < arr.length; i++) {
      da[i] = arr[i];
    }
    return new DoubleArr(da, sh);
  }
  
  private static Value norm(Obj o) {
    if (o instanceof VarArr) return ((VarArr) o).materialize();
    if (o instanceof Settable) return (Value) ((Settable) o).get();
    if (!(o instanceof Value)) throw new DomainError("Trying to use "+o.humanType(true)+" as array");
    return (Value) o;
  }
  
  public static ChrArr toAPL(String s) {
    return new ChrArr(s);
  }
  public static boolean bool(Obj v, Scope sc) {
    Scope.Cond c = sc.cond;
    if (sc.condSpaces) {
      if (v instanceof Char) {
        return ((Char) v).chr != ' ';
      }
    }
    if (!(v instanceof Num)) throw new DomainError("⎕COND does not accept "+v.humanType(false));
    Num n = (Num) v;
    switch (c) {
      case _01:
        if (n.equals(Num.ZERO)) return false;
        if (n.equals(Num.ONE)) return true;
        throw new DomainError("⎕COND='01' expected condition to be 0 or 1, got "+n.asInt());
      case gt0:
        return n.compareTo(Num.ZERO)>0;
      case ne0:
        return n.compareTo(Num.ZERO)!=0;
      default: throw new IllegalStateException("unknown ⎕COND "+c);
    }
  }
  public static boolean bool(double v, Scope sc) {
    switch (sc.cond) {
      case _01:
        if (v == 0) return false;
        if (v == 1) return true;
        throw new DomainError("⎕COND='01' expected condition to be 0 or 1, got "+v);
      case gt0:
        return v>0;
      case ne0:
        return v!=0;
      default: throw new IllegalStateException("unknown ⎕COND");
    }
  }
}
