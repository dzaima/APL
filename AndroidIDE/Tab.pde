abstract static class Tab {
  abstract void show();
  abstract void hide();
  abstract String name();
  void mouseWheel(int dir) { }
}



static class REPL extends Tab {
  final ROText historyView;
  final APLField input;
  ArrayList<String> inputs = new ArrayList();
  String tmpSaved;
  int iptr = 0; // can be ==input.size()
  REPL() {
    historyView = new ROText(0, top, a.width, 340-top);
    input = new APLField(0, 350, a.width, 40) {
      boolean apl() {
        return !(line.startsWith(":") || line.startsWith(")"));
      }
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
          } else if (nm.equals("g")) {
            topbar.toNew(new Grapher(arg));
          } else if (nm.equals("tsz")) {
            top = int(arg);
            redrawAll();
          } else if (nm.equals("f") || nm.equals("fx")) {
            final boolean ex = nm.equals("fx");
            String[] ps = arg.split("/");
            String[] lns = a.loadStrings(arg);
            topbar.toNew(new Editor(ps[ps.length-1], lns==null? "" : join(lns, "\n")) {
              public void save(String t) {
                try {
                  a.saveStrings(arg, new String[]{t});
                  if (ex) Main.exec(ta.allText(), dzaimaSC);
                } catch (Throwable e) {
                  println(e.getMessage());
                  Main.lastError = e;
                }
              }
            });
          } else if (nm.equals("ex")) {
            String[] lns = a.loadStrings(arg);
            if (lns != null) {
              StringBuilder s = new StringBuilder();
              for (String c : lns) s.append(c).append("\n");
              for (String c : it.get(s.toString())) textln(c);
            } else textln("file "+arg+" not found");
          } else if (nm.equals("ed")) {
            Obj o = dzaimaSC.get(arg);
            if (o instanceof Dfn) {
              topbar.toNew(new Ed(nm, ((Dfn ) o).code.source()));
            }
            if (o instanceof Dmop) {
              topbar.toNew(new Ed(nm, ((Dmop) o).code.source()));
            }
            if (o instanceof Ddop) {
              topbar.toNew(new Ed(nm, ((Ddop) o).code.source()));
            }
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
          modified = true;
          
          if (line.length() != 0 && iptr == inputs.size()) {
            tmpSaved = line;
          }
          iptr--;
          if (iptr < 0) iptr = 0;
          line = inputs.get(iptr);
          sx = ex = line.length();
        } else if (s.equals("down")) {
          if (inputs.size() == 0) return;
          modified = true;
          
          iptr++;
          if (iptr >= inputs.size()) {
            iptr = inputs.size();
            line = tmpSaved == null? "" : tmpSaved;
          } else line = inputs.get(iptr);
          sx = ex = line.length();
        } else if (s.equals("copy")) {
          append("app.cpy");
        }
      }
      void textln(String ln) {
        historyView.append(ln);
      }
      void newline() {
        try {
          eval();
        } catch (Throwable t) {
          Main.lastError = t;
          println(t.getMessage());
        }
        clear();
      }
    };
  }
  void show() {
    int ih = int(isz*input.extraH);
    d.noStroke();
    d.fill(#101010);
    d.rectMode(CORNER);
    d.rect(0, top, d.width, freey()-top-ih);
    input.move(0, freey()-ih, d.width, ih);
    historyView.move(0, top, d.width, freey()-top-ih);
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



abstract static class Editor extends Tab {
  String name;
  APLTextarea ta;
  Editor(String name, String val) {
    this.name = name;
    ta = new APLTextarea(0, 0, 0, 0) {
      void eval() {
        save(ta.allText());
      }
      void extraSpecial(String s) {
        if (s.equals("close")) {
          eval();
          topbar.close();
        } else println("unknown special " + s);
      }
    };
    ta.append(val);
    ta.cx = ta.cy = 0;
  }
  abstract void save(String val);
  void show() {
    ta.move(0, top, d.width, freey()-top);
    ta.show();
    textInput = ta;
  }
  void hide() {
    ta.hide();
  }
  String name() {
    return name;
  }
}


static class Grapher extends Tab {
  Graph g;
  final APLField input;
  Obj last;
  Grapher(String def) {
    g = new Graph(0, top, d.width, freey()-top-isz);
    input = new APLField(0, 350, d.width, 40, def) {
      void eval() {
        modified();
      }
      void modified() {
        if (it instanceof DzaimaAPL) {
          last = ((DzaimaAPL) it).eval(line);
          if (last instanceof Fun) g.newFun((Fun) last);
        }
      }
      void extraSpecial(String s) {
        if (s.equals("close")) {
          eval();
          topbar.close();
        } else println("unknown special " + s);
      }
    };
  }
  
  void show() {
    int ih = int(isz*input.extraH);
    g.move(0, top, d.width, freey()-top-ih);
    g.show();
    input.move(0, freey()-ih, d.width, ih);
    input.show();
    textInput = input;
  }
  void hide() {
    g.hide();
    input.hide();
  }
  String name() {
    return "grapher";
  }
  void mouseWheel(int dir) {
    g.mouseWheel(dir);
  }
}
