package com.lhk;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentSkipListSet;

public class SkipListTest {

    static class SkipNode implements Comparable {
        private int num;
        private String str;

        public SkipNode(int num, String str) {
            this.num = num;
            this.str = str;
        }

        public int getNum() {
            return num;
        }

        public SkipNode setNum(int num) {
            this.num = num;
            return this;
        }

        public String getStr() {
            return str;
        }

        public SkipNode setStr(String str) {
            this.str = str;
            return this;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SkipNode skipNode = (SkipNode) o;
            return num == skipNode.num;
        }

        @Override
        public String toString() {
            return "SkipNode{" +
                    "num=" + num +
                    ", str='" + str + '\'' +
                    '}';
        }

        @Override
        public int compareTo(Object o) {
            SkipNode skipNode = (SkipNode) o;
            return num - skipNode.num;
        }
    }

    public static void main(String[] args) {
        ConcurrentSkipListSet<SkipNode> skipListSet = new ConcurrentSkipListSet<>();
        long startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            skipListSet.add(new SkipNode(i, "idnews" + "0" + i + "00" + i + ":hash"));
        }
        System.out.println("foreach insert time cost" + (System.nanoTime() - startTime));
        startTime = System.nanoTime();
        NavigableSet<SkipNode> skipNodeSet = new ConcurrentSkipListSet<>();
        int countNum = 0;
        for (int i = 0; i < 10000; i++) {
            skipNodeSet = skipListSet.headSet(new SkipNode(500, ""));
            List<SkipNode> testList = new ArrayList<>(skipNodeSet);
            countNum += testList.size();
        }
        System.out.println("find head time cost" + (System.nanoTime() - startTime) / 10000);
        startTime = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            skipNodeSet = skipListSet.tailSet(new SkipNode(500, ""));
            List<SkipNode> testList = new ArrayList<>(skipNodeSet);
            countNum += testList.size();
        }
        System.out.println("find tail time cost" + (System.nanoTime() - startTime) / 10000);
    }
}
