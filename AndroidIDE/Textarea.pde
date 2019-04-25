class APLTextarea extends Drawable implements TextReciever {
  float tsz;
  APLTextarea(int x, int y, int w, int h) {
    super(x, y, w, h);
    lines = new ArrayList();
    lines.add("");
  }
  int tt = 0; // 
  int yoff = 0; // scroll
  
  void redraw() {
    tsz = min(width, height)/20;
  }
  boolean modified = true;
  final int hsz = 300;
  final State[] history = new State[hsz];
  int hptr = 0; // points to the current modification
  void tick() {
    if (!visible) return;
    if (mousePressed && !pmousePressed && smouseIn()) {
      textInput = this;
    }
    if (cy < 0 || cy > lines.size() || cx < 0 || cx > lines.get(cy).length()) {
      lines.add("CX was "+cx+"; CY was "+cy);
      cx = 0;
      cy = 0;
    }
    if (modified) {
      hptr++;
      hptr%= hsz;
      history[hptr] = new State(allText(), cx, cy);
      modified = false;
    }
    clip(x, y, w, h);
    if (mousePressed && smouseIn()) {
      yoff+= mouseY-pmouseY;
      if (yoff > 0) yoff = 0;
    }
    if (pmousePressed && !mousePressed && smouseIn() && dist(mouseX, mouseY, smouseX, smouseY) < 10) {
      cy = constrain(floor((mouseY-y-yoff)/tsz), 0, lines.size()-1);
      cx = constrain(round((mouseX-x)/textWidth("H")), 0, lines.get(cy).length());
      tt = 0;
      //if (cy < 0) {
      //  lines.append("CY<0!!1!11!!");
      //  cy = 0;
      //}
      //if (cy >= lines.size()) cy = lines.size()-1;
    }
    //fill(#333333);
    fill(#101010);
    noStroke();
    rectMode(CORNER);
    rect(x, y, w, h);
    textAlign(LEFT, TOP);
    fill(#D2D2D2);
    stroke(#D2D2D2);
    textSize(tsz);
    int dy = 0;
    for (String s : lines) {
      text(s, x, y + dy*tsz + yoff);
      dy++;
    }
    tt--;
    if (tt < 0) tt = 60;
    if (tt > 30 || this != textInput) {
      float px = x + max(textWidth(lines.get(cy).substring(0, cx)), 3);
      line(px, tsz*cy + yoff + y, px, tsz*(cy+1) + yoff + y);
    }
    noClip();
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
    if (!allText().equals("")) modified = true;
    lines = new ArrayList();
    lines.add("");
    cx = 0;
    cy = 0;
  }
  void append(String str) {
    if (!str.equals("")) modified = true;
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
  void eval() { }
  void ldelete() {
    tt = 0;
    if (cx != 0 || cy != 0) modified = true;
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
      cx--;
      if (cx == -1) {
        if (cy == 0) cx = 0;
        else {
          cy--;
          cx = lines.get(cy).length();
        }
      }
    }
    else if (s.equals("right")) {
      cx++;
      if (cx == lines.get(cy).length()+1) {
        if (cy == lines.size()-1) cx--;
        else {
          cx = 0;
          cy++;
        }
      }
    }
    else if (s.equals("up")) {
      if (cy > 0) {
        cy--;
        cx = Math.min(cx, lines.get(cy).length());
      } else {
        cx = 0;
      }
    }
    else if (s.equals("down")) {
      if (cy < lines.size()-1) {
        cy++;
        cx = Math.min(cx, lines.get(cy).length());
      } else {
        cx = lines.get(cy).length();
      }
    }
    else if (s.equals("openPar")) {
      append("()");
      cx--;
    }
    else if (s.equals("closePar")) {
      append("()"); // TODO  
      cx--;
    }
    else if (s.equals("undo")) {
      hptr+= hsz-1;
      hptr%= hsz;
      to(history[hptr]);
    }
    else if (s.equals("redo")) {
      hptr++;
      hptr%= hsz;
      to(history[hptr]);
    }
    else extraSpecial(s);
  }
  void extraSpecial(String s) {
    println("unknown special " + s);
  }
  void to(State st) {
    if (st != null) {
      lines = st.lns();
      cx = st.cx;
      cy = st.cy;
    } else clear();
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
  void rdelete() {
    
  }
}
