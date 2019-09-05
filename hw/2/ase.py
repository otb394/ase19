import re
import math


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
        self.lo = min(self.col_count, self.lo)
        self.high = max(self.col_count, self.high)
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


class Col(object):
  def __init__(self, oid,col,text):
    self.oid = oid
    self.col = col
    self.text = text
    self.num = Num()


class Tbl(object):
    def __init__(self):
        self.oid = 1
        self.cols = []
        self.rows = []
        self.index = 1
        self.arr_to_skip = []


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


    def createColumns(self, lst):
        for colname in lst:
          i = lst.index(colname)
          if(colname[0]=='?'):
            self.arr_to_skip.append(i)
          else:
            self.index+=1
            self.cols.append(Col(self.index, i, colname))


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
          col.num.__add_num__(row.cells[i])


    def print_final_structure(self):
        print("PART 3")
        print("t.cols")
        for index, col in enumerate(self.cols):
            print('| ',index+1)
            print("|  | col: ", (index+1))
            print("|  | hi: ", self.cols[index].num.high)
            print("|  | lo: ", self.cols[index].num.lo)
            print("|  | m2: ", self.cols[index].num.m2)
            print("|  | mu: ", self.cols[index].num.mu)
            print("|  | sd: ", self.cols[index].num.sd)
            print("|  | oid: ", self.cols[index].oid)
            print("|  | txt: ", self.cols[index].text)
        print("t.oids: ", self.oid)
        print("t.rows")
        for index, row in enumerate(self.rows):
            if(not row.skipped_row):
                print("| ", (index+1))
                print("|  | cells")
                for index,cell in enumerate(row.cells):
                    print("|  | |  ",(index + 1), ":",str(cell))
                print("|  | oid: %d" % row.oid)


    def readAndCreate(self):
        s="""
        $cloudCover, $temp, ?$humid, <wind,  $playHours
        1,         2,    3,     4,      5   
        0,           85,    85,     0,      0
        0,           80,    90,     10,     0
        0,          83,    86,     0,      4
        0,         70,    96,     0,      3
        0,         65,    70,     20,     0
        0,          64,    65,     15,     5
        0,           72,    95,     0,      0
        0,           69,    70,     0,      4
        0,          75,    80,     0,      3
        5,           75,    70,     18,     4
        2,          72,    83,     15,     5
        3,          81,    75,     0,      2
        4,         71,    91,     15,     0
        """
        print("PART 1")
        for lst in self.fromString(s):
            print(lst)
        print()
        print("PART 2")
        for index, lst in enumerate(self.fromString(s)):
            if index == 0:
                self.createColumns(lst)
            else:
                self.createRows(lst)
        print_col = []
        for col in self.cols:
            print_col.append(col.text)
        print(print_col)
        
        for index , row in enumerate(self.rows):
            if row.skipped_row:
                print ("E> skipping line ",(index+1))
            else:
                print(row.cells)
        print()
        self.find()
        return self


if __name__=="__main__":
    
    Tbl().readAndCreate().print_final_structure()
