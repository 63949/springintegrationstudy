package com.example.demo;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
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

        List<Person> persons = Arrays.asList(
                new Person("lpz",42),
                new Person("lph",30),
                new Person("lym",12),
                new Person("cxy",12)
        );

        List<Person> filted = persons
                .stream()
                .filter(person -> person.name.startsWith("lp"))
                .collect(Collectors.toList()); // 收集成一个List
        System.out.println(filted);

        Map<Integer,List<Person>> personsByAge = persons
                .stream()
                .collect(Collectors.groupingBy( o -> o.age));// 流化后用collect收集,按年龄收集

        personsByAge
                .forEach((age,p) -> System.out.format("age %s: %s\n",age,p));// (age,p)是Map定义的排列顺序,这里是推断出来的。

        Double averageAge = persons
                .stream()
                .collect(Collectors.averagingInt(p->p.age));

        System.out.println(averageAge);

        IntSummaryStatistics ageSummary = persons
                .stream()
                .collect(Collectors.summarizingInt(p -> p.age));

        System.out.println(ageSummary);
        // collect用法很多,反正是收集每个输入结果进行运算,注意collect里面的都是Collectors.的成员

        String phrase = persons
                .stream()
                .filter(person -> person.age >=18)
                .map(person -> person.name) // 取出person中的一个成员
                .collect(Collectors.joining(" and ","在中国"," 是合法年龄"));
        System.out.println(phrase);

//        转换成map时如何避免key冲突
        Map<Integer,String> map = persons
                .stream()
                .collect(Collectors.toMap(
                        p->p.age, // map的key
                        p->p.name,// map的value
                        (name1,name2)->name1+";"+name2 //避免冲突的HASH函数
                ));
        System.out.println(map);

        // 自定义一个Collector接口：我们的目标是将stream中所有用户的用户名变成大写并用"|"符号连接成一个字符串。
        Collector<Person,StringJoiner,String> personNameCollector =
               /* Collector.of(
                        ()->new StringJoiner(" | "), // supplier 提供者
                        (j,p)->j.add(p.name.toUpperCase()), // accumulator 累加器,每个对象进来都要做这一步
                        (j1,j2)->j1.merge(j2), //combiner // 连接器
                        StringJoiner::toString  // finisher // 完成
                );*/
                new Collector<Person, StringJoiner, String>() {

                    @Override
                    public Supplier<StringJoiner> supplier() {
//                        return () -> new StringJoiner(" | "); // ()推断为Supplier接口的get函数
                        return new Supplier<StringJoiner>() { // 这里就是匿名类和lamda表达式的转换由系统自动完成
                            @Override
                            public StringJoiner get() {
                                return new StringJoiner(" | ");
                            }
                        };
                    }
                    @Override
                    public BiConsumer<StringJoiner, Person> accumulator() {
                        //return (j,p)->j.add(p.name.toUpperCase());
                        return  new BiConsumer<StringJoiner, Person>() {
                            @Override
                            public void accept(StringJoiner stringJoiner, Person person) {
                                stringJoiner.add(person.name.toUpperCase());
                            }
                        };
                    }
                    @Override
                    public BinaryOperator<StringJoiner> combiner() {
//                        return (j1,j2)->j1.merge(j2);
                        return new BinaryOperator<StringJoiner>() {
                            @Override
                            public StringJoiner apply(StringJoiner stringJoiner, StringJoiner stringJoiner2) {
                                return stringJoiner.merge(stringJoiner2);
                            }
                        };
                    }
                    @Override
                    public Function<StringJoiner, String> finisher() {
//                        return StringJoiner::toString;
                        return new Function<StringJoiner, String>() {
                            @Override
                            public String apply(StringJoiner stringJoiner) {
                                return stringJoiner.toString();
                            }
                        };
                    }
                    @Override
                    public Set<Characteristics> characteristics() {
//                        return new HashSet<>();
                        return new Set<Characteristics>() {
                            @Override
                            public int size() {
                                return 0;
                            }

                            @Override
                            public boolean isEmpty() {
                                return false;
                            }

                            @Override
                            public boolean contains(Object o) {
                                return false;
                            }

                            @Override
                            public Iterator<Characteristics> iterator() {
                                return null;
                            }

                            @Override
                            public Object[] toArray() {
                                return new Object[0];
                            }

                            @Override
                            public <T> T[] toArray(T[] a) {
                                return null;
                            }

                            @Override
                            public boolean add(Characteristics characteristics) {
                                return false;
                            }

                            @Override
                            public boolean remove(Object o) {
                                return false;
                            }

                            @Override
                            public boolean containsAll(Collection<?> c) {
                                return false;
                            }

                            @Override
                            public boolean addAll(Collection<? extends Characteristics> c) {
                                return false;
                            }

                            @Override
                            public boolean retainAll(Collection<?> c) {
                                return false;
                            }

                            @Override
                            public boolean removeAll(Collection<?> c) {
                                return false;
                            }

                            @Override
                            public void clear() {
                            }
                        };
                    }
                };

               String names = persons.stream().collect(personNameCollector);
               System.out.println(names);

    }


}

class Person{
    String name;
    int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return name;
    }
}


