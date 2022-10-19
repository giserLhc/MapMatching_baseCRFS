# encoding: utf-8
import csv
import arcpy

# 点数据
pointShp = "E:\\Desktop\MapMatch\\demo2.shp"
# 线数据
lineShp = "E:\\Desktop\MapMatch\\东莞市路网.shp"

arcpy.Near_analysis(pointShp, lineShp, "", "true")
rows = arcpy.SearchCursor(pointShp)

res = []
num = 0
for row in rows:
    num += 1
    res.append([row.NEAR_X, row.NEAR_Y])
    print num
     if row.Near_DIST>0:
         cur = arcpy.InsertCursor(pointShp)
         new_row = cur.newRow()
    
        newPoint = arcpy.Point()
        newPoint.X = row.NEAR_X
         newPoint.Y = row.NEAR_Y
         pointGeometry = arcpy.PointGeometry(newPoint)
         new_row.shape = pointGeometry
    
         #把不相交点的属性赋值给对应的新点
         new_row.NEAR_DIST=row.NEAR_DIST
         new_row.NEAR_X=row.NEAR_X
         new_row.NEAR_Y = row.NEAR_Y
         cur.insertRow(new_row)  # 插入新点
print res
csvFile2 = open('res.csv', 'wb')
writer = csv.writer(csvFile2)
m = len(res)
writer.writerow(['X', 'Y'])
for i in range(m):
    writer.writerow(res[i])
