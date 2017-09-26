package kr.co.infonetworks.www.app.activity;

/**
 * Created by andy on 2016. 6. 12..
 */
public class ListViewNotifyItem {
    private String rnum; //key
    private String refer_rnum;
    private String origin_rnum;

    private String req_type;
    private String reqtype_name;
    private String reg_time; //예) 09:47:53
    private String reg_name; //예) 홍길동 실장 / AAAA부동산
    private String display_data; //예) 주상복합-전세..←행복한

    private int icon;

    public ListViewNotifyItem() {

    }

    public String getRnum() {
        return rnum;
    }

    public void setRnum(String rnum) {
        this.rnum = rnum;
    }

    public String getRefer_rnum() {
        return refer_rnum;
    }

    public void setRefer_rnum(String refer_rnum) {
        this.refer_rnum = refer_rnum;
    }

    public String getOrigin_rnum() {
        return origin_rnum;
    }

    public void setOrigin_rnum(String origin_rnum) {
        this.origin_rnum = origin_rnum;
    }

    public String getReqtype_name() {
        return reqtype_name;
    }

    public void setReqtype_name(String reqtype_name) {
        this.reqtype_name = reqtype_name;
    }

    public String getDisplay_data() {
        return display_data;
    }

    public void setDisplay_data(String display_data) {
        this.display_data = display_data;
    }

    public String getReg_time() {
        return reg_time;
    }

    public void setReg_time(String reg_time) {
        this.reg_time = reg_time;
    }

    public String getReg_name() {
        return reg_name;
    }

    public void setReg_name(String reg_name) {
        this.reg_name = reg_name;
    }

    public String getReq_type() {
        return req_type;
    }


    public void setReq_type(String req_type) {
        this.req_type = req_type;
    }

    public int getIcon(){return icon;}

    public void setIcon(int icon) {
        this.icon = icon;
    }

}
