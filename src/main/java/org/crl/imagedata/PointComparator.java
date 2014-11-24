package org.crl.imagedata;

import java.util.Comparator;

public class PointComparator implements Comparator<Point> {
    public int compare(Point p1,Point  p2) {
    	Integer p1x=p1.getXCoord();
    	Integer p2x=p2.getXCoord();
    	Integer p1y=p1.getYCoord();
        if(p1x==p2x){
        	return p1y.compareTo(p2.getYCoord());
        }
        return p1x.compareTo(p2x);
       
    }           
};