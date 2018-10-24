LL<Point> points = new LL<Point>();
PQ<Double, Point> pq = new PQ<Double, Point>();
double[] b;
Fun fn;
int pts = 20000;
double scale = 10;



void initFn() {
  fn = (Fun)resVal;
  points.clear();
  pq.clear();
  bounds();
  add(b[0], points.start);
  add(b[1], points.last());
}
void functionGrapher() {
  bounds();
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
  int millis = millis(); // TODO make work if program has been running longer than 24 days
  while (true) {
    PQ.Item bg = pq.biggest(); // can't be PQ<Double, Point>.Item because Processing :|
    if (bg != null && ((Double) bg.m) > b[2]) {
      Point p = (Point) bg.t;
      add((p.x + p.pnode.next.v.x)/2,  p.pnode);
    } else break;
    if (millis()-millis > 5) break;
  }
  println("sz", pq.size(), points.size);
  
  while (true) {
    PQ.Item sm = pq.smallest(); // can't be PQ<Double, Point>.Item because Processing :|
    if (sm != null) {
      Point p = (Point) sm.t;
      if ((Double) sm.m < b[2]/4) {
        remove(p.pnode);
      } else break;
    }
    else break;
  }
  
  noFill();
  stroke(0xffd2d2d2);
  strokeWeight(height/200f / (float)fullS);
  n = points.first();
  boolean drawing = false;
  while (n != points.end) {
    if (n.v.y==null || Double.isNaN(n.v.y)) {
      if (drawing) {
        drawing = false;
        endShape();
      }
    } else if (n.v.y==Double.POSITIVE_INFINITY) {
      if (drawing) {
        vertex((float) (n.prev.v.x*scale), (float) fullY);
        drawing = false;
        endShape();
      }
    } else if (n.v.y==Double.NEGATIVE_INFINITY) {
      if (drawing) {
        vertex((float) (n.prev.v.x*scale), (float) (fullY + height/fullS));
        drawing = false;
        endShape();
      }
    } else {
      if (!drawing) {
        drawing = true;
        beginShape();
      }
      vertex((float)(n.v.x*scale), -(float)(n.v.y*scale));
    }
    n = n.next;
  }
  if (drawing) endShape();
  popMatrix();
  stroke(0xff666666);
  strokeWeight(height/200f);
  float hl = (float)(-fullY*fullS);
  line(0, hl, width, hl);
  float vl = (float)(-fullX*fullS);
  line(vl, 0, vl, height);
}

void add(double pos, LLNode<Point> l) {
  Double res;
  try {
    res = ((Num) fn.call(new Num(pos))).doubleValue();
  } catch (Throwable e) {
    res = null;
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
    fullX/scale,
    (fullX + width/fullS)/scale,
    (width/fullS)/scale / pts,
  };
}
class PQ<M extends Comparable, T> {
  LL<Item> items = new LL<Item>();
  //Node<Item> add(M m, T t) {
  //  Node<Item> n = items.first();
  //  while (n != items.end && n.v.m.compareTo(m) < 0) n = n.next;
  //  Item item = new Item(m, t);
  //  item.n = n.addBefore(item);
  //  return item.n;
  //}
  LLNode<Item> add(M m, T t) {
    LLNode<Item> n = items.first();
    while (n != items.end && n.v.m.compareTo(m) < 0) n = n.next;
    Item item = new Item(m, t);
    item.n = n.addBefore(item);
    return item.n;
  }
  Item biggest() {
    return items.last().v;
  }
  Item smallest() {
    return items.first().v;
  }
  class Item {
    M m;
    T t;
    LLNode<Item> n;
    Item(M m, T t) {
      this.m = m;
      this.t = t;
    }
    void remove() {
      n.remove();
      if(rmd)throw null;rmd=true;
    }
    boolean rmd;
  }
  int size() {
    return items.size;
  }
  void clear() {
    items.clear();
  }
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
  Double y;
  LLNode<Point> pnode;
  LLNode pqr;
  Point (double x, Double y) {
    this.x = x;
    this.y = y;
  }
}
