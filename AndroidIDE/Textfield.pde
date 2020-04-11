static class APLField extends Drawable implements TextReciever {
  float tsz, chw;
  SyntaxHighlight hl;
  Theme th = new Theme();
  
  float extraH = 1.2;
  float xoff = 0;
  
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
    d.textSize(tsz);
    if (hl!=null) hl.g = d;
    chw = d.textWidth("H");
  }
  boolean saveUndo = true;
  boolean modified = false;
  final int hsz = 300;
  final State[] history = new State[hsz];
  int hptr = 0; // points to the current modification
  void tick() {
    if (!visible) return;
    if (a.mousePressed && !pmousePressed && smouseIn()) {
      textInput = this;
    }
    if (a.mousePressed && smouseIn()) {
      xoff+= a.mouseX-a.pmouseX;
    }
    float maxx = w*.8/chw > line.length()? 0 : (line.length() - 2)*chw;
    if (xoff < -maxx) xoff = (int) -maxx;
    if (xoff > 0) xoff = 0;
    
    if (modified || saveUndo) {
      modified();
      hl = new SyntaxHighlight(line, th, d);
      modified = false;
    }
    if (saveUndo) {
      hptr++;
      hptr%= hsz;
      history[hptr] = new State(allText(), sx, ex);
      saveUndo = false;
    }
    beginClip(d, x, y, w, h);
    if (pmousePressed && !a.mousePressed && smouseIn() && dist(a.mouseX, a.mouseY, smouseX, smouseY) < 10) {
      d.textSize(tsz);
      sx = constrain(round((a.mouseX-x-xoff)/d.textWidth("H")), 0, line.length());
      ex = sx;
      tt = 0;
    }
    d.fill(#101010);
    d.noStroke();
    d.rectMode(CORNER);
    d.rect(x, y, w, h);
    //text(line, x, y + dy*tsz + h*.1);
    if (apl()) hl.draw(x + xoff, y, tsz, sx); //SyntaxHighlight.apltext(line, x, y + dy*tsz + h*.1, tsz, new Theme(), g);
    else {
      d.fill(#D2D2D2);
      d.textAlign(LEFT, TOP);
      d.textSize(tsz);
      textS(d, line, x + xoff, y);
    }
    tt--;
    if (tt < 0) tt = 60;
    
    float spx = x + max(d.textWidth(line.substring(0, sx)), 3) + xoff;
    float epx = x + max(d.textWidth(line.substring(0, ex)), 3) + xoff;
    float sy = y + h*.1;
    float ey = y + h*.9;
    if (tt > 30 || this != textInput) {
      d.strokeWeight(tsz*.05);
      d.stroke(th.caret);
      d.line(epx, sy, epx, ey);
    }
    if (!one()) {
      d.fill(0x20ffffff);
      d.noStroke();
      d.rectMode(CORNERS);
      d.rect(spx, sy, epx, ey);
    }
    endClip(d);
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
    modified = true;
    saveUndo = true;
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
      if (sx!=ex && !a.cshift()) {
        sx = ex = Math.min(sx, ex);
      } else {
        ex = Math.max(0, ex-1);
        if (a.ctrl) {
          while (ex>0 && !Character.isAlphabetic(line.charAt(ex))) ex--;
          while (ex>0 &&  Character.isAlphabetic(line.charAt(ex))) ex--;
          if (ex<line.length() && !Character.isAlphabetic(line.charAt(ex))) ex++;
        }
        if (!a.cshift()) sx = ex;
      }
    }//asdas    asdas asd asd a   sdasda
    else if (s.equals("right")) {
      int len = line.length();
      if (sx!=ex && !cshift()) {
        sx = ex = Math.max(sx, ex);
      } else {
        if (ctrl) {
          while (ex<len && !Character.isAlphabetic(line.charAt(ex))) ex++;
          while (ex<len &&  Character.isAlphabetic(line.charAt(ex))) ex++;
        } else ex = Math.min(len, ex+1);
        if (!cshift()) sx = ex;
      }
    }
    else if (s.equals("home")) {
      ex = 0;
      if (!cshift()) sx = ex;
    }
    else if (s.equals("end")) {
      ex = line.length();
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
      if (sx==ex) extraSpecial(s);
      else {
        int min = min(sx, ex);
        int max = max(sx, ex);
        a.copy(line.substring(min, max));
      }
    }
    else if (s.equals("cut")) {
      if (sx != ex) {
        special("copy");
        ldelete();
      }
    }
    else if (s.equals("paste")) {
      a.paste(this);
    }
    else if (s.equals("match")) {
      int sel = hl.sel(sx);
      if (sel != -1) sx = ex = sel;
    } else if (s.equals("sall")) {
      sx = 0;
      ex = line.length();
    } else extraSpecial(s);
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
