package com.nowcode.commuity.domain;

public class Page {

    //当前页码
    private int current = 1;
    //显示上限
    private int limit = 10;
    //数据总量（用于计算总页数）
    private int rows;

    private String path;




    public void setCurrent(int current) {
        if(current >= 1){
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit >=1 && limit <= 100){
            this.limit = limit;
        }

    }

    public int getRows() {
        return rows;
    }
    public int getCurrent() {
        return current;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取当前页的起始行
     * @return
     */
    public int getOffset(){
        return (current-1)*limit;
    }

    /**
     * 获取总页数
     * @return
     */
    public int getTotal(){
        if(rows % limit == 0){
            return rows /limit;
        }else {
            return rows/limit +1;
        }
    }

    /**
     * 获取页面显示的起始页
     * @return
     */
    public int getFrom(){
        int from = current-2;
        return from <1 ? 1:from;

    }

    /**
     *获取页面显示的结束页码
     * @return
     */
    public int getTo(){
        int to = current+2;
        int total = getTotal();
        return to > total ? total :to;
    }


}
