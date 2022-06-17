package data;

import java.io.Serializable;

public enum StandardOfLiving implements Serializable {
    ULTRA_HIGH("ultra high"),
    HIGH("high"),
    MEDIUM("medium"),
    ULTRA_LOW("ultra low"),
    NIGHTMARE("nightmare");
    private String st;

     StandardOfLiving(String st) {
        this.st = st;
    }
}
