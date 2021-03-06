package chapter14;

import java.util.function.Consumer;

/**
 * 函数式方法不允许修改任何全局数据结构或者任何作为参数传入的参数。为什么呢？
 * 因为一旦对这些数据进行修改，两次相同的调用就很可能产生不同的
 * 结构 -- 这违背了引用透明性原则，我们也就无法将方法简单地看作由参数到结果的映射。
 */
public class PersistentTrainJourney {

    public static void main(String[] args) {
        TrainJourney tj1 = new TrainJourney(40, new TrainJourney(30, null));
        TrainJourney tj2 = new TrainJourney(20, new TrainJourney(50, null));

        TrainJourney appended = append(tj1, tj2);
        visit(appended, tj -> { System.out.print(tj.price + " - "); });
        System.out.println();

        // A new TrainJourney is created without altering tj1 and tj2.
        TrainJourney appended2 = append(tj1, tj2);
        visit(appended2, tj -> { System.out.print(tj.price + " - "); });
        System.out.println();

        // tj1 is altered but it's still not visible in the results.
        TrainJourney linked = link(tj1, tj2);
        visit(linked, tj -> { System.out.print(tj.price + " - "); });
        System.out.println();

        // ... but here, if this code is uncommented, tj2 will be appended
        // at the end of the already altered tj1. This will cause a
        // StackOverflowError from the endless visit() recursive calls on
        // the tj2 part of the twice altered tj1.
        /*TrainJourney linked2 = link(tj1, tj2);
        visit(linked2, tj -> { System.out.print(tj.price + " - "); });
        System.out.println();*/
    }

    static class TrainJourney {
        public int price;
        public TrainJourney onward;

        public TrainJourney(int p, TrainJourney t) {
            price = p;
            onward = t;
        }
    }

    static TrainJourney link(TrainJourney a, TrainJourney b) {
        if (a == null) {
            return b;
        }
        TrainJourney t = a;
        while (t.onward != null) {
            t = t.onward;
        }
        t.onward = b;
        return a;
    }

    static TrainJourney append(TrainJourney a, TrainJourney b) {
        return a == null ? b : new TrainJourney(a.price, append(a.onward, b));
    }

    static void visit(TrainJourney journey, Consumer<TrainJourney> c) {
        if (journey != null) {
            c.accept(journey);
            visit(journey.onward, c);
        }
    }
}
