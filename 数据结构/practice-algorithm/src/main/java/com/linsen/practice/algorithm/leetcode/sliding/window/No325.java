package com.linsen.practice.algorithm.leetcode.sliding.window;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lane.lin
 * @Description
 * 325. 和等于 k 的最长子数组长度
 * 给定一个数组 nums 和一个目标值 k，找到和等于 k 的最长子数组长度。如果不存在任意一个符合要求的子数组，则返回 0
 * nums 数组的总和是一定在 32 位有符号整数范围之内的。
 * @since 2020/12/15
 */
public class No325 {


    public static void main(String[] args) {
        No325 target = new No325();
        int[] nums = new int[]{1,-1,5,-2,3};
        System.out.println(target.maxSubArrayLen(nums, 3));
    }


    public int maxSubArrayLen(int[] nums, int k){
        int len = nums.length;
        Map<Integer, Integer> map = new HashMap<>();
        int sum = 0;
        int ans = 0;
        for(int i = 0; i < len; i++){
            sum = sum + nums[i];
            //相当于窗口右移一位
            if(sum == k){
                ans = i + 1;
                //有可能就是答案
            }
            //精髓
            //如果map中存在当前sum[i] - k 的和，说明，一定存在子数组和=k
            //例如：前i=3的和=5，k=8，前i=8的和=13，则一定存在子数组从i=4到i=8的和=13-5=8
            if(map.containsKey(sum - k)){
                ans = Math.max(ans, i - map.get(sum - k));
            }
            if(!map.containsKey(sum)){
                map.put(sum, i);
                //后面再遇到的不用加进去，因为长度肯定大，我们要比较小的，因为这个东西是要被减去的。
            }
        }
        return ans;
    }


    public int maxSubArrayLen2(int[] nums, int k){
        int len = nums.length;
        int max = 0;
        int[] sum = new int[len + 1];
        Map<Integer, Integer> map = new HashMap<>();
        map.put(0, 0);
        for(int i = 1; i <= len; i++){
            sum[i] = sum[i - 1] + nums[i - 1];
            if(!map.containsKey(sum[i])){
                map.put(sum[i], i);
            }
        }

        //从后向前遍历数组，i为子数组的结尾，寻找符合条件的前缀和及其索引
        for (int i = len; i > max; i--){
            //精髓
            //如果map中存在当前sum[i] - k 的和，说明，一定存在子数组和=k
            //例如：前i=3的和=5，k=8，前i=8的和=13，则一定存在子数组从i=4到i=8的和=13-5=8
            if(map.containsKey(sum[i] - k)){
                max = Math.max(max, i - map.get(sum[i] - k));
            }
        }
        return max;
    }
}
