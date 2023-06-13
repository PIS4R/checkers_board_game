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
        int[] coords = new int[2];

        directions = new ArrayList<int[]>();
        directions.add(coords);
        directions.add(coords);
        directions.add(coords);
        directions.add(coords);

        //initDirections();
        int p = 0;
    }


    public int setDirectionEmpty(int direction){
        if(direction < 0 || direction > 3)
            return 1;

        switch(direction){
            case(DOWN_RIGHT): //down-right
                directions.set(DOWN_RIGHT, null);
                break;
            case(DOWN_LEFT): //down-left
                directions.set(DOWN_LEFT, null);
                break;
            case(UP_RIGHT): //up-right
                directions.set(UP_RIGHT, null);
                break;
            case(UP_LEFT): //up-left
                directions.set(UP_LEFT, null);
                break;
        }
        return 0;
    }

    public int setDirection(int newNode_x, int  newNode_y, int direction){
        if(direction < 0 || direction > 3)
            return 1;

        int coords[] = new int[2];
        coords[0] = newNode_x;
        coords[1] = newNode_y;

        switch(direction){
            case(DOWN_RIGHT): //down-right
                directions.set(DOWN_RIGHT, coords);
                break;
            case(DOWN_LEFT): //down-left
                directions.set(DOWN_LEFT, coords);
                break;
            case(UP_RIGHT): //up-right
                directions.set(UP_RIGHT, coords);
                break;
            case(UP_LEFT): //up-left
                directions.set(UP_LEFT, coords);
                break;
        }
        return 0;
    }

    private void initDirections(){
        int[] temp = new int[2];
        temp[0] = 0;
        temp[1] = 0;

        directions.set(DOWN_RIGHT, temp);
        directions.set(DOWN_LEFT, temp);
        directions.set(UP_RIGHT, temp);
        directions.set(UP_LEFT, temp);
    }

    public boolean isDirectionEmpty(int direction){
        if(directions.get(direction) == null){
            //System.out.println("directions of a node [" + this.node_X+','+this.node_Y+"] = "+ directions.get(direction));
            if(directions.get(direction) != null)
                System.out.println(directions.get(direction)[0]+','+directions.get(direction)[1]);

            return true;
        }
        return false;
    }

}
