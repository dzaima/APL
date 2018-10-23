class APLGraphics extends APLMap {
  PGraphics g;
  APLGraphics () {
  }
  Arr toArr() {
    throw new SyntaxError("Converting a Graphics object to array");
  }
  void set(Value k, Obj v) {
    String s = k.fromAPL().toLowerCase();
    switch (s) {
      
      case     "fill":
        if (((Value) v).ia == 0) g.noFill();
        else g.fill(col(v));
      return;
      case   "stroke": g.stroke(col(v)); return;
      case "textsize": g.textSize(w.toInt(tf)); return;
      default: throw new DomainError("setting non-existing key "+s+" for Graphics");
    }
  }
  Obj getRaw(Value k) {
    String s = k.fromAPL().toLowerCase();
    switch (s) {
      case "background": case "bg": return new Fun(0x001) {
        public Obj call(Value w) {
          g.background(col(w));
          return w;
        }
      };
      case "text": return new Fun(0x010) {
        public Obj call(Value a, Value w) {
          XY p = new XY(a);
          g.text(w.fromAPL(), (float)p.x, (float)p.y);
          return w;
        }
      };
      case "textalign": case "ta": return new Fun(0x011) {
        public Obj call(Value w) {
          String hs = w.fromAPL();
          Integer h = hs.equals("center")? (Integer)CENTER : hs.equals("left")? (Integer)LEFT : hs.equals("right")? (Integer)RIGHT : null;
          if (h == null) throw new DomainError("textAlign with invalid horizontal align", this, w);
          g.textAlign(h);
          return w;
        }
        public Obj call(Value a, Value w) {
          String hs = w.fromAPL();
          Integer h = hs.equals("center")? (Integer)CENTER : hs.equals("left")? (Integer)LEFT : hs.equals("right")? (Integer)RIGHT : null;
          if (h == null) throw new DomainError("textAlign with invalid horizontal align "+hs, this, w);
          String vs = a.fromAPL();
          Integer v = vs.equals("center")? (Integer)CENTER : vs.equals("top")? (Integer)TOP : vs.equals("bottom")? (Integer)BOTTOM : null;
          if (v == null) throw new DomainError("textAlign with invalid vertical align "+vs, this, w);
          g.textAlign(h, v);
          return w;
        }
      };
      case "rect": return new ForFA() {
        public void setup(Value a) {
          if (a == null) {  g.rectMode(CORNERS); return; }
          switch(a.fromAPL().toLowerCase()) { default: throw new DomainError("⍺ for G.ellipse can't be "+a);
            case  "corner": g.rectMode(CORNER ); break;
            case "corners": g.rectMode(CORNERS); break;
            case  "radius": g.rectMode(RADIUS ); break;
            case  "center": g.rectMode(CENTER ); break;
          }
        }
        public void draw(float[] fa) {
          if (fa.length >= 5) {
            g.fill((int)(long)fa[4]);
            if (fa.length >= 6) g.stroke((int)(long)fa[5]);
          }
          g.rect(fa[0], fa[1], fa[2], fa[3]);
        }
      };
      case "ellipse": return new ForFA() {
        public void setup(Value a) {
          if (a == null) {  g.ellipseMode(CORNERS); return; }
          switch(a.fromAPL().toLowerCase()) { default: throw new DomainError("⍺ for G.ellipse can't be "+a);
            case  "corner": g.ellipseMode(CORNER ); break;
            case "corners": g.ellipseMode(CORNERS); break;
            case  "radius": g.ellipseMode(RADIUS ); break;
            case  "center": g.ellipseMode(CENTER ); break;
          }
        }
        public void draw(float[] fa) {
          g.ellipse(fa[0], fa[1], fa[2], fa[3]);
        }
      };
      case "circle": return new ForFA() {
        public void setup(Value a) {
          if (a == null) {  g.ellipseMode(RADIUS ); return; }
          switch(a.fromAPL().toLowerCase()) { default: throw new DomainError("⍺ for G.ellipse can't be "+a);
            case  "radius": g.ellipseMode(RADIUS ); break;
            case  "center": g.ellipseMode(CENTER ); break;
          }}
        public void draw(float[] fa) {
          g.ellipse(fa[0], fa[1], fa[2], fa[2]);
        }
      };
      case "point": case "pt": return new ForFA() {
        public void draw(float[] fa) {
          if ((fa.length&2) == 1) throw new DomainError("G.line recieved odd length array", null, w);
          if (fa.length > 4) {
            g.beginShape(POINTS);
            for (int i = 0; i < fa.length; i+= 2) g.vertex(fa[i], fa[i+1]);
            g.endShape();
          }
        }
      };
      case "line": case "ln": return new ForFA() {
        public void setup(Value a) {
          g.strokeWeight(a == null? 1 : a.toInt(this));
          g.noFill();
        }
        public void draw(float[] fa) {
          if ((fa.length&2) == 1) throw new DomainError("G.line recieved odd length array", null, w);
          if (fa.length > 4) {
            g.beginShape();
            for (int i = 0; i < fa.length; i+= 2) g.vertex(fa[i], fa[i+1]);
            g.endShape();
          }
        }
      };
      case "loop": return new ForFA() {
        public void setup(Value a) {
          g.strokeWeight(a == null? 1 : a.toInt(this));
        }
        public void draw(float[] fa) {
          if ((fa.length&2) == 1) throw new DomainError("G.line recieved odd length array", null, w);
          if (fa.length > 2) {
            g.beginShape();
            for (int i = 0; i < fa.length; i+= 2) g.vertex(fa[i], fa[i+1]);
            g.endShape(CLOSE);
          }
        }
      };
      default: return NULL;
    }
  }
  int size() {
    throw new SyntaxError("Getting size of the P5 object");
  }
  String toString() { return "G"; }
}
