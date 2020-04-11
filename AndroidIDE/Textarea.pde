static class APLTextarea extends Drawable implements TextReciever {
  float tsz, chw; // text size, char width
  void setsz(float sz) {
    tsz = sz;
    d.textSize(sz);
    chw = d.textWidth('H');
  }
  SyntaxHighlight hl;
  Theme th = new Theme();

  APLTextarea(int x, int y, int w, int h) {
    super(x, y, w, h);
    lines = new ArrayList();
    lines.add("");
    setsz(max(a.width, a.height)/40);
  }
  int tt = 0; // caret flicker timer
  int xoff = 0; // scroll
  int yoff = 0;

  void redraw() {
    if (hl!=null) hl.g = d;
  }
  boolean saveUndo = true;
  boolean modified = false;
  boolean cursorMoved = false;
  final int hsz = 300;
  final State[] history = new State[hsz];
  int hptr = 0; // points to the current modification
  void tick() {
    if (!visible) return;
    if (a.mousePressed && !pmousePressed && smouseIn()) {
      textInput = this;
    }
    if (cy < 0 || cy > lines.size() || cx < 0 || cx > lines.get(cy).length()) {
      lines.add("CX was "+cx+"; CY was "+cy);
      cx = 0;
      cy = 0;
    }
    if (cursorMoved || modified || saveUndo) {
      xoff = (int) min(0, constrain(xoff, -(cx-2)*chw, w - (cx+2)*chw));
      yoff = (int) min(0, constrain(yoff, -(cy  )*tsz, h - (cy+1)*tsz));
      cursorMoved = false;
    }
    if (modified || saveUndo) {
      StringBuilder all = new StringBuilder();
      for (String s : lines) all.append(s).append("\n");
      hl = new SyntaxHighlight(all.toString(), th, d);
      modified = false;
    }
    if (saveUndo) {
      hptr++;
      hptr%= hsz;
      history[hptr] = new State(allText(), cx, cy);
      saveUndo = false;
    }
    beginClip(d, x, y, w, h);
    if (a.mousePressed && smouseIn()) {
      yoff+= a.mouseY-a.pmouseY;
      xoff+= a.mouseX-a.pmouseX;
      int max = 0;
      for (String s : lines) max = max(max, s.length());
      float maxx = (max - 2)*chw;
      if (xoff < -maxx) xoff = (int) -maxx;
      if (w > (max + 5)*chw) xoff = 0;
      float maxy = tsz * (lines.size() - 2);
      if (yoff < -maxy) yoff = (int) -maxy;
      
      if (yoff > 0) yoff = 0;
      if (xoff > 0) xoff = 0;
    }
    if (pmousePressed && !a.mousePressed && smouseIn() && dist(a.mouseX, a.mouseY, smouseX, smouseY) < 10) {
      cy = constrain(floor((a.mouseY-y-yoff)/tsz), 0, lines.size()-1);
      cx = constrain(round((a.mouseX-x-xoff)/chw), 0, lines.get(cy).length());
      tt = 0;
      //if (cy < 0) {
      //  lines.append("CY<0!!1!11!!");
      //  cy = 0;
      //}
      //if (cy >= lines.size()) cy = lines.size()-1;
    }
    //fill(#333333);
    d.fill(#101010);
    d.noStroke();
    d.rectMode(CORNER);
    d.rect(x, y, w, h);
    d.textAlign(LEFT, TOP);
    //textSize(tsz);
    //int dy = 0;
    //for (String s : lines) {
    //  SyntaxHighlight.apltext(s, x, y + dy*tsz + yoff, tsz, new Theme(), g);
    //  //text(s, x, y + dy*tsz + yoff);
    //  dy++;
    //}
    hl.draw(x + xoff, y + yoff, y, y+h, tsz, fullPos());
    
    tt--;
    if (tt < 0) tt = 60;
    if (tt > 30 || this != textInput) {
      float px;
      //if (mousePressed) px = x + max(textWidth(lines.get(cy).substring(0, cx)), 3) + xoff; else
      px = x + max(chw * cx, 3) + xoff;
      d.stroke(th.caret);
      d.line(px, tsz*cy + yoff + y, px, tsz*(cy+1) + yoff + y);
    }
    endClip(d);
  }

  ArrayList<String> lines;
  int cx, cy;
  String allText() {
    String s = "";
    for (int i = 0; i < lines.size(); i++) {
      if (i != 0) s+= '\n';
      s+= lines.get(i);
    }
    return s;
  }
  void clear() {
    if (!allText().equals("")) saveUndo = true;
    lines = new ArrayList();
    lines.add("");
    cx = 0;
    cy = 0;
  }
  void append(String str) {
    if (!str.equals("")) saveUndo = true;
    tt = 0;
    for (char c : sit(str)) {
      if (c == '\n') newline();
      else {
        String ln = lines.get(cy);
        lines.set(cy, ln.substring(0, cx) + c + ln.substring(cx));
        cx++;
      }
    }
  }
  void newline() {
    String ln = lines.get(cy);
    String ln1 = ln.substring(0, cx);
    String ln2 = ln.substring(cx);
    lines.set(cy, ln1);
    lines.add(cy+1, ln2);
    cy++;
    cx = 0;
  }
  void eval() {
  }
  void ldelete() {
    tt = 0;
    if (cx != 0 || cy != 0) saveUndo = true;
    if (cx == 0) {
      if (cy != 0) {
        String pln = lines.get(cy-1);
        cx = pln.length();
        lines.set(cy-1, pln + lines.get(cy));
        lines.remove(cy);
        cy--;
      }
    } else {
      String ln = lines.get(cy);
      lines.set(cy, ln.substring(0, cx-1) + ln.substring(cx));
      cx--;
    }
  }
  void special(String s) {
    tt = 0;
    if (s.equals("eval")) {
      eval();
    } else if (s.equals("left")) {
      left();
    } else if (s.equals("right")) {
      right();
    } else if (s.equals("up")) {
      cursorMoved = true;
      if (cy > 0) {
        cy--;
        cx = Math.min(cx, lines.get(cy).length());
      } else {
        cx = 0;
      }
    } else if (s.equals("down")) {
      cursorMoved = true;
      if (cy < lines.size()-1) {
        cy++;
        cx = Math.min(cx, lines.get(cy).length());
      } else {
        cx = lines.get(cy).length();
      }
    } else if (s.equals("openPar")) {
      append("()");
      cx--;
    } else if (s.equals("wrapPar")) {
      append(")");
      String all = allText();
      int cl = 1;
      int ocp = 0;
      for (int y = 0; y < cy; y++) ocp+= lines.get(y).length()+1;
      ocp+= cx;
      int cp = Math.max(0, Math.min(ocp-2, all.length()-1))+1;
      while (cp > 0) {
        cp--;
        if (all.charAt(cp) == '\n') break;
        if ("([{".contains(Character.toString(all.charAt(cp)))) { cl--; if (cl==0){ cp++; break; } }
        if (")]}".contains(Character.toString(all.charAt(cp))))   cl++;
      }
      //println(cp);
      to(Math.max(0, cp));
      append("(");
      to(ocp);
      right();
      //println(allText());
      //println(cx, cy);
      //cx--;
    } else if (s.equals("undo")) {
      hptr+= hsz-1;
      hptr%= hsz;
      to(history[hptr]);
      modified = true;
    } else if (s.equals("redo")) {
      hptr++;
      hptr%= hsz;
      to(history[hptr]);
      modified = true;
    } else if (s.equals("paste")) {
      a.paste(this);
    } else if (s.equals("match")) {
      int sel = hl.sel(fullPos());
      if (sel != -1) to(sel);
    } else extraSpecial(s);
  }
  void to(int full) {
    int s = 0;
    int e = hl.lnstarts.length;
    while(s+1 != e) {
      int n = (s+e)/2;
      if (hl.lnstarts[n] < full) s = n;
      else e = n;
    }
    cy = s;
    cx = full - hl.lnstarts[cy];
    println(full,"->",cx,cy);
    cursorMoved = true;
  }
  void extraSpecial(String s) {
    println("unknown special " + s);
  }
  void to(State st) {
    if (st != null) {
      lines = st.lns();
      cx = st.cx;
      cy = st.cy;
    }
  }
  class State {
    String code;
    int cx, cy;
    State(String code, int cx, int cy) {
      this.cx = cx;
      this.cy = cy;
      this.code = code;
    }
    ArrayList<String> lns() {
      ArrayList<String> r = new ArrayList(Arrays.asList(split(code, "\n")));
      if (r.size() == 0) r.add("");
      return r;
    }
  }
  
  void left() {
    cursorMoved = true;
    cx--;
    if (cx == -1) {
      if (cy == 0) cx = 0;
      else {
        cy--;
        cx = lines.get(cy).length();
      }
    }
  }
  void right() {
    cursorMoved = true;
    cx++;
    if (cx == lines.get(cy).length()+1) {
      if (cy == lines.size()-1) cx--;
      else {
        cx = 0;
        cy++;
      }
    }
  }
  
  void rdelete() {
    right();
    ldelete();
  }
  void pasted(String s) {
    append(s);
  }
  int fullPos() {
    return cx + hl.lnstarts[cy];
  }
}
