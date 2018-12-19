LL<Point> points = new LL<Point>();
PQ<Double, Point> pq = new PQ<Double, Point>();
double[] b;
Fun fn;
int pts = 20000;
double scale = 1;



void initFn() {
  fn = (Fun)resVal;
  points.clear();
  pq.clear();
  bounds();
  add(b[0], points.start);
  add(b[1], points.last());
}

class Line {
  ArrayList<Double> ptsx = new ArrayList<Double>();
  ArrayList<Double> ptsy = new ArrayList<Double>();
  void add(double x, double y) {
    ptsx.add(x);
    ptsy.add(y);
  }
  void draw() {
    boolean drawing = false;
    int len = ptsx.size();
    for (int i = 0; i < len; i++) {
      double x = ptsx.get(i);
      double y = ptsy.get(i);
      if (Double.isNaN(y)) {
        if (drawing) {
          drawing = false;
          endShape();
        }
      } else if (y == Double.POSITIVE_INFINITY) {
        if (drawing) {
          vertex((float) (x*scale), (float) fullY);
          drawing = false;
          endShape();
        }
      } else if (y==Double.NEGATIVE_INFINITY) {
        if (drawing) {
          vertex((float) (x*scale), (float) (fullY + height/fullS));
          drawing = false;
          endShape();
        }
      } else {
        if (!drawing) {
          drawing = true;
          beginShape();
        }
        vertex((float)(x*scale), -(float)(y*scale));
      }
    }
    if (drawing) endShape();
  }
}

void functionGrapher() {
  bounds();
  Obj s = global.get("dxy");
  boolean joined = true;
  float ph = height/200f;
  if (s == null) {
    pts = 1000;
  } else {
    pts = ((Value)s).get(0).asInt();
    joined = ((Value)s).get(1).asDouble() != 0;
    ph = (float) ((Value)s).get(2).asDouble();
  }
  ph/= (float)fullS;
  double sCut = b[0]-b[2];
  double eCut = b[1]+b[2];
  double sEnd = b[0]+b[2];
  double eSrt = b[1]-b[2];
  LLNode<Point> n = points.first();
  while (n != points.end) {
    if (n.v.x < sCut) remove(n);
    else break;
    n = n.next;
  }
  boolean sInR = n != points.end && n.v.x < sEnd; // start in range
  n = points.last();
  while (n != points.start) {
    if (n.v.x > eCut) remove(n);
    else break;
    n = n.prev;
  }
  boolean eInR = n != points.start && n.v.x > eSrt; 
  
  if (!sInR) add(b[0], points.start);
  if (!eInR) add(b[1], points.last());
  long nt = System.nanoTime();
  int ptsadded = 0;
  while (pq.size() > 0) {
    PQNode<Double, Point> bg = pq.biggest();
    if (((Double) bg.m) > b[2]) {
      Point p = (Point) bg.v;
      add((p.x + p.pnode.next.v.x)/2,  p.pnode);
      ptsadded++;
    } else break;
    if (System.nanoTime()-nt > 5E6) break;
  }
  //println(points.size, ptsadded, ptsadded*1f/(System.nanoTime()-nt)*1E9, frameRate);
  
  while (pq.size() > 0) {
    PQNode<Double, Point> sm = pq.smallest(); // can't be PQ<Double, Point>.Item because Processing :|
    if ((Double) sm.m < b[2]/4) {
      remove(sm.v.pnode);
    } else break;
  }
  
  noFill();
  stroke(0xffd2d2d2);
  strokeWeight(ph);
  n = points.first();
  if (joined && n != points.end && n.next != points.end) {
    ArrayList<Line> lns = new ArrayList<Line>();
    n = n.next;
    while (n != points.end) {
      LLNode<Point> p = n.prev;
      double[] na = n.v.y;
      if (lns.size() == na.length) {
        for (int i = 0; i < na.length; i++) {
          lns.get(i).add(n.v.x, na[i]);
        }
      } else {
        for(Line l : lns) l.draw();
        lns.clear();
        for(double y : na) {
          Line ln = new Line();
          ln.add(n.v.x, y);
          lns.add(ln);
        }
      }
      n = n.next;
    }
    for(Line l : lns) l.draw();
  } else {
    //beginShape(POINTS);
    //strokeWeight(ph);
    while (n != points.end) {
      //vertex((float)(n.v.x*scale), -(float)(n.v.y*scale));
      for (double y : n.v.y) ellipse((float)(n.v.x*scale), -(float)(y*scale), ph, ph);
      n = n.next;
    }
    //endShape();
  }
  popMatrix();
}

