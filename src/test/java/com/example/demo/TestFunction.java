package com.example.demo;

import java.util.Date;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class TestFunction {

    public static  void  main(String [] args){
        Function<String,Integer> findWordCount = new Function<String, Integer>() {
            @Override
            public Integer apply(String s) {
                return s.split(" ").length;
            }
        };
        Integer count = findWordCount.apply("hh hhh");
        System.out.println(count);

        Supplier<MyDate> myDateSupplier = MyDate::new;
        MyDate myDate = myDateSupplier.get();

        Function<Date,String> dayPrinter = myDate::getDayName;
        System.out.println(dayPrinter.apply(new Date()));

        Supplier<String> dayPrinter1 = myDate :: getDayName;
        System.out.println(dayPrinter1.get());

        Predicate<Date> weekendPredicate = myDate::isWeekend;
        System.out.println(weekendPredicate.test(myDate.getNextDate()));

        Predicate<Date> thursdayPredicate = MyDate::isSatuday;
        System.out.println(thursdayPredicate.test(myDate.getNextDate()));
    }
}
