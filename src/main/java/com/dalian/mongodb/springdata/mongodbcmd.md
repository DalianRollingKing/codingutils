# mongodb 命令行
## 拟定数据结构
{
    "id":1,
    "name":"rollingking",
    "location":"hangzhou",
    "partner":[
        {"name":"a wei","job":"developer"},
        {"name":"a hao","job":"driver"}
    ]
}
## 查询
db.collection.find({})
1. 等于条件查询
db.collection.find({"name":"rollingking"})
复杂的等于条件
db.collection.find({"partner.name":"a wei")
数组的等于条件细分
苛刻的匹配
db.collection.find({"partner":{$elementMatch:{"name":"a wei","job":"driver"}) 此时查询不到匹配的文档
宽松的匹配
db.collection.find({"partner.name":"a wei","job":"driver"}) 此时会匹配数组里有name=a wei和job=driver的文档
而不是必须要满足该字段数组至少一个元素同时满足name为a wei并且job为driver的。
$or和$in的问题
相同字段执行等于检查时，建议使用 $in 而不是 $or
例如db.collection.find({"id":{$in:[1,2,3]}}) 
其实db.collection.find({$or:[{"id":1},{"id":2},{"id":3}])
2. 不等查询
$lt less than
$lte less than or equal
$gt greater than
$gte grater than or equal
$ne not equal
db.collection.find({"id":{$gte:50}})

## 返回结果过滤
一个 projection（映射） 不能 同时 指定包括和排除字段，除了排除 _id 字段。 在 显式包括 字段的映射中，_id 字段是唯一一个您可以 显式排除 的。
db.collection.find( { name: "jinwei" }, { name: 1, location: 1 } ) 结果将返回 name，location，_id字段
db.collection.find( { name: "jinwei" }, { name: 1, location: 1 ,_id : 0 } ) 结果将不返回 _id字段
1.嵌入字段的过滤
db.collection.find( { name: "jinwei" }, { name: 1, location: 1 ，partner.name:1} ) 将显示_id, name，localtion 以及partner，但partner只包含name字段
db.collection.find( { name: "jinwei" }, { name: 0, location: 0，partner.name:0} ) 同理将排除这些字段

## 查询null
 $type and $exists 
 
## 更新
$set ， $currentDate:
db.collection.update(
   { "partner.name": "a wei" },
   {
     $set: { "partner.name": "a xiao, id: 100,  },
     $currentDate: { lastModified: true }
   },
   { multi: true }
)
multi -> 为替换所有

## 聚合
db.collection.aggregate([{project...},{match...},{group...}])
project
db.collection.aggregate([{$project:{_id:0, name:1, location:1 }}]) 类似于find projection
match 
db.collection.aggregate(
    [
         {
              $match: {"id": {$gte: 5}}
         }
    ]
   )
group 必须有_id 这一列
$sum
从集合中的所有文档中求出定义的值。
db.mycol.aggregate([{$group : {_id : "$by_user", num_tutorial : {$sum : "$likes"}}}])
$avg
计算集合中所有文档的所有给定值的平均值。
db.mycol.aggregate([{$group : {_id : "$by_user", num_tutorial : {$avg : "$likes"}}}])
$min
从集合中的所有文档获取相应值的最小值。
db.mycol.aggregate([{$group : {_id : "$by_user", num_tutorial : {$min : "$likes"}}}])
$max
从集合中的所有文档获取相应值的最大值。
db.mycol.aggregate([{$group : {_id : "$by_user", num_tutorial : {$max : "$likes"}}}])
$sort
db.collection.aggregate([{$sort: {"id": 1}}]) 1 升序 -1 降序

limit
db.collection.aggregate([{$limit: 2}]) 只返回前两条
skip 
db.collection.aggregate([{$skip: 1}]) 跳过第一条