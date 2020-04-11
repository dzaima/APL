#!/usr/bin/env node
fs=require('fs');
if (!process.argv[2]) {
  process.argv[2] = 'kbs.txt';
}
function chunks(arr, len) {
  let res = [];
  let i = 0;
  while(i < arr.length) {
    res.push(arr.slice(i, i+len));
    i+= len;
  }
  return res;
}
chunks("hello!", 2);

function parse(chr, mods) {
  let o = {chr: chr};
  if (mods[chr]) {
    for (let mod of mods[chr]) {
      if (mod === 'sd') o.sd = true;
      else {
        let [key, val] = mod.split(/(?<!=)=/);
        if (key === 'rep') {
          o.rep = parseInt(val);
          if (o.rep != val) throw new Error("couldn't parse number "+val);
        }
        else if (key === 'spec') o.spec = val;
        else if (key === 'goto') o.goto = val;
        else if (key === 'chr') o.chr = val;
        else if (key === 'type') o.type = val;
        else throw new Error("unknown mod "+mod);
      }
    }
  }
  return o;
}

fs.readFile(process.argv[2], 'utf8', (e,c) => {
  let arr = c.split('\n');
  let mods = {};
  let layoutArr = [];
  let layouts = {};
  while (arr.length) {
    let header = arr.shift();
    if (header.startsWith("LAYOUT ")) {
      let [_, name, size, mainName, fullName] = header.split(" ");
      let [w, h] = size.split("×").map(c=>parseInt(c));
      let o = {w, h, name, obj: {mainName, fullName}};
      layouts[name] = o;
      layoutArr.push(o);
    }
    if (header.startsWith("MOD ")) {
      let [_, chr, ...data] = header.split(" ");
      if (!mods[chr]) mods[chr] = [];
      for (let cd of data) mods[chr].push(cd);
    }
    if (header.startsWith("PART ")) {
      arr.shift(); // column names
      let [_, name, layoutName] = header.split(" ");
      let layout = layouts[layoutName];
      let w = layout.w;
      let h = layout.h;
      let ps = arr.slice(0, h).map(c=>chunks(c, w+1).map(n=>n.slice(0, w)));
      console.table(ps);
      let res = [];
      for (let y = 0; y < h; y++) {
        let row = [];
        let drow = ps[y];
        for (let x = 0; x < w; x++) {
          row.push({
            col: drow[3][x],
            def  : parse(drow[0][x], mods),
            up   : parse(drow[1][x], mods),
            down : parse(drow[2][x], mods),
            left : parse(drow[4][x], mods),
            right: parse(drow[5][x], mods),
          });
        }
        res.push(row);
      }
      layout.obj[name] = res;
    }
  }
  
  for (let layout of layoutArr) {
    fs.writeFile(layout.name+".json", 
      JSON.stringify({
        "colors": ["ff101010", "FF134ADB", "FF282828", "FF353535"],
        "defcol": 1,
        ...layout.obj
      }), e=>console.log(e)
    );
  }
});