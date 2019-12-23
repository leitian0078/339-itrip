package cn.bdqn.entity;

import org.apache.solr.client.solrj.beans.Field;

import java.io.Serializable;

public class HotelEntity implements Serializable {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String hotelName() {
        return hotelName;
    }

    public void setHothlname(String hotelName) {
        hotelName = hotelName;
    }
    @Field
    private String id;
    @Field
    private String hotelName;
}
