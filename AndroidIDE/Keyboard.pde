class Keyboard extends Drawable {
  int xam, yam;
  int kw, kh; // key width/height
  JSONObject data;
  int[] cols;
  int defcol;
  int shiftMode; // 0 = none, 1 = temp, 2 = hold;
  Key[][] keys;
  Keyboard(int x, int y, int w, int h, int xam, int yam, JSONObject data) {
    super(x, y, w, h); // don't draw by default
    this.data = data;
    this.xam = xam;
    this.yam = yam;
    this.kw = w/xam;
    this.kh = h/yam;
    keys = new Key[yam][xam];
    for (int cy = 0; cy < yam; cy++) for (int cx = 0; cx < xam; cx++) keys[cy][cx] = new Key(cx, cy, this);
    JSONArray colsA = data.getJSONArray("colors");
    cols = new int[colsA.size()];
    for(int i = 0; i < cols.length; i++) {
      cols[i] = Integer.parseUnsignedInt(colsA.getString(i), 16);
    }
    defcol = cols[data.getInt("defcol")];
    loadLayout(data.getString("mainName"));
  }
  void loadLayout(String name) {
    JSONArray arr = data.getJSONArray(name);
    for (int y = 0; y < yam; y++) {
      JSONArray row = arr.getJSONArray(y);
      for (int x = 0; x < xam; x++) {
        keys[y][x].load(row.getJSONObject(x));
      }
    }
    redraw();
  }
  void redraw() {
    if (visible) {
      for (Key[] row : keys) for (Key k : row) k.redraw();
    }
  }
  
  Key start;
  
  void tick() {
    if (!pmousePressed && mousePressed && mouseInMe()) {
      int mx = (mouseX-x) / kw;
      int my = (mouseY-y) / kh;
      if (mx >= 0 && my >= 0 && mx < xam && my < yam) start = keys[my][mx];
    }
    if (pmousePressed && !mousePressed && start != null) {
      Action a = findAction();
      start = null;
      if (a != null) a.call();
    }
    if (start != null) {
      if (millis() - mouseStart > 200) {
        Action a = findAction();
        if (a != null && a.rep) a.call();
      }
    }
  }
  Action findAction() {
    if (dist(mouseX, mouseY, smouseX, smouseY) > kh) { // gesture
      int dx = mouseX - smouseX;
      int dy = mouseY - smouseY;
      if (Math.abs(dx) > Math.abs(dy)) {
        if (dx > 0) return start.actions[4];
        else        return start.actions[3];
      } else {
        if (dy > 0) return start.actions[2];
        else        return start.actions[1];
      }
    } else return start.actions[0];
  }
}

void keyboard(int x, int y, int w, int h, String file) {
  if (kb != null) kb.delete();
  JSONObject o = loadJSONObject(file);
  JSONArray main = o.getJSONArray(o.getString("mainName"));
  kb = new Keyboard(x, y, w, h, main.getJSONArray(0).size(), main.size(), o);
  kb.align(BOTTOM);
  kb.show();
}

static final String[] dirs = new String[]{"def", "up", "down", "left", "right"};
static final float[][] offsets = {
  {.5, .5 }, // center
  {.5, .2 }, // up
  {.5, .87}, // down
  {.2, .5 }, // left
  {.8, .5 }, // right
};

class Key extends Drawable {
  Keyboard b;
  
  int col = #222222;
  Action[] actions = new Action[5]; // C U D L R
  
  Key(int x, int y, Keyboard b) {
    super(x, y, b.kw, b.kh);
    this.b = b;
  }
  
  void redraw() {
    fill(col);
    noStroke();
    int px = b.x + x*w;
    int py = b.y + y*h;
    rect(px, py, w, h);
    for(int i = 0; i < 5; i++) {
      Action a = actions[i];
      float[] offs = offsets[i];
      if (a == null) continue;
      String t = a.chr;
      if (b.shiftMode != 0 && t.length() == 1) t = t.toUpperCase();
      fill(255);
      textAlign(CENTER, CENTER);
      float yoff = 0;
      
      if (i == 0) {
        if (a.sd) {
          textSize(h*.33f);
          yoff = h/20f;
        } else {
          textSize(h*.5f);
        }
      } else {
        if (a.sd) {
          textSize(h*.1f);
          yoff = h/40f;
        } else {
          textSize(h*.2f);
        }
      }
      
      text(t, px + w*offs[0], py + h*offs[1] + yoff);
    }
  }
  void load(JSONObject o) {
    for (int i = 0; i < 5; i++) {
      JSONObject c = o.getJSONObject(dirs[i]);
      if (c == null) actions[i] = null;
      else actions[i] = new Action(c, b);
    }
    col = o.hasKey("col")? b.cols[o.getInt("col")] : b.defcol;
  }
}


class Action {
  final String chr, spec, type;
  final boolean rep, sd;
  final Keyboard b;
  Action (JSONObject o, Keyboard b) {
    chr = o.getString("chr");
    String type = o.getString("type");
    if (type == null) this.type = chr;
    else this.type = type;
    
    spec = o.getString("spec");
    rep = o.hasKey("rep")? o.getBoolean("rep") : false;
    sd = o.hasKey("sd")? o.getBoolean("sd") : false;
    this.b = b;
  }
  void call() {
    if (textInput == null) return;
    if (spec == null) {
      String toType = type;
      if (b.shiftMode != 0) {
        if (type.length() == 1) toType = toType.toUpperCase();
        if (b.shiftMode == 1) {
          b.shiftMode = 0;
          b.redraw();
        }
      }
      textInput.append(toType);
      return;
    }
    if (spec.startsWith("goto_")) {
      b.loadLayout(spec.substring(5));
      return;
    }
    switch(spec) {
      case "del": textInput.delete(); return;
      case "clear": textInput.clear(); return;
      case "shift": 
        b.shiftMode++;
        if (b.shiftMode > 2) b.shiftMode = 0;
        b.redraw();
        return;
    }
    println("unknown type "+spec);
  }
}
