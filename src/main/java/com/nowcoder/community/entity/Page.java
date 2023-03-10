package com.nowcoder.community.entity;

public class Page {
    //当前页码
    private int current = 1;

    //当前显示条数
    private int limit = 10;

    //总数据条数
    private int rows;

    //查询路径
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current>=1){
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit >= 1 && limit <= 100){
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if(rows >= 0){
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    //获取当前页起始行
    public int getOffset(){
        return (current-1)*limit;
    }

    //获取总的页数
    public int getTotal(){
        if((rows & limit) == 0){
            return rows / limit;
        }else{
            return rows / limit + 1;
        }
    }

    //获取起始页码
    public int getFrom(){
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    //获取结束页码
    public int getTo(){
        int to = current + 2;
        return to > getTotal() ? getTotal() : to;
    }
}
