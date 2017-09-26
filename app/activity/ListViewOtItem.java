package kr.co.infonetworks.www.app.activity;

/**
 * Created by andy on 2016. 6. 12..
 */
public class ListViewOtItem {

    private String title;
    private String cnt;
    private String date;
    private String display_data; //[rnum]type-category-region
    private String rnum; //key
    private String reg_id; //등록자id
    private String company; //업소명 + 등록자명

    private Boolean imageYN;

    public ListViewOtItem() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCnt() {
        return cnt;
    }

    public void setCnt(String cnt) {
        this.cnt = cnt;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDisplay_data() {
        return display_data;
    }

    public void setDisplay_data(String display_data) {
        this.display_data = display_data;
    }

    public String getRnum() {
        return rnum;
    }

    public void setRnum(String rnum) {
        this.rnum = rnum;

    }

    public Boolean getImageYN() {
        return imageYN;
    }

    public void setImageYN(Boolean imageYN) {
        this.imageYN = imageYN;
    }

    public String getReg_id() {
        return reg_id;
    }

    public void setReg_id(String reg_id) {
        this.reg_id = reg_id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
