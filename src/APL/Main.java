package APL;

import APL.types.*;
import APL.errors.*;

import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class Main {
  public static boolean debug = false;
  static int printlvl = 0;
  public static Error up = new Error("A problem has been detected and APL has been shut down to prevent damage to your computer.");
  static long startingMillis = System.currentTimeMillis();
  private static boolean escape = false;
  private static String buffer = "";
  
  public static void main(String[] args) {
    try {
      Scope global = new Scope();
      if (args.length > 0) {
        if (args[0].contains("d")) debug = true;
        if (args.length > 1) {
          int rest = args[0].contains("e") ? 2 : 1;
          for (int i = rest; i < args.length; i++) {
            String s = readFile(args[i]);
            if (s == null) colorprint("File " + s + " not found", 246);
            else exec(s, global);
          }
          if (args[0].contains("e")) {
            Obj r = exec(args[1], global);
            if (!r.shy) println(r.toString());
          }
        }
      }
      if (args.length >= 1 && args[0].contains("p")) escape = true;
      if (args.length == 0 || args[0].contains("r")) { // REPL
        Scanner console = new Scanner(System.in);
        
        while (true) {
          //        print("> ");
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
                default:
                  throw new SyntaxError("Undefined user command");
              }
            } else {
              Obj r = exec(cr, global);
              if (!r.shy) println(r.toString());
            }
          } catch (APLError e) {
            String[] ns = e.getClass().getName().split("[$.]");
            colorprint(ns[ns.length - 1] + ": " + e.getMessage(), 246);
          } catch (java.util.NoSuchElementException e) {
            break; // REPL ended
          }
          flush(false);
        }
      }
    } catch (Throwable e) {
      colorprint(e + ": " + e.getMessage(), 246);
    }
    flush(true);
  }
  
  private static void flush(boolean stop) {
    if (escape) {
      String res = "\"" + buffer
        .replace("\\", "\\\\")
        .replace("\n", "\\n")
        .replace("\"", "\"") + "\"";
      if (stop) res+= "e";
      System.out.println(res);
      buffer = "";
    }
    System.out.flush();
  }
  
  static void print(String s) {
    if (escape) buffer += s;
    else System.out.print(s);
  }
  
  public static void println(String s) {
    if (escape) print(s + "\n");
    else System.out.println(s);
  }
  
  private static String readFile(String path) {
    try {
      byte[] encoded = Files.readAllBytes(Paths.get(path));
      return new String(encoded, StandardCharsets.UTF_8);
    } catch (IOException e) {
      return null;
    }
  }
  
  private static Obj exec(String s, Scope sc) {
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
}
