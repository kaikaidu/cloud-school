//package com.amway.acti.controller.frontendcontroller;
//
//import com.amway.acti.CloudschoolApplication;
//import com.amway.acti.dto.CommonResponseDto;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.*;
//
//import static org.junit.Assert.*;
//
///**
// * <一句话功能简述>
// * <功能详细描述>
// *
// * @author zhengweishun
// * @version [版本号, 2018/4/24]
// * @see [相关类/方法]
// * @since [产品/模块版本]
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = CloudschoolApplication.class)
//public class ActivityControllerTest {
//
//    @Autowired
//    private ActivityController activityController;
//
//    @Test
//    public void signUp() {
////        CommonResponseDto dto = activityController.signUp(4, 7835, "7835");
////        System.out.println(dto.getCode());
//    }
//
//    @Test
//    public void signUps() {
//        // 创建任务集合
//        List<Callable<CommonResponseDto>> tasks = new ArrayList<Callable<CommonResponseDto>>();
//        Callable<CommonResponseDto> task = null;
//        // 创建线程池
//        ExecutorService exec = Executors.newFixedThreadPool(5);
//        for (int i = 0; i < 5; i++) {
//            int userId = 7833 + i;
//            task = new Callable<CommonResponseDto>() {
//                @Override
//                public CommonResponseDto call() throws Exception {
//                    CommonResponseDto dto = activityController.signUp(4, userId, "userId");
//                    return dto;
//                }
//            };
//            tasks.add(task);
//        }
//
//        long s = System.currentTimeMillis();
//
//        List<Future<CommonResponseDto>> results = null;
//        try {
//            results = exec.invokeAll(tasks);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("执行任务消耗了 ：" + (System.currentTimeMillis() - s)
//                + "毫秒");
//
//        for (Future<CommonResponseDto> future : results) {
//            try {
//                System.out.println(future);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}