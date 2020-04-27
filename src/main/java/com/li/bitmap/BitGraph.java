package com.li.bitmap;

public class BitGraph {
    final int BITS_PRE_WORD = 32;
    final static int max = 16;

    void setBit(int[] arr, int n) {
        arr[n / BITS_PRE_WORD] |= (1 << (n % BITS_PRE_WORD));
    }

    void clearBit(int[] arr, int n) {
    }

    int getBit(int[] arr, int n) {
        return (arr[n / BITS_PRE_WORD] & (1 << (n % BITS_PRE_WORD))) != 0 ? 1 : 0;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        BitGraph bg = new BitGraph();
        int[] datas = new int[]{1, 13, 14, 15, 7, 8, 9, 13, 1, 13, 14, 15, 7, 8, 9, 13, 2};
        int[] arr = new int[max / 32 + 1];
        for (int data : datas) {
            bg.setBit(arr, data);
        }
        int count = 0;
        for (int i = 0; i < max; i++) {
            if (bg.getBit(arr, i) == 1) {
                System.out.println(i);
                ++count;
            }
        }
        System.out.println("count" + count);
    }
}
