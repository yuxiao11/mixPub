##推荐召回
---------------------------------------------------------------------------------------------------------------------
| 1) ServiceLogUtil.debug("cotagvideo {} cost:{}", uid, cost)| ServiceLogUtil.debug("JpTag {} cost:{}", tag, cost);   |
| 2) ServiceLogUtil.debug("UserProfile {} {} cost:{}", uid, logType, cost);                                           |
---------------------------------------------------------------------------------------------------------------------
      ServiceLogUtil目前统一成这两种格式，[channel uid|tag cost:{}]  空格为分隔符，不需多余的 逗号、冒号