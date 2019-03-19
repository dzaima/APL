public enum d { up, down, left, right, def }
class Key {
  Action N, U, D, L, R;
  Action[] all;
  int x, y, col;
  Key(JSONObject o, int x, int y) {
    this.x = x;
    this.y = y;
    N = o.hasKey("def"  )? new Action(o.getJSONObject("def"  ), d.def  , x, y) : new NOOP();
    U = o.hasKey("up"   )? new Action(o.getJSONObject("up"   ), d.up   , x, y) : new NOOP();
    D = o.hasKey("down" )? new Action(o.getJSONObject("down" ), d.down , x, y) : new NOOP();
    L = o.hasKey("left" )? new Action(o.getJSONObject("left" ), d.left , x, y) : new NOOP();
    R = o.hasKey("right")? new Action(o.getJSONObject("right"), d.right, x, y) : new NOOP();
    col=           o.getInt       ("col"  );
    all = new Action[]{N, U, D, L, R};
  }
  void draw() {
    int rx = x*w;
    int ry = y*h + height-vC*h;
  
    fill(index[col]);
    noStroke();
    rect(rx, ry, w, h);
    for (Action o : all) o.draw(rx, ry);
  }
  Action resp(float dx, float dy) {
    float adx = Math.abs(dx);
    float ady = Math.abs(dy);
    if (adx < .5 && ady < .5) return N;
    
    if (adx > ady) { // horiz
      if (dx > 0) return R;
      return L;
    } else { // vert
      if (dy > 0) return D;
      return U;
    }
  }
  void click(float dx, float dy) {
    Action c = resp(dx, dy);
    c.click();
  }
  void repeat(float dx, float dy) {
    Action c = resp(dx, dy);
    c.rep();
  }
}

static final float[][] offsets = {
  {.5, .2 }, // up
  {.5, .87}, // down
  {.2, .5 }, // left
  {.8, .5 }, // right
  {.5, .5 }, // center
};
class NOOP extends Action {
  void draw(int rx, int ry) { }
  void click() { }
  void rep() { }
}
class Action {
  String disp;
  String type;
  String special;
  boolean repeat = false;
  boolean sd = false;
  d dir;
  int diri, x, y;
  Action() {}
  Action(JSONObject o, d d, int x, int y) {
    disp = o.getString("chr");
    this.x = x;
    this.y = y;
    switch(d) {
      case    up: diri=0; break;
      case  down: diri=1; break;
      case  left: diri=2; break;
      case right: diri=3; break;
      case   def: diri=4; break;
    }
    this.dir = d;
    if (o.hasKey("type")) {
      type = o.getString("type");
    } else if (o.hasKey("spec")) {
      special = o.getString("spec");
    } else type = disp;
    if (o.hasKey("rep")) repeat = o.getBoolean("rep");
    if (o.hasKey("sd")) sd = o.getBoolean("sd");
    if (o.hasKey("to")) special = o.getString("to");
  }
  void draw(int rx, int ry) {
    float[] c = offsets[diri];
    fill(255);
    if (dir == d.def) {
      if (sd) {
        textSize(h*.33f);
        ry+=h/20;
      } else {
        textSize(h*.5f);
      }
      text(disp, rx + w*c[0], ry + h*c[1]);
    } else {
      if (sd) {
        textSize(h*.1f);
        ry+=h/40;
      } else {
        textSize(h*.2f);
      }
      text(disp, rx + w*c[0], ry + h*c[1]);
    }
  }
  void click() {
    lastTouch = millis();
    if (type != null) ins(type);
    if (special != null) {
      switch (special) {
        case "exec": exec(); break;
        case "delete": 
          if (ptr == 0) break;
          program = program.substring(0, ptr-1)+program.substring(ptr);
          ptr--;
        break;
        case "up": {
          int[] pos2d = pos2d();
          pos2d[1]--;
          toPos(pos2d);
        break; }
        case "down": {
          int[] pos2d = pos2d();
          pos2d[1]++;
          toPos(pos2d);
        break; }
        case "left":
          ptr--;
          if (ptr < 0) ptr = 0;
        break;
        case "right":
          ptr++;
          if (ptr > program.length()) ptr--;
        break;
        case "undo":
          histptr--;
          if (histptr < 0) histptr = history.length-1;
          program = history[histptr];
          ptr = historyPtrs[histptr];
          if (program == null) program = "";
          noHist = true;
        break;
        case "redo":
          histptr++;
          if (histptr >= history.length) histptr = 0;
          program = history[histptr];
          ptr = historyPtrs[histptr];
          if (program == null) program = "";
          noHist = true;
        break;
        case "clear":
          program = "";
          ptr = 0;
        break;
        case "graph":
          mode = 2;
          fullX = -width/20;
          fullY = -height/20;
          fullS = 10;
          if (resVal instanceof VarArr) resVal = ((VarArr)resVal).materialize();
          if (resVal instanceof Variable) resVal = ((Variable)resVal).get();
          if (resVal instanceof Fun) initFn();
        break;
        case "ascii":
          mode = 1;
          fullX = -width/20;
          fullY = -height/20;
          fullS = 1;
        break;
        case "extra":
          chars = Echars;
        break;
        case "def":
          chars = Dchars;
        break;
        case "wrap":
          int ppos = ptr-1;
          int lvl = 1;
          while (ppos >= 0) {
            char c = program.charAt(ppos);println(c,lvl,ppos);
            if (c == '(' || c == '[' || c == '{') lvl--;
            if (c == ')' || c == ']' || c == '}') lvl++;
            if (lvl <= 0) break;
            ppos--;
          }
          ppos++;
          ptr++;
          program = program.substring(0, ppos)+"("+program.substring(ppos);
          ins(")");
        break;
        case "copy":
          copyText(program);
        break;
        case "paste":
          pasteText();
        break;
        //case "":
        //break;
        default: println("unknown special "+special);
      }
    }
  }
  void rep() {
    if (repeat) click();
  }
}
