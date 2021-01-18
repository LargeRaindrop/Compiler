package miniplc0java.analyser;

public class Global {
    Integer isConst;
    Integer cnt;
    String item;

    public Global(Integer isConst){
        this.isConst = isConst;
        this.cnt = 0;
        this.item = null;
    }

    public Global(Integer isConst, Integer cnt, String item){
        this.isConst = isConst;
        this.cnt = cnt;
        this.item = item;
    }

    public Integer getIsConst() {
        return isConst;
    }

    public void setIsConst(Integer isConst) {
        this.isConst = isConst;
    }

    public Integer getCnt() {
        return cnt;
    }

    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "Global: " +
                "isConst=" + isConst +
                ", count=" + cnt +
                ", items=" + item;
    }
}
