package cn.vic.travel.network;

import java.util.List;

/**
 * Snake 创建于 2018/5/9.
 */

class TipList {
    private Point[] tipArray;
    private static final TipList ourInstance = new TipList();

    static TipList getInstance() {
        return ourInstance;
    }

    private TipList() {

    }
}
