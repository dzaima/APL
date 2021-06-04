abstract static class Tab extends SimpleMap {
  abstract void show();
  abstract void hide();
  abstract String name();
  void mouseWheel(int dir) { }
  Obj getv(String k) {
    switch (k) {
      case "name": return Main.toAPL(name());
      case "close": return new Fun() {
        public String repr() { return Tab.this+".close"; }
        public Value call(Value w) {
          topbar.close(Tab.this);
          return Num.ONE;
        }
      };
      default: return Null.NULL;
    }
  }
  void setv(String k, Obj v) {
    String s = k.toLowerCase();
    switch (k) {
      default: throw new DomainError("setting non-existing key "+s+" for tab");
    }
  }
  String toString() { return "tab["+name()+"]"; }
  boolean equals(Object o) { return this == o; }
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
        if (line.startsWith(":") || line.equals(")help")) {
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
          } else if (nm.equals("h") || nm.equals("help")) {
            if (arg.length()==0) {
              textln("commands:");
              textln(":h/:help  view this help page");
              textln(":h kb     view help for keyboard layout");
              textln(":h c      view character docs");
              textln(":isz sz   change input box font size");
              textln(":hsz sz   change REPL history font size");
              textln(":tsz sz   change top bar size");
              textln(":g expr   graph the expression (editable in the window)");
              textln(":clear    clear REPL history");
              textln(":f  path  edit file at the path");
              textln(":fx path  edit file at the path, executing on save");
              textln(":ex path  execute file at the path");
              textln(":ed fn    edit the function by name in another window (= - save, ⏎ - newline, X - save (!) & close)");
            } else {
              if (arg.equals("kb")) {
                topbar.toNew(new HelpEd(":help kb", join(a.loadStrings("help_kb.txt"), '\n')));
              } else if (arg.equals("c")) {
                topbar.toNew(new HelpEd(":help c", join(a.loadStrings("help_c.txt"), '\n')));
              } else textln("unknown help page \""+arg+"\"");
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
  Obj getv(String k) {
    if (k.equals("eq")) return Main.toAPL(input.line);
    return super.getv(k);
  }
  void setv(String k, Obj v) {
    if (k.equals("eq")) { input.clear(); input.append(((Value) v).asString()); }
    else super.setv(k, v);
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

static class HelpEd extends Editor {
  HelpEd(String name, String val) {
    super(name, val);
    ta.th = new NoErrTheme();
  }
  void save(String val) {
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
  Obj getv(String k) {
    if (k.equals("eq")) return Main.toAPL(input.line);
    if (k.equals("am")) return new Num(g.pts);
    if (k.equals("ln")) return new Num(g.joined? 1 : 0);
    if (k.equals("sz")) return new Num(g.ph);
    if (k.equals("x" )) return new Num(g.fullX + (g.x + g.w/2)/g.fullS);
    if (k.equals("y" )) return new Num(g.fullY + (g.y + g.h/2)/g.fullS);
    if (k.equals("w" )) return new Num(g.w/g.fullS);
    if (k.equals("freq")) return new Num(g.freq);
    if (k.equals("batch")) return new Num(g.mul);
    if (k.equals("gd")) return new DoubleArr(new double[]{g.pts, g.joined? 1 : 0, g.ph, g.mul});
    return super.getv(k);
  }
  void setv(String k, Obj v) {
    if (k.equals("eq")) { input.clear(); input.append(((Value) v).asString()); }
    else if (k.equals("am"   )) g.pts = ((Value)v).asInt();
    else if (k.equals("ln"   )) g.joined = Main.bool(v);
    else if (k.equals("sz"   )) g.ph = (float)((Value)v).asDouble();
    else if (k.equals("batch")) g.mul = ((Value)v).asInt();
    else if (k.equals("x")) g.fullX = ((Value) v).asDouble() - (g.x + g.w/2)/g.fullS;
    else if (k.equals("y")) g.fullY = ((Value) v).asDouble() - (g.y + g.h/2)/g.fullS;
    else if (k.equals("w")) {
      double wnt = g.w / (float) ((Value) v).asDouble();
      double sc = wnt / g.fullS;
      double pS = g.fullS;
      g.fullS*= sc;
      double scalechange = 1/g.fullS - 1/pS;
      g.fullX-= ((g.x+g.w/2) * scalechange);
      g.fullY-= ((g.y+g.h/2) * scalechange);
    }
    else if (k.equals("freq")) g.freq = ((Value) v).asInt();
    else if (k.equals("gd")) { Value a = (Value) v; setv("am", a.get(0)); setv("ln", a.get(1)); setv("sz", a.get(2)); setv("batch", a.get(3)); }
    else super.setv(k, v);
  }
}
