package com.amway.acti.base.aop;

//@Aspect
//@Component
//@Slf4j
public class OperationLogAspect {

   /* @Autowired
    private OperationLogMapper operationLogMapper;

    @Pointcut("execution(public * com.amway.acti.controller.backendcontroller..*.*(..))")
    public void operationLog(){

    }

    @Before("operationLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable{
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        User user = (User)request.getSession().getAttribute(Constants.BACKEND_USER_SESSSION_KEY);
//        log.info("==========>id: "+UUID.randomUUID().toString().replaceAll("-",""));
//        log.info("==========>BeforeTime: "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//        log.info("==========>USER: "+(StringUtils.isEmpty(user)?"":user.getName()));
//        log.info("==========>URL: "+request.getRequestURL().toString());
//        log.info("==========>HTTP_METHOD: "+request.getMethod());
//        log.info("==========>IP: "+request.getHeader("X-Forwarded-For") != null ? request.getHeader("X-Forwarded-For") : request.getRemoteAddr());
//        log.info("==========>CLASS_METHOD: "+joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName());
        OperationLog operationLog = new OperationLog();
        operationLog.setId(UUID.randomUUID().toString().replaceAll("-",""));
        operationLog.setUserName(StringUtils.isEmpty(user)?"":user.getName());
        operationLog.setBeforeTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        operationLog.setUrl(request.getRequestURL().toString());
        operationLog.setHttpMethod(request.getMethod());
        operationLog.setIp(request.getHeader("X-Forwarded-For") != null ? request.getHeader("X-Forwarded-For") : request.getRemoteAddr());
        String classMethod = joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName();
        operationLog.setClassMethod(classMethod.substring(classMethod.lastIndexOf("controller"),classMethod.length()));
//        operationLogMapper.insertSelective(operationLog);
    }

    @AfterReturning(returning = "ret", pointcut = "operationLog()")
    public void doAfterReturning(Object ret) throws Throwable{
//        log.info("==========>AfterTime: "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//        log.info("==========>RESPONSE:"+ret);

    }*/
}
