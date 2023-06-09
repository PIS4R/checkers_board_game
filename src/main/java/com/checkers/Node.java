package com.checkers;

import java.util.ArrayList;



public class Node {


    final static int DOWN_RIGHT = 0;
    final static int DOWN_LEFT = 1;
    final static int UP_RIGHT = 2;
    final static int UP_LEFT = 3;

    int node_X = -1;
    int node_Y = -1;

    ArrayList<int[]> directions;

    public Node(int x, int y){

        node_X = x;
        node_Y = y;
        directions = new ArrayList<int[]>();
        initDirections();

    }


    public int setDirection(int newNode_x, int  newNode_y, int direction){
        if(direction < 0 || direction > 3)
            return 1;

        int coords[] = {0, 0};
        coords[0] = newNode_x;
        coords[1] = newNode_y;

        switch(direction){
            case(DOWN_RIGHT): //down-right
                directions.add(DOWN_RIGHT, coords);
                break;
            case(DOWN_LEFT): //down-left
                directions.add(DOWN_LEFT, coords);
                break;
            case(UP_RIGHT): //up-right
                directions.add(UP_RIGHT, coords);
                break;
            case(UP_LEFT): //up-left
                directions.add(UP_LEFT, coords);
                break;
        }
        return 0;
    }

    private void initDirections(){
        directions.add(DOWN_RIGHT, null);
        directions.add(DOWN_LEFT, null);
        directions.add(UP_RIGHT, null);
        directions.add(UP_LEFT, null);
    }

    public boolean isDirectionEmpty(int direction){
        if(directions.get(direction) == null){
            return true;
        }
        return false;
    }

}
