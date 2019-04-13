abstract class Tab {
  abstract void show();
  abstract void hide();
  abstract String name();
}
class REPL extends Tab {
  final ROText REPLH;
  final APLField REPLField;
  REPL() {
    REPLH = new ROText(0, top, width, 340-top);
    REPLField = new APLField(0, 350, width, 40) {
      Interpreter it = new Dyalog();
      
      void eval() {
        textln("  "+line+"\n");
        if (line.startsWith(":")) {
          String cmd = line.substring(1);
          int i = cmd.indexOf(" "); 
          String nm = i==-1? cmd : cmd.substring(0, i);
          String arg = i==-1? "" : cmd.substring(i+1);
          String argl = arg.toLowerCase();
          if (nm.equals("sz")) REPLH.setSize(int(arg));
          else if (nm.equals("i")) {
            if (argl.equals("dyalog")) {
              it = new Dyalog();
            }
            if (argl.equals("dzaima")) {
              it = new DzaimaAPL();
            }
          } else if (nm.equals("clear")) {
            REPLH.set(new ArrayList());
          } else if (nm.equals("top")) {
            top = int(arg);
            redrawAll();
          } else textln("Command "+nm+" not found");
          //else if (nm.equals(""))
          return;
        }
        
        if (line.startsWith(")")) {
          for (String s : it.special(line.substring(1))) textln(s);
          return;
        }
        String[] res = it.get(line);
        for (String ln : res) {
          textln(ln);
        }
      }
      void textln(String ln) {
        REPLH.append(ln);
      }
      void newline() {
        eval();
        clear();
      }
    };
  }
  void show() {
    REPLField.move(0, freey-REPLField.h);
    REPLH.move(0, top, width, freey-top-REPLField.h);
    REPLField.show();
    REPLH.show();
    textInput = REPLField;
    println("+"+this);
  }
  void hide() {
    println("-"+this);
    REPLField.hide();
    REPLH.hide();
    if (textInput == REPLField) textInput = null;
  }
  String name() {
    return "REPL";
  }
}