void add(double pos, LLNode<Point> l) {
  double[] res;
  try {
    res = ((Value) fn.call(new Num(pos))).asDoubleArr();
  } catch (Throwable e) {
    res = new double[0];
  }
  Point p = new Point(pos, res);
  LLNode<Point> r = l.next;
  LLNode<Point> c = l.addAfter(p);
  p.pnode = c;
  if (l != points.start) {
    if (l.v.pqr != null) {
      l.v.pqr.remove();
      l.v.pqr = null;
    }
    addPQ(l, c);
  }
  if (r != points.end) addPQ(c, r);
}
void addPQ(LLNode<Point> l, LLNode<Point> r) {
  double d = r.v.x - l.v.x;
  l.v.pqr = pq.add(d, l.v);
}
void remove(LLNode<Point> n) {
  n.remove();
  LLNode<Point> l = n.prev;
  LLNode<Point> r = l.next;
  if (n.v.pqr != null) { n.v.pqr.remove(); n.v.pqr = null; }
  if (r == points.end && l.v.pqr != null) {
    l.v.pqr.remove();
    l.v.pqr=null;
  }
  if (l != points.start && r != points.end) {
    l.v.pqr.remove();
    addPQ(l, r);
  }
}



void bounds() {
  b = new double[] {
    fullX/scale, // starting visible x
    (fullX + width/fullS)/scale, // ending visible x
    (width/fullS)/scale / pts,
  };
}


class LL<T> {
  LLNode<T> start = new SNode<T>(this);
  LLNode<T> end = new ENode<T>(this);
  
  LL() {
    start.next = end;
    end.prev = start;
  }
  
  int size = 0;
  
  void addLast(T v) {
    end.addBefore(v);
  }
  
  void addFirst(T v) {
    start.addAfter(v);
  }
  
  void clear() {
    start.next = end;
    end.prev = start;
    size = 0;
  }
  LLNode<T> last() {
    return end.prev;
  }
  LLNode<T> first() {
    return start.next;
  }
}
class SNode<T> extends LLNode<T> {
  SNode(LL<T> ll) {
    super(ll, null, null, null);
  }
  void addBefore() { throw new IllegalStateException("adding before starting element"); }
}
class ENode<T> extends LLNode<T> {
  ENode(LL<T> ll) {
    super(ll, null, null, null);
  }
  void addAfter() { throw new IllegalStateException("adding after ending element"); }
}
class LLNode<T> {
  LLNode<T> prev;
  LLNode<T> next;
  LL<T> ll;
  T v;
  LLNode (LL<T> ll, LLNode<T> p, LLNode<T> n, T v) {
    prev = p;
    next = n;
    this.ll = ll;
    this.v = v;
  }
  LLNode<T> addAfter(T v) {// println(this, next, v);
    LLNode<T> n = new LLNode<T>(ll, this, next, v);
    next.prev = n;
    next = n;
    ll.size++;
    return n;
  }
  LLNode<T> addBefore(T v) {
    LLNode<T> n = new LLNode<T>(ll, prev, this, v);
    prev.next = n;
    prev = n;
    ll.size++;
    return n;
  }
  void remove() {
    prev.next = next;
    next.prev = prev;
    ll.size--;
    if(rmd)throw null;rmd=true;
  }
  boolean rmd;
}

class Point {
  double x;
  double[] y;
  LLNode<Point> pnode;
  PQNode pqr;
  Point (double x, double[] y) {
    this.x = x;
    this.y = y;
  }
  String toString() {
    return x+"";
  }
}
