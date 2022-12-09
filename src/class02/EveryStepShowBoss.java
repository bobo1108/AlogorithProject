package class02;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Create by Thinkpad on 2022/12/9.
 */
public class EveryStepShowBoss {


    public static List<List<Integer>> topK(int[] arr, boolean[] op, int k) {
        List<List<Integer>> ans = new ArrayList<>();
        WhosYourDaddy whosYourDaddy = new WhosYourDaddy(k);
        for (int i = 0; i < arr.length; i++) {
            whosYourDaddy.operate(i, arr[i], op[i]);
            ans.add(whosYourDaddy.getDaddies());
        }
        return ans;
    }

    public static class WhosYourDaddy {
        private HashMap<Integer, Customer> customer;
        private HeapGreater<Customer> candHeap;
        private HeapGreater<Customer> daddyHeap;
        private final int daddyLimit;


        public WhosYourDaddy(int k) {
            customer = new HashMap<Integer, Customer>();
            candHeap  = new HeapGreater<>(new CandidateComparator());
            daddyHeap = new HeapGreater<>(new DaddyComparator());
            daddyLimit = k;
        }

        // 当前处理i号事件，arr[i] -> id,  buyOrRefund
        public void operate(int time, int id, boolean buyOrRefund) {
            if (!buyOrRefund && !customer.containsKey(id)) {
                return;
            }

            if (!customer.containsKey(id)) {
                customer.put(id, new Customer(id, 0, 0));
            }

            Customer c = this.customer.get(id);

            if (buyOrRefund) {
                c.buy ++;
            } else {
                c.buy--;
            }

            if (c.buy == 0) {
                customer.remove(id);
            }

            if (!candHeap.contains(c) && !daddyHeap.contains(c)) {
                if (daddyHeap.size() < daddyLimit) {
                    c.enterTime = time;
                    daddyHeap.push(c);
                } else {
                    c.enterTime = time;
                    candHeap.push(c);
                }
            } else if (candHeap.contains(c)) {
                if (c.buy == 0) {
                    candHeap.remove(c);
                } else {
                    candHeap.resign(c);
                }
            } else {
                if (c.buy == 0) {
                    daddyHeap.remove(c);
                } else {
                    daddyHeap.resign(c);
                }
            }
            daddyMove(time);
        }

        private void daddyMove(int time) {
            if (candHeap.isEmpty()) {
                return;
            }

            if (daddyHeap.size() < daddyLimit) {
                Customer pop = candHeap.pop();
                pop.enterTime = time;
                daddyHeap.push(pop);
            } else {
                if (candHeap.peek().buy > daddyHeap.peek().buy) {
                    Customer oldDaddy = daddyHeap.pop();
                    Customer newDaddy = candHeap.pop();
                    oldDaddy.enterTime = time;
                    newDaddy.enterTime = time;
                    daddyHeap.push(newDaddy);
                    candHeap.push(oldDaddy);
                }
            }
        }

        public List<Integer> getDaddies() {
            List<Customer> customers = daddyHeap.getAllElements();
            List<Integer> ans = new ArrayList<>();

            for (Customer c : customers) {
                ans.add(c.id);
            }

            return ans;
        }

        private class CandidateComparator implements Comparator<Customer> {
            @Override
            public int compare(Customer o1, Customer o2) {
                return o1.buy != o2.buy ? (o2.buy - o1.buy) : (o1.enterTime - o2.enterTime);
            }
        }

        private class DaddyComparator implements Comparator<Customer>{
            @Override
            public int compare(Customer o1, Customer o2) {
                return o1.buy != o2.buy ? (o1.buy - o2.buy) : (o1.enterTime - o2.enterTime);
            }
        }
    }

    private static class Customer {
        public int id;
        public int buy;
        public int enterTime;

        public Customer(int v, int b, int o ) {
            id = v;
            buy = b;
            enterTime = o;
        }
    }
}
