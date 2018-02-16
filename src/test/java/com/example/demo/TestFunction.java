package com.example.demo;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TestFunction {

    public static void main(String[] args) {
        Function<String, Integer> findWordCount = new Function<String, Integer>() {
            @Override
            public Integer apply(String s) {
                return s.split(" ").length;
            }
        };
        Integer count = findWordCount.apply("hh hhh");
        System.out.println(count);

        Supplier<MyDate> myDateSupplier = MyDate::new;
        MyDate myDate = myDateSupplier.get();

        Function<Date, String> dayPrinter = myDate::getDayName;
        System.out.println(dayPrinter.apply(new Date()));

        Supplier<String> dayPrinter1 = myDate::getDayName;
        System.out.println(dayPrinter1.get());

        Predicate<Date> weekendPredicate = myDate::isWeekend;
        System.out.println(weekendPredicate.test(myDate.getNextDate()));

        Predicate<Date> thursdayPredicate = MyDate::isSatuday;
        System.out.println(thursdayPredicate.test(myDate.getNextDate()));

        // java流式操作
        /*stream包含中间（intermediate operations）和最终（terminal operation）两种形式的操作。中间操作
        （intermediate operations）的返回值还是一个stream，因此可以通过链式调用将中间操作（intermediate
        operations）串联起来。最终操作（terminal operation）只能返回void或者一个非stream的结果。
        ilter, map ，sorted是中间操作，而forEach是一个最终操作
        链式调用也被称为操作管道流
        */
        /*
        大多stream操作接受某种形式的lambda表达式作为参数，通过方法接口的形式指定操作的具体行为，
        这些方法接口的行为基本上都是无干扰(non-interfering)和无状态(stateless)。
        无干扰(non-interfering)的方法的定义是：该方法不修改stream的底层数据源,
        比如上述例子中：没有lambda表达式添加或者删除myList中的元素。
        无状态(stateless)方法的定义：操作的执行是独立的，
        比如上述例子中，没有lambda表达式在执行中依赖可能发生变化的外部变量或状态。
         */

        List<String> myList = Arrays.asList("a1", "a2", "b1", "c2", "c1");
        myList
                .stream()
                .filter(s -> s.startsWith("c"))
                .map(String::toUpperCase)
                .sorted()
                .forEach(System.out::println);

        /*streams分类：stream()和parallelStream()
        * 顺序流（sequential streams）
        * 并发stream(Parallel streams)
        * */

        Arrays.asList("s1","s2","s3")
                .stream()
                .findFirst()
                .ifPresent(System.out::println);

        Stream.of("s1","s2","s3")
                .findFirst()
                .ifPresent(System.out::println);

        // 基本类型流,可以使用一些特殊表达式
        IntStream.range(1,4)
                .forEach(System.out::println);
//lamda表达式在这里的使用方式是输入op有提示
        IntStream.range(1,4)
                .map( operand -> 2*operand+1 )
                .average()
                .ifPresent(System.out::println);
//        常规对象流和基本类型流之间可以转换
        IntStream.range(1,4)
                .mapToObj( i -> "a"+ i)
                .forEach(System.out::println);

        Stream.of(1,2,3)
                .mapToInt(i -> i+1)
                .mapToObj(i -> "b"+i)
                .forEach(System.out::println);

//        处理顺序:Laziness（延迟加载）是中间操作（intermediate operations）的一个重要特性
//        这是因为只有最终操作（terminal operation）存在的时候，中间操作（intermediate operations）才会执行。
        IntStream.range(1,4)
                .filter(s ->{
                    System.out.println("filter:"+s);
                    return true;
                });// 不会打印结果
//每一个元素沿着链垂直移动，第一个字符串"d2"执行完filter和forEach后第二个元素"a2"才开始执行。
//        调整steream执行链顺序可以提高执行效率
        IntStream.range(1,4)
                .filter(s->{
                    System.out.println("filter:"+s);
                    return true;
                })
                .forEach(s->System.out.println("forEach"+s));

//        流复用
        Stream<String >stream = Stream.of("a1","a2","b1")
                .filter(s -> s.startsWith("a"));
        stream.noneMatch(s->true);
//        stream.anyMatch(s -> true);
        //当你执行完任何一个最终操作（terminal operation）的时候流就被关闭了 java.lang.IllegalStateException: stream has already been operated upon or closed
//解决方法如下
        /*Supplier<Stream<String>> streamSupplier = new Supplier<Stream<String>>() {
            @Override
            public Stream<String> get() {
                return Stream.of("a1","a2","b1")
                        .filter(s -> s.startsWith("a"));
            }
        };*/
//        简化的写法为
       /* Supplier<Stream<String>> streamSupplier =
                ()-> {// Supplier是一个没有参数只有返回值的函数所以用()表示,->后面的内容表示函数体
                    return Stream.of("a1", "a2", "b1")
                            .filter(s -> s.startsWith("a"));
                };*/
//       再简化
        Supplier<Stream<String>> streamSupplier =
                ()-> // Supplier是一个没有参数只有返回值的函数所以用()表示,->后面的内容表示函数体
                    Stream.of("a1", "a2", "b1")
                            .filter(s -> s.startsWith("a"));
        System.out.println(streamSupplier.get()
                .noneMatch(s -> true));
        System.out.println(streamSupplier.get()
                .anyMatch(s -> true));
    }
}
