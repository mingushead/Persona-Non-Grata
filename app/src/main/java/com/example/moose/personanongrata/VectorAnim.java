package com.example.moose.personanongrata;

/**
 * Created by Moose on 07/11/2015.
 */
public class VectorAnim {
    int x,y;

    public VectorAnim(int _x, int _y){
        this.x = _x;
        this.y = _y;
    }

    void setX(int newX){
        this.x = newX;
    }

    void setY(int newY){
        this.y = newY;
    }

    void addX(int add){
        this.x += add;
    }

    void addY(int add){
        this.y += add;
    }

    void addVector(VectorAnim vector){
        this.addX(vector.getX());
        this.addY(vector.getY());
    }

    void subX(int subtract){
        this.x -= subtract;
    }

    void subY(int subtract){
        this.y -= subtract;
    }

    void subtractVector(VectorAnim vector){
        this.subX(vector.getX());
        this.subY(vector.getY());
    }

    void setXY(int _x, int _y){
        this.setX(_x);
        this.setY(_y);
    }

    int getX(){
        return x;
    }

    int getY(){
        return y;
    }

    VectorAnim lerp(VectorAnim vec1, VectorAnim vec2, float divider){
        VectorAnim vector = new VectorAnim(0,0);



        vector.setX(Math.round(vec1.getX() + (vec2.getX()-vec1.getX()) * divider));
        vector.setY(Math.round(vec1.getY() + (vec2.getY()-vec1.getY()) * divider));

        return vector;
    }

}
