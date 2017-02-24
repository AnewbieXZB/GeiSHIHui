package com.example.s.why_no.bean;

/**
 * Created by S on 2016/11/2.
 */

public class Goods implements Comparable{

    /**
     * uid = 商品id
     * id = 唯一id
     uname = 商品名称
     uimg = 首图连接
     details = 商品连接
     classification = 分类
     extension = 推广链接
     price = 价格
     volume = 销售量
     wangwang = 旺旺
     wwid = 汪汪id
     shopname = 商家名称
     platform = 平台
     yhid = 优惠卷id
     total = 优惠卷总量
     surplus = 优惠卷剩余量
     denomination = 优惠额度
     starttime = 开始时间
     endtime = 结束时间
     yhurl = 优惠连接
     tgurl = 推广链接
     ification = 分类
     top = 热度
     heat = 置顶
     off = 下架
     roll = 折扣价
     minimum = 最低价
     discount = 优惠券的面值
     */
    public String uid  = "";
    public int id;
    public String uname = "";
    public String uimg = "";
    public String details = "" ;
    public String classification = "";
    public float price;
    public int volume ;
    public String wangwang= "";
    public String wwid= "";
    public String shopname = "";
    public String platform= "";
    public String yhid= "";
    public String total = "";
    public String surplus= "";
    public String denomination= "";
    public String starttime= "" ;
    public String endtime= "";
    public String yhurl= "";
    public String tgurl= "";
    public String ification= "";
    public String top = "";
    public String heat= "";
    public String off= "";
    public String extension ="";
    public float roll ;
    public float minimum ;
    public int discount;
    public String text;

    public int vis = 0;// 标记是否处于能删除状态   1 显示 0 删除
    public boolean isCheck = false;//标记是否能删除 true删除 false不删除

    public int getVis() {
        return vis;
    }

    public void setVis(int vis) {
        this.vis = vis;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "classification='" + classification + '\'' +
                ", uid='" + uid + '\'' +
                ", id=" + id +
                ", uname='" + uname + '\'' +
                ", uimg='" + uimg + '\'' +
                ", details='" + details + '\'' +
                ", price=" + price +
                ", volume=" + volume +
                ", wangwang='" + wangwang + '\'' +
                ", wwid='" + wwid + '\'' +
                ", shopname='" + shopname + '\'' +
                ", platform='" + platform + '\'' +
                ", yhid='" + yhid + '\'' +
                ", total='" + total + '\'' +
                ", surplus='" + surplus + '\'' +
                ", denomination='" + denomination + '\'' +
                ", starttime='" + starttime + '\'' +
                ", endtime='" + endtime + '\'' +
                ", yhurl='" + yhurl + '\'' +
                ", tgurl='" + tgurl + '\'' +
                ", ification='" + ification + '\'' +
                ", top='" + top + '\'' +
                ", heat='" + heat + '\'' +
                ", off='" + off + '\'' +
                ", extension='" + extension + '\'' +
                ", roll=" + roll +
                ", minimum=" + minimum +
                ", discount=" + discount +
                ", text='" + text + '\'' +
                ", vis=" + vis +
                ", isCheck=" + isCheck +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        Goods goods = (Goods) o;
        if (goods.roll > this.roll) {
            return 1;
        } else if (goods.roll < this.roll) {
            return -1;
        } else {
            return 0;
        }
    }
}
