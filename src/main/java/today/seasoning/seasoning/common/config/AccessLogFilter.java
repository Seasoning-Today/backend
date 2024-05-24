package today.seasoning.seasoning.common.config;


import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import org.springframework.beans.factory.annotation.Value;

@WebFilter(urlPatterns = "/monitoring/*")
public class AccessLogFilter implements Filter {

    @Value("${server.tomcat.accesslog.condition-unless}")
    private String conditionUnlessKey;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        request.setAttribute(conditionUnlessKey, conditionUnlessKey);
        chain.doFilter(request, response);
    }

}
