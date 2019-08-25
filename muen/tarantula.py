
import xlrd
import math
# Give the location of the file
loc = (r'F:\Document\article\journal\accuracy.xlsx')

wb = xlrd.open_workbook(loc)
sheet = wb.sheet_by_index(0)
print(sheet.cell_value(1, 0))


total = sheet.cell_value(1,2)
F = sheet.cell_value(1,3)
P = sheet.cell_value(1,4)
print(total)
i = 1

import numpy as np
from pyitlib import discrete_random_variable as drv
#X = np.array((0,0))
#print(drv.entropy(X))

res = ""
i=1
for i in range(sheet.nrows -2):
    j=0
    while j<1:

        ftop = sheet.cell_value(int(i+1),int(j))

        j = j+1
        ptof = sheet.cell_value(int(i+1) , int(j))
        if ftop==0: tarantula =0
        else:
             tarantula = (ftop/F) / ((ftop/F)+(ptof/P))

        res = res+ str(tarantula) + " , "




print(res)
# Extracting number of rows
#print(sheet.nrows)
#print(res)


