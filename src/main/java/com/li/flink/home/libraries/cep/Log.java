package com.li.flink.home.libraries.cep;

public class Log{

    public int id;
    public String sql;

    public Log(int id, String sql) {
        this.id = id;
        this.sql = sql;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public String toString() {
        return "Log{" +
                "id=" + id +
                ", sql='" + sql + '\'' +
                '}';
    }
}
