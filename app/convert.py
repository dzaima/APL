#!/usr/bin/python3
# horrible code that shouldn't exist
from subprocess import call
import glob
import os, re

os.system('rm -rf ./APL/src')
os.system('rm -rf ./build/')
os.system('cp -rf ../src ./APL/src')

print('copied')

rtp='Float[][][]'
for filename in glob.iglob('APL/src/APL/**/*.java', recursive=True):
  with open(filename, 'r') as myfile:
    data=myfile.read()
  data = re.sub(' var ', ' '+rtp+' ', data)
  data = re.sub(r'\(var ', '('+rtp+' ', data)
  with open(filename, 'w') as myfile:
    myfile.write(data)
print('replaced')
for i in range(5):


  os.system('APL/build -Xdiags:verbose 2> err');
  count = 0
  with open('err', 'r') as myfile:
    for line in myfile:
      if ((' cannot be converted to '+rtp) in line):
        name, ln, _1, _2, rest = line.split(':');
        ln = int(ln)
        # error example:
        # APL/src/APL/Main.java:182: error: incompatible types: Token cannot be converted to Float[][][]
        # path:ln:error:IT: Token cannot be converted to Float[][][]
        tp = rest.split(' cannot be converted to ')[0];
        if ('Float' not in tp):
          count+= 1;
          with open(name, 'r') as myfile:
            data=myfile.read()
          data = data.split('\n')
          data[ln-1] = data[ln-1].replace(rtp, tp)
          data = '\n'.join(data)
          with open(name, 'w') as myfile:
            myfile.write(data)
      # if (('error: incompatible types: cannot infer type arguments for') in line): TODO
        
  print("error count:", count)
os.system('./build8');