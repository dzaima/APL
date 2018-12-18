class PQ<M extends Comparable<? super M>, V> {
  static final byte lc = 32;
  PQNode<M, V>[][] B = new PQNode[lc][];
  PQNode<M, V>[][] S = new PQNode[lc][];
  int size = 0;
  byte l = 0; // layer count
  PQNode<M, V> add (M m, V v) {
    PQNode<M, V> n = new PQNode(this, m, v);
    //println("add "+n+"\n",this);
    
    size++;
    if ((size & size-1) == 0) { // size is now 2^n
      //println("extending");
      if (B[l] == null) B[l] = new PQNode[size];
      if (S[l] == null) S[l] = new PQNode[size];
      l++;
    }
    int pos = size - (1 << l-1);
    //println(pos, size, l);
    n.Bp = pos;
    n.Sp = pos;
    n.Bl = (byte)(l-1);
    n.Sl = (byte)(l-1);
    B[l-1][pos] = n;
    S[l-1][pos] = n;
    while (n.Bl != 0 && B[n.Bl-1][n.Bp>>1].m.compareTo(n.m)<0) n.Bswap(B[n.Bl-1][n.Bp>>1]);
    while (n.Sl != 0 && S[n.Sl-1][n.Sp>>1].m.compareTo(n.m)>0) {
      n.Sswap(S[n.Sl-1][n.Sp>>1]);
    }
    //println("after:", this+"\n");
    
    return n;
  }
  String toString() {
    int i = 0;
    String s = "l "+l+" sz "+size+"\n";
    while (B[i] != null) {
      s+= Arrays.toString(B[i++]);
    }
    s+= "\n";
    i = 0;
    while (S[i] != null) {
      s+= Arrays.toString(S[i++]);
    }
    return s;
  }
  void remove (PQNode<M, V> o) {
    //println("rm "+o+"\nbefore:", this);
    int p = size - (1 << l-1);
    byte cl = (byte)(l-1);
    PQNode<M, V> b = B[cl][p];
    PQNode<M, V> s = S[cl][p];
    B[cl][p] = S[cl][p] = null;
    if (b == o) {
      B[cl][p] = null;
    } else {
      B[o.Bl][o.Bp] = b;
      b.Bl = o.Bl;
      b.Bp = o.Bp;
      while (b.Bl != l-1) {
        PQNode<M, V> left = B[b.Bl+1][b.Bp*2];
        PQNode<M, V> right = B[b.Bl+1][b.Bp*2 + 1];
        if (b.Bl == l-2) {
          if (left == null) break; // left is null
          if (right == null) { // right is null = left is not
            if (left.m.compareTo(b.m)>0) left.Bswap(b);
            break;
          }
        }
        assert left != null && right != null;
        if (left.m.compareTo(right.m)>0) { // left's worse
          if ( left.m.compareTo(b.m)>0)  left.Bswap(b);
          else break;
        } else { // right's worse
          if (right.m.compareTo(b.m)>0) right.Bswap(b);
          else break;
        }
      }
    }
    
    if (s == o) {
      S[cl][p] = null;
    } else {
      S[o.Sl][o.Sp] = s;
      s.Sl = o.Sl;
      s.Sp = o.Sp;
      while (s.Sl != l-1) {
        PQNode<M, V> left = S[s.Sl+1][s.Sp*2];
        PQNode<M, V> right = S[s.Sl+1][s.Sp*2 + 1];
        if (s.Sl == l-2) {
          if (left == null) break; // left is null
          if (right == null) { // right is null = left is not
            if (left.m.compareTo(s.m)<0) left.Sswap(s);
            break;
          }
        }
        assert left != null && right != null;
        if (left.m.compareTo(right.m)<0) { // left's worse
          if ( left.m.compareTo(s.m)<0)  left.Sswap(s);
          else break;
        } else { // right's worse
          if (right.m.compareTo(s.m)<0) right.Sswap(s);
          else break;
        }
      }
    }
    
    size--;
    
    if ((size & size+1) == 0) { // pop level; size = 2^n
      //println("removing level");
      B[l] = null; // delete layer one down, not l-1 so it can't bounce around a power of 2 creating arrays
      S[l] = null;
      l--;
    }
    //println("after:", this+"\n");
    o.pq = null; // no leaks please
    
  }
  void clear() {
    B = new PQNode[32][];
    S = new PQNode[32][];
    size = 0;
    l = 0;
  }
  PQNode<M, V> biggest() {
    return B[0][0];
  }
  PQNode<M, V> smallest() {
    return S[0][0];
  }
  int size() {
    return size;
  }
}
class PQNode<M extends Comparable<? super M>, V> {
  int Bp, Sp;
  byte Bl, Sl;
  PQ<M, V> pq;
  M m;
  V v;
  PQNode(PQ<M, V> pq, M m, V v) {
    this.pq = pq;
    this.m = m;
    this.v = v;
  }
  void remove() {
    pq.remove(this);
  }
  
  void Bswap(PQNode<M, V> n) {
    int nBp = n.Bp;
    byte nBl = n.Bl;
    n.Bl = Bl;
    n.Bp = Bp;
    Bl = nBl;
    Bp = nBp;
    pq.B[  Bl][  Bp] = this;
    pq.B[n.Bl][n.Bp] = n;
  }
  void Sswap(PQNode<M, V> n) {
    int nSp = n.Sp;
    byte nSl = n.Sl;
    n.Sl = Sl;
    n.Sp = Sp;
    Sl = nSl;
    Sp = nSp;
    pq.S[  Sl][  Sp] = this;
    pq.S[n.Sl][n.Sp] = n;
  }
  
  String toString() {
    return m+"";
  }
}
