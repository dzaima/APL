package APL;

import APL.errors.*;
import APL.tokenizer.*;
import APL.tokenizer.types.*;
import APL.types.*;
import APL.types.arrs.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

@SuppressWarnings("WeakerAccess") // for use as a library
public class Main {
  public static final String CODEPAGE = "\0\0\0\0\0\0\0\0\0\t\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~÷×↑↓⌈⌊≠∊⍺⍴⍵⍳∍⋾⎕⍞⌸⌺⍇⍁⍂⌻⌼⍃⍄⍈⍌⍍⍐⍓⍔⍗⍯⍰⍠⌹⊆⊇⍶⍸⍹⍘⍚⍛⍜⍊≤≥⍮ϼ⍷⍉⌽⊖⊙⌾○∘⍟⊗¨⍨⍡⍥⍩⍣⍢⍤⊂⊃∩∪⊥⊤∆∇⍒⍋⍫⍱⍲∨∧⍬⊣⊢⌷⍕⍎←→⍅⍆⍏⍖⌿⍀⍪≡≢⍦⍧⍭‽⍑∞…√ᑈᐵ¯⍝⋄⌶⍙";
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
  static final long startingMillis = System.currentTimeMillis();
  public static Scanner console;
  public static Tokenable faulty;
  public static APLError lastError = null;
  public static void main(String[] args) {
    colorful = System.console() != null && System.getenv().get("TERM") != null;
    console = new Scanner(System.in);
    Sys sys = StdSys.inst;
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
                  System.out.println("Usage: APL [options]");
                  System.out.println("Options:");
                  System.out.println("-f file: execute the contents of the file");
                  System.out.println("-e code: execute the argument as APL");
                  System.out.println("-p code: execute the argument as APL and print its result");
                  System.out.println("-i     : execute STDIN as APL");
                  System.out.println("-r     : start the REPL after everything else");
                  System.out.println("-s     : start the REPL without \">\" after everything else");
                  System.out.println("-d     : enable debug mode");
                  System.out.println("-q     : quote strings in output");
                  System.out.println("-b     : disable boxing");
                  System.out.println("-c     : disable colorful printing");
                  System.out.println("-q     : enable quoting strings");
                  System.out.println("⎕A←B   : set quad A to B");
                  System.out.println("-D file: run the file as SBCS");
                  System.out.println("-E a b : encode the file A in the SBCS, save as B");
                  System.out.println("If given no arguments, an implicit -r will be added");
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
                    sys.execFile(Paths.get(name), sys.gsc);
                    break;
                  case '⎕':
                    throw new DomainError("⎕ settings must be a separate argument");
                  case 'e':
                    String code = args[++i];
                    exec(code, sys.gsc);
                    break;
                  case 'p':
                    code = args[++i];
                    sys.println(exec(code, sys.gsc).toString());
                    break;
                  case 'i':
                    StringBuilder s = new StringBuilder();
                    while (console.hasNext()) {
                      s.append(console.nextLine()).append('\n');
                    }
                    exec(s.toString(), sys.gsc);
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
                      exec(res.toString(), sys.gsc);
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
          } else if (p.charAt(0)=='⎕') {
            int si = p.indexOf('←');
            if (si == -1) throw new DomainError("argument `"+p+"` didn't contain a `←`");
            String qk = p.substring(0, si);
            String qv = p.substring(si+1);
            sys.gsc.set(qk, exec(qv, sys.gsc));
          } else {
            throw new DomainError("Unknown command-line argument " + p);
          }
        }
      } catch (APLError e) {
        e.print(sys);
        throw e;
      } catch (Throwable e) {
        e.printStackTrace();
        sys.colorprint(e + ": " + e.getMessage(), 246);
        throw e;
      }
    }
    if (args.length == 0 || REPL) {
      if (!silentREPL) sys.print("> ");
      while (console.hasNext()) {
        faulty = null;
        String cr = console.nextLine();
        sys.lineCatch(cr);
        if (!silentREPL) sys.print("> ");
      }
    }
  }
  
  
  public static class StdSys extends Sys {
    public static final StdSys inst = new StdSys();
    public void println(String s) {
      System.out.println(s);
    }
    public void print(String s) {
      System.out.print(s);
    }
  
    public void colorprint(String s, int col) {
      if (colorful) println("\u001b[38;5;" + col + "m" + s + "\u001b[0m");
      else println(s);
    }
  
    public void off(int code) {
      System.exit(code);
    }
  
    public String input() {
      return console.nextLine();
    }
  }
  
  
  
  
  
  public static String formatAPL(int[] ia) {
    if (ia.length == 0) return "⍬";
    StringBuilder r = new StringBuilder(Num.formatInt(ia[0]));
    for (int i = 1; i < ia.length; i++) {
      r.append(" ");
      r.append(Num.formatInt(ia[i]));
    }
    return r.toString();
  }
  static String readFile(String path) {
    return readFile(Paths.get(path));
  }
  static String readFile(Path path) {
    try {
      byte[] encoded = Files.readAllBytes(path);
      return new String(encoded, StandardCharsets.UTF_8);
    } catch (IOException e) {
      String msg = "File " + path + " not found";
      if (path.startsWith("'") && path.endsWith("'")  ||  path.startsWith("\"") && path.endsWith("\"")) {
        msg+= " (argument shouldn't be surrounded in quotes)";
      }
      DomainError ne = new DomainError(msg);
      ne.initCause(e);
      throw ne;
    }
  }
  
  public static Obj rexec(LineTok s, Scope sc) {
    return new Exec(s, sc).exec();
  }
  public static Obj exec(String s, Scope sc) {
    BasicLines t = Tokenizer.tokenize(s);
    printdbg(sc, t);
    return execLines(t, sc);
  }
  public static Obj exec(LineTok s, Scope sc) {
    Obj val = new Exec(s, sc).exec();
    if (val instanceof Settable) val = ((Settable) val).get();
    return val;
  }
  public static Value vexec(String s, Scope sc) {
    Obj val = Main.exec(s, sc);
    if (val instanceof Value) return (Value) val;
    throw new SyntaxError("expected array, got " + val.humanType(true));
  }
  public static Value vexec(LineTok s, Scope sc) {
    Obj val = Main.exec(s, sc);
    if (val instanceof Value) return (Value) val;
    throw new SyntaxError("expected array, got " + val.humanType(true), s);
  }
  
  
  public static void printdbg(Scope sc, Object... args) {
    if (!debug) return;
    StringBuilder r = new StringBuilder();
    if (args.length > 0) r.append(args[0]);
    for (int i = 1; i < args.length; i++) r.append(" ").append(args[i]);
    sc.sys.println(r.toString());
  }
  
  public static boolean isBool(Value a) {
    if (!(a instanceof Num)) return false;
    Num n = (Num) a;
    return n.num==0 || n.num==1;
  }
  
  enum EType {
    all
  }
  
  
  public static Obj execLines(TokArr<LineTok> lines, Scope sc) {
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
          if (bool(vexec(guard, sc))) {
            var expr = LineTok.inherit(tokens.subList(guardPos+(endAfter? 2 : 1), tokens.size()));
            res = exec(expr, sc);
            if (endAfter) return res;
          }
        } else if (eguardPos != -1) {
          var guard = LineTok.inherit(tokens.subList(0, eguardPos));
          Value r = vexec(guard, sc);
          EType t;
          if (r.equals(Num.ZERO)) t = EType.all;
          else throw new DomainError("guard "+r+" not supported", guard);
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
        if (t == EType.all) return vexec(entry.getValue(), sc);
      }
      throw e;
    }
    return res;
  }
  public static boolean bool(double d) {
    if (d == 1) return true;
    if (d == 0) return false;
    throw new DomainError("Expected boolean, got "+d);
  }
  
  public static boolean bool(Obj v) {
    if (v instanceof Num) {
      double num = ((Num) v).num;
      if (num == 1) return true;
      if (num == 0) return false;
    }
    throw new DomainError("Expected boolean, got "+v, v);
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
  
  public static ChrArr toAPL(String s) {
    return new ChrArr(s);
  }
  
  
  public static String repeat(String s, int l) {
    StringBuilder r = new StringBuilder();
    for (int i = 0; i < l; i++) r.append(s);
    return r.toString();
  }
}