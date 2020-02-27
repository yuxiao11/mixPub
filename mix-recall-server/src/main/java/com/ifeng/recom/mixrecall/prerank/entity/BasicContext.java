package com.ifeng.recom.mixrecall.prerank.entity;

import java.io.Serializable;

public class BasicContext implements Serializable {
	
	private static final String SEPARATOR = "\001"; //字符串类型的分隔符
	private static final String STRING_PLACEHOLDER = "-"; //字符串类型的占位符
	private static final long serialVersionUID = 1052700151210093851L;

	//渠道号
	private String publishid;
	//流量来源（站内、站外等）
	private String source;
	//网络状态2g/4g/wifi
	private String net;
	//频道（当前频道，目前只有头条）
	private String channel;
	//搜索关键字
	private String keyword;
	//ab测试标识
	private String flowtag;
	//加载时间戳
	private String ts;
	//操作类型（下拉、上拉、点击、编辑推荐等）
	private String optype;
	//此次应召回item数
	private String maxitems;
	//终端品牌+型号（型号，品牌）
	private String ua;
	//操作系统+版本号
	private String mos;
	//客户端版本号
	private String softversion;
	//用户id
	private String uid;
	//检索批次id
	private String sid;
	//一级城市
	private String loc1;
	//二级城市
    private String loc2;
	//三级城市
	private String loc3;

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	//位置
	private String pos;

	public String getEngine() {
		return engine;
	}

	public void setEngine(String engine) {
		this.engine = engine;
	}

	//推荐模型
	private String engine;

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	//召回通道
	private String reason;

	public int getPullNum() {
		return pullNum;
	}

	public void setPullNum(int pullNum) {
		this.pullNum = pullNum;
	}

	//下拉次数
	private int pullNum;

	public String getPublishid() {
		return publishid;
	}

	public void setPublishid(String publishid) {
		this.publishid = publishid;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getNet() {
		return net;
	}

	public void setNet(String net) {
		this.net = net;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getFlowtag() {
		return flowtag;
	}

	public void setFlowtag(String flowtag) {
		this.flowtag = flowtag;
	}

	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}

	public String getOptype() {
		return optype;
	}

	public void setOptype(String optype) {
		this.optype = optype;
	}

	public String getMaxitems() {
		return maxitems;
	}

	public void setMaxitems(String maxitems) {
		this.maxitems = maxitems;
	}

	public String getUa() {
		return ua;
	}

	public void setUa(String ua) {
		this.ua = ua;
	}

	public String getMos() {
		return mos;
	}

	public void setMos(String mos) {
		this.mos = mos;
	}

	public String getSoftversion() {
		return softversion;
	}

	public void setSoftversion(String softversion) {
		this.softversion = softversion;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}
    public  String getLoc1() {
        return loc1;
    }
    public void   setLoc1(String loc1) {
        this.loc1 = loc1;
    }
    public String getLoc2() {
        return loc2;
    }
    public void   setLoc2(String loc2) {
        this.loc2 = loc2;
    }
    public String getLoc3() {
        return loc3;
    }
    public void   setLoc3(String loc3) {
        this.loc3 = loc3;
    }

	@Override
	public String toString() {
        StringBuilder sb = new StringBuilder();
		sb.append(publishid).append(SEPARATOR);
		sb.append(source).append(SEPARATOR);
		sb.append(net).append(SEPARATOR);
		sb.append(channel).append(SEPARATOR);
		sb.append(keyword).append(SEPARATOR);
		sb.append(flowtag).append(SEPARATOR);
		sb.append(ts).append(SEPARATOR);
		sb.append(optype).append(SEPARATOR);
		sb.append(maxitems).append(SEPARATOR);
		sb.append(ua).append(SEPARATOR);
		sb.append(mos).append(SEPARATOR);
		sb.append(softversion).append(SEPARATOR);
		sb.append(uid).append(SEPARATOR);
		sb.append(sid).append(SEPARATOR);
        sb.append(loc1).append(SEPARATOR);
        sb.append(loc2).append(SEPARATOR);
        sb.append(loc3).append(SEPARATOR);
        return sb.toString();
	}

	private String [] basicContexts = null;
    public String[] getBasicTexts() {
    	if (basicContexts != null) {
    		return basicContexts;
		}
		basicContexts = this.toString().split(SEPARATOR);
    	return basicContexts;
//        return this.toString().split(SEPARATOR);
    }

	//TEST
	public static void main(String[] args) {
		System.out.println(new BasicContext().getBasicTexts().length);
	}
}
