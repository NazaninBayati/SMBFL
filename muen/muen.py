
# Program to extract number
# of rows using Python
import xlrd
import math
# Give the location of the file
loc = (r'F:\Document\article\journal\results\cal\scores.xlsx')

wb = xlrd.open_workbook(loc)
sheet = wb.sheet_by_index(0)
print(sheet.cell_value(1, 0))


total = sheet.cell_value(1,2)
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
        mu1 =(ftop / total)
        mu2 =(ptof / total)

        m11 = mu1 * (math.log2(mu1 + 0.0000001))
        m22 = mu2 * (math.log2(mu2 + 0.0000001))
        muen = (-m11+m22 )
        if (muen > 0):
            print("******************line number is ***************")
            print(i+2)
            print("************************************************")
      #  X = np.array((mu1))
       # mu11 = drv . entropy(X)
       # Y = np.array((mu2))
       # mu22 = drv.entropy(Y)
       # muen = mu11 - mu22


        res = res + str( muen) +" , "


print(res)
# Extracting number of rows
#print(sheet.nrows)
#print(res)





