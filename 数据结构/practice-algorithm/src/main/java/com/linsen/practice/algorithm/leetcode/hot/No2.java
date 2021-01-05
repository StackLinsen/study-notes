package com.linsen.practice.algorithm.leetcode.hot;

import java.util.List;

/**
 * @author lane.lin
 * @Description 给你两个 非空 的链表，表示两个非负的整数。它们每位数字都是按照 逆序 的方式存储的，并且每个节点只能存储 一位 数字。
 * <p>
 * 请你将两个数相加，并以相同形式返回一个表示和的链表。
 * <p>
 * 你可以假设除了数字 0 之外，这两个数都不会以 0 开头。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/add-two-numbers
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 * @since 2021/1/4
 */
public class No2 {

    public static void main(String[] args) {
        No2 clazz = new No2();
        ListNode l1 = new ListNode(2, new ListNode(4, new ListNode(3)));
        ListNode l2 = new ListNode(5, new ListNode(6, new ListNode(4)));
        ListNode result = clazz.addTwoNumbers(l1, l2);
        System.out.println(result.val);
        System.out.println(result.next.val);
        System.out.println(result.next.next.val);
    }

    /**
     * 完成度：70%，主要是生成ListNode不会操作
     * 模拟法
     * @param l1
     * @param l2
     * 时间复杂度：O(max(m,n))
     * 空间复杂度：O(max(m,n))
     * @return
     */
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        int carry = 0;
        ListNode head = null;
        ListNode tail = null;
        while(null != l1 || null != l2){
            int n1 = l1 != null ? l1.val : 0;
            int n2 = l2 != null ? l2.val : 0;
            int sum = n1 + n2 + carry;

            if(head == null){
                head = tail = new ListNode(sum % 10);
            }else{
                tail.next = new ListNode(sum % 10);
                tail = tail.next;
            }
            carry = sum / 10;
            if(l1 != null){
                l1 = l1.next;
            }
            if(l2 != null){
                l2 = l2.next;
            }
        }
        if(carry > 0){
            tail.next = new ListNode(carry);
        }
        return head;
    }



    public static class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }


}
