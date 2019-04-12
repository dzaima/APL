class APLField extends Drawable implements TextReciever {
  float tsz;
  APLField(int x, int y, int w, int h) {
    super(x, y, w, h);
    line = "";
    show();
  }
  int tt = 0;
  
  void redraw() {
    tsz = h*.8;
  }
  boolean modified = true;
  final int hsz = 300;
  final State[] history = new State[hsz];
  int hptr = 0; // points to the current modification
  void tick() {
    if (mousePressed && !pmousePressed && dragged()) {
      textInput = this;
    }
    if (cx < 0 || cx > line.length()) {
      line+= "CX was "+cx;
      cx = 0;
    }
    if (modified) {
      hptr++;
      hptr%= hsz;
      history[hptr] = new State(allText(), cx);
      modified = false;
    }
    clip(x, y, w, h);
    if (pmousePressed && !mousePressed && dragged() && dist(mouseX, mouseY, smouseX, smouseY) < 10) {
      cx = constrain(round((mouseX-x)/textWidth("H")), 0, line.length());
      tt = 0;
      //if (0 < 0) {
      //  lines.append("0<0!!1!11!!");
      //  0 = 0;
      //}
      //if (0 >= lines.size()) 0 = lines.size()-1;
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
    text(line, x, y + dy*tsz + h*.1);
    tt--;
    if (tt < 0) tt = 60;
    if (tt > 30 || this != textInput) {
      strokeWeight(tsz*.05);
      float px = x + max(textWidth(line.substring(0, cx)), 3);
      line(px, y + h*.1, px, y + h*.9);
    }
    noClip();
  }
  
  String line;
  int cx;
  String allText() {
    return line;
  }
  void clear() {
    if (!allText().equals("")) modified = true;
    line = "";
    cx = 0;
  }
  void append(String str) {
    if (!str.equals("")) modified = true;
    tt = 0;
    for (char c : sit(str)) {
      if (c == '\n') newline();
      else {
        line = line.substring(0, cx) + c + line.substring(cx);
        cx++;
      }
    }
  }
  void newline() { }
  void eval() { }
  void backspace() {
    tt = 0;
    if (cx != 0) modified = true;
    if (cx == 0) {
    } else {
      line = line.substring(0, cx-1) + line.substring(cx);
      cx--;
    }
  }
  void special(String s) {
    tt = 0;
    if (s.equals("eval")) {
      eval();
    } else if (s.equals("left")) {
      cx--;
      if (cx == -1) cx = 0;
    }
    else if (s.equals("right")) {
      cx++;
      if (cx == line.length()+1) {
        cx--;
      }
    }
    else if (s.equals("up")) {
      cx = 0;
    }
    else if (s.equals("down")) {
      cx = line.length();
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
    else println("unknown special " + s);
  }
  void to(State st) {
    if (st != null) {
      line = st.code;
      cx = st.cx;
    } else clear();
  }
  class State {
    String code;
    int cx;
    State(String code, int cx) {
      this.cx = cx;
      this.code = code;
    }
  }
}
