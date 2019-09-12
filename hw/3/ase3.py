import re
import math

class Col(object):
  def __init__(self, oid,col,text,col_type):
    self.oid = oid
    self.col = col
    self.text = text
    self.num = Num()
    self.sym = Sym()
    self.col_type = col_type

class My(object):
  def __init__(self):
      self.goals = []
      self.xs = []
      self.nums = []
      self.syms = []
      self.xsyms = []
      self.w = []

class Sym(object):
  def __init__(self):
    self.sym_map = {}
    self.total_count = 0
    self.mode = ''
    self.mode_count = 0
    self.words = []

  def __add_sym__(self, symbol):
    self.total_count+=1
    if (symbol in self.sym_map):
      total = self.sym_map[symbol]+1
      self.sym_map[symbol] = total
      self.words.append(symbol)
      if(total > self.mode_count):
        self.mode_count =total
        self.mode = symbol
    else:
      self.sym_map[symbol] = 1
      self.words.append(symbol)
        
  def sym_ent(self):
    entropy=0
    for i in self.sym_map:
        p = self.sym_map[i]/self.total_count
        entropy -= p*math.log(p)/math.log(2)
    return entropy

class Num(object):
    def __init__(self):
        self.sd = 0
        self.m2 = 0
        self.mu = 0
        self.lo = 10 ** 32
        self.high = -1 * (self.lo)
        self.col_count = 0

    def __add_num__(self, n):
        self.col_count += 1
        self.lo = min(n, self.lo)
        self.high = max(n, self.high)
        delta = n - self.mu;
        self.mu += delta / self.col_count
        self.m2 += delta * (n-self.mu)
        self.sd = self._NumSd()

    def _NumSd(self):
        if self.m2 < 0 or self.col_count < 2:
            return 0;
        else:
            return math.sqrt((self.m2 / (self.col_count - 1)));

class Row(object):
  def __init__(self,oid,cells,bool_skip):
    self.oid = oid
    self.cells = cells
    self.cooked = []
    self.dom = 0
    self.skipped_row = bool_skip

class Abcd(object):
    def __init__(self):
        self.a = {}
        self.b = {}
        self.c = {}
        self.d = {}
        self.known = {}
        self.yes = 0
        self.no = 0

    def abcd1(self, want, got):
        if (want not in self.known):
            self.known[want] = 1
            self.a[want] = self.yes + self.no
            self.b[want] = 0
            self.c[want] = 0
            self.d[want] = 0
        
        if (got not in self.known):
            self.known[got] = 1
            self.a[got] = self.yes + self.no
            self.b[got] = 0
            self.c[got] = 0
            self.d[got] = 0

        if (want == got):
            self.yes+=1
        else:
            self.no+=1
        for x in self.known:
            if (want == x):
                if (want == got):
                    self.d[x]+=1
                else:
                    self.b[x]+=1
            else:
                if (got == x):
                    self.c[x]+=1
                else:
                    self.a[x]+=1

    def report(self):
        p = " %4.2f"
        q = " %4s"
        r = " %5s"
        s = " |"
        ds = "----"
        formatString = r+s+r+s+r+s+r+s+r+s+r+s+r+s+q+s+q+s+q+s+q+s+q+s+q+s+" class"
        print(formatString % ("db","rx","num","a","b","c","d","acc","pre","pd","pf","f","g"))
        formatString = r+ s+r+s+r+s+r+s+r+s+r+s+r+s+q+s+q+s+q+s+q+s+q+s+q+s+"-----"
        print(formatString % (ds,ds,ds,ds,ds,ds,ds,ds,ds,ds,ds,ds,ds))
        for x in self.known:
            pd = 0
            pf = 0
            pn = 0
            prec = 0
            g = 0
            f = 0
            acc = 0
            a = self.a[x]
            b = self.b[x]
            c = self.c[x]
            d = self.d[x]
            if (b+d > 0):
                pd = d/(b+d)
            if (a+c > 0):
                pf = c/(a+c)
            if (a+c > 0):
                pn = (b+d)/(a+c)
            if (c+d > 0):
                prec = d/(c+d)
            if (1-pf+pd > 0):
                g = 2*(1-pf)*pd/(1-pf+pd)
            if (prec+pd > 0):
                f = 2*prec*pd/(prec+pd)
            if (self.yes + self.no > 0):
                acc = self.yes / (self.yes + self.no)
            formatString = r+s+   r+s+ r+s+       r+s+r+s+r+s+r+s+p+s+p+s+ p+s+p+s+p+s+p+s+" %s"
            print(formatString % ("data","rx",self.yes+self.no, a, b, c, d, acc, prec, pd, pf, f, g, x))

