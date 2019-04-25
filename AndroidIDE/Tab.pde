abstract class Tab {
  abstract void show();
  abstract void hide();
  abstract String name();
}


class REPL extends Tab {
  final ROText historyView;
  final APLField input;
  ArrayList<String> inputs = new ArrayList();
  String tmpSaved;
  int iptr = 0; // can be ==input.size()
  REPL() {
    historyView = new ROText(0, top, width, 340-top);
    input = new APLField(0, 350, width, 40) {
      Interpreter it = new DzaimaAPL();
      
      void eval() {
        tmpSaved = null;
        inputs.add(line);
        iptr = inputs.size();
        textln("  "+line+"\n");
        if (line.startsWith(":")) {
          String cmd = line.substring(1);
          int i = cmd.indexOf(" "); 
          String nm = i==-1? cmd : cmd.substring(0, i);
          final String arg = i==-1? "" : cmd.substring(i+1);
          String argl = arg.toLowerCase();
          if (nm.equals("hsz")) historyView.setSize(int(arg));
          else if (nm.equals("isz")) {
            isz = int(arg);
            redrawAll();
          } else if (nm.equals("i")) {
            if (argl.equals("dyalog")) {
              it = new Dyalog();
            }
            if (argl.equals("dzaima")) {
              it = new DzaimaAPL();
            }
          } else if (nm.equals("clear")) {
            historyView.set(new ArrayList());
          } else if (nm.equals("top")) {
            top = int(arg);
            redrawAll();
          } else if (nm.equals("f")) {
            String[] ps = arg.split("/");
            String[] lns = loadStrings(arg);
            topbar.toNew(new Editor(ps[ps.length-1], lns==null? "" : join(lns, "\n")) {
              public void save(String t) {
                saveStrings(arg, new String[]{t});
              }
            });
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
      void extraSpecial(String s) {
        if (s.equals("up")) {
          if (inputs.size() == 0) return;
          if (line.length() != 0 && iptr == inputs.size()) {
            tmpSaved = line;
          }
          iptr--;
          if (iptr < 0) iptr = 0;
          line = inputs.get(iptr);
          sx = ex = line.length();
        } else {
          if (inputs.size() == 0) return;
          iptr++;
          if (iptr >= inputs.size()) {
            iptr = inputs.size();
            line = tmpSaved == null? "" : tmpSaved;
          } else line = inputs.get(iptr);
          sx = ex = line.length();
        }
      }
      void textln(String ln) {
        historyView.append(ln);
      }
      void newline() {
        eval();
        clear();
      }
    };
  }
  void show() {
    int ih = int(isz*input.extraH);
    input.move(0, freey-ih, width, ih);
    historyView.move(0, top, width, freey-top-ih);
    historyView.end();
    input.show();
    historyView.show();
    textInput = input;
  }
  void hide() {
    input.hide();
    historyView.hide();
    if (textInput == input) textInput = null;
  }
  String name() {
    return "REPL";
  }
}



abstract class Editor extends Tab {
  String name;
  APLTextarea a;
  Editor(String name, String val) {
    this.name = name;
    a = new APLTextarea(0, 0, 0, 0) {
      void eval() {
        save(a.allText());
      }
      void extraSpecial(String s) {
        if (s.equals("close")) {
          eval();
          topbar.close();
        } else println("unknown special " + s);
      }
    };
    a.append(val);
  }
  abstract void save(String val);
  void show() {
    a.move(0, top, width, freey-top);
    a.show();
    textInput = a;
  }
  void hide() {
    a.hide();
  }
  String name() {
    return name;
  }
}
