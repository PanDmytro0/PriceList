package com.fernfog.pricelist;

public class MyData implements Cloneable {

    String photoLink;
    String count;
    String inPack;
    String description;
    String name;

    MyData(String name, String photoLink, String count, String inPack, String description) {
        this.name = name;
        this.photoLink = photoLink;
        this.count = count;
        this.inPack = inPack;
        this.description = description;
    }

    MyData(String photoLink) {
        this.photoLink = photoLink;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public String getCount() {
        return count;
    }

    public String getInPack() {
        return inPack;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