class Tbl(object):
    def __init__(self):
        self.oid = 1
        self.cols = []
        self.rows = []
        self.index = 1
        self.arr_to_skip = []
        self.my = My()

    def compiler(self, x):
        "return something that can compile strings of type x"
        try:
            int(x); return int
        except:
            try:
                float(x); return float
            except ValueError:
                return str

    def string(self, s):
        "read lines from a string"
        for line in s.splitlines():
            yield line

    def row(self, src, sep=",", doomed=r'([\n\t\r ]|#.*)'):
        "convert lines into lists, killing whitespace and comments"
        for line in src:
            line = line.strip()
            line = re.sub(doomed, '', line)
            if line:
                yield line.split(sep)

    def cells(self, src):
        "convert strings into their right types"
        oks = None
        for n, cells in enumerate(src):
            if n == 0:
                yield cells
            else:
                oks = [self.compiler(cell) for cell in cells]
                yield [f(cell) for f, cell in zip(oks, cells)]

    def fromString(self, s):
        "putting it all together"
        for lst in self.cells(self.row(self.string(s))):
            yield lst

    def get_col_type(self, ch, index):
      if(ch == '>'):
        self.my.goals.append(index+1)
        self.my.nums.append(index+1)
        return "Num"
      elif(ch == '<'):
        self.my.goals.append(index+1)
        self.my.w.append(index+1)
        self.my.nums.append(index+1)
        return "Num"
      elif(ch == '$'):
        self.my.nums.append(index+1)
        self.my.xs.append(index+1)
        self.my.xsyms.append(index + 1)
        return "Num"
      elif(ch == '!'):
          self.my.goals.append(index+1)
          self.my.syms.append(index+1)
          return "Sym"
      else:  
        self.my.syms.append(index+1)
        self.my.xs.append(index + 1)
        self.my.xsyms.append(index+1)     
        return "Sym"

    def createColumns(self, lst):
        for colname in lst:
          i = lst.index(colname)
          if(colname[0]=='?'):
            self.arr_to_skip.append(i)
          else:
            self.index+=1
            col_type = self.get_col_type(colname[0], i)
            self.cols.append(Col(self.index, i+1, colname, col_type))

    def createRows(self, lst):
        self.index += 1

        for i in self.arr_to_skip:
          if i<len(lst):
            del lst[i]

        if len(lst)==len(self.cols):
          self.rows.append(Row(self.index, lst, False))
        else:
          self.rows.append(Row(self.index, None, True))

    def find(self):
      for i, col in enumerate(self.cols):
        for row in (self.rows):
          if col.col_type == "Num":
            col.num.__add_num__(row.cells[i])
          elif (col.col_type == "Sym"):
            col.sym.__add_sym__(row.cells[i])

    def print_final_structure(self):
      print("Task3: modify class Tbl")
      print("t.cols")
      for index, col in enumerate(self.cols):
          print("| ", col.col)
          if col.col_type == "Sym":
            print("|  | add: Sym1")
            print("|  | cnt")
            for symbol in col.sym.sym_map:
              print("|  |  |{} :{} ".format(symbol, col.sym.sym_map[symbol]))
            print("|  | col: ", col.col)
            print("|  | mode: ", col.sym.mode)
            print("|  | most: ", col.sym.mode_count)
            print("|  | n: ", col.sym.total_count)
            print("|  | oid: ", self.cols[index].oid)
            print("|  | txt: ", self.cols[index].text)
          elif col.col_type == "Num":
            print("|  | add: Num1")
            print("|  | col: ", col.col)
            print("|  | hi: ", self.cols[index].num.high)
            print("|  | lo: ", self.cols[index].num.lo)
            print("|  | m2: ", self.cols[index].num.m2)
            print("|  | mu: ", self.cols[index].num.mu)
            print("|  | n: ", )
            print("|  | oid: ", self.cols[index].oid)
            print("|  | sd: ", self.cols[index].num.sd)
            print("|  | txt: ", self.cols[index].text)
      
      print("t.my")
      print("| class: %d" % (len(self.cols)+len(self.arr_to_skip)))
      print("| goals:")
      for k in self.my.goals:
          print("|  | %d " % k)
      print("| nums:")
      for k in self.my.nums:
          print("|  | %d " % k)
      print("| syms:")
      for k in self.my.syms:
          print("|  | %d " % k)
      print("| w:")
      for k in self.my.w:
          print("|  |  | {} : {}".format(k, -1))
      print("| xnums:")
      print("| xs:")
      for k in self.my.xs:
          print("|  | %d " % k)
      print("| xsyms:")
      for k in self.my.xsyms:
          print("|  | %d " % k)

    def readAndCreate(self):
        s="""
        outlook, ?$temp,  <humid, wind, !play
        rainy, 68, 80, FALSE, yes # comments
        sunny, 85, 85,  FALSE, no
        sunny, 80, 90, TRUE, no
        overcast, 83, 86, FALSE, yes
        rainy, 70, 96, FALSE, yes
        rainy, 65, 70, TRUE, no
        overcast, 64, 65, TRUE, yes
        sunny, 72, 95, FALSE, no
        sunny, 69, 70, FALSE, yes
        rainy, 75, 80, FALSE, yes
        sunny, 75, 70, TRUE, yes
        overcast, 72, 90, TRUE, yes
        overcast, 81, 75, FALSE, yes
        rainy, 71, 91, TRUE, no
        """
#        print("PART 1")
#        for lst in self.fromString(s):
#            print(lst)
#        print()
#        print("PART 2")
        for index, lst in enumerate(self.fromString(s)):
            if index == 0:
                self.createColumns(lst)
            else:
                self.createRows(lst)
#        print_col = []
#        for col in self.cols:
#            print_col.append(col.text)
#        print(print_col)
#        
#        for index , row in enumerate(self.rows):
#            if row.skipped_row:
#                print ("E> skipping line ",(index+1))
#            else:
#                print(row.cells)
#        print()
        self.find()
        return self

def _abcd():
    abcd = Abcd()
    for i in range(6):
        abcd.abcd1("yes","yes")
    for i in range(2):
        abcd.abcd1("no","no")
    for i in range(5):
        abcd.abcd1("maybe","maybe")
    abcd.abcd1("maybe","no")
    print("Task2: Abcd")
    abcd.report()

def _symTest():
    sym = Sym()
    sym.__add_sym__('a')
    sym.__add_sym__('a')
    sym.__add_sym__('a')
    sym.__add_sym__('a')
    sym.__add_sym__('b')
    sym.__add_sym__('b')
    sym.__add_sym__('c')
    print("Task1: Sym")
    print("Input: aaaabbc")
    print("Entropy = %f" % sym.sym_ent())

if __name__=="__main__":
    
    _symTest()
    print()
    print()
    _abcd()
    print()
    print()
    Tbl().readAndCreate().print_final_structure()


