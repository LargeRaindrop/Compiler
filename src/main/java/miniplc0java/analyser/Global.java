package miniplc0java.analyser;

public class Global {
    Integer isConst;
    Integer cnt;
    String items;

    public Global(Integer isConst){
        this.isConst = isConst;
        this.cnt = 0;
        this.items = null;
    }

    public Global(Integer isConst, Integer cnt, String items){
        this.isConst = isConst;
        this.cnt = cnt;
        this.items = items;
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

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Global: " +
                "isConst=" + isConst +
                ", count=" + cnt +
                ", items=" + items +
                "}";
    }
}
