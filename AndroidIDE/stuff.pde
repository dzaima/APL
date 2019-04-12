Iterable<Character> sit(final String s) { // String iterator
  return new Iterable() {
    public Iterator<Character> iterator() {
      return new Iterator<Character> () {
        int p = 0;
        public boolean hasNext() {
          return p < s.length();
        }
        public Character next() {
          return s.charAt(p++);
        }
      };
    }
  };
}
