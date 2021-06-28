abstract static class Tab extends SimpleMap {
  abstract void show();
  abstract void hide();
  abstract String name();
  void mouseWheel(int dir) { }
  void closed() { }
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
  final String name;
  final ROText historyView;
  final APLField input;
  ArrayList<String> inputs = new ArrayList();
  String tmpSaved;
  int iptr = 0; // can be ==input.size()
  boolean main;
  final Interpreter it;
  
  void addln(String s) {
    add(s+"\n");
  }
  void add(String s) {
    historyView.appendLns(s);
  }
  
  REPL(String name, final Interpreter it) {
    this.it = it;
    this.name = name;
    it.l = new ItListener() {
      public void println(String ln) {
        add(ln+"\n");
      }
      public void print(String ln) {
        add(ln);
      }
      public void off(int code) {
        topbar.close(REPL.this);
      }
      public String input() {
        return "";
      }
      public void inputMode(boolean enabled, boolean highlight) {
        if (!enabled) input.th = errTheme;
        else if (highlight) input.th = it.theme();
        else input.th = whiteTheme;
      }
    };
    historyView = new ROText(0, top, a.width, 340-top) {
      void tick() {
        it.tick();
        super.tick();
      }
    };
    input = new APLField(0, 350, a.width, 40) {
      boolean apl() {
        return !(line.startsWith(":") || line.startsWith(")") || line.startsWith("]"));
      }
      void eval() {
        tmpSaved = null;
        inputs.add(line);
        iptr = inputs.size();
        if (line.startsWith(":") || line.equals(")help") && it instanceof DzaimaAPL) {
          addln("  "+line);
          String cmd = line.substring(1);
          int i = cmd.indexOf(" "); 
          String nm = i==-1? cmd : cmd.substring(0, i);
          final String arg = i==-1? "" : cmd.substring(i+1);
          if (nm.equals("hsz")) historyView.setSize(int(arg));
          else if (nm.equals("isz")) {
            isz = int(arg);
            redrawAll();
          } else if (nm.equals("i")) {
            String[] parts = split(arg, ' ');
            String type = parts[0].toLowerCase();
            if (type.equals("ride")) {
              topbar.toNew(new REPL("RIDE", new RIDE(parts.length==1? RIDE_IP : parts[1], false)));
            } else if (type.equals("ridew")) {
              topbar.toNew(new REPL("RIDE", new RIDE(parts.length==1? RIDE_IP : parts[1], true)));
            } else if (type.equals("dzaima")) {
              topbar.toNew(new REPL("dzaima/APL", new DzaimaAPL()));
            } else if (type.equals("tryapl")) {
              topbar.toNew(new REPL("TryAPL", new TryAPL()));
            } else addln("unknown interpreter: \""+type+"\". See :h for options");
          } else if (nm.equals("clear")) {
            historyView.set(new ArrayList());
          } else if (nm.equals("g")) {
            if (it instanceof DzaimaAPL) topbar.toNew(new Grapher(it, arg));
            else addln(":g only supported in dzaima/APL");
          } else if (nm.equals("tsz")) {
            top = int(arg);
            redrawAll();
          //} else if (nm.equals("ex")) {
          //  String[] lns = a.loadStrings(arg);
          //  if (lns != null) {
          //    StringBuilder s = new StringBuilder();
          //    for (String c : lns) s.append(c).append("\n");
          //    for (String c : it.get(s.toString())) textln(c);
          //  } else textln("file "+arg+" not found");
          } else if (nm.equals("h") || nm.equals("help")) {
            if (arg.length()==0) {
              textln("commands:");
              textln(":h/:help  view this help page");
              textln(":h kb     view help for keyboard layout");
              if (it instanceof DzaimaAPL) {
                textln(":h c      view character docs");
                textln(":g expr   graph the expression (editable in the window)");
                textln(")ed fn    edit the function in another window (= - save, ⏎ - newline, X - save (!) & close)");
                textln(")ef path  edit file at path");
                textln(")efx path edit file at path, executing on save");
                textln(")fx path  execute file at path");
              }
              textln(":isz sz   change input box font size");
              textln(":hsz sz   change REPL history font size");
              textln(":tsz sz   change top bar size");
              textln(":clear    clear REPL history");
              textln(":i type   start another REPL. Types:");
              textln("    'dzaima' for another dzaima/APL REPL");
              textln("    'tryapl' for a TryAPL interface (requires internet)");
              textln("    'ride ip:port' (defaults: ip "+RIDE_IP+", port "+RIDE_PORT+")");
              textln("    'ridew ip:port' for RIDE started with RIDE_INIT=CONNECT:…");
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
        
        it.sendLn(line);
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
        } else if (s.equals("close")) {
          topbar.close();
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
    input.th = it.theme();
    it.lSet();
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
  String name() { return name; }
  Obj getv(String k) {
    if (k.equals("eq")) return Main.toAPL(input.line);
    return super.getv(k);
  }
  void setv(String k, Obj v) {
    if (k.equals("eq")) { input.clear(); input.append(((Value) v).asString()); }
    else super.setv(k, v);
  }
  void closed() {
    it.close();
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
          close();
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
  void close() {
    ta.eval();
    topbar.close();
  }
}

static class HelpEd extends Editor {
  HelpEd(String name, String val) {
    super(name, val);
    ta.th = noErrTheme;
  }
  void save(String val) {
  }
}

static class Grapher extends Tab {
  Graph g;
  final APLField input;
  Grapher(final Interpreter it, String def) {
    g = new Graph(0, top, d.width, freey()-top-isz);
    input = new APLField(0, 350, d.width, 40, def) {
      void eval() {
        modified();
      }
      void modified() {
        try {
          g.newFun(it.getFn(line));
        } catch (Throwable t) { /* too bad */ }
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
