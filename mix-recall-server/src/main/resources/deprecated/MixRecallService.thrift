namespace java com.ifeng.recom.mixrecall.thrift.servicequality

 struct QualityPoolRequest {
 	1: required string uid,
 	2: required string flowType,
 	3: optional string jsonExt,
 	4: optional i32 size
 }

 service MixRecallService{
 	binary doRecall(1:string uid)

 	binary recallQualityPool(1:QualityPoolRequest request),
 	binary recallMix(1:string uid)
    binary recallExplore(1:string uid)
    binary recallVideo(1:string uid)

    binary getRecomDocList(1:string mixRequestInfo)

    list<string> recallPush(1:string uid)

    binary standby(1:string para)
 	string ping()
 }