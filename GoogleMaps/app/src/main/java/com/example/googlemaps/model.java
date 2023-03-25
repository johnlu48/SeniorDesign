package com.example.googlemaps;

public class model {
    private String name;
    //private String type;
    private String rating;

    public model(String name, String rating){
        this.name = name;
        //this.type = type;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getType() {
//        return type;
//    }

//    public void setType(String type) {
//        this.type = type;
//    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
