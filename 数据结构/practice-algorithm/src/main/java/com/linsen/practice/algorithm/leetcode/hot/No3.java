package com.linsen.practice.algorithm.leetcode.hot;

import java.util.HashSet;
import java.util.Set;

/**
 * @author lane.lin
 * @Description 给定一个字符串，请你找出其中不含有重复字符的 最长子串 的长度
 * @since 2021/1/5
 */
public class No3 {


    public static void main(String[] args) {

    }

    /**
     * 滑动窗口
     * @param s
     * @return
     */
    public int lengthOfLongestSubstring(String s) {
        Set<Character> occ = new HashSet<Character>();
        int n = s.length();
        int rk = -1, ans = 0;
        for(int i = 0; i < n; i++){
            //左指针往右移动了一位，则将集合中的去掉
            if(i != 0){
                occ.remove(s.charAt(i - 1));
            }
            //不停的往右移动直到遇到重复的
            while(rk + 1 < n && !occ.contains(s.charAt(rk + 1))){
                occ.add(s.charAt(rk + 1));
                ++rk;
            }
            ans = Math.max(ans, rk - i + 1);
        }
        return ans;
    }
}
