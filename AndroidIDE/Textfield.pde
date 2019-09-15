class APLField extends Drawable implements TextReciever {
  float tsz;
  SyntaxHighlight hl;
  Theme th = new Theme();
  
  float extraH = 1.2;
  
  APLField(int x, int y, int w, int h) {
    this(x, y, w, h, "");
  }
  APLField(int x, int y, int w, int h, String text) {
    super(x, y, w, h);
    line = text;
  }
  int tt = 0; // caret flicker timer
  
  void modified() { } // for overriding
  
  void redraw() {
    tsz = h/extraH;
  }
  boolean saveUndo = true;
  boolean modified = false;
  final int hsz = 300;
  final State[] history = new State[hsz];
  int hptr = 0; // points to the current modification
  void tick() {
    if (!visible) return;
    if (mousePressed && !pmousePressed && smouseIn()) {
      textInput = this;
    }
    if (modified || saveUndo) {
      modified();
      hl = new SyntaxHighlight(line, th, g);
      modified = false;
    }
    if (saveUndo) {
      hptr++;
      hptr%= hsz;
      history[hptr] = new State(allText(), sx, ex);
      saveUndo = false;
    }
    clip(x, y, w, h);
    if (pmousePressed && !mousePressed && smouseIn() && dist(mouseX, mouseY, smouseX, smouseY) < 10) {
      sx = constrain(round((mouseX-x)/textWidth("H")), 0, line.length());
      ex = sx;
      tt = 0;
    }
    fill(#101010);
    noStroke();
    rectMode(CORNER);
    rect(x, y, w, h);
    //text(line, x, y + dy*tsz + h*.1);
    if (apl()) hl.draw(x, y, tsz, sx); //SyntaxHighlight.apltext(line, x, y + dy*tsz + h*.1, tsz, new Theme(), g);
    else {
      fill(#D2D2D2);
      g.textAlign(LEFT, TOP);
      textSize(tsz);
      text(line, x, y);
    }
    tt--;
    if (tt < 0) tt = 60;
    
    float spx = x + max(textWidth(line.substring(0, sx)), 3);
    float epx = x + max(textWidth(line.substring(0, ex)), 3);
    float sy = y + h*.1;
    float ey = y + h*.9;
    if (tt > 30 || this != textInput) {
      strokeWeight(tsz*.05);
      stroke(th.caret);
      line(epx, sy, epx, ey);
    }
    if (!one()) {
      fill(0x20ffffff);
      noStroke();
      rectMode(CORNERS);
      rect(spx, sy, epx, ey);
    }
    noClip();
  }
  
  String line;
  int sx;
  int ex;
  boolean one() {
    return sx == ex;
  }
  boolean apl() {
    return true;
  }
  String allText() {
    return line;
  }
  void clear() {
    if (!allText().equals("")) saveUndo = true;
    line = "";
    sx = 0;
    ex = 0;
  }
  void deleteSel() {
    int min = min(sx, ex);
    int max = max(sx, ex);
    if (!one()) line = line.substring(0, min) + line.substring(max);
    ex = min;
    sx = min;
  }
  void append(String str) {
    deleteSel();
    if (!str.equals("")) saveUndo = true;
    tt = 0;
    for (char c : sit(str)) {
      if (c == '\n') newline();
      else {
        line = line.substring(0, sx) + c + line.substring(sx);
        sx++;
        ex++;
      }
    }
  }
  void newline() { }
  void eval() { }
  void ldelete() {
    tt = 0;
    if (!one()) {
      deleteSel();
      return;
    }
    if (sx != 0) saveUndo = true;
    if (sx == 0) {
    } else {
      line = line.substring(0, sx-1) + line.substring(sx);
      sx--;
      ex--;
    }
  }
  void special(String s) {
    tt = 0;
    if (s.equals("eval")) {
      eval();
    } else if (s.equals("left")) {
      ex--;
      if (ex == -1) ex = 0;
      if (!cshift()) sx = ex; 
    }
    else if (s.equals("right")) {
      ex++;
      if (ex == line.length()+1) {
        ex--;
      }
      if (!cshift()) sx = ex;
    }
    else if (s.equals("openPar")) {
      append("()");
      ex--;
      sx--;
    }
    else if (s.equals("wrapPar")) {
      append(")");
      String all = allText();
      int cl = 1;
      int ocp = sx;
      int cp = Math.max(0, Math.min(ocp-2, all.length()-1))+1;
      while (cp > 0) {
        cp--;
        if (all.charAt(cp) == '\n') break;
        if ("([{".contains(Character.toString(all.charAt(cp)))) { cl--; if (cl==0){ cp++; break; } }
        if (")]}".contains(Character.toString(all.charAt(cp))))   cl++;
      }
      //println(cp);
      ex = sx = Math.max(0, cp);
      append("(");
      ex = sx = ocp + 1;
      //println(allText());
      //println(cx, cy);
      //cx--;
    } 
    else if (s.equals("undo")) {
      hptr+= hsz-1;
      hptr%= hsz;
      to(history[hptr]);
      modified = true;
    }
    else if (s.equals("redo")) {
      hptr++;
      hptr%= hsz;
      to(history[hptr]);
      modified = true;
    }
    else if (s.equals("copy")) {
      int min = min(sx, ex);
      int max = max(sx, ex);
      copy(line.substring(min, max));
    }
    else if (s.equals("paste")) {
      paste(this);
    }
    else extraSpecial(s);
  }
  void pasted(String s) {
    append(s);
  }
  void extraSpecial(String s) {
    println("unknown special " + s);
  }
  void to(State st) {
    if (st != null) {
      line = st.code;
      sx = st.sx;
      ex = st.ex;
    } else clear();
  }
  class State {
    String code;
    int sx, ex;
    State(String code, int sx, int ex) {
      this.sx = sx;
      this.ex = ex;
      this.code = code;
    }
  }
  void rdelete() {
    if (!one()) {
      deleteSel();
      return;
    }
    if (sx != line.length()) {
      line = line.substring(0, sx) + line.substring(sx+1);
      saveUndo = true;
    }
  }
}
